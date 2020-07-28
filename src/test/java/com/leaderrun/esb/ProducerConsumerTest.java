package com.leaderrun.esb;

import java.util.Date;

import org.junit.Test;

import com.leaderrun.esb.event.BusinessEvent;
import com.leaderrun.esb.event.BusinessEventHeader;

public class ProducerConsumerTest {

	@Test
	public void test() throws Exception {
		String nameserver = "127.0.0.1:9876";
//		String nameserver = "192.168.88.21:9876";
		String topicName = "TestTopic";

		Producer producer = new Producer(nameserver, "ASN");
		producer.start();

		Consumer consumer = new Consumer(nameserver, "KN", topicName, "TAG-A");
		consumer.register(bizEvents -> {
			bizEvents.forEach(bizEvent -> {
				System.out.println("Business Event: " + bizEvent.getBody());
			});
			return true;
		});
		consumer.start();

		for (int i = 0; i < 1000; i++) {
			producer.send(topicName, "TAG-A", new BusinessEvent<Date>(
					new BusinessEventHeader("001", "ASN", "asn.submitted", "1.0.0", new Date()), new Date()));
		}
		Thread.sleep(1000L);
		producer.stop();
		consumer.stop();
	}

}
