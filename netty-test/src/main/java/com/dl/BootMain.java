package com.dl;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dl.http.HttpServer;

/**
 * 
 * webbench 3000并发 1min 阿里云4C32G （再增加并发性能也没啥提升了，可能得在多台机器上起多个webbench）
 * server 阿里云4C32G -Xmx4096m -Xms4096m -Xmn2g 
 *   netty：38088qps(dynamic api)                  
 *   undertow: 14290qps(dynamic api)
 *   tomcat：17262qps(dynamic api)
 *   tomcat apr:25606qps(dynamic api)
 *   nginx: 50399qps(static file)                  
 *   node6: 3000+qps?(static file)
 *   express: 2000+qps?(dynamic api)
 *   go: 40493qps(dynamic api) 30000(static file)  
 *   
 *  还是之前的阿里机器，2018-02-02晚上测试，test3做client，test2做server，性能下降了？
 *  netty 18699 go 29930 nginx 40102 openresty 41077  
 *  
 *  test3对test2redis-benchmark get/set均12W+
 *  macpro本机redis-benchmark get/set均6W+
 *  
 *  redis get qps测试：
 *  openresty 连接池5：18125 连接池100：26726
 *  go        连接池5：14825 连接池100：19113
 *  
 * @author tallong
 *
 */

@SpringBootApplication
public class BootMain {
	public static void main(String[] args) throws Exception {
		
		HttpServer.run(null);
	}
}
