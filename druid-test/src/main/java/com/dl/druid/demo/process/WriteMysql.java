package com.dl.druid.demo.process;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class WriteMysql implements IProcess {
	public static Connection conn = null;
	public static PreparedStatement pstmt = null;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00.000'+08:00'");

	public WriteMysql(String ds) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://hdfs-secondary-namenode:8066/mydb", "mycat", "123456");
		pstmt = conn.prepareStatement(
				"INSERT INTO event_mysql(timestamp,os_version,city,province,model,os,wifi,screen_width,screen_height,app_version,manufacturer,"
						+ "country,event,channel,keyword,username,gender,birthday,registerchannel,productname,producttype,shopname,"
						+ "allowancetype,paymentmethod,cancelreason,canceltiming,supplymethod,servicecontent,servicestatus,"
						+ "shipprice_level,producttotalprice_level,ordertotalprice_level,paymentamount_level,shipprice,productunitprice,"
						+ "productamount,producttotalprice,ordertotalprice,allowanceamount,paymentamount,supplytime) "
						+ ""
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

	}

	@Override
	public void apply(List<HashMap<String, Object>> data) {
		try {
			for (HashMap<String, Object> obj : data) {
				pstmt.setDate(1, new java.sql.Date(sdf.parse((String) obj.get("timestamp")).getTime()));
				pstmt.setString(2, (String) obj.get("os_version"));
				pstmt.setInt(41, obj.get("SupplyTime") == null ? 0 : (int) obj.get("SupplyTime"));
				pstmt.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
