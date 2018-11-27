package com.dl.kafka;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class KafkaTest {

	/**
	 * [kafka]
	 * 1. kafka-server-start -daemon /usr/local/etc/kafka/server.properties
	 * 2. kafka-topics --zookeeper localhost:2181 --list 
	 * 3. kafka-topics --zookeeper localhost:2181 --create --partitions 1 --replication-factor 1 --topic myTopic
	 * 
	 * kafka-topics --zookeeper localhost:2181 --describe --topic test
	 * kafka-topics --zookeeper locahost:2181 --delete --topic test
	 * kafka-console-producer --broker-list localhost:9092 --topic test     
	 * kafka-console-consumer --zookeeper localhost:2181 --from-beginning --topic test
	 * kafka-server-stop
	 * 
	 * [elasticsearch]
	 * 1. bin/plugin install mobz/elasticsearch-head
	 * 2. elasticsearch -Des.insecure.allow.root=true -d
	 * 3. 用head插件创建my_index索引
	 * 4.
curl -XPUT 'http://127.0.0.1:9200/my_index_3/default/_mappings' -d '{
            "default": {
                "_all": {
                    "analyzer": "standard",
                    "search_analyzer": "standard",
                    "term_vector": "no",
                    "store": "false"
                },
                "properties": {
                    "timestamp": {
                        "type": "date",
                        "format": "yyy-MM-dd HH:mm:ss||epoch_millis"
                    },
                    "usercode": {
                        "type": "string",
                        "index": "not_analyzed"
                    },
                    "param": {
                        "type": "string",
                        "store": "no",
                        "term_vector": "with_positions_offsets",
                        "analyzer": "standard",
                        "search_analyzer": "standard",
                        "include_in_all": "true",
                        "boost": 8
                    }
                }
            }
}'
	 * 
	 * [logstash]
	 * 1. bin/logstash -f mytopic.conf
input {
    kafka {
        zk_connect => "127.0.0.1:2181"
        group_id => "logstash"
        topic_id => "myTopic"
        codec => json
        reset_beginning => false
        consumer_threads => 5
        decorate_events => true
    }
}
output {
  elasticsearch {
      hosts => ["127.0.0.1:9200"]
      index => "my_index"
      document_type => "default"
  }
}
	 */
	public static void main(String[] args) {
		writeMsgToKafka();
//		readMsgFromKafka();
	}


	static void writeMsgToKafka() {
		//Kafka
		Properties p = new Properties();
		p.setProperty("bootstrap.servers", "172.20.3.122:9092");
		p.setProperty("compression.type", "snappy");
		p.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		p.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		Producer<String, String> producer = new KafkaProducer<String, String>(p);

		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(Inclusion.NON_NULL);

		String value = null;
		try {
			String param = jsonMapper.writeValueAsString(new KafkaMqMessage.KeyValueParam("key1", "val1"));
			KafkaMqMessage kafkaMqMessage = new KafkaMqMessage(Calendar.getInstance().getTimeInMillis(), "5",param);
			kafkaMqMessage.setValue(10);
			value = jsonMapper.writeValueAsString(kafkaMqMessage);
			System.out.println(value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ProducerRecord<String, String> record = new ProducerRecord<String, String>("testgroup", "1",value);
		producer.send(record);

		producer.close();
	}
	
	static void readMsgFromKafka(){
		subscribeSpecificPartitionsConsumer();
	}

	
	static void autoCommitOffsetConsumer(){
		 Properties props = new Properties();
		 props.put("bootstrap.servers", "localhost:9092");
		 props.put("group.id", "DemoConsumer");
		 props.put("enable.auto.commit", "true");
		 props.put("auto.commit.interval.ms", "1000");
		 props.put("session.timeout.ms", "30000");
		 props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		 props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		 
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(ImmutableList.of("testgroup"));
        
        try{
	        while (true) {
	            ConsumerRecords<String, String> records = consumer.poll(100);
	            for (ConsumerRecord<String, String> record : records)
	                System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
	        }
        }finally{
        	consumer.close();
        }
	}
	
	//同一group的两个consumer，如果producer生产了一条消息，两个consumer都会收到，why？
	//offset还是会提交到group，但是kafka不会帮我们处理同一group的consumer只有一个消费消息，所以这种情况不能起多个consumer吧？
	static void subscribeSpecificPartitionsConsumer(){
		 Properties props = new Properties();
		 props.put("bootstrap.servers", "localhost:9092");
		 props.put("enable.auto.commit", "false");//
		 props.put("auto.commit.interval.ms", "1000");
		 props.put("session.timeout.ms", "30000");
		 props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		 props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		 props.setProperty("max.poll.records", "1000");//
		 props.put("group.id", "DemoConsumer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

		TopicPartition topicPartition = new TopicPartition("testgroup", 0);
		consumer.assign(ImmutableList.of(topicPartition));
		consumer.seekToBeginning(topicPartition);


		try{
			while(true){
				ConsumerRecords<String, String> records = consumer.poll(1000);
				records.forEach(r -> System.out
						.println(r.topic() + "," + r.partition() + "," + r.offset() + "," + r.key() + "," + r.value()));
		
				if(records.count()>0){
				    List<ConsumerRecord<String, String>> partRecords = records.records(topicPartition);
			        long lastOffset = partRecords.get(partRecords.size()-1).offset();
			        consumer.commitSync(ImmutableMap.of(topicPartition,new OffsetAndMetadata(lastOffset+1)));
				}
			}
		}finally{
			consumer.close();
		}

	}
    
	@SuppressWarnings("unused")
	static void manualControlOffsetConsumer(){
		 Properties props = new Properties();
		 props.put("bootstrap.servers", "localhost:9092");
		 props.put("group.id", "DemoConsumer");
		 props.put("enable.auto.commit", "false");//
		 props.put("auto.commit.interval.ms", "1000");
		 props.put("session.timeout.ms", "30000");
		 props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		 props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		 
		 KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		 consumer.subscribe(ImmutableList.of("testgroup"));
       
	     final int minBatchSize = 2;
	     List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
	     
	     try{
		   while (true) {
			   if(false){
			       ConsumerRecords<String, String> records = consumer.poll(100);
			       for (ConsumerRecord<String, String> record : records)
			    	   buffer.add(record);
			       
			       if(buffer.size()>=minBatchSize){
			    	   insertIntoDb(buffer);
			    	   consumer.commitSync();
			    	   buffer.clear();
			       }
			   }else{
				   ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				   for(TopicPartition partition : records.partitions()){
					   List<ConsumerRecord<String, String>> partRecords = records.records(partition);
					   insertIntoDb(partRecords);
				       
				       long lastOffset = partRecords.get(partRecords.size()-1).offset();
				       consumer.commitSync(ImmutableMap.of(partition,new OffsetAndMetadata(lastOffset+1)));
				   }
			   }
		           
		   }
	     }finally{
	    	 consumer.close();
	     }
	}
	
	static void insertIntoDb(List<ConsumerRecord<String, String>>  buffer){
		for(ConsumerRecord<String, String> record : buffer){
			System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
		}
	}

}
