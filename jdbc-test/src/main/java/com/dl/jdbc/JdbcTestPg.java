package com.dl.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;

public class JdbcTestPg {

	public static void main(String[] args) throws Exception{
        Properties prop = new Properties();
        prop.put("user",	 "gpadmin");
        prop.put("password", "123456");
        
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://10.205.16.196:5432/poc",
				prop);
        conn.setAutoCommit(false);
        
//        //1.query
//        Statement stmt = conn.createStatement();
//        // Turn use of the cursor on.
//        stmt.setFetchSize(5);
//        ResultSet rs = stmt.executeQuery("select * from ontime limit 10");
//        while (rs.next()) {
//            System.out.println(rs.getString("origin"));
//        }
//        rs.close();
//        stmt.close();
        
//		//2.update
//		PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ontime WHERE origin = ?");
//		pstmt.setString(1, "unknown");
//		int rowsDeleted = pstmt.executeUpdate();
//		System.out.println(rowsDeleted + " rows deleted");
//		pstmt.close();
        
//        //3.ddl
//        Statement st = conn.createStatement();
//        st.execute("DROP TABLE  if exists mytable");
//        st.close();
//        
        //4.Stored Function
//        CallableStatement upperProc = conn.prepareCall("{ ? = call upper( ? ) }");
//        upperProc.registerOutParameter(1, Types.VARCHAR);
//        upperProc.setString(2, "lowercase to uppercase");
//        upperProc.execute();
//        System.out.println(upperProc.getString(1));
//        upperProc.close();
//        
//        //return results as a set：set fetch size work
//        Statement setstmt = conn.createStatement();
//        setstmt.execute("CREATE OR REPLACE FUNCTION setoffunc() RETURNS SETOF int AS "
//        		+ "' SELECT 1 UNION SELECT 2;' LANGUAGE sql");
//        ResultSet setrs = setstmt.executeQuery("SELECT * FROM setoffunc()");
//        while (setrs.next()) {
//            System.out.println(setrs.getInt(1));
//        }
//        setrs.close();
//        setstmt.close();
//        
//        //return results as a refcursor：：set fetch size not work
//        Statement refstmt = conn.createStatement();
//        refstmt.execute("CREATE OR REPLACE FUNCTION refcursorfunc() RETURNS refcursor AS '"
//        		+ " DECLARE "
//        		+ "    mycurs refcursor; "
//        		+ " BEGIN "
//        		+ "    OPEN mycurs FOR SELECT 1 UNION SELECT 2; "
//        		+ "    RETURN mycurs; "
//        		+ " END;' language plpgsql");
//        refstmt.close();
//        CallableStatement proc = conn.prepareCall("{ ? = call refcursorfunc() }");
//        proc.registerOutParameter(1, Types.OTHER);
//        proc.execute();
//        ResultSet results = (ResultSet) proc.getObject(1);
//        while (results.next()) {
//        		System.out.println(results.getInt(1));
//        }
//        results.close();
//        proc.close();
        
        //5.binary data
        //CREATE TABLE images (imgname text, img bytea);
        File file = new File("myimage.jpg");
        FileInputStream fis = new FileInputStream(file);
        PreparedStatement ps = conn.prepareStatement("INSERT INTO images VALUES (?, ?)");
        ps.setString(1, file.getName());
        ps.setBinaryStream(2, fis, (int)file.length());
        ps.executeUpdate();
        ps.close();
        fis.close();
        
        ps = conn.prepareStatement("SELECT img FROM images WHERE imgname = ?");
        ps.setString(1, "myimage.jpg");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            byte[] imgBytes = rs.getBytes(1);
            System.out.println(imgBytes.length);
        }
        rs.close();
        ps.close();
        
        
        conn.commit();
        conn.close();

	}

}
