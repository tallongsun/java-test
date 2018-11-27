package com.dl.kylin;

//hadoop 2.7.2
//hive 2.0.1
//hbase 1.1.5
//kylin 1.6.0
//kylin把hive的数据处理后存入hbase，跟druid类似，但性能应该会差一些
public class KylinTest {

	/**
	 * 1.启动hdfs
	 * 2.配置~/.bash_profile 
			export HIVE_HOME=/usr/local/Cellar/hive/2.0.1/libexec/
			export KYLIN_HOME=/Users/tallong/Downloads/apache-kylin-1.6.0-hbase1.x-bin
			export HIVE_CONF=$HIVE_HOME/conf
			export HCAT_HOME=$HIVE_HOME/hcatalog
		注释掉hadoop-evn.sh中的capacity-scheduler相关内容
	 * 3.启动hbase 
	 * 4.bin/kylin.sh start
	 *   tail -f log/kylin.out
	 * 5.http://127.0.0.1:7070/kylin
	 *   (ADMIN/KYLIN)
	 *   
	 * TODO:参考http://kylin.apache.org/docs16/tutorial/kylin_sample.html，跑个例子具体看看
	 *   用不用起个hiveserver2？
	 *   hive和kylin目录下都有个metastore_db目录，"schematool -dbType derby -initSchema"会冲突？
	 *   
	 */
	public static void main(String[] args) {

	}

}
