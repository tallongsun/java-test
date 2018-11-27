package com.dl.presto;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.facebook.presto.client.ClientSession;
import com.facebook.presto.client.Column;
import com.facebook.presto.client.QueryError;
import com.facebook.presto.client.QueryResults;
import com.facebook.presto.client.StatementClient;
import com.google.common.collect.Lists;

import io.airlift.http.client.HttpClientConfig;
import io.airlift.http.client.jetty.JettyHttpClient;
import io.airlift.json.JsonCodec;
import io.airlift.units.Duration;

/**
 * [服务器（jmx）]
 * 1.配置
 * etc/node.properties
	node.environment=production
	node.id=ffffffff-ffff-ffff-ffff-ffffffffffff
	node.data-dir=/var/presto/data
 * etc/jvm.config
	-server
	-Xmx1G
	-XX:+UseG1GC
	-XX:G1HeapRegionSize=32M
	-XX:+UseGCOverheadLimit
	-XX:+ExplicitGCInvokesConcurrent
	-XX:+HeapDumpOnOutOfMemoryError
	-XX:OnOutOfMemoryError=kill -9 %p
 * etc/config.properties
	coordinator=true
	node-scheduler.include-coordinator=true
	http-server.http.port=8080
	query.max-memory=512MB
	query.max-memory-per-node=128MB
	discovery-server.enabled=true
	discovery.uri=http://example.net:8080
 * etc/log.properties
 	com.facebook.presto=INFO
 * etc/catalog/jmx.properties
 	connector.name=jmx
 * 
 * 2.启动服务器
 *  sudo bin/launcher run 
 * 
 * 3.启动客户端
 *  ./presto --server localhost:8080 --catalog jmx
 *  show shemas;
 *  use current;
 *  
 * 每增加一种connector，需要在etc/catalog/下增加相应的属性文件
 * 目前presto支持很多datasource，比如hive，mysql，postgreSQL，mongodb，redis，kafka，cassandra，jmx
 * 
 * [客户端（hive）]
 * 1.命令行
 * mv presto-cli-0.161-executable.jar presto
 * chmod a+x presto
 * ./presto --server 10.200.32.95:8285 --catalog hive --schema default
 * show tables;
 * 
 * 2.web
 * yanagishima是一个很不错的presto web平台，（catalog|schema|table），schema就相当于database了
 * 
 * 3.程序
 * 如下
 * 
 */
public class PrestoTest {

	public static void main(String[] args) throws Exception{
        PrestoModel config = new PrestoModel();
        config.prestoCoordinatorServer = "http://10.200.32.95:8285";
        config.prestoRedirectServer = "http://10.200.32.95:8285";
        config.catalog = "hive";
        config.schema = "default";
        config.userName = "rongpal";
        config.password = "";
        config.source = "";
        config.query = "select * from wechat_member limit 10";
        
        ClientSession clientSession = new ClientSession(URI.create(config.prestoCoordinatorServer),
                config.userName, config.source, config.catalog, config.schema,
                TimeZone.getDefault().getID(), Locale.getDefault(), new HashMap<>(), null, false, new Duration(2, TimeUnit.MINUTES));
        
        HttpClientConfig httpClientConfig = new HttpClientConfig().setConnectTimeout(new Duration(10, TimeUnit.SECONDS));
        JettyHttpClient httpClient = new JettyHttpClient(httpClientConfig);
        JsonCodec<QueryResults> jsonCodec = JsonCodec.jsonCodec(QueryResults.class);
        
		try(StatementClient client = new StatementClient(httpClient, jsonCodec, clientSession, config.query)){
            while (client.isValid() && (client.current().getData() == null)) {
                client.advance();
            }
            if (client.isClosed()){
            	throw new RuntimeException("Query aborted by user");
            }
            if (client.isGone()) {
                throw new RuntimeException("Query is gone (server restarted?)");
            }
            if (client.isFailed()) {
                QueryResults results = client.finalResults();
                String queryId = results.getId();
                QueryError error = results.getError();
                String message = String.format("query id: %s Query failed (#%s): %s", queryId, results.getId(), error.getMessage());
                System.err.println(message);
                return;
            }
            PrestoResultModel prestoResultModel = new PrestoResultModel();
            QueryResults results = client.isValid() ? client.current() : client.finalResults();
            String queryId = results.getId();
            if (results.getUpdateType() != null) {
                prestoResultModel.query = config.query;
                prestoResultModel.queryId = queryId;
                prestoResultModel.updateType = results.getUpdateType();
                System.out.println(prestoResultModel);
                return;
            }
            
            if (results.getColumns() == null) {
                throw new NullPointerException(String.format("Query %s has no columns\n", results.getId()));
            } 
            
            prestoResultModel.queryId = queryId;
            prestoResultModel.updateType = results.getUpdateType();
            List<String> columns = Lists.transform(results.getColumns(), Column::getName);
            prestoResultModel.columns = columns;
            List<List<String>> rowDataList = new ArrayList<>();
            
            while (client.isValid()) {
                Iterable<List<Object>> data = client.current().getData();
                if (data != null) {
                    for (List<Object> row : data) {
                        List<String> columnDataList = new ArrayList<>();
                        List<Object> tmpColumnDataList = row.stream().collect(Collectors.toList());
                        for (Object tmpColumnData : tmpColumnDataList) {
                            columnDataList.add(tmpColumnData.toString());
                        }
                        rowDataList.add(columnDataList);
                    }
                }
                client.advance();
            }
            
            prestoResultModel.records = rowDataList;
            prestoResultModel.lineNumber = rowDataList.size();
            System.out.println(prestoResultModel);
		}
	}
	
	public static class PrestoModel{
	    public String userName; // 用户名
	    public String password; // 用户密码
	    public String prestoCoordinatorServer;
	    public String prestoRedirectServer;
	    public String catalog;
	    public String schema;
	    public String source;
	    public String query;
	    
		@Override
		public String toString() {
			return "PrestoModel [userName=" + userName + ", password=" + password + ", prestoCoordinatorServer="
					+ prestoCoordinatorServer + ", prestoRedirectServer=" + prestoRedirectServer + ", catalog="
					+ catalog + ", schema=" + schema + ", source=" + source + ", query=" + query + "]";
		}
	    
	    

	}
	
	public static class PrestoResultModel {

		public String query;
		public String queryId;
		public List<String> columns;
		public List<List<String>> records;
		public int lineNumber;
		public String updateType;
		
		@Override
		public String toString() {
			return "PrestoResultModel [query=" + query + ", queryId=" + queryId + ", columns=" + columns + ", records="
					+ records + ", lineNumber=" + lineNumber + ", updateType=" + updateType + "]";
		}

	}

}
