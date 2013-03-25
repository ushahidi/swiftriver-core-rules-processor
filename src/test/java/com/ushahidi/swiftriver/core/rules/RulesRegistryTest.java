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

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ushahidi.swiftriver.core.model.Rule;
import com.ushahidi.swiftriver.core.rules.dao.RuleDao;

@ContextConfiguration(locations = "/applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RulesRegistryTest {
	
	@Autowired
	private RuleDao ruleDao;
	
	private RulesRegistry rulesRegistry;
	
	private ConcurrentMap<Long, Map<Long, Rule>> rulesMap;
	
	@Before
	public void setUp() {
		rulesMap = new ConcurrentHashMap<Long, Map<Long,Rule>>();

		rulesRegistry = new RulesRegistry();
		rulesRegistry.setRulesMap(rulesMap);
		rulesRegistry.setRuleDao(ruleDao);
	}
	
	@Test
	public void testInit() throws Exception {
		rulesRegistry.init();
		assertEquals(2, rulesRegistry.getRulesMap().size());
	}
}
