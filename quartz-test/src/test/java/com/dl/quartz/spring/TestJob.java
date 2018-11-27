package com.dl.quartz.spring;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class TestJob extends QuartzJobBean{
	private static int count;
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		count++;
		if(count ==5){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("execute Internal "+count);
	}

}
