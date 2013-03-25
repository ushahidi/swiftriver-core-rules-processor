/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/agpl.html>
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ushahidi.swiftriver.core.model.RawDrop;
import com.ushahidi.swiftriver.core.model.Rule;
import com.ushahidi.swiftriver.core.model.Rule.RuleAction;
import com.ushahidi.swiftriver.core.model.Rule.RuleCondition;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

/**
 * RulesExecutor class
 * 
 * The RulesExectuor applies the a set of rules to the supplied drop. A rule is
 * comprised of a set of conditions and actions. The conditions outline the 
 * requirements that must be met in order for the actions can be fired. The conditions
 * are based on the properties of a {@link RawDrop} object and are checked before
 * the drop enters a river
 * 
 * @author ekala
 */
public class RulesExecutor {

	private String dropContent = null;
	
	//> List of rivers from which the drop is excluded
	private List<Long> excludedRiverIds = new ArrayList<Long>();
	
	static final Logger LOG  = LoggerFactory.getLogger(RulesExecutor.class);

	public void applyRules(RawDrop drop, ConcurrentMap<Long,Map<Long,Rule>> rulesMap) {
		// Check if the list of bucket ids has been initialized
		if (drop.getBucketIds() == null) {
			drop.setBucketIds(new ArrayList<Long>());
		}
		
		// Check if the mark as read list has been initialized
		if (drop.getMarkAsRead() == null) {
			drop.setMarkAsRead(new ArrayList<Long>());
		}

		if (drop.getChannel().equals("rss")) {
			// Clean the content of all HTML
			try {
				dropContent = ArticleExtractor.INSTANCE.getText(drop.getContent());
			} catch (BoilerpipeProcessingException e) {
				e.printStackTrace();
			}
		} else {
			dropContent = drop.getContent();
		}

		LOG.debug("Applying rules to drop");

		for (Long riverId: drop.getRiverIds()) {
			// Check if the destination river has any defined rules
			if (rulesMap.containsKey(riverId)) {
				for (Map.Entry<Long, Rule> entry: rulesMap.get(riverId).entrySet()) {
					executeRule(riverId, drop, entry.getValue());
				}
			}
		}
		
		drop.getRiverIds().removeAll(excludedRiverIds);
		LOG.debug("Rules exection complete");
	}

	/**
	 * Runs the {@link Rule} specified in <code>rule</code> against the {@link RawDrop}
	 * specified in <code>drop</code>
	 *  
	 * @param riverId
	 * @param drop
	 * @param rule
	 */
	private void executeRule(Long riverId, RawDrop drop, Rule rule) {
		// Check each condition
		if (conditionsMatch(drop, rule.getConditions(), rule.isMatchAllConditions())) {
			LOG.debug(String.format("Conditions for rule %d passed.", rule.getId()));
			performRuleActions(riverId, drop, rule.getActions());
		}
	}

	/**
	 * Checks if the {@link RawDrop} specified in <code>drop</code> meets the
	 * list of conditions specified in <code>conditions</code>
	 * 
	 * @param drop
	 * @param conditions
	 * @param matchAllConditions
	 * @return
	 */
	private boolean conditionsMatch(RawDrop drop,
			List<RuleCondition> conditions, boolean matchAllConditions) {		
		// Minimum no. of conditions to pass in order for the rule actions to fire 
		int expectedMatchCount = matchAllConditions ? conditions.size() : 1;

		// Tracks the no. of conditions matched
		int matchCount = 0;
		for (RuleCondition condition: conditions) {
			String fieldValue = null;
			if (condition.getField().equals("title")) {
				fieldValue = drop.getTitle();
			} else if (condition.getField().equals("content")) {
				fieldValue = dropContent;
			} else if (condition.getField().equals("source")) {
				fieldValue = drop.getIdentityName();
			}
			
			if (condition.getOperator().equals("is")) {
				matchCount += condition.getValue().equals(fieldValue) ? 1 : 0;
			} 
			
			if (condition.getOperator().equals("contains")) {
				matchCount += contains(fieldValue, condition.getValue()) ? 1 : 0;
			}
			
			if (condition.getOperator().equals("does not contain")) {
				matchCount += !contains(fieldValue, condition.getValue()) ? 1 : 0;
			}
		}

		return matchCount >= expectedMatchCount;
	}

	/**
	 * Checks whether the <code>java.lang.String</code> specified in
	 * <code>value</code> exists in the input <code>java.lang.String</code>
	 * specified in <code>subject</code>
	 * 
	 * @param subject
	 * @param value
	 * @return
	 */
	private boolean contains(String subject, String value) {
		String regex = null;
		
		if (hasSpace(value)) {
			regex = String.format("(?i)(%s)+?", value);
		} else {
			// Use word boundary - case insensitive
			regex = String.format("(?i)(\\b%s\\b)+?", value);
		}
		
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(subject).find();
	}
	
	/**
	 * Checks whether the provided input contains whitespace characters
	 * @param subject
	 * @return
	 */
	private boolean hasSpace(String subject) {
		Pattern pattern = Pattern.compile("\\s");
		return pattern.matcher(subject).find();
	}

	/**
	 * Performs the rule actions when the conditions have passed
	 * 
	 * @param riverId
	 * @param drop
	 * @param actions
	 */
	private void performRuleActions(Long riverId, RawDrop drop, List<RuleAction> actions) {
		for (RuleAction action: actions) {
			if (action.getAddToBucket() != null && action.getAddToBucket() > 0) {
				drop.getBucketIds().add(action.getAddToBucket());
			}

			// Remove the drop from the river?
			if (action.isRemoveFromRiver()) {
				for (int i = 0; i < drop.getRiverIds().size(); i++) {
					if (drop.getRiverIds().get(i).equals(riverId)) {
						excludedRiverIds.add(riverId);
						break;
					}
				}
			}
			
			// Mark the drop as read for the destination river
			if (action.isMarkAsRead()) {
				drop.getMarkAsRead().add(riverId);
			}
		}
	}

}
