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
package com.ushahidi.swiftriver.core.rules.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.rules.dao.RuleDao;

/**
 * {@link RuleDao} implementation class
 * 
 * @author ekala
 */
@Repository
public class JdbcRuleDao implements RuleDao {

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.rules.dao.RuleDao#findAll()
	 */
	public List<Map<String,Object>> findAll() {
		String sql = "SELECT `river_rules`.`id`, `river_id`, `rule_name` AS `name`, " +
				"`rule_conditions` AS `conditions`, `rule_actions` AS `actions`" +
				"FROM `river_rules` " +
				"INNER JOIN `rivers` ON (`river_rules`.`river_id` = `rivers`.`id`) " +
				"WHERE `rivers`.`river_full` = 0 " +
				"AND `rivers`.`river_expired` = 0";

		return jdbcTemplate.queryForList(sql);
	}


}
