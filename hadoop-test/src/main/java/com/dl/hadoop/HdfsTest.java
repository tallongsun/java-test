package com.dl.hadoop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.IOUtils;

import com.dl.hadoop.util.StreamUtils;

//hadoop 2.7.2
public class HdfsTest {
	/**
	 * 1.配置ssh免密钥：cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys(需要先打开系统偏好设置-共享-远程登录)
	 * 2.vi ~/.bash_profile：export HADOOP_HOME=/usr/local/Cellar/hadoop/2.7.2/libexec/
	 * 3./usr/local/Cellar/hadoop/2.7.2/libexec/etc/hadoop/core-site.xml
		<property>
	     <name>fs.default.name</name>                                     
	     <value>hdfs://localhost:9000</value>                             
	  	</property>
	  	 /usr/local/Cellar/hadoop/2.7.2/libexec/etc/hadoop/hdfs-site.xml 
	  	<property>
	     <name>dfs.replication</name>
	     <value>1</value>
	    </property>
	 * 4.bin/hdfs namenode -format
 		 sbin/start-dfs.sh
	 * 5.hadoop fs -ls / ,或者也可以访问http://localhost:50070/，查看hdfs文件的存储状况
	 * 6./usr/local/Cellar/hadoop/2.7.2/libexec/etc/hadoop/yarn-site.xml
		<property>  
         <name>yarn.nodemanager.aux-services</name>  
         <value>mapreduce_shuffle</value>  
		</property> 
	     /usr/local/Cellar/hadoop/2.7.2/libexec/etc/hadoop/mapred-site.xml
	 	<property>
         <name>mapreduce.framework.name</name>
         <value>yarn</value>
        </property>
		<property>
         <name>mapreduce.jobhistory.address</name>
         <value>localhost:10020</value>
        </property>
        <property>
         <name>mapreduce.jobhistory.webapp.address</name>
         <value>localhost:19888</value>
        </property>
	 * 7.sbin/start-yarn.sh (http://localhost:8088/)
	 * 8.sbin/mr-jobhistory-daemon.sh start historyserver (http://localhost:19888/)
	 * 
	 */
	public static void main(String[] args)  throws Exception{
		//init
		Configuration hdfsConf = new Configuration();
		System.setProperty("HADOOP_USER_NAME", "tallong");
		hdfsConf.set("fs.default.name", "hdfs://localhost:9000");
		FileSystem hdfs = FileSystem.get(hdfsConf);
		
		//write
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamUtils.transfer(new File(HdfsTest.class.getResource("/file/test.png").getFile()),bos);
		byte[] content = bos.toByteArray();
		int len = content.length;
		String category = "test-category";
		String key = UUID.randomUUID().toString().replaceAll("\\-", "");
		
		final Path dir = new Path("/test/" + category);
		if (!hdfs.exists(dir)) {
			hdfs.mkdirs(dir);
		}
		final Path path = new Path("/test/" + category + "/" + key);
		FSDataOutputStream os = hdfs.create(path, true);
		IOUtils.copyBytes(new ByteArrayInputStream(content), os, 4096);
		os.flush();
		IOUtils.closeStream(os);
		
		//list
		RemoteIterator<LocatedFileStatus> list = hdfs.listFiles(path, false);
		while(list.hasNext()){
			LocatedFileStatus lfs = list.next();
			System.out.println(lfs.getPath()+","+lfs.getLen()+","+lfs.isFile());
		}
		
		//load
		bos = new ByteArrayOutputStream();
		if (hdfs.exists(path)) {
			FSDataInputStream is = hdfs.open(path);
			IOUtils.copyBytes(is, bos, 4096);
			IOUtils.closeStream(is);
		}
		System.out.println(bos.toByteArray().length == len);
			
		//remove
		boolean flag = hdfs.delete(path, false);
		System.out.println(flag);
		
		//close
		hdfs.close();
	}

}
