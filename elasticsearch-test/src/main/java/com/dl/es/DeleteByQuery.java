package com.dl.es;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class DeleteByQuery {
	private static String CLUSTER_NAME = "my-application";
	private static String HOST_NAME = "10.200.10.36:9300";
	
	private static String INDEX_NAME = "agent-data";
	private static String TYPE_NAME = "Span";

	private static String SYSTEM_ID = "10001";

	public static void main(String[] args) throws Exception{
		if(args.length==1){
			SYSTEM_ID = args[0];
			System.out.println(SYSTEM_ID);
		}
		
		
		TransportClient esClient = initClient();
		

		delete_by_query(esClient);

		
		esClient.close();
	}
	
	
	
	 static void delete_by_query(TransportClient esClient) throws Exception{
		
		//multi index & type
		SearchResponse response = null;
		
		//scroll
		response = esClient.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME)
			.setSearchType(SearchType.DEFAULT)
			.setScroll(new TimeValue(60000))
			.setQuery(QueryBuilders.termQuery("systemId", SYSTEM_ID))
			.setSize(100).execute().get();
		while(true){
			System.out.println("---"+response.getHits().getHits().length);
			response = esClient.prepareSearchScroll(response.getScrollId())
					.setScroll(new TimeValue(60000))
					.execute().get();
			if(response.getHits().getHits().length == 0){
				break;
			}
			List<String> ids = new ArrayList<>();
			for (final SearchHit hit : response.getHits().getHits()) {
				ids.add(hit.getId());
			}
			bulk(esClient,ids);
		}
		

		

	}
	
	
	 static void bulk(TransportClient esClient,List<String> ids) throws Exception{
		 BulkRequestBuilder bulk = esClient.prepareBulk();
		 for(String id : ids){
			 bulk.add(esClient.prepareDelete(INDEX_NAME, TYPE_NAME,id));
		 }

		BulkResponse response = bulk.execute().get();
		if(response.hasFailures()){
			System.out.println("err");
		}else{
			System.out.println(response.getItems());
		}
	}

	 static TransportClient initClient() {
	        TransportClient client = new PreBuiltTransportClient(
	                Settings.builder().put("cluster.name", "rx_es").build());
	        String[] a = { "172.20.2.1:9300","172.20.2.2:9300","172.20.2.3:9300" };
	        for (String pair : a) {
	            String[] parts = pair.split(",");
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
