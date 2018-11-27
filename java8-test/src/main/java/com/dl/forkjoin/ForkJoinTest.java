package com.dl.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTest {
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		CountTask task = new CountTask(1, 4);
		Future<Integer> future = pool.submit(task);
		try {
			System.out.println(future.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	
	public static class CountTask extends RecursiveTask<Integer>{

		private static final long serialVersionUID = -1983371601108132676L;
		
		private static final int THRESHOLD = 2;
		private int start;
		private int end;
		
		public CountTask(int start,int end){
			this.start = start;
			this.end = end;
		}
		
		@Override
		protected Integer compute() {
			int sum = 0;
			if((end-start)<=THRESHOLD){
				for(int i=start;i<=end;i++){
					sum += i;
				}
			}else{
				int middle = (start+end)/2;
				CountTask leftTask = new CountTask(start, middle);
				CountTask rightTask = new CountTask(middle+1, end);
				leftTask.fork();
				rightTask.fork();
				
				int leftResult = leftTask.join();
				int rightResult = rightTask.join();
				sum = leftResult + rightResult;
			}		
			return sum;
		}
		
	}
}
