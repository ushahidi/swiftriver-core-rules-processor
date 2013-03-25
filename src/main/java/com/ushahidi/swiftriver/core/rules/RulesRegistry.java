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

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ushahidi.swiftriver.core.model.Rule;
import com.ushahidi.swiftriver.core.model.Rule.RuleAction;
import com.ushahidi.swiftriver.core.model.Rule.RuleCondition;
import com.ushahidi.swiftriver.core.rules.dao.RuleDao;

public class RulesRegistry {

	private RuleDao ruleDao;
	
	private ConcurrentMap<Long, Map<Long, Rule>> rulesMap;

	private ObjectMapper mapper = new ObjectMapper();
	
	static final Logger LOG = LoggerFactory.getLogger(RulesRegistry.class); 

	public RuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(RuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}
	
	public ConcurrentMap<Long, Map<Long, Rule>> getRulesMap() {
		return rulesMap;
	}

	public void setRulesMap(ConcurrentMap<Long, Map<Long, Rule>> rulesMap) {
		this.rulesMap = rulesMap;
	}

	public void init() throws SQLException, JsonGenerationException, JsonMappingException, IOException {
		// Initialize the rules map
		rulesMap = new ConcurrentHashMap<Long, Map<Long,Rule>>();

		// Get the rules from the DB and convert to DTO as they
		// are added to the dropRulesMap
		for (Map<String, Object> entry: ruleDao.findAll()) {

			List<RuleCondition> conditions = mapper.readValue(((String) entry.get("conditions")), 
					new TypeReference<List<RuleCondition>>() {});

			List<RuleAction> actions = mapper.readValue(((String) entry.get("actions")), 
					new TypeReference<List<RuleAction>>() {});

			Rule rule = new Rule();
			rule.setId(((BigInteger) entry.get("id")).longValue());
			rule.setRiverId(((BigInteger) entry.get("river_id")).longValue());
			rule.setConditions(conditions);
			rule.setActions(actions);
			rule.setMatchAllConditions((Boolean) entry.get("all_conditions"));

			// Get the rules for the river with the specified id
			Map<Long, Rule> riverRules = getRulesMap().get(rule.getRiverId());

			if (riverRules == null) {
				riverRules = new HashMap<Long, Rule>();
			}

			riverRules.put(rule.getId(), rule);

			// Update the map
			getRulesMap().put(rule.getRiverId(), riverRules);
		}
		
	}
	
	/**
	 * Deletes a rule from the rules map
	 * 
	 * @param rule
	 */
	public synchronized void deleteRule(Rule rule) {
		Map<Long, Rule> riverRules = rulesMap.get(rule.getRiverId());
		if (riverRules == null)
			return;
		
		if (!riverRules.containsKey(rule.getId()))
			return;
		
		// Delete the rule from the list
		riverRules.remove(rule.getId());

		// Put back the modified rules list
		rulesMap.put(rule.getRiverId(), riverRules);
		LOG.debug(String.format("Rule %d deleted from the registry" , rule.getId()));
	}
	
	/**
	 * Adds a rule to the rules map
	 * 
	 * @param rule
	 */
	public synchronized void addRule(Rule rule) {
		Map<Long, Rule> riverRules = rulesMap.get(rule.getRiverId());
		
		// Check if any rules exist for the river associated with the rule
		if (riverRules == null) {
			riverRules = new HashMap<Long, Rule>();
		}
		
		riverRules.put(rule.getId(), rule);
		rulesMap.put(rule.getRiverId(), riverRules);
		
		LOG.debug(String.format("Rule %d added to the rules registry", rule.getId()));
	}
	
	/**
	 * Modifies a rule in the rules map
	 * 
	 * @param rule
	 */
	public synchronized void updateRule(Rule rule) {
		Map<Long, Rule> riverRules = rulesMap.get(rule.getRiverId());
		if (riverRules == null) {
			LOG.error(String.format("Could not update rule %d. River %d does not exist in the rules registry",
					rule.getId(), rule.getRiverId()));
			return;
		}
		
		riverRules.put(rule.getId(), rule);
		rulesMap.put(rule.getRiverId(), riverRules);

		LOG.debug(String.format("Updated rule %d in the registry", rule.getId()));
	}

	
}
