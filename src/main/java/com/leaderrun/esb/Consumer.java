package com.leaderrun.esb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.leaderrun.esb.event.BusinessEvent;
import com.leaderrun.esb.utils.JSONUtils;

public class Consumer {

	final static Logger log = LoggerFactory.getLogger(Consumer.class);

	final String nameserver;
	final String groupName;
	final String topic;
	final String tag;

	// runtime consumer
	DefaultMQPushConsumer consumer;
	List<BusinessEventListener> listeners = new ArrayList<>();

	public Consumer(String nameserver, String groupName, String topic, String tag) {
		this.nameserver = nameserver;
		this.groupName = groupName;
		this.topic = topic;
		this.tag = tag;
	}

	public synchronized void start() throws Exception {
		consumer = new DefaultMQPushConsumer(groupName);
		consumer.setNamesrvAddr(nameserver);
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.setMessageModel(MessageModel.BROADCASTING);
		consumer.subscribe(topic, tag);

		listeners.forEach(lsn -> {
			consumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
						ConsumeConcurrentlyContext context) {
					try {
						List<BusinessEvent<JsonNode>> jsevent = msgs.stream() //
								.map(msg -> JSONUtils.toObject(msg.getBody(),
										new TypeReference<BusinessEvent<JsonNode>>() {
										})) //
								.collect(Collectors.toList());

						boolean processOk = lsn.onMessage(jsevent);
						return processOk ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS
								: ConsumeConcurrentlyStatus.RECONSUME_LATER;
					} catch (Exception e) {
						log.error("Error Consuming Message", e);
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
				}
			});
		});
		consumer.start();
		log.debug("Broadcast Consumer (for Topic {}) Started", topic);
	}

	public <T> void register(BusinessEventListener listener) {
		listeners.add(listener);
	}

	public synchronized void stop() {
		if (consumer != null) {
			consumer.shutdown();
		}
		consumer = null;
	}

	@Override
	protected void finalize() throws Throwable {
		this.stop();
		super.finalize();
	}

}
