package com.dl.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

public class MyElasticJob implements SimpleJob {

	@Override
	public void execute(ShardingContext context) {
		switch (context.getShardingItem()) {
		case 0:
			// do something by sharding item 0
			System.out.println(String.format("do sth. by sharding %s=%s",context.getShardingItem(),context.getShardingParameter()));
			break;
		case 1:
			// do something by sharding item 1
			System.out.println(String.format("do sth. by sharding %s=%s",context.getShardingItem(),context.getShardingParameter()));
			break;
		case 2:
			// do something by sharding item 2
			System.out.println(String.format("do sth. by sharding %s=%s",context.getShardingItem(),context.getShardingParameter()));
			break;
		// case n: ...
		}
	}

}
