package com.dl.druid.demo.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.codehaus.jackson.map.ObjectMapper;

public class WriteKafka implements IProcess {
	private Producer<String, byte[]> producer;
	private String topic;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private long count;
	
	public WriteKafka(String topic){
		this.topic = topic;
		
		Properties p = new Properties();
		p.setProperty("bootstrap.servers", "localhost:9092");
		p.setProperty("compression.type", "snappy");
		p.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		p.setProperty("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		this.producer = new KafkaProducer<String, byte[]>(p);
	}
	
	
	@Override
	public void apply(List<HashMap<String, Object>> data) {
		List<Future<RecordMetadata>> futures = new ArrayList<Future<RecordMetadata>>();
        for (HashMap<String, Object> obj : data) {
            byte[] objbytes = null;
            try {
                objbytes = objectMapper.writeValueAsBytes(obj);
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
            Future<RecordMetadata> future = producer.send(new ProducerRecord<String, byte[]>(topic, "demo",objbytes), new Callback() {

                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        System.out.println(exception.getMessage());
                    } else {
                        System.out.println(count++);
                    }

                }

            });
            
            futures.add(future);
        }
        for(Future<RecordMetadata> f : futures){
        	try {
				f.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}

}
