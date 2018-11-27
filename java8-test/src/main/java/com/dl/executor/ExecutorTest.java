package com.dl.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorTest {
	public static void main(String[] args) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
		scheduler.scheduleAtFixedRate(new Runnable() {
			int count;
			@Override
			public void run() {
				count++;
				if(count==5){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//会补偿miss的调度
				System.out.println(System.currentTimeMillis());
			}
		}, 1, 1,TimeUnit.SECONDS);
		
//		scheduler.scheduleWithFixedDelay(new Runnable() {
//			int count;
//			@Override
//			public void run() {
//				count++;
//				if(count==5){
//					try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				//不会补偿miss的调度
//				System.out.println(System.currentTimeMillis());
//			}
//		}, 1, 1,TimeUnit.SECONDS);
	}
}
