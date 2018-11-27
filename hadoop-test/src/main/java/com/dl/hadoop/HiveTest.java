package com.dl.hadoop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

//hive 2.0.1
public class HiveTest {
	/**
	 * 1.vi ~/.bash_profile：export HIVE_HOME=/usr/local/Cellar/hive/2.0.1/libexec/
	 * 2./usr/local/Cellar/hadoop/2.7.2/libexec/etc/hadoop/core-site.xml
		 <property>
		      <name>hadoop.proxyuser.tallong.groups</name>
		      <value>*</value>
		 </property>
		 <property>
		      <name>hadoop.proxyuser.tallong.hosts</name>
		      <value>*</value>
		  </property>
	 * 3.重启hdfs
	 * 4.bin/schematool -dbType derby -initSchema
 		 bin/hiveserver2
	 * 5.bin/beeline -u jdbc:hive2://localhost:10000
	 * 
	 * TODO:默认metadata使用derby存储，改用mysql存储（配置hive-site.xml）
	 */
	public static void main(String[] args) throws Exception {
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "tallong", "");
		Statement stmt = con.createStatement();
		String tableName = "pokes";
		stmt.execute("drop table if exists " + tableName);
		stmt.execute("create table " + tableName + " (key int, value string)");
		System.out.println("Create table success!");
		
 
        // describe table
        String sql = "desc " + tableName;
        System.out.println("Running: " + sql);
        ResultSet res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(res.getString(1) + "\t" + res.getString(2));
        }
        
        // insert 
        sql = "insert into " + tableName + " values(1,'value1'),(2,'value2')";
        System.out.println("Running: " + sql);
        stmt.executeUpdate(sql);
 
        // select 
        sql = "select * from " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(String.valueOf(res.getInt(1)) + "\t"+ res.getString(2));
        }
 
	}

}
