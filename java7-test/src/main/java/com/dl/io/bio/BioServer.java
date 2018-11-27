package com.dl.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {
	private static int DEFAULT_PORT = 12345;
	private static ServerSocket server;
	private static ExecutorService executorService = Executors.newFixedThreadPool(60);  
	
	public static void start() throws IOException {
		start(DEFAULT_PORT);
	}

	public synchronized static void start(int port) throws IOException {
		if (server != null)
			return;
		try {
			server = new ServerSocket(port);
			System.out.println("服务器已启动，端口号：" + port);
			// 通过无线循环监听客户端连接
			// 如果没有客户端接入，将阻塞在accept操作上。
			while (true) {
				Socket socket = server.accept();
				// 当有新的客户端接入时，会执行下面的代码
				// 然后创建一个新的线程处理这条Socket链路
//				new Thread(new ServerHandler(socket)).start();
				executorService.execute(new ServerHandler(socket));  
			}
		} finally {
			if (server != null) {
				System.out.println("服务器已关闭。");
				server.close();
				server = null;
			}
		}
	}

	public static class ServerHandler implements Runnable {
		private Socket socket;

		public ServerHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			BufferedReader in = null;
			PrintWriter out = null;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				String expression;
				String result;
				while (true) {
					// 通过BufferedReader读取一行
					// 如果已经读到输入流尾部，返回null,退出循环
					// 如果得到非空值，就尝试计算结果并返回
					if ((expression = in.readLine()) == null)
						break;
					System.out.println("服务器收到消息：" + expression);
					try {
						result = expression;
					} catch (Exception e) {
						result = "计算错误：" + e.getMessage();
					}
					out.println(result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 一些必要的清理工作
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					in = null;
				}
				if (out != null) {
					out.close();
					out = null;
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					socket = null;
				}
			}
		}

	}
}
