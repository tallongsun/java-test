package com.dl.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class JdbcTestSimple {
	public static void main(String[] args) throws Exception {
//		System.setProperty("socksProxyHost", "127.0.0.1");
//      System.setProperty("socksProxyPort", "8889");
        
        Properties prop = new Properties();
        prop.put("socksProxyHost","127.0.0.1"); 
        prop.put("socksProxyPort","8889"); 
        prop.put("user",	 "root");
        prop.put("password", "123456");
        
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bas_test",
				prop);
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from event");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + " " + rs.getString("event"));
        }
        
        rs.close();
        stmt.close();
        conn.close();
	}
}
