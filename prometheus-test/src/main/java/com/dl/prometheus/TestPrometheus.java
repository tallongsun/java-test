package com.dl.prometheus;

import java.util.Random;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

public class TestPrometheus {
	//requests_total{key="test0",} 3.0
	//requests_total{key="test1",} 0.0
	private static final Counter requests = Counter.build().labelNames("key").name("requests_total").help("Total requests.").register();
	//inprogress_requests 0.0
	private static final Gauge inprogressRequests = Gauge.build().name("inprogress_requests")
			.help("Inprogress requests.").register();
	//requests_size_bytes_count 3.0
	//requests_size_bytes_sum 936.0
	//requests_size_bytes{quantile="0.5",} 186.0
	//requests_size_bytes{quantile="0.9",} 295.0
	private static final Summary receivedBytes = Summary.build().quantile(0.5, 0.05).quantile(0.9, 0.01)
			.name("requests_size_bytes").help("Request size in bytes.").register();
	//The default buckets are intended to cover a typical web/rpc request from milliseconds to seconds. They can be overridden with the buckets() method 
	/*
	requests_latency_seconds_count 3.0
	requests_latency_seconds_sum 4.35369E-4
	requests_latency_seconds_bucket{le="0.005",} 3.0
	requests_latency_seconds_bucket{le="0.01",} 3.0
	requests_latency_seconds_bucket{le="0.025",} 3.0
	requests_latency_seconds_bucket{le="0.05",} 3.0
	requests_latency_seconds_bucket{le="0.075",} 3.0
	requests_latency_seconds_bucket{le="0.1",} 3.0
	requests_latency_seconds_bucket{le="0.25",} 3.0
	requests_latency_seconds_bucket{le="0.5",} 3.0
	requests_latency_seconds_bucket{le="0.75",} 3.0
	requests_latency_seconds_bucket{le="1.0",} 3.0
	requests_latency_seconds_bucket{le="2.5",} 3.0
	requests_latency_seconds_bucket{le="5.0",} 3.0
	requests_latency_seconds_bucket{le="7.5",} 3.0
	requests_latency_seconds_bucket{le="10.0",} 3.0
	requests_latency_seconds_bucket{le="+Inf",} 3.0
	 */
	private static final Histogram requestLatency = Histogram.build()
			.name("requests_latency_seconds").help("Request latency in seconds.").register();
	
	
	private static final Random rand = new Random();

	public static void main(String[] args) throws Exception {
		TestPrometheus p = new TestPrometheus();

		//collect gc,mem,jmx,classloading,thread
		//DefaultExports.initialize();
		
		//other available collectors:log,guava cache,hibernate,jetty,servlet,spring aop
		
		//http://localhost:1234/metrics
		HTTPServer server = new HTTPServer(1234);
		
		while (true) {
			p.processRequest();
			System.out.println(requests.labels("test0").get());
			System.out.println(requests.labels("test1").get());
			System.out.println(inprogressRequests.get());
			System.out.println(receivedBytes.get().sum);
			System.out.println(receivedBytes.get().quantiles);
			Thread.sleep(5000);

		}

	}

	public void processRequest() {
		requests.labels("test"+rand.nextInt(2)).inc();

		inprogressRequests.inc();
		Histogram.Timer requestTimer = requestLatency.startTimer();
		System.out.println("process...");
		receivedBytes.observe(rand.nextInt(1024));
		requestTimer.observeDuration();
		inprogressRequests.dec();
		
		//The Pushgateway allows ephemeral and batch jobs to expose their metrics to Prometheus.

	}

}
