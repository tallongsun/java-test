package com.dl.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.sql.Date;

public class JdbcTest {

	//5000万(2.3GB)数据测试
	/*
CREATE TABLE `event` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `dimension1` int(11) DEFAULT NULL,
  `metric1` int(11) DEFAULT NULL,
  `ts` datetime DEFAULT NULL,
  `event` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=260846136 DEFAULT CHARSET=utf8;
	 */
	public static void main(String[] args)  throws Exception{
		
		boolean isVertica = true;
		if(isVertica){
			//insert1 20m
			//count 0.1s
			//group 0.65s select dimension1,date(ts),sum(metric1) from event where event='buy' group by dimension1,date(ts)
			Class.forName("com.vertica.jdbc.Driver");
		}else{
			//insert1 5m
			//count 6.5s
			//group 65s select dimension1,date_format(ts,'%Y-%m-%d'),sum(metric1) from event where event='buy' group by dimension1,date_format(ts,'%Y-%m-%d')
			Class.forName("com.mysql.jdbc.Driver");
		}
		
		int threadNum = 1;
		int totalDays = 1000;
		int daysPerThread = totalDays/threadNum;
		
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);

		long start = System.currentTimeMillis();
		List<Future<?>> futures = new ArrayList<>();
		for(int i=0;i<threadNum;i++){
			Future<?> f = executor.submit(new InsertTask(i*daysPerThread,(i+1)*daysPerThread,isVertica));
			futures.add(f);
		}
		
		for(Future<?> f : futures){
			f.get();
		}
		
		System.out.println("total:"+(System.currentTimeMillis()-start));

		executor.shutdown();
	}
	
	public static class InsertTask implements Runnable{
		private Random r;
		private int begin;
		private int end;
		private boolean isVertica;
		
		public InsertTask(int begin,int end,boolean isVertica){
			this.begin = begin;
			this.end = end;
			this.r = new Random();
			this.isVertica = isVertica;
		}
		@Override
		public void run() {
			Connection conn = null; 
			PreparedStatement pstmt = null;
			try{
				if(isVertica){
					conn = DriverManager.getConnection("jdbc:vertica://172.20.2.245:5433/CubeDbs",
							"dbadmin","P@ssw0rd");
				}else{
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bas_test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true",
							"root","123456");
				}

				conn.setAutoCommit(false);
				
				int columnCount = 100;
				
				if(isVertica){
					StringBuilder sb = new StringBuilder();
					sb.append("insert into sx_test.event(");
					for(int i=1;i<=columnCount/2;i++){
						sb.append("dimension"+i+",metric"+i+",");
					}
					sb.append("ts,event) values (");
					for(int i=1;i<=columnCount;i++){
						sb.append("?,");
					}
					sb.append("?,?)");
					pstmt = conn.prepareStatement(sb.toString());
				}else{
					pstmt = conn.prepareStatement("insert into event(dimension1,metric1,ts,event) values (?,?,?,?)");
				}
				
				
				for(int i=begin;i<end;i++){
					long start1 = System.currentTimeMillis();
					for(int j=0;j<50000;j++){
						int random = r.nextInt(10);
						for(int k=1;k<=columnCount;k+=2){
							pstmt.setInt(k, random);
							pstmt.setInt(k+1, 1);
						}
						pstmt.setDate(columnCount+1, new Date(0+i*24*3600*1000L));
						pstmt.setString(columnCount+2, "buy");
						pstmt.addBatch();
					}
					pstmt.executeBatch();
					conn.commit();
					
					System.out.println(System.currentTimeMillis()-start1);
				}
				conn.commit();
			}catch(Exception e){
				e.printStackTrace();
				try {
					if(conn!=null){
						conn.rollback();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}finally{
				try {
					if(pstmt!=null){
						pstmt.close();
					}
					if(conn!=null){
						conn.close();
					}
				} catch (SQLException e) {
				}
			}
		}
		
	}

}
