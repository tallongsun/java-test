package com.dl.kafka.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka.producer")
public class KafkaProducerConfigurationProperties {
    private String servers;
    private String compressionType;
    private int bufferMemory;
    private int batchSize;
    private int lingerMs;
    private String topic;
	public String getServers() {
		return servers;
	}
	public void setServers(String servers) {
		this.servers = servers;
	}
	public String getCompressionType() {
		return compressionType;
	}
	public void setCompressionType(String compressionType) {
		this.compressionType = compressionType;
	}
	public int getBufferMemory() {
		return bufferMemory;
	}
	public void setBufferMemory(int bufferMemory) {
		this.bufferMemory = bufferMemory;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public int getLingerMs() {
		return lingerMs;
	}
	public void setLingerMs(int lingerMs) {
		this.lingerMs = lingerMs;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
    
    
}
