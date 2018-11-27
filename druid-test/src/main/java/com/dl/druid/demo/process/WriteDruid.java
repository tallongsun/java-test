package com.dl.druid.demo.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dl.druid.TranquilityTest;
import com.metamx.tranquility.config.DataSourceConfig;
import com.metamx.tranquility.config.PropertiesBasedConfig;
import com.metamx.tranquility.config.TranquilityConfig;
import com.metamx.tranquility.druid.DruidBeams;
import com.metamx.tranquility.tranquilizer.MessageDroppedException;
import com.metamx.tranquility.tranquilizer.Tranquilizer;
import com.twitter.util.FutureEventListener;

import scala.runtime.BoxedUnit;

public class WriteDruid implements IProcess {
	private static Logger log = LoggerFactory.getLogger(TranquilityTest.class);
	
	private Tranquilizer<Map<String, Object>> sender;
	
	public WriteDruid(String datasource){
	    final TranquilityConfig<PropertiesBasedConfig> config = TranquilityConfig.read(
	    		TranquilityTest.class.getClassLoader().getResourceAsStream("demo.json"));
	    
	    final DataSourceConfig<PropertiesBasedConfig> wikipediaConfig = config.getDataSource(datasource);
	    sender = DruidBeams.fromConfig(
	    		wikipediaConfig).buildTranquilizer(wikipediaConfig.tranquilizerBuilder());
	    sender.start();
	}
	
	
	@Override
	public void apply(List<HashMap<String, Object>> data) {
		data.clear();
		HashMap<String,Object> map = new HashMap<>();
		map.put("city", "x");
		map.put("PaymentAmount", 2);
		map.put("timestamp",new DateTime().toString());
		data.add(map);
		//TODO:配置多长时间发送一次
        for(HashMap<String, Object> obj:data){
            sender.send(obj).addEventListener(
                    new FutureEventListener<BoxedUnit>()
                    {
    	                @Override
    	                public void onSuccess(BoxedUnit value)
    	                {
    	                  log.info("Sent message: %s", obj);
    	                  System.out.println(obj);
    	                }

    	                @Override
    	                public void onFailure(Throwable e)
    	                {
    	                  if (e instanceof MessageDroppedException) {
    	                    log.warn(String.format("Dropped message: %s", obj),e);
    	                  } else {
    	                    log.error(String.format("Failed to send message: %s", obj),e);
    	                  }
    	                  e.printStackTrace();
    	                }
                    }
            );
        }
        sender.flush();
	}

}
