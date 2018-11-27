package com.dl.es;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.min.InternalMin;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/*
curl -XPUT 'http://127.0.0.1:9200/my_index_1/' -d '{
    "settings" : {
        "index" : {
            "number_of_shards" : 2,
            "number_of_replicas" : 1
        }
    }
}'
curl -XPUT 'http://127.0.0.1:9200/my_index_1/type1/_mappings' -d '{
"type1": {
    "_all": {
        "analyzer": "standard",
        "search_analyzer": "standard",
        "term_vector": "no",
        "store": "false"
    },
    "properties": {
        "usercode": {
            "type": "string",
            "index": "not_analyzed"
        },
        "param": {
            "type": "string",
            "store": "no",
            "term_vector": "with_positions_offsets",
            "analyzer": "standard",
            "search_analyzer": "standard",
            "include_in_all": "true",
            "boost": 8
        }
    }
}
}'
*/
public class EsTest {

	private static final String INDEX_NAME = "my_index_3";

	//TODO::同步方式操作,SearchType,dateHistogram,facet
	public static void main(String[] args) throws Exception{
		
		TransportClient esClient = initClient();
		
//		//增
		bulk(esClient);
//		query2(esClient);
//		
//		
//		//改
//		update(esClient);
//		
//		
//		//查
//		query(esClient);
//		group(esClient);
		
		
//		//删
//		delete(esClient);
		
		
		esClient.close();
	}

