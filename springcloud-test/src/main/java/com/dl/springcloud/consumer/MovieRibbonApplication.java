package com.dl.springcloud.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@ComponentScan
@EnableDiscoveryClient
@EnableCircuitBreaker
//turbine监控可以提供两个consumer
public class MovieRibbonApplication {

	  @Bean
	  @LoadBalanced
	  public RestTemplate restTemplate() {
	    return new RestTemplate();
	  }
	  public static void main(String[] args) {
	    SpringApplication.run(MovieRibbonApplication.class, args);
	}

}
