package com.dl.io.aio;

import java.util.Scanner;

public class Test {
	/**
	 * 由于异步非阻塞，客户端和服务器都只需要一个proactor线程，饭好了就送过来吧。
	 * 且不需要selector轮询，性能理论上更好，osx系统原生应该不支持aio
	 * 且编程模型比nio更简单
	 * 
	 * 
	 * 有没有异步阻塞呢？用户编程是可以实现的，发起一个异步操作，然后却阻塞等待其返回，可能由于是一个非常重要的业务操作，不拿到返回根本无法进行后续处理
	 * 
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception{  
        //运行服务器  
        AioServer.start();  
        //避免客户端先于服务器启动前执行代码  
        Thread.sleep(100);  
        //运行客户端   
        AioClient.start();  
        System.out.println("请输入请求消息：");  
        Scanner scanner = new Scanner(System.in);  
        while(AioClient.sendMsg(scanner.nextLine()));  
    }  
}
