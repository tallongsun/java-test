package com.dl.vertica;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class FunnelTest {
	public static void main(String[] args) {
		Map<String,Map<String,TreeSet<Long>>> allMap = new HashMap<>();
		
		Map<String,TreeSet<Long>> map = new LinkedHashMap<>();
		TreeSet<Long> set1 = new TreeSet<>();
		set1.add(1L);
		set1.add(2L);
		set1.add(3L);
		map.put("A", set1);
		TreeSet<Long> set2 = new TreeSet<>();
		set2.add(5L);
		set2.add(7L);
		map.put("B", set2);
		TreeSet<Long> set3 = new TreeSet<>();
		set3.add(6L);
		map.put("C", set3);
		allMap.put("u1", map);
		
		map = new LinkedHashMap<>();
		set1 = new TreeSet<>();
		set1.add(1L);
		set1.add(2L);
		set1.add(3L);
		map.put("A", set1);
		set2 = new TreeSet<>();
		set2.add(5L);
		set2.add(7L);
		map.put("B", set2);
		set3 = new TreeSet<>();
		set3.add(100L);
		map.put("C", set3);
		allMap.put("u2", map);
		System.out.println(allMap);
		
		Map<String,Set<String>> counter = new HashMap<>();
		for(Entry<String,Map<String,TreeSet<Long>>> userEntry : allMap.entrySet()){
			String sid = userEntry.getKey();
			Entry<String,TreeSet<Long>> preEntry = null;
			for(Entry<String,TreeSet<Long>> entry : userEntry.getValue().entrySet()){
				if(preEntry!=null){
					TreeSet<Long> value = entry.getValue();
					int cnt = 0;
					Iterator<Long> ite  = value.iterator();
					while(ite.hasNext()){
						long v = ite.next();
						TreeSet<Long> preValue = preEntry.getValue();
						if(!preValue.subSet(v-3, v).isEmpty()){
							cnt++;
						}else{
							ite.remove();
						}
					}
					System.out.println(entry.getKey()+":"+cnt);
					if(cnt>0){
						Set<String> sids = counter.get(entry.getKey());
						if(sids == null){
							sids = new HashSet<String>();
							counter.put(entry.getKey(), sids);
						}
						sids.add(sid);
					}
				}else{
					int cnt = entry.getValue().size();
					System.out.println(entry.getKey()+":"+cnt);
					if(cnt>0){
						Set<String> sids = counter.get(entry.getKey());
						if(sids == null){
							sids = new HashSet<String>();
							counter.put(entry.getKey(), sids);
						}
						sids.add(sid);
					}
				}
				preEntry = entry;
			}
		}
		System.out.println(counter);
	}
}
