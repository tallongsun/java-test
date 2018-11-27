package com.dl.druid.query;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yahoo.memory.NativeMemory;
import com.yahoo.sketches.theta.Sketch;
import com.yahoo.sketches.theta.Sketches;

public class QueryTest {
	private static final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
	
	private static final String url = "http://localhost:8082/druid/v2/?pretty";
	
	public static void main(String[] args) throws Exception{
		
		String query = loadQuery("groupby.json");
		
		System.out.println(query);
		
		long start = System.currentTimeMillis();
		String result = executeQuery(url,query);
		System.out.println((System.currentTimeMillis()-start));
	
		System.out.println(result);
		
//		parseResult(result);
		
		asyncHttpClient.close();
	}
	
	private static void parseResult(String result){
        if (StringUtils.isNotEmpty(result)) {
        	JSONArray jsonAry = JSONObject.parseArray(result);
            if (jsonAry != null && jsonAry.size() > 0) {
                JSONObject jsonObj = JSONObject.parseObject(jsonAry.get(0).toString());
                if(jsonObj!=null){
                	JSONObject subJsonObj = JSONObject.parseObject(jsonObj.getString("result"));
                	if(subJsonObj!=null){
                		System.out.println(subJsonObj.getLongValue("count"));
                		System.out.println(subJsonObj.getString("unique-views"));
                        Sketch sketch = Sketches.wrapSketch(new NativeMemory(subJsonObj.getBytes("unique-views")));
                        System.out.println((int)sketch.getEstimate());
                	}
                }
            }
         }
		
	}
	
	private static String loadQuery(String queryFile) throws Exception{
		String query = FileUtils.readFileToString(new File("query/"+queryFile));
		return query;
	}
	
	private static String executeQuery(String url,String query) {
		List<ListenableFuture<String>> futures = new ArrayList<>();
        String result = null;
        try {
        	for(int i=0;i<100;i++){
	            ListenableFuture<String> future = asyncHttpClient.preparePost(url)
	                    .addHeader("content-type", "application/json")
	                    .setBody(query.getBytes("UTF-8")).execute(new AsyncCompletionHandler<String>() {
	                        @Override
	                        public String onCompleted(Response response) throws Exception {
	                            return response.getResponseBody(Charset.forName("UTF-8"));
	                        }
	
	                        @Override
	                        public void onThrowable(Throwable t) {
	                        }
	                    });
	            futures.add(future);
        	}
        	futures.forEach(f -> {
				try {
					f.get();
				} catch (Exception e) {
				}
			});
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return result;
    }
}
