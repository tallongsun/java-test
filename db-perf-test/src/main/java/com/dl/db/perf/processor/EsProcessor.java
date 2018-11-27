package com.dl.db.perf.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dl.db.perf.KeyValue;
import com.dl.db.perf.Processor;
import com.dl.db.perf.SimpleObject;

public class EsProcessor implements Processor{
	private Client esClient;
	
	private String indexName;
	
	private boolean isBigField = true;
	
	public EsProcessor(Client esClient, String indexName,boolean isBigField) {
		this.esClient = esClient;
		this.indexName = indexName;
		this.isBigField = isBigField;
	}

	@Override
	public void write(List<SimpleObject> allData) {
		if(isBigField){
			try {
				esClient.prepareIndex(indexName,"default").setSource(
						JSON.toJSONString(new KeyValue("key",JSON.toJSONString(allData)))).execute().get();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e); 
			}
		}else{
			BulkRequestBuilder builder = esClient.prepareBulk();
			for(SimpleObject data : allData){
				builder.add(esClient.prepareIndex(indexName,"default").setSource(JSON.toJSONString(data)));
			}
			
			try {
				builder.execute().get();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e); 
			}
		}

	}

	@Override
	public List<SimpleObject> read() {
		List<SimpleObject> records = new ArrayList<>();
		SearchResponse response = null;
		if(isBigField){
			BoolQueryBuilder builder = QueryBuilders.boolQuery();
			builder.must(QueryBuilders.termQuery("keyField", "key"));
			try {
				response = esClient.prepareSearch(indexName).setTypes("default")
					.setSearchType(SearchType.DEFAULT)
					.setQuery(builder.buildAsBytes())
					.setSize(10000)
					.execute().get();
				for (final SearchHit hit : response.getHits()) {
					Map<String,Object> sources = hit.getSource();
					records = JSON.parseObject(sources.get("valueField").toString(), new TypeReference<List<SimpleObject>>(){});
				}
				return records;
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e); 
			}
		}else{
			
			
			BoolQueryBuilder builder = QueryBuilders.boolQuery();
			builder.must(QueryBuilders.termQuery("vertex", "n1"));
			builder.must(QueryBuilders.termQuery("status", "converted"));
			try {
				response = esClient.prepareSearch(indexName).setTypes("default")
					.setSearchType(SearchType.DEFAULT)
					.setScroll(new TimeValue(60000))
					.setQuery(builder.buildAsBytes())
					.setSize(10000)
					.execute().get();
				for (final SearchHit hit : response.getHits()) {
					Map<String,Object> sources = hit.getSource();
					records.add(JSON.parseObject(JSON.toJSONString(sources), SimpleObject.class));
				}
				while(true){
					response = esClient.prepareSearchScroll(response.getScrollId())
							.setScroll(new TimeValue(60000))
							.execute().get();
					if(response.getHits().getHits().length == 0){
						break;
					}
					for (final SearchHit hit : response.getHits().getHits()) {
						Map<String,Object> sources = hit.getSource();
						records.add(JSON.parseObject(JSON.toJSONString(sources), SimpleObject.class));
					}
					
				}
				return records;
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e); 
			}
		}

		
	}

	@Override
	public void clear() {
		esClient.admin().indices().prepareDelete(indexName).execute().actionGet();
	}

}
