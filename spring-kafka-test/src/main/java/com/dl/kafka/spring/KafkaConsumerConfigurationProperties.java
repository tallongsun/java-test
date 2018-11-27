package com.dl.kafka.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaConsumerConfigurationProperties {
    private String servers;
    private String groupId;
    private Integer concurrent;
	public String getServers() {
		return servers;
	}
	public void setServers(String servers) {
		this.servers = servers;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Integer getConcurrent() {
		return concurrent;
	}
	public void setConcurrent(Integer concurrent) {
		this.concurrent = concurrent;
	}
    
    
}
