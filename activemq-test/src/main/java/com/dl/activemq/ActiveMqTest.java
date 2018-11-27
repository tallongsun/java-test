package com.dl.activemq;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMqTest {

	public static void main(String[] args) throws Exception {
		
		TopicConnectionFactory connFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

		try {
			conn = connFactory.createTopicConnection();
			session = conn.createTopicSession(true, Session.AUTO_ACKNOWLEDGE);
			conn.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}

		try {
			receiverMessage("myTopic");
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		try {
			senderMessage("myTopic", new ActiveMqMessage("测试任务","TestTask"));
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	private static TopicConnection conn = null;
	private static TopicSession session = null;
	
	public static void receiverMessage(String topicName) throws JMSException {
		Topic topic = session.createTopic("myTopic");
		TopicSubscriber subscriber = session.createSubscriber(topic, null, true);
		subscriber.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if (message != null) {
					MapMessage map = (MapMessage) message;
					try {
						String taskName = map.getString("taskName");
						String serviceName = map.getString("serviceName"); 
						System.out.println(taskName + "," + serviceName);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}

			}
		});
	}
	
	public static void senderMessage(String topicName, ActiveMqMessage message) throws JMSException {
		// 发送的时候要新建一个链接，用完就关闭
		TopicConnection connection = null;
		TopicSession session = null;
		try {
			TopicConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
			connection = factory.createTopicConnection();
			connection.start();

			session = connection.createTopicSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(topicName);
			TopicPublisher publisher = session.createPublisher(topic);
			publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
	        MapMessage map = session.createMapMessage();
	        map.setString("taskName", message.getTaskName());
	        map.setString("serviceName", message.getServiceName());
	        publisher.send(map);

			session.commit();
		} catch (JMSException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}
}
