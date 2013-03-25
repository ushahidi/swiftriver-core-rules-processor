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
import java.util.concurrent.ConcurrentMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ushahidi.swiftriver.core.model.RawDrop;
import com.ushahidi.swiftriver.core.model.Rule;

/**
 * Test for {@link RulesExecutor}
 * 
 * @author ekala
 */
@ContextConfiguration(locations="/applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RulesExecutorTest {

	@Autowired
	private RulesRegistry rulesRegistry;
	
	private ObjectMapper mapper = new ObjectMapper();

	private ConcurrentMap<Long, Map<Long, Rule>> rulesMap;
	
	@Before
	public void setUp() {
		rulesMap = rulesRegistry.getRulesMap();
	}

	@Test
	public void testAddToBucketAction() throws Exception {
		RulesExecutor executor = new RulesExecutor();
		String dropJSON = "{\"droplet_title\":\"Kenya tops in Africa in wooing investors\", \"droplet_raw\": \"Kenya is leading the rest of Africa in attracting foreign direct investment, according to a new investor confidence survey that has further raised the profile of the country.\", \"droplet_content\":\"Kenya is leading the rest of Africa in attracting foreign direct investment, according to a new investor confidence survey that has further raised the profile of the country.\", \"droplet_locale\":\"en-us\", \"channel\":\"rss\",\"tags\":[],\"links\":[],\"media\":[],\"places\":[{\"longitude\":38,\"latitude\":1,\"place_name\":\"Kenya\"},{\"longitude\":26,\"latitude\":-30,\"place_name\":\"Africa\"}], \"identity_name\": \"Daily Nation RSS Feeds:Home\", \"identity_username\":\"http://www.nation.co.ke/-/1148/1148/-/xvvu7uz/-/index.html\", \"identity_avatar\":null,\"droplet_date_pub\": \"Mon, 7 May 2012 14:26:59 +0300\",\"droplet_orig_id\": \"313fd103bfd79a726798b034081c627c\", \"droplet_type\":\"original\", \"river_id\":[1]}";

		RawDrop drop = mapper.readValue(dropJSON, RawDrop.class);
		executor.applyRules(drop, rulesMap);

		assertEquals(1, drop.getBucketIds().size());
	}
	
	@Test
	public void testRemoveFromRiver() throws Exception {
		RulesExecutor executor = new RulesExecutor();
		String dropJSON = "{\"droplet_title\":\"@MarmiteJunction only after Justin Beiber solves the economic crisis, and Lady Gaga sorts out the situation in Syria.\", \"droplet_raw\": \"@MarmiteJunction only after Justin Beiber solves the economic crisis, and Lady Gaga sorts out the situation in Syria.\", \"droplet_content\":\"@MarmiteJunction only after Justin Beiber solves the economic crisis, and Lady Gaga sorts out the situation in Syria.\", \"droplet_locale\":\"en-us\", \"channel\":\"twitter\",\"tags\":[],\"links\":[],\"media\":[],\"places\":[{\"longitude\":38,\"latitude\":35,\"place_name\":\"Syria\"}], \"identity_name\": \"Dan Blows\", \"identity_username\":\"blowski\", \"identity_orig_id\": \"1441251\", \"identity_avatar\":\"http://a0.twimg.com/profile_images/1140244376/dan_small_normal.jpg\",\"droplet_date_pub\": \"Mon, 7 May 2012 14:26:59 +0300\",\"droplet_orig_id\": \"228744039225241600\", \"droplet_type\":\"original\", \"river_id\":[1,2,3]}";
		RawDrop drop = mapper.readValue(dropJSON, RawDrop.class);

		int size = drop.getRiverIds().size();

		executor.applyRules(drop, rulesMap);

		assertEquals(size - 1, drop.getRiverIds().size());
	}
	
}
