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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

import com.rabbitmq.client.Channel;
import com.ushahidi.swiftriver.core.model.RawDrop;
import com.ushahidi.swiftriver.core.model.Rule;

/**
 * Tests passing of drops to the {@link RulesExecutor} when a message
 * is received o the DROP_FILTER_QUEUE
 *  
 * @author ekala
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DropFilterQueueConsumerTest {
	
	private DropFilterQueueConsumer dropFilterQueueConsumer;
	
	private RulesExecutor mockRulesExecutor;

	private RulesRegistry rulesRegistry;
	
	private AmqpTemplate mockAmqpTemplate;
	
	private ObjectMapper objectMapper = new ObjectMapper();

	@Captor
	private ArgumentCaptor<ConcurrentMap<Long, Map<Long, Rule>>> rulesMapArgument;
	
	@Before
	public void setUp() {
		mockRulesExecutor = mock(RulesExecutor.class);
		rulesRegistry = new RulesRegistry();
		rulesRegistry.setRulesMap(new ConcurrentHashMap<Long, Map<Long,Rule>>());
		mockAmqpTemplate = mock(AmqpTemplate.class);

		dropFilterQueueConsumer = new DropFilterQueueConsumer();
		dropFilterQueueConsumer.setRulesExecutor(mockRulesExecutor);
		dropFilterQueueConsumer.setRulesRegistry(rulesRegistry);
		dropFilterQueueConsumer.setAmqpTemplate(mockAmqpTemplate);
		dropFilterQueueConsumer.setObjectMapper(objectMapper);
	}
	
	@Test
	public void onMessage() throws Exception {
		Message mockMessage = mock(Message.class);
		Channel mockChannel = mock(Channel.class);
		MessageProperties mockMessageProperties = mock(MessageProperties.class);

		String dropJSON = "{\"source\":\"semantics\",\"identity_orig_id\": \"http://feeds.bbci.co.uk/news/rss.xml\", \"droplet_raw\": \"The danger of growing resistance to antibiotics should be treated as seriously as the threat of terrorism, England's chief medical officer says.\", \"droplet_orig_id\": \"c558d88a44fc70da36d04746574e05e4\", \"droplet_locale\": \"en-gb\", \"identity_username\": \"http://www.bbc.co.uk/news/#sa-ns_mchannel=rss&ns_source=PublicRSS20-sa\", \"droplet_date_pub\": \"Mon, 11 Mar 2013 07:32:59 +0000\", \"droplet_type\": \"original\", \"identity_avatar\": \"http://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif\", \"droplet_title\": \"Antibiotic resistance 'threat to UK'\", \"links\": [{\"url\": \"http://www.bbc.co.uk/news/health-21737844#sa-ns_mchannel=rss&ns_source=PublicRSS20-sa\", \"original_url\": true}], \"droplet_content\": \"The danger of growing resistance to antibiotics should be treated as seriously as the threat of terrorism, England's chief medical officer says.\", \"identity_name\": \"BBC News - Home\", \"channel\": \"rss\", \"river_id\": [2], \"bucket_id\": []}";

		when(mockMessage.getBody()).thenReturn(dropJSON.getBytes());
		when(mockMessage.getMessageProperties()).thenReturn(mockMessageProperties);
		when(mockMessageProperties.getReplyTo()).thenReturn("reply-to-queue");
		when(mockMessageProperties.getCorrelationId()).thenReturn("drop-correlation-id".getBytes());
		
		// Send the drop to the rules executor
		dropFilterQueueConsumer.onMessage(mockMessage, mockChannel);
		
		ArgumentCaptor<RawDrop> dropArgument = ArgumentCaptor.forClass(RawDrop.class);
		ArgumentCaptor<String> routingKeyArgument = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<MessagePostProcessor> postProcessorArgument = ArgumentCaptor
				.forClass(MessagePostProcessor.class);

		verify(mockRulesExecutor).applyRules(dropArgument.capture(), rulesMapArgument.capture());
		verify(mockAmqpTemplate).convertAndSend(routingKeyArgument.capture(), 
				dropArgument.capture(), postProcessorArgument.capture());

		String routingKey = routingKeyArgument.getValue();
		assertEquals("reply-to-queue", routingKey);
		RawDrop drop = dropArgument.getValue();		
		assertEquals("rules", drop.getSource());
		
	}
}
