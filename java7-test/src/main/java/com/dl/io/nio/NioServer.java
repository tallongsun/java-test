package com.dl.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
	private static int DEFAULT_PORT = 12345;
	private static ServerHandler serverHandle;

	public static void start() throws IOException {
		start(DEFAULT_PORT);
	}

	public synchronized static void start(int port) throws IOException {
		if (serverHandle != null)
			serverHandle.stop();
		serverHandle = new ServerHandler(port);
		new Thread(serverHandle, "Server").start();
	}

	public static class ServerHandler implements Runnable {
		private Selector selector;
		private ServerSocketChannel serverChannel;
		private volatile boolean started;

		public ServerHandler(int port) {
			try {
				// 创建选择器
				selector = Selector.open();
				// 打开监听通道
				serverChannel = ServerSocketChannel.open();
				serverChannel.configureBlocking(false);// 开启非阻塞模式
				serverChannel.socket().bind(new InetSocketAddress(port), 1024);
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				started = true;
				System.out.println("服务器已启动，端口号：" + port);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		public void stop() {
			started = false;
		}

		@Override
		public void run() {
			while (started) {
				try {
					// 无论是否有读写事件发生，selector每隔1s被唤醒一次
					selector.select(1000);
					// 阻塞,只有当至少一个注册的事件发生的时候才会继续.
					// selector.select();
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> it = keys.iterator();
					SelectionKey key = null;
					while (it.hasNext()) {
						key = it.next();
						it.remove();
						try {
							 handleInput(key);
						} catch (Exception e) {
							if (key != null) {
								key.cancel();
								if (key.channel() != null) {
									key.channel().close();
								}
							}
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			// selector关闭后会自动释放里面管理的资源
			if (selector != null)
				try {
					selector.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		private void handleInput(SelectionKey key) throws IOException{  
	        if(key.isValid()){  
	            if(key.isAcceptable()){  
	                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();  
	                SocketChannel sc = ssc.accept();  
	                sc.configureBlocking(false);  
	                sc.register(selector, SelectionKey.OP_READ);  
	            }  
	            if(key.isReadable()){  
	                SocketChannel sc = (SocketChannel) key.channel();  
	                ByteBuffer buffer = ByteBuffer.allocate(1024);  
	                int readBytes = sc.read(buffer);  
	                if(readBytes>0){  
	                    buffer.flip();  
	                    byte[] bytes = new byte[buffer.remaining()];  
	                    buffer.get(bytes);  
	                    String expression = new String(bytes,"UTF-8");  
	                    System.out.println("服务器收到消息：" + expression);  
	                    String result = null;  
	                    try{  
	                        result = expression;  
	                    }catch(Exception e){  
	                        result = "计算错误：" + e.getMessage();  
	                    }  
	                    doWrite(sc,result);  
	                }else if(readBytes<0){  //链路已经关闭，释放资源  
	                    key.cancel();  
	                    sc.close();  
	                }  
	            }  
	        }  
	    }  
	    //异步发送应答消息  
	    private void doWrite(SocketChannel channel,String response) throws IOException{  
	        byte[] bytes = response.getBytes();  
	        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);  
	        writeBuffer.put(bytes);  
	        writeBuffer.flip();  
	        channel.write(writeBuffer);  
	    }  

	}
}
