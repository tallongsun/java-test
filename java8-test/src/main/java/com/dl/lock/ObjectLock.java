package com.dl.lock;

public class ObjectLock {
	
	private final Object sigLock = new Object();
	
	private boolean paused = true;
	
	public static void main(String[] args) throws InterruptedException {
		ObjectLock lock = new ObjectLock();
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.run();
			}
		}).start();
		
		Thread.sleep(3000);
		lock.stopPaused();
	}
	
	public void run(){
		System.out.println("run start");
		synchronized (sigLock) {
			System.out.println("run get sigLock");
			while(paused){
				try {
					System.out.println("run wait start");
//					Thread.sleep(1000);//如果用sleep，stopPaused就拿不到锁了
					sigLock.wait(1000);
					System.out.println("run wait end");
				} catch (InterruptedException e) {
					System.out.println("run wait fail");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void stopPaused(){
		System.out.println("stopPaused start");
		synchronized (sigLock) {
			System.out.println("stopPaused get sigLock");
			paused = false;
			sigLock.notifyAll();
			System.out.println("stopPaused notify");
		}
	}
}
