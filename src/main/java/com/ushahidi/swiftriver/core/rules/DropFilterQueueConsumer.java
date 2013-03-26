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
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.rabbitmq.client.Channel;
import com.ushahidi.swiftriver.core.model.RawDrop;
import com.ushahidi.swiftriver.core.model.Rule;

/**
 * This class is an asynchronous consumer for consuming messages
 * from the DROP_FILTER_QUEUE queue. When a message is received, it is
 * marshalled into a {@link RawDrop} object before being passed to the 
 * {@link RulesExecutor} object for application of the rules
 *  
 * @author ekala
 *
 */
public class DropFilterQueueConsumer implements ChannelAwareMessageListener {
	
	private ObjectMapper mapper = new ObjectMapper();
	
		private RulesRegistry rulesRegistry;

	private RulesExecutor rulesExecutor;

	private AmqpTemplate amqpTemplate;
	
	public DropFilterQueueConsumer() {
		rulesExecutor = new RulesExecutor();
	}

	public void setRulesRegistry(RulesRegistry rulesRegistry) {
		this.rulesRegistry = rulesRegistry;
	}

	public void setRulesExecutor(RulesExecutor rulesExecutor) {
		this.rulesExecutor = rulesExecutor;
	}

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
		
	}

	@Override
	public void onMessage(Message message, Channel channel) throws Exception, JsonGenerationException,
		JsonMappingException, IOException {
		
		// Serialize the message into a Drop POJO
		RawDrop drop = mapper.readValue(new String(message.getBody()), RawDrop.class);

		ConcurrentMap<Long, Map<Long, Rule>> rulesMap = rulesRegistry.getRulesMap();

		// Send the drop for rules processing
		rulesExecutor.applyRules(drop, rulesMap);
		
		// Set the source to "rules"
		drop.setSource("rules");

		// Get he correlation id and callback queue name
		final String correlationId = new String(message.getMessageProperties().getCorrelationId());
		final String callbackQueueName = message.getMessageProperties().getReplyTo();

		// Place drop on the publishing queue
		amqpTemplate.convertAndSend(drop, new MessagePostProcessor() {
			public Message postProcessMessage(Message message) throws AmqpException {
				message.getMessageProperties().setCorrelationId(correlationId.getBytes());
				message.getMessageProperties().setReplyTo(callbackQueueName);

				return message;
			}
			
		});
	}

}
