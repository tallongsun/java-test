package com.dl.db.perf.processor;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dl.db.perf.KeyValue;
import com.dl.db.perf.Processor;
import com.dl.db.perf.SimpleObject;
import com.dl.db.perf.dao.KeyValueMapper;
import com.dl.db.perf.dao.SimpleObjectMapper;

public class MysqlProcessor implements Processor{
	private SimpleObjectMapper simpleObjectMapper;
	private KeyValueMapper keyValueMapper;
	
	private boolean isBigField = true;
	
	public MysqlProcessor(SimpleObjectMapper mysqlMapper,KeyValueMapper keyValueMapper,boolean isBigField ) {
		this.simpleObjectMapper = mysqlMapper;
		this.keyValueMapper = keyValueMapper;
		this.isBigField = isBigField;
	}

	@Override
	public void write(List<SimpleObject> allData) {
		if(isBigField){
			keyValueMapper.insert(new KeyValue("key", JSON.toJSONString(allData)));
		}else{
			int pageSize = 10000;
			int total = allData.size();
			
			int page = (total+pageSize-1)/pageSize;
			for(int curPage = 0;curPage<page;curPage++){
				int s = curPage * pageSize;
				int e = (curPage+1) * pageSize;
				if(e>total){
					e = total;
				}
				simpleObjectMapper.insertList(allData.subList(s, e));
//				simpleObjectMapper.insert(allData.get(s));
			}
		}

		
	}

	@Override
	public List<SimpleObject> read() {
		if(isBigField){
			return JSON.parseObject(keyValueMapper.getAll().get(0).getValueField(),new TypeReference<List<SimpleObject>>(){});
		}else{
			return simpleObjectMapper.getAll();
		}
	}

	@Override
	public void clear() {
		if(isBigField){
			keyValueMapper.deleteAll();
		}else{
			simpleObjectMapper.deleteAll();
		}
	}

}
