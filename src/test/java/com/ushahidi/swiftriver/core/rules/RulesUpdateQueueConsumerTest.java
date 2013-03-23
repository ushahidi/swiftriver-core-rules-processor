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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.rabbitmq.client.Channel;
import com.ushahidi.swiftriver.core.model.Rule;

/**
 * Tests update to the rules when a message that matches
 * the web.river.rules.* pattern is received on the MQ
 * 
 * @author ekala
 *
 */
public class RulesUpdateQueueConsumerTest {
	
	private ConcurrentMap<Long, List<Object>> dropRulesMap;
	
	private RulesUpdateQueueConsumer rulesUpdateQueueConsumer;
	
	private RulesRegistry rulesRegistry;
	
	@Before
	public void setUp() {
		dropRulesMap = new ConcurrentHashMap<Long, List<Object>>();
		rulesRegistry = mock(RulesRegistry.class);

		rulesUpdateQueueConsumer = new RulesUpdateQueueConsumer();
		rulesUpdateQueueConsumer.setDropRulesMap(dropRulesMap);
		rulesUpdateQueueConsumer.setRulesRegistry(rulesRegistry);
	}
	
	@Test
	public void onAddRuleMessage() throws Exception {
		Message mockMessage = mock(Message.class);
		Channel mockChannel = mock(Channel.class);

		MessageProperties mockMessageProperties = mock(MessageProperties.class);
		
		String messageBody = "{\"id\": 1, \"river_id\": 20, \"name\": \"Keyword Filter\", \"conditions\": [{\"field\": \"title\", \"operator\": \"contains\", \"value\": \"kenya\"}], \"actions\": [{\"addToBucket\": 2}], \"all_conditions\": false}";
		
		when(mockMessage.getBody()).thenReturn(messageBody.getBytes());
		when(mockMessage.getMessageProperties()).thenReturn(mockMessageProperties);
		when(mockMessageProperties.getReceivedRoutingKey()).thenReturn("web.river.rules.add");
		
		rulesUpdateQueueConsumer.onMessage(mockMessage, mockChannel);
		
		ArgumentCaptor<Rule> ruleArgument = ArgumentCaptor.forClass(Rule.class);
		verify(rulesRegistry).addRule(ruleArgument.capture());

		Rule rule = ruleArgument.getValue();
		
		assertEquals(1L, rule.getId());
		assertEquals("Keyword Filter", rule.getName());
		assertEquals(20L, rule.getRiverId());

	}
	
	@Test
	public void onUpdateRuleMessage() throws Exception {
		Message mockMessage = mock(Message.class);
		Channel mockChannel = mock(Channel.class);
		MessageProperties mockMessageProperties = mock(MessageProperties.class);
		
		String messageBody = "{\"id\": 1, \"river_id\": 20, \"name\": \"Keyword Filter\", \"conditions\": [{\"field\": \"title\", \"operator\": \"contains\", \"value\": \"kenya\"}], \"actions\": [{\"addToBucket\": 2}], \"all_conditions\": true}";

		when(mockMessage.getBody()).thenReturn(messageBody.getBytes());
		when(mockMessage.getMessageProperties()).thenReturn(mockMessageProperties);
		when(mockMessageProperties.getReceivedRoutingKey()).thenReturn("web.river.rules.update");
		
		rulesUpdateQueueConsumer.onMessage(mockMessage, mockChannel);
		
		ArgumentCaptor<Rule> ruleArgument = ArgumentCaptor.forClass(Rule.class);

		verify(rulesRegistry).updateRule(ruleArgument.capture());
	}
	
	@Test
	public void onDeleteRuleMessage() throws Exception {
		Message mockMessage = mock(Message.class);
		Channel mockChannel = mock(Channel.class);
		MessageProperties mockMessageProperties = mock(MessageProperties.class);
		
		String messageBody = "{\"id\": 1, \"river_id\": 20, \"name\": \"Keyword Filter\", \"conditions\": [{\"field\": \"title\", \"operator\": \"contains\", \"value\": \"kenya\"}], \"actions\": [{\"addToBucket\": 2}], \"all_conditions\": false}";
		when(mockMessage.getBody()).thenReturn(messageBody.getBytes());
		when(mockMessage.getMessageProperties()).thenReturn(mockMessageProperties);
		when(mockMessageProperties.getReceivedRoutingKey()).thenReturn("web.river.rules.delete");
		
		rulesUpdateQueueConsumer.onMessage(mockMessage, mockChannel);
		ArgumentCaptor<Rule> ruleArgument = ArgumentCaptor.forClass(Rule.class);
		verify(rulesRegistry).deleteRule(ruleArgument.capture());
	}
	
}

	
