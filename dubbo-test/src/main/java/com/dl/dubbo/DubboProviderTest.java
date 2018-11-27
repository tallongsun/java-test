package com.dl.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.dl.dubbo.service.XxxService;

/**
 * 
 * Dubbo配置方式提供服务
 *   启动SimpleMonitor后(注册中心也配置为zookeeper)，可以通过http://127.0.0.1:8080/查看各服务状态
 *   启动admin后，可以在web后台进行服务治理
 */
@EnableAutoConfiguration
@ComponentScan
@ImportResource({"classpath:dubbo-service.xml"})
public class DubboProviderTest {

	public static void main(String[] args) {
		
		ApplicationContext ctx = SpringApplication.run(DubboProviderTest.class, args);
		XxxService xxxService = ctx.getBean(XxxService.class);
		System.out.println(xxxService.xxx());

	}

}
