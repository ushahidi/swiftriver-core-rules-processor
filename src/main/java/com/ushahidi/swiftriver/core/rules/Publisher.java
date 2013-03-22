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

import java.util.concurrent.BlockingQueue;

import org.springframework.amqp.core.AmqpTemplate;

public class Publisher extends Thread {
	
	private AmqpTemplate amqpTemplate;
	
	private BlockingQueue<Object> publishQueue;

	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public BlockingQueue<Object> getPublishQueue() {
		return publishQueue;
	}

	public void setPublishQueue(BlockingQueue<Object> publishQueue) {
		this.publishQueue = publishQueue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			while(true) {
				publishDrop(publishQueue.take());
			}
		} catch (InterruptedException e) {
			
		}
	}

	public void publishDrop(Object drop) {
		amqpTemplate.convertAndSend(drop);
	}
	
	
}