	 static void insert(TransportClient esClient){
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(Inclusion.NON_NULL);

		String value = null;
		try {
			String param = jsonMapper.writeValueAsString(new KafkaMqMessage.KeyValueParam("key1", "val1"));
			KafkaMqMessage kafkaMqMessage = new KafkaMqMessage(Calendar.getInstance().getTimeInMillis(), "user2",param);
			value = jsonMapper.writeValueAsString(kafkaMqMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IndexResponse response = null;
		try{
			response = esClient.prepareIndex(INDEX_NAME,"default","6")
				.setSource(value)
				.execute()
				.get();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String id = response.getId();
		long version = response.getVersion();
		System.out.println(id+","+version);
	}
	
	 static void update(TransportClient esClient) throws Exception{
		UpdateResponse response = esClient.prepareUpdate(INDEX_NAME,"default","1")
			.setDoc(XContentFactory.jsonBuilder().startObject().field("usercode","x").endObject())
			.get();
		System.out.println(response.getId()+","+response.getVersion());
	}
	
	 static void delete(TransportClient esClient)  throws Exception{
		DeleteResponse response = esClient.prepareDelete(INDEX_NAME,"default","1")
				.execute().get();
		System.out.println(response.getId());
	}
	
	
	@SuppressWarnings("deprecation")
	 static void query(TransportClient esClient) throws Exception{
		//get by id
		GetResponse getResponse = null;
		try {
			getResponse = esClient.prepareGet(INDEX_NAME,"default","1")
				.execute()
				.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(getResponse.getSourceAsString());
		
		//multi index & type
		SearchResponse response = null;
		response = esClient.prepareSearch(INDEX_NAME,"my_index_2").setTypes("default","type1","type2")
			.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
			.setQuery(QueryBuilders.termQuery("usercode", "y"))
			.setSize(10).setFrom(0).setExplain(true)
			.execute().get();
		
		System.out.println(response.getHits().getTotalHits());
		
		//scroll
		response = esClient.prepareSearch(INDEX_NAME)
			.setSearchType(SearchType.DEFAULT)
			.setScroll(new TimeValue(60000))
			.setQuery(QueryBuilders.termQuery("usercode", "5"))
			.setSize(100).execute().get();
		while(true){
			System.out.println("---"+response.getHits().getHits().length);
			response = esClient.prepareSearchScroll(response.getScrollId())
					.setScroll(new TimeValue(60000))
					.execute().get();
			if(response.getHits().getHits().length == 0){
				break;
			}
		}
		
		//query
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		builder.must(QueryBuilders.rangeQuery("timestamp").lte(Calendar.getInstance().getTimeInMillis()));
		builder.must(QueryBuilders.termQuery("usercode", "5"));
		builder.must(QueryBuilders.matchQuery("param", "value"));
		
		BytesReference query = builder.buildAsBytes();
		String[] includes = {"timestamp","usercode","param"};
		try {
			response = esClient.prepareSearch(INDEX_NAME)
					.addSort("timestamp", SortOrder.DESC)
					.setQuery(builder)
					.setSize(10)
					.setFrom((1 - 1) * 10)
					.setFetchSource(includes, null).execute().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SearchHits hits = response.getHits();
		long total = hits.getTotalHits();
		List<KafkaMqMessage> records = new ArrayList<>();
		for (final SearchHit hit : hits) {
			Map<String,Object> sources = hit.getSource();
			KafkaMqMessage msg = new KafkaMqMessage();
			msg.setTimestamp(((Number)sources.get("timestamp")).longValue());
			msg.setUsercode(sources.get("usercode").toString());
			msg.setParam(sources.get("param").toString());
			records.add(msg);
		}
		System.out.println(total);
		System.out.println(records);
		

	}
	
	static void query2(TransportClient esClient) throws Exception{
		SearchResponse response = null;
		//query
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
//		builder.must(QueryBuilders.termQuery("usercode", "u1"));
		
		long start = System.currentTimeMillis();
		BytesReference query = builder.buildAsBytes();
		String[] includes = {"timestamp","usercode","param"};
		List<KafkaMqMessage> records = new ArrayList<>();
		try {
			response = esClient.prepareSearch("my_index_1").setTypes("u1")
					.setSearchType(SearchType.DEFAULT)
					.setScroll(new TimeValue(60000))
					.setQuery(builder)
					.setSize(10000)
					.setFetchSource(includes, null).execute().get();
			
			for(int i=0;i<10;i++){
				response = esClient.prepareSearchScroll(response.getScrollId())
						.setScroll(new TimeValue(60000))
						.execute().get();
				SearchHits hits = response.getHits();
				long total = hits.getTotalHits();
				System.out.println(total);
				for (final SearchHit hit : hits) {
					Map<String,Object> sources = hit.getSource();
					KafkaMqMessage msg = new KafkaMqMessage();
					msg.setTimestamp(((Number)sources.get("timestamp")).longValue());
					msg.setUsercode(sources.get("usercode").toString());
					msg.setParam(sources.get("param").toString());
					records.add(msg);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(records.size());
		System.out.println(System.currentTimeMillis()-start);//1300/10W
	}
	
	 static void group(TransportClient esClient) throws Exception{
		SearchResponse response = esClient.prepareSearch(INDEX_NAME)
					.setQuery(QueryBuilders.matchAllQuery())
					.addAggregation(AggregationBuilders.terms("group_by_usercode").field("usercode")
							.subAggregation(AggregationBuilders.min("min_timestamp").field("timestamp")))
					.execute().get();
		
		Terms terms = response.getAggregations().get("group_by_usercode");
		System.out.println("group size : "+terms.getBuckets().size());
		for(Bucket b : terms.getBuckets()){
			String _usercode = b.getKeyAsString();
			InternalMin _timestamp = (InternalMin)b.getAggregations().get("min_timestamp");
			System.out.println("group id : "+_usercode+", count : "+b.getDocCount()+", min_timestamp : "+_timestamp.getValueAsString());
			
			response = esClient.prepareSearch(INDEX_NAME)
					.setQuery(QueryBuilders.boolQuery()
							.must(QueryBuilders.termQuery("usercode", _usercode))
							.must(QueryBuilders.termQuery("timestamp", _timestamp.getValue())))
					.execute().get();
			
			if(response.getHits().getTotalHits()>0){
				String _id = response.getHits().getAt(0).getId();
				System.out.println("doc id : "+_id);
				
//				response = esClient.prepareSearch(INDEX_NAME)
//						.setQuery(QueryBuilders.boolQuery()
//								.must(QueryBuilders.termQuery("_id", _id))
//								.must(QueryBuilders.rangeQuery("timestamp").lte(Calendar.getInstance().getTimeInMillis()))
//								.must(QueryBuilders.termsQuery("usercode","4","5")))
//						.execute().get();
//				System.out.println(response.getHits().getAt(0).getId());
			}
			
		}
		

		
	}
	
	 static void bulk(TransportClient esClient) throws Exception{
		BulkRequestBuilder builder = esClient.prepareBulk();
		for(int i=1;i<=20000;i++){
			String uid = "u"+i;
//			for(int k = 1;k<10;k++){
//				for(int j=k*100000;j<=k*100000+100000;j++){
				for(int j=1;j<=10;j++){
					builder.add(esClient.prepareIndex("my_index_1","default").setRouting(uid)
							.setSource(XContentFactory.jsonBuilder().startObject()
									.field("usercode",uid)
									.field("timestamp",j)
									.field("param","ios")
									.endObject())); 
				}
//			}
		}
		
		BulkResponse response = builder.execute().get();
					
		if(response.hasFailures()){
			System.out.println("err");
		}else{
			System.out.println(response.getItems());
		}
	}

	 static TransportClient initClient() {
//		Settings settings = Settings.settingsBuilder().put("cluster.name", "rx_es").build();
//		String[] a = { "172.20.2.1:9300","172.20.2.2:9300","172.20.2.3:9300" };
//		TransportAddress[] tas = new TransportAddress[a.length];
//		try {
//			for (int i = 0; i < a.length; i++) {
//				final String host = a[i];
//				final String[] b = host.split("\\:", 2);
//				if (b == null || b.length != 2) {
//					throw new RuntimeException("invliad host: " + host);
//				}
//
//				tas[i] = new InetSocketTransportAddress(InetAddress.getByName(b[0]), Integer.parseInt(b[1]));
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		TransportClient esClient = TransportClient.builder().settings(settings).build().addTransportAddresses(tas);
//		return esClient;
		
        TransportClient client = new PreBuiltTransportClient(
                Settings.builder().put("cluster.name", "rx_es").build());
        String[] a = { "127.0.0.1:9300","172.20.2.2:9300","172.20.2.3:9300" };
        for (String pair : a) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                try {
					client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(parts[0].trim()),
					        Integer.parseInt(parts[1])));
				} catch (Exception e){
					e.printStackTrace();
				}
            }
        }
        return client;
	}


}
