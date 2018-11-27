package com.dl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
//		Random r = new Random();
//		IntStream.rangeClosed(1, 50).forEach(i -> System.out.println(r.nextGaussian()));
//		
//		Integer integer = 0;
//		System.out.println(Long.parseLong(String.valueOf(integer)));
//		
//		List<Object> list = new ArrayList<>();
//		list.add(1);
//		for(int i=0;i<list.size();i++){
//			list.set(i, 2);
//		}
//
//		System.out.println(list);
		
//		long start = System.currentTimeMillis();
//		List<A> list = new ArrayList<>();
//		for(int i=0;i<1000000;i++){
//			list.add(new A(i));
//		}
//		System.out.println(System.currentTimeMillis()-start);
		
//		long start = System.currentTimeMillis();
//		List list = new LinkedList<>();
//		for(int i=0;i<1000000;i++){
//			list.add(new A(i));
//		}
//		System.out.println(System.currentTimeMillis()-start);
		
//		List t = new ArrayList<>(3);
//		t.get(0);
		
//		long start = System.currentTimeMillis();
//		for(int i=0;i<5000000;i++){
//			list.get(i);
//		}
//		System.out.println(System.currentTimeMillis()-start);
		
		List<Num> list = new ArrayList<>();
		list.add(new Num(1,"c"));
		list.add(new Num(2,"c"));
		list.add(new Num(100,"c"));
		list.add(new Num(19,"c"));
		
//		for(Num num : list) {
//		if(num.a == 100) {
//			System.out.println(num);
//		}
//	}
	
		for(int j=0;j<20;j++) {
			long now = System.currentTimeMillis();
			list.stream().anyMatch(i -> i.a == 100);
			System.out.println(System.currentTimeMillis()-now);
		
		}
				
	}
	
	public static class Num{
		private int a;
		private String b;
		
		public Num(int a, String b) {
			super();
			this.a = a;
			this.b = b;
		}
		
		
	}
	
	public static class A<T>{
		private int i;
		public A(int i){
			this.i = i;
		}
		public int getI(){
			return i;
		}
	}

}
