package com.dl.guava.collection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;
import com.google.common.collect.TreeRangeMap;
import com.google.common.collect.TreeRangeSet;

public class CollectionTest {
	public static void main(String[] args) {
		testRangeSet();
	}
	
	public static void testImmutableCollection(){
		ImmutableSet<String> colors = ImmutableSet.of("red","orange","yellow");
		System.out.println(colors);
		
		ImmutableSet<String> copyColors = ImmutableSet.copyOf(colors);
		System.out.println(copyColors.asList().get(0));
	}
	
	
	public static void testMultiSet(){
		Multiset<String>  multiSet = HashMultiset.create();
		multiSet.add("a");
		multiSet.add("a");
		multiSet.add("b");
		System.out.println(multiSet.size());
		System.out.println(multiSet.count("a"));
		System.out.println(multiSet.entrySet());
		System.out.println(multiSet.elementSet());
		multiSet.setCount("b", 2);
		multiSet.remove("a");
		System.out.println(multiSet.size());
		System.out.println(multiSet.count("a"));
		System.out.println(multiSet.entrySet());
		System.out.println(multiSet.elementSet());
		
		SortedMultiset<String> sMultiSet = TreeMultiset.create();
		sMultiSet.add("a");
		sMultiSet.add("a");
		sMultiSet.add("b");
		System.out.println(sMultiSet.size());
		System.out.println(sMultiSet.count("a"));
		System.out.println(sMultiSet.entrySet());
		System.out.println(sMultiSet.elementSet());
		sMultiSet.setCount("b", 2);
		sMultiSet.remove("a");
		System.out.println(sMultiSet.size());
		System.out.println(sMultiSet.count("a"));
		System.out.println(sMultiSet.entrySet());
		System.out.println(sMultiSet.elementSet());
	}
	
	public static void testMultiMap(){
		SetMultimap<String, Integer> multiMap = HashMultimap.create();
		multiMap.put("a", 1);
		multiMap.put("a", 2);
		multiMap.put("a", 4);
		multiMap.put("b", 3);
		System.out.println(multiMap.entries());
		Set<Integer>  children = multiMap.get("b");
		children.clear();
		children.add(5);
		children.add(6);
		System.out.println(multiMap.entries());
		multiMap.putAll("c",Arrays.asList(9,10));
		multiMap.remove("b", 5);
		System.out.println(multiMap.entries());
		multiMap.removeAll("c");
		System.out.println(multiMap.entries());
		multiMap.replaceValues("c", Arrays.asList(9,10));
		System.out.println(multiMap.entries());
		System.out.println(multiMap.asMap());
		System.out.println(multiMap.keySet());
		System.out.println(multiMap.keys());
		System.out.println(multiMap.values());
		
		SetMultimap<String, Integer> tMultiMap = TreeMultimap.create();
		tMultiMap.put("a", 1);
		tMultiMap.put("a", 2);
		System.out.println(tMultiMap.asMap());
		
		ListMultimap<String, Integer> lMultiMap = ArrayListMultimap.create();
		lMultiMap.put("a", 1);
		lMultiMap.put("a", 2);
		System.out.println(lMultiMap.asMap());
	}
	
	public static void testBiMap(){
		BiMap<String,Integer> userId = HashBiMap.create();
		userId.put("Bob", 42);
		System.out.println(userId.inverse().get(42));
	}
	
	
	public static void testTable(){
		Table<Integer,String,String> records = HashBasedTable.create();
		records.put(1, "Bob", "Bob 111");
		records.put(1, "Doe", "Doe 111");
		records.put(2, "Doe", "Doe 222");
		
		System.out.println(records.row(1));
		System.out.println(records.column("Doe"));
		System.out.println(records.rowKeySet());
		System.out.println(records.columnKeySet());
		System.out.println(records.rowMap());
		System.out.println(records.cellSet());
	}
	
	public static void testClassToInstanceMap(){
		ClassToInstanceMap<Number> numberDefaults = MutableClassToInstanceMap.create(); 
		numberDefaults.putInstance(Integer.class, Integer.valueOf(0));
		numberDefaults.putInstance(Long.class, Long.valueOf(1));
		System.out.println(numberDefaults);
	}
	
	public static void testRangeSet(){
		
		RangeSet<Integer> rangeSet = TreeRangeSet.create();
		rangeSet.add(Range.closed(1, 10));
		rangeSet.add(Range.closedOpen(2, 11));
		System.out.println(rangeSet);
		
//		RangeSet<Integer> rangeSet = TreeRangeSet.create();
//		rangeSet.add(Range.closed(1, 10));
//		rangeSet.add(Range.closedOpen(11, 15));
//		rangeSet.add(Range.closedOpen(15, 20));
//		rangeSet.add(Range.openClosed(0, 0));
//		rangeSet.remove(Range.open(5, 10));
//		System.out.println(rangeSet);
//		System.out.println(rangeSet.complement());
//		System.out.println(rangeSet.subRangeSet(Range.closed(12, 25)));
//		System.out.println(rangeSet.asRanges());
//		
//		System.out.println(rangeSet.contains(3));
//		System.out.println(rangeSet.rangeContaining(3));
//		System.out.println(rangeSet.encloses(Range.closed(12, 15)));
//		System.out.println(rangeSet.span());
	}
	
	public static void testRangeMap(){
		RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
		rangeMap.put(Range.closed(1, 10), "foo");
		rangeMap.put(Range.open(3, 6), "bar");
		rangeMap.put(Range.open(10, 20), "foo"); 
		rangeMap.remove(Range.closed(5, 11)); 
		System.out.println(rangeMap);
		System.out.println(rangeMap.asMapOfRanges());
		System.out.println(rangeMap.subRangeMap(Range.closedOpen(12, 15)));
	}
	
	public void testSets(){
		Set<StatefulUser> set1 = new HashSet<>();
		set1.add(new StatefulUser(1,"u1"));
		
		Set<StatefulUser> set2 = new HashSet<>();
		set2.add(new StatefulUser(2,"u1"));
		set2.add(new StatefulUser(2,"u2"));
		
		Set<StatefulUser> unionSet1 = Sets.union(set1, set2);
		System.out.println(unionSet1);
		
		
		Set<StatefulUser> set3 = new HashSet<>();
		set3.add(new StatefulUser(1,"u1"));
		
		Set<StatefulUser> set4 = new HashSet<>();
		set4.add(new StatefulUser(2,"u1"));
		set4.add(new StatefulUser(2,"u3"));
		
		Set<StatefulUser> unionSet2 = Sets.union(set3, set4);
		System.out.println(unionSet2);
		
		System.out.println(Sets.intersection(unionSet1, unionSet2));
	}
	
	public static class StatefulUser{
		private int state;
		private String uid;
		
		public StatefulUser(int state, String uid) {
			this.state = state;
			this.uid = uid;
		}
		
		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + state;
			result = prime * result + ((uid == null) ? 0 : uid.hashCode());
			return result;
		}



		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StatefulUser other = (StatefulUser) obj;
			if (state != other.state)
				return false;
			if (uid == null) {
				if (other.uid != null)
					return false;
			} else if (!uid.equals(other.uid))
				return false;
			return true;
		}



		@Override
		public String toString() {
			return "StatefulUser [state=" + state + ", uid=" + uid + "]";
		}
		
	}
}
