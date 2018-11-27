package com.dl.httpclient;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.sun.deploy.net.proxy.BrowserProxyInfo;
import com.sun.deploy.net.proxy.ProxyInfo;
import com.sun.deploy.net.proxy.ProxyType;
import com.sun.deploy.net.proxy.SunAutoProxyHandler;

public class HttpclientTest {

	
	public static void main(String[] args) throws Exception{
		
		BrowserProxyInfo  b = new BrowserProxyInfo(); 
		b.setType(ProxyType.AUTO);
		b.setAutoConfigURL("file:///Users/tallong/JavaProjects/test/httpclient-test/proxy.pac");
		URL url = new URL("http://www.baidu.com");
		SunAutoProxyHandler handler = new SunAutoProxyHandler();
		handler.init(b);
		ProxyInfo[] ps = handler.getProxyInfo(url);
		
	    String proxyHost = "";  
	    int proxyPort = 0;  
		for (ProxyInfo p : ps) {
			if (p.isSocksUsed()) {
				proxyHost = p.getSocksProxy().trim();
				proxyPort = p.getSocksPort();
			}else if (p.isProxyUsed()) {
				proxyHost = p.getProxy().trim();
				proxyPort = p.getPort();
			}else {
				proxyHost = p.toString();
			}
			System.out.println(proxyHost+":"+proxyPort);
		}
	    Proxy httpProxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
	    HttpURLConnection c = (HttpURLConnection)url.openConnection(httpProxy);
        c.setDoOutput(true);  
        c.setDoInput(true);  
        c.setRequestMethod("GET");
        System.setProperty("sun.net.client.defaultConnectTimeout", "3000");  
        System.setProperty("sun.net.client.defaultReadTimeout", "3000");  
		c.connect();
		OutputStreamWriter out = new OutputStreamWriter(c.getOutputStream(), "UTF-8"); 
		out.flush();
		BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));  
        String line;  
        String result = "";  
        while ((line = in.readLine()) != null) {  
            result += line;  
        }  
		System.out.println(result);
		
		
//		CloseableHttpClient httpclient = HttpClients.createDefault();
		
//		CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(new AuthScope("127.0.0.1", 8888),
//                new UsernamePasswordCredentials("admin", "123456"));
//		CloseableHttpClient httpclient = HttpClientBuilder.create().setMaxConnTotal(1024).setMaxConnPerRoute(1024)
//				.setDefaultCredentialsProvider(credsProvider).build();
//		
//        RequestConfig config = RequestConfig.custom().setProxy(new HttpHost("127.0.0.1", 8888, "http")).build();
//		HttpGet httpGet = new HttpGet("https://www.baidu.com");
//		httpGet.setConfig(config);
//		
//		CloseableHttpResponse response1 = httpclient.execute(httpGet);
//		
//		try {
//		    System.out.println(response1.getStatusLine());
//		    HttpEntity entity1 = response1.getEntity();
//		    System.out.println(entity1);
//		    EntityUtils.consume(entity1);
//		    System.out.println(entity1);
//		} finally {
//		    response1.close();
//		}
		
//		HttpPost httpPost = new HttpPost("http://targethost/login");
//		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
//		nvps.add(new BasicNameValuePair("username", "vip"));
//		nvps.add(new BasicNameValuePair("password", "secret"));
//		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//		CloseableHttpResponse response2 = httpclient.execute(httpPost);
//
//		try {
//		    System.out.println(response2.getStatusLine());
//		    HttpEntity entity2 = response2.getEntity();
//		    System.out.println(entity2);
//		    EntityUtils.consume(entity2);
//		    System.out.println(entity2);
//		} finally {
//		    response2.close();
//		}
	}
}
