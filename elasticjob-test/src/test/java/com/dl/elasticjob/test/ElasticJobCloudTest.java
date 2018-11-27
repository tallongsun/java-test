package com.dl.elasticjob.test;

import com.dangdang.ddframe.job.cloud.api.JobBootstrap;

/**
 * 0.export MESOS_NATIVE_JAVA_LIBRARY=/usr/local/lib/libmesos.dylib
 * 1.启动mesos master
 *   ./bin/mesos-master.sh --ip=127.0.0.1 --work_dir=./mesos --zk=zk://localhost:2181/mesos --quorum=1
 * 2.启动mesos agent
 *   ./bin/mesos-agent.sh --work_dir=./mesos --master=zk://localhost:2181/mesos
 * 3.启动scheduler
 *   elastic-job-cloud-scheduler-2.1.0项目，bin/start.sh 
 * 4.启动一个http server，将elastic-job-example-cloud项目打包后.tar.gz文件发布到http server
 * 5.登录scheduler控制台添加app和job
 *   访问http://localhost:8899/，添加app和job
 * 6.mesos-1.2.0/build/mesos/slaves/目录下看job的执行日志
 * 
 * @author tallong
 *
 */
public class ElasticJobCloudTest {

	public static void main(String[] args) {
		JobBootstrap.execute();
	}	

}
