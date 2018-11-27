package com.dl.io.nio;

import java.util.Scanner;

public class Test {
	/**
	 * 未考虑半包粘包处理
	 * 
	 * 由于非阻塞，客户端和服务器都只需要一个reactor线程，饭好了就告诉你，过来取饭吧。
	 * 
	 * 避免了bio网络较差时读不到数据一致阻塞等待的问题，nio读不到会直接返回0.
	 * 但如何解决nio读某个客户端大量数据时阻塞问题呢？答案是，一个boss线程，N个worker线程
	 * 
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception{  
        //运行服务器  
        NioServer.start();  
        //避免客户端先于服务器启动前执行代码  
        Thread.sleep(100);  
        //运行客户端   
        NioClient.start();  
        while(NioClient.sendMsg(new Scanner(System.in).nextLine()));  
    }  
}
