package com.dl.springcloud.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dl.springcloud.provider.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class RibbonService {
	@Autowired
	private RestTemplate restTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(RibbonService.class);

	@HystrixCommand(fallbackMethod = "fallback")
	public User findById(Long id) {
		return this.restTemplate.getForObject("http://microservice-provider-user/" + id, User.class);
	}

	public User fallback(Long id) {
		LOGGER.info("异常发生,进入fallback方法,接收的参数:id = {}", id);
		User user = new User();
		user.setId(-1L);
		user.setUsername("default username");
		user.setAge(0);
		return user;
	}
}
