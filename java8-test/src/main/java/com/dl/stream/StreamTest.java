package com.dl.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamTest {

	public static void main(String[] args) throws Exception{
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(new Transaction(1, Transaction.GEOCERY, 1000));
		transactions.add(new Transaction(2, Transaction.GEOCERY, 1100));
		transactions.add(new Transaction(3, Transaction.GEOCERY, 100));
		transactions.add(new Transaction(4, Transaction.GEOCERY, 10));
		transactions.add(new Transaction(5, Transaction.GEOCERY, 10000));
		transactions.add(new Transaction(6, Transaction.GEOCERY, 1000));
		transactions.add(new Transaction(7, 0, 1000));
		
		List<Integer> resultIds = transactions.parallelStream().filter(t -> t.getType()==Transaction.GEOCERY)
			.sorted(new Comparator<Transaction>() {
				@Override
				public int compare(Transaction o1, Transaction o2) {
					return o2.getValue()-o1.getValue();
				}
			})
			.map(new Function<Transaction, Integer>() {
				@Override
				public Integer apply(Transaction t) {
					return t.getId();
				}
			}).collect(Collectors.toList());
		System.out.println(resultIds);
		
		resultIds = transactions.stream().filter(t -> t.getType()==Transaction.GEOCERY)
				.sorted(new Comparator<Transaction>() {
					@Override
					public int compare(Transaction o1, Transaction o2) {
						return o2.getValue()-o1.getValue();
					}
				})
				.map(Transaction::getId).collect(Collectors.toList());
		System.out.println(resultIds);
		
		int sumVal = transactions.stream().filter(t -> t.getType()==Transaction.GEOCERY)
			.mapToInt(t -> t.getValue())
			.sum();
		System.out.println(sumVal);
		
		transactions.add(new Transaction(8, Transaction.GEOCERY, 5000));
		sumVal = transactions.stream().filter(t -> t.getType()==Transaction.GEOCERY)
				.mapToInt(t -> t.getValue())
				.sum();
		System.out.println(sumVal);
		
		IntStream.of(new int[]{1, 2, 3}).forEach(System.out::println);
		IntStream.range(1, 3).forEach(System.out::println);
		IntStream.rangeClosed(1, 3).forEach(System.out::println);
		
		Stream<String> stream = Stream.of("a", "b", "c");
		String [] strArray = new String[] {"a", "b", "c"};
		stream = Stream.of(strArray);
		stream = Arrays.stream(strArray);
		List<String> list = Arrays.asList(strArray);
		stream = list.stream();
		
//		String[] strArray1 = stream.toArray(String[]::new);
		List<String> list1 = stream.collect(Collectors.toList());
//		List<String> list2 = stream.collect(Collectors.toCollection(ArrayList::new));
//		Set<String> set1 = stream.collect(Collectors.toSet());
//		Stack<String> stack1 = stream.collect(Collectors.toCollection(Stack::new));
//		String str = stream.collect(Collectors.joining());
		System.out.println(list1);
		
		List<String> output = list1.stream().map(s -> s.toUpperCase()).collect(Collectors.toList());
		System.out.println(output);
		
		List<Integer> nums = Arrays.asList(1, 2, 3, 4);
		List<Integer> squareNums = nums.stream().map(n -> n * n).collect(Collectors.toList());
		System.out.println(squareNums);
		
		Stream<List<Integer>> inputStream = Stream.of(
				 Arrays.asList(1),
				 Arrays.asList(2, 3),
				 Arrays.asList(4, 5, 6)
				 );
		List<Integer> outputStream = inputStream.flatMap(childList -> childList.stream()).collect(Collectors.toList());
		System.out.println(outputStream);
		
		Integer[] sixNums = {1, 2, 3, 4, 5, 6};
		Integer[] evens = Stream.of(sixNums).filter(n -> n%2 == 0).toArray(Integer[]::new);
		System.out.println(Arrays.toString(evens));
		
		BufferedReader reader = new BufferedReader(new FileReader(new File("test.txt")));
		output = reader.lines().
				 flatMap(line -> Stream.of(line.split(" "))).
				 filter(word -> word.length() > 0).
				 collect(Collectors.toList());
		System.out.println(output);
		reader.close();
		
		transactions.stream()
		 .filter(t -> t.getType() == Transaction.GEOCERY)
		 .forEach(t -> System.out.println(t.getId()));
		
		Stream.of("one", "two", "three", "four")
		 .filter(e -> e.length() > 3)
		 .peek(e -> System.out.println("Filtered value: " + e))
		 .map(String::toUpperCase)
		 .peek(e -> System.out.println("Mapped value: " + e))
		 .collect(Collectors.toList());
		
		String concat = Stream.of("A", "B", "C", "D").reduce("", String::concat);
		System.out.println(concat);
		double minValue = Stream.of(-1.5, 1.0, -3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);
		System.out.println(minValue);
		int sumValue = Stream.of(1, 2, 3, 4).reduce(0, Integer::sum);
		System.out.println(sumValue);
		sumValue = Stream.of(1, 2, 3, 4).reduce(Integer::sum).get();
		System.out.println(sumValue);
		concat = Stream.of("a", "B", "c", "D", "e", "F").filter(x -> x.compareTo("Z") > 0).reduce("", String::concat);
		System.out.println(concat);
		
		List<Integer> transactionList = transactions.stream().map(t -> t.getId()).limit(5).skip(3).collect(Collectors.toList());
		System.out.println(transactionList);
		List<Transaction> transactionList2 = transactions.stream().limit(6).sorted((t1,t2)->t1.getValue()-t2.getValue()).limit(5).collect(Collectors.toList());
		System.out.println(transactionList2);
		
		BufferedReader br = new BufferedReader(new FileReader(new File("test.txt")));
		int longest = br.lines().mapToInt(String::length).max().getAsInt();
		br.close();
		System.out.println(longest);
		
		br = new BufferedReader(new FileReader(new File("test.txt")));
		List<String> words = br.lines().flatMap(line -> Stream.of(line.split(" ")))
				.filter(word -> word.length() > 0)
				.map(String::toLowerCase)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		br.close();
		System.out.println(words);
		
		boolean isAllBig = transactions.stream().allMatch(t -> t.getValue()>1000);
		System.out.println(isAllBig);
		boolean isAnySmall = transactions.stream().anyMatch(t -> t.getValue()<200);
		System.out.println(isAnySmall);
		
		Random seed = new Random();
		Stream.generate(() -> seed.nextInt()).limit(3).forEach(System.out::println);
		IntStream.generate(() -> (int) (System.nanoTime() % 100)).limit(3).forEach(System.out::println);
		Stream.generate(() -> new Transaction(seed.nextInt(10), seed.nextInt(2), seed.nextInt(5000)))
			.limit(10).forEach(t -> {System.out.println(t);});
		
		Stream.iterate(0, n -> n + 3).limit(10).forEach(x -> System.out.print(x + " "));
		System.out.println();
		
		Map<Integer,List<Transaction>> groups = transactions.stream().collect(
				Collectors.groupingBy(t -> t.getType()));
		for(int key : groups.keySet()){
			System.out.println(key + "=" + groups.get(key).size());
		}
		
		Map<Boolean,List<Transaction>> parts = transactions.stream().collect(
				Collectors.partitioningBy(t -> t.getValue()<500));
		for(boolean key : parts.keySet()){
			System.out.println(key + "=" + parts.get(key).size());
		}
	}

}
