package com.leaderrun.esb;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leaderrun.esb.event.BusinessEvent;
import com.leaderrun.esb.utils.JSONUtils;

public class Producer {

	final static Logger log = LoggerFactory.getLogger(Producer.class);

	final String nameserver;
	final String groupName;

	// runtime producer
	DefaultMQProducer producer;

	public Producer(String nameserver, String groupName) {
		this.nameserver = nameserver;
		this.groupName = groupName;
	}

	public synchronized void start() throws Exception {
		producer = new DefaultMQProducer(groupName);
		producer.setNamesrvAddr(nameserver);
		producer.start();
	}

	public void createTopic(String topic) {
		String key = producer.getCreateTopicKey();
		try {
			producer.createTopic(key, topic, 1);
		} catch (MQClientException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> boolean send(String topic, String tag, BusinessEvent<T> event) {
		try {
			byte[] data = JSONUtils.toJsonBytes(event);
			Message msg = new Message(topic, tag, data);
			SendResult sendResult = producer.send(msg);
			SendStatus status = sendResult.getSendStatus();
			if (log.isTraceEnabled()) {
				log.trace("Producer SendResult: {}", sendResult);
			}
			if (status != SendStatus.SEND_OK) {
				log.error("Producer Send Error: {}", sendResult);
			}
			return (status == SendStatus.SEND_OK);
		} catch (Exception e) {
			log.error("Producer Send Error", e);
			throw new RuntimeException(e);
		}
	}

	public synchronized void stop() {
		if (producer != null) {
			producer.shutdown();
		}
		producer = null;
	}

	@Override
	protected void finalize() throws Throwable {
		this.stop();
		super.finalize();
	}
}
