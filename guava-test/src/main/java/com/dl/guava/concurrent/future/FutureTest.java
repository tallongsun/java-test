package com.dl.guava.concurrent.future;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class FutureTest {
	private static ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
	
	public static void main(String[] args) throws Exception {
		long start = Calendar.getInstance().getTimeInMillis();
		
//		functionWithJava8();
		
		testTransform();

		System.out.println("test over...cost "+(Calendar.getInstance().getTimeInMillis()-start)+"ms");
		
		executor.shutdown();
	}
	
	public static void testListenableFuture() {
		//create future
		ListenableFuture<String> explosionFuture = executor.submit(() -> {
			System.out.println("push big red button...");
			return "explosion";
		});
		//add callback
		Futures.addCallback(explosionFuture, new FutureCallback<String>() {
			@Override
			public void onSuccess(String result) {
				System.out.println("walk away from "+result+"...");
			}
			@Override
			public void onFailure(Throwable t) {
				System.out.println("escape...");
			}
		});
		

	}
	
	public static void testTransformAsync() {
		ListenableFuture<String> queryFuture = Futures.transform(
			executor.submit(() -> {
				//pool-1-thread-1
				System.out.println(Thread.currentThread().getName());
				System.out.println("look up key...");
				return "key1";
			}),
			(AsyncFunction<String, String>)((String key) -> {
				//pool-1-thread-2
				System.out.println(Thread.currentThread().getName());
				return executor.submit(()->{
					//pool-1-thread-3
					System.out.println(Thread.currentThread().getName());
					System.out.println("read value...");
					return "value1";
				});
			}),
			executor);//执行function，跟inputFuture，使用同一线程池，但不使用同一线程
		
//		Futures.addCallback(queryFuture, new FutureCallback<String>(){
//			@Override
//			public void onSuccess(String result) {
//				//main or pool-1-thread-x
//				System.out.println(Thread.currentThread().getName());
//				System.out.println(result);
//			}
//			@Override
//			public void onFailure(Throwable t) {
//				t.printStackTrace();
//			}
//		});
		
		try{
			//还是这种方式更靠谱些，保证main线程执行。callback方式不能保证在main线程执行，因此如果main线程注册完callback就退出了，线程池被关闭，就悲剧了。
			//如果非要使用callback方式，本例中，线程池关闭应该在onSuccess处理最后，保证所有线程都执行完。
			System.out.println(queryFuture.get());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void testTransform() {
		ListenableFuture<ListenableFuture<String>> queryFuture = Futures.transform(
			executor.submit(() -> {
				//pool-1-thread-1
				System.out.println(Thread.currentThread().getName());
				System.out.println("look up key...");
				return "key1";
			}),
			(String key) -> {
				//pool-1-thread-2
				System.out.println(Thread.currentThread().getName());
				return executor.submit(()->{
					//pool-1-thread-3
					System.out.println(Thread.currentThread().getName());
					System.out.println("read value...");
					return "value1";
				});
			},
			executor);
		
		
		try{
			System.out.println(queryFuture.get().get());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testList() throws Exception{
		//适合多线程map，单线程reduce的场景
		List<ListenableFuture<String>> futureList = Arrays.asList("1","2").stream().map(item -> executor.submit(()->item)).collect(Collectors.toList());
		
		ListenableFuture<List<String>> future = Futures.allAsList(futureList);
		
		System.out.println(future.get());
	}
	
	
	public static void functionWithJava8() throws Exception{
		//定义MapReduce处理
		Function<List<String>,ListenableFuture<Object>> mr = (List<String> allFinalData) -> 
			Futures.transform(
				Futures.allAsList(allFinalData.stream().map(
					data -> executor.submit(new Callable<Character>() {
						@Override
						public Character call() throws Exception {
							return data.charAt(0);
						}
					})).collect(Collectors.toList())), 
				(List<Character> input) -> input,
				executor);	
		
		//运行处理
		ListenableFuture<ListenableFuture<Object>> outputFuture = 
			Futures.transform(
				Futures.transform(
					Futures.allAsList(Arrays.asList("1","2","3").stream().map( 
						elem -> Futures.transform(
							executor.submit(new QueryTask(elem)),  
							(String data) ->Arrays.asList(data.split(";")), 
							executor)).collect(Collectors.toList())),
					(List<List<String>> allData) -> {
						List<String> result = Lists.newArrayList();
						allData.forEach(item -> result.addAll(item));
						return result;
					},
					executor),
				mr);
		
		System.out.println(outputFuture.get().get());

	}
	
	public static void functionWithGuava() throws Exception{
		Function<String, List<String>> step2 = new Function<String, List<String>>() {
			public List<String> apply(String data) {
				return Arrays.asList(data.split(";"));
			}
		};
		//查询并反序列化数据
		Function<String, ListenableFuture<List<String>>> step12 = elem ->
				Futures.transform(executor.submit(new QueryTask(elem)), step2, executor);
				
		//汇总所有数据
		Function<List<List<String>>,List<String>> step3 = allData -> {
			List<String> result = Lists.newArrayList();
			allData.forEach(item -> result.addAll(item));
			return result;
		};
				
		
		//Map
		Function<String,ListenableFuture<Character>> step4 = new Function<String,ListenableFuture<Character>>(){
			public ListenableFuture<Character> apply(String data) {
				return executor.submit(new Callable<Character>() {
					@Override
					public Character call() throws Exception {
						return data.charAt(0);
					}
				});
			}
		};
		//Reduce
		Function<List<Character>,Object> step5 = new Function<List<Character>,Object>(){
			public Object apply(List<Character> input) {
				return input;
			}
		};
		//对汇总数据做MapReduce处理
		Function<List<String>,ListenableFuture<Object>> step45 = new Function<List<String>,ListenableFuture<Object>>(){
			public ListenableFuture<Object> apply(List<String> allFinalData) {
				System.out.println("mr...");
				return Futures.transform(Futures.allAsList(Lists.transform(allFinalData, step4)), step5,executor);
			}
		};
		
		ListenableFuture<ListenableFuture<Object>> outputFuture = Futures.transform(
				Futures.transform(Futures.allAsList(Lists.transform(Arrays.asList("1","2","3"), step12)),step3,executor),step45);
		
		System.out.println(outputFuture.get().get());
		
	}

	public static class QueryTask implements Callable<String>{
		private String key;
		public QueryTask(String key){
			this.key = key;
		}
		public String call() {
			switch(key){
			case "1":
				return "tallong;ivy;jason";
			case "2":
				return "jack;tom";
			case "3":
				return "jane;rose";
			}
			return "";
		}
	}
	
}
