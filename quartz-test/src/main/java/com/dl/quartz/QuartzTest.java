package com.dl.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTest {

	public static void main(String[] args)  throws Exception {
		// Grab the Scheduler instance from the Factory
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		// and start it off
		scheduler.start();

		// define the job and tie it to our MyJob class
		JobDetail job = JobBuilder.newJob(MyJob.class).withIdentity("job1", "group1").build();
		job.getJobDataMap().put("k1", "v1");

//		// Trigger the job to run now, and then repeat every 40 seconds
//		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startNow()
//				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();
		
		// .withMisfireHandlingInstructionFireAndProceed().withMisfireHandlingInstructionDoNothing().withMisfireHandlingInstructionIgnoreMisfires()
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("t1", "g1").startNow().withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?")
				.withMisfireHandlingInstructionDoNothing()).build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(job, trigger);
		
//		JobDetail job2 = JobBuilder.newJob(MyJob.class).withIdentity("job2", "group1").build();
//		job2.getJobDataMap().put("k2", "v2");
//		
//		// Trigger the job to run now, and then repeat every 40 seconds
//		Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("trigger2", "group1").startNow()
//				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();
//		scheduler.scheduleJob(job2,trigger2);
		
		Thread.sleep(600000);
		scheduler.shutdown();
	}
	@DisallowConcurrentExecution
	public static class MyJob implements org.quartz.Job {

		public MyJob() {
		}

		private static int count;
		public void execute(JobExecutionContext context) throws JobExecutionException {
//			for(String k : context.getJobDetail().getJobDataMap().keySet()){
//				System.out.println(k);
//			}
			count++;
			if(count==5){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.err.println("Hello World!  MyJob is executing."+count);
		}
	}
}
