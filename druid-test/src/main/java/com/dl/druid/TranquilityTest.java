package com.dl.druid;

import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.metamx.tranquility.config.DataSourceConfig;
import com.metamx.tranquility.config.PropertiesBasedConfig;
import com.metamx.tranquility.config.TranquilityConfig;
import com.metamx.tranquility.druid.DruidBeams;
import com.metamx.tranquility.tranquilizer.MessageDroppedException;
import com.metamx.tranquility.tranquilizer.Tranquilizer;
import com.twitter.util.FutureEventListener;

import scala.runtime.BoxedUnit;

/**
 * 因为druid和zk配置在别的机器上，连接zk后执行
 *   get /druid/discovery/druid:overlord/ec035a6f-c878-4178-a808-7b7a072136c8 
 *   发现druid:overlord的adrress是caas-ignite-1
 *   
 * 因此本机host需要加上10.200.32.68 caas-ignite-1
 *
 */
public class TranquilityTest {
	private static Logger log = LoggerFactory.getLogger(TranquilityTest.class);

	public static void main(String[] args) {
		log.debug("xx");
	    }

	public static void testTran() {
		final TranquilityConfig<PropertiesBasedConfig> config = TranquilityConfig.read(
	    		TranquilityTest.class.getClassLoader().getResourceAsStream("example.json"));
	    
	    final DataSourceConfig<PropertiesBasedConfig> wikipediaConfig = config.getDataSource("wikipedia");
	    final Tranquilizer<Map<String, Object>> sender = DruidBeams.fromConfig(
	    		wikipediaConfig).buildTranquilizer(wikipediaConfig.tranquilizerBuilder());

	    sender.start();
	    try {
	        // Send 10000 objects

	        for (int i = 0; i < 10000; i++) {
	          // Build a sample event to send; make sure we use a current date
	          final Map<String, Object> obj = ImmutableMap.<String, Object>of(
	              "timestamp", new DateTime().toString(),
	              "page", "foo",
	              "added", i
	          );

	          // Asynchronously send event to Druid:
	          sender.send(obj).addEventListener(
	              new FutureEventListener<BoxedUnit>()
	              {
	                @Override
	                public void onSuccess(BoxedUnit value)
	                {
	                  log.info("Sent message: %s", obj);
	                }

	                @Override
	                public void onFailure(Throwable e)
	                {
	                  if (e instanceof MessageDroppedException) {
	                    log.warn(String.format("Dropped message: %s", obj),e);
	                  } else {
	                    log.error(String.format("Failed to send message: %s", obj),e);
	                  }
	                }
	              }
	          );
	        }
	      }
	      finally {
	        sender.flush();
	        sender.stop();
	      }
	}

}
