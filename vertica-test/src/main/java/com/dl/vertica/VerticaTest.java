package com.dl.vertica;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Properties;

import com.vertica.jdbc.VerticaConnection;
import com.vertica.jdbc.VerticaDayTimeInterval;
import com.vertica.jdbc.VerticaPreparedStatement;
import com.vertica.jdbc.VerticaYearMonthInterval;

public class VerticaTest {
	public static void main(String[] args) {
		try {
			Class.forName("com.vertica.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find the JDBC driver class.");
			e.printStackTrace();
			return; 
		}
		
        Properties myProp = new Properties();
        myProp.put("user", "dbadmin");
        myProp.put("password", "123456");
        myProp.put("loginTimeout", "35");
//        myProp.put("DirectBatchInsert", "true");//When loading large batches of data (more than 100MB or so), you should load the data directly into ROS containers
        myProp.put("binaryBatchInsert", "true");
        myProp.put("ConnSettings", "SET LOCALE TO en_GB");
//        myProp.put("ConnectionLoadBalance", 1);
        myProp.put("BackupServerNode", "47.96.37.161,47.96.37.161");
        Connection conn;
        for (int i=1; i <= 1; i++) {
        try {
        	System.out.print("Connect attempt #" + i + "...");
        	conn = DriverManager.getConnection(
			        "jdbc:vertica://47.96.37.161:5433/streamDB",myProp);
            System.out.println("Connected!");
            conn.setClientInfo("APPLICATIONNAME", "JDBC Client - Data Load");
            System.out.println("New Conn label: " + conn.getClientInfo("APPLICATIONNAME"));
            
            Statement stmt = conn.createStatement();
//            stmt.execute("SELECT SET_LOAD_BALANCE_POLICY('ROUNDROBIN');");
            ResultSet rs = stmt.executeQuery("SELECT node_name FROM v_monitor.current_session;");      
            rs.next();
            System.out.println("Connected to node " + rs.getString(1).trim());
            
            // time the connection was created.
            System.out.println("DirectBatchInsert state: "
                            + ((VerticaConnection) conn).getProperty(
                                            "DirectBatchInsert"));
            
            // Change it and show it again
            ((VerticaConnection) conn).setProperty("DirectBatchInsert", false);
            System.out.println("DirectBatchInsert state is now: " +
                             ((VerticaConnection) conn).getProperty(
                                             "DirectBatchInsert"));
            
            rs = stmt.executeQuery("SHOW LOCALE");
            System.out.print("Query reports that Locale is set to: ");
            while (rs.next()) {
                System.out.println(rs.getString(2).trim());
            }
            
            stmt.execute("SET LOCALE TO en_US");
            rs = stmt.executeQuery("SHOW LOCALE");
            System.out.print("Query now reports that Locale is set to: ");
            while (rs.next()) {
                System.out.println(rs.getString(2).trim());
            }
            
            System.out.println("Transaction Isolation Level: "
                            + conn.getTransactionIsolation());
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            System.out.println("Transaction Isolation Level: "
                            + conn.getTransactionIsolation());
            
            
            stmt.executeUpdate(
            		"DROP TABLE IF EXISTS test_all_types cascade");
            stmt.executeUpdate("CREATE TABLE test_all_types ("
                            + "c0 INTEGER, c1 TINYINT, c2 DECIMAL, "
                            + "c3 MONEY, c4 DOUBLE PRECISION, c5 REAL)");
            // Add a row of values to it.
            stmt.executeUpdate("INSERT INTO test_all_types VALUES("
                            + "111111111111, 444, 55555555555.5555, "
                            + "77777777.77,  88888888888888888.88, " 
                            + "10101010.10101010101010)");
            rs = stmt.executeQuery("SELECT * FROM test_all_types");
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
            	String[] vertTypes = new String[] {"INTEGER", "TINYINT",
            			 "DECIMAL", "MONEY", "DOUBLE PRECISION", "REAL"};
            	for (int x=1; x<7; x++) { 
            		System.out.println("\n\nColumn " + x + " (" + vertTypes[x-1]
            				+ ")");
            		System.out.println("\tgetColumnType()\t\t"
                            + md.getColumnType(x));
            		System.out.println("\tgetColumnTypeName()\t"
                            + md.getColumnTypeName(x));
                    System.out.println("\tgetShort()\t\t"
                            + rs.getShort(x)); 
                    System.out.println("\tgetLong()\t\t" + rs.getLong(x));
                    System.out.println("\tgetInt()\t\t" + rs.getInt(x));
                    System.out.println("\tgetByte()\t\t" + rs.getByte(x));                    
            	}
            }
            rs.close();
            stmt.executeUpdate("drop table test_all_types cascade");
            
            
            stmt.execute("DROP TABLE IF EXISTS interval_demo");
            stmt.executeUpdate("CREATE TABLE interval_demo("
                    + "DayInt INTERVAL DAY TO SECOND, "
                    + "MonthInt INTERVAL YEAR TO MONTH)");
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO interval_demo VALUES(?,?)");
            VerticaDayTimeInterval dayInt = new VerticaDayTimeInterval(10, 0,
                    5, 40, 0, 0, false);
            VerticaYearMonthInterval monthInt = new VerticaYearMonthInterval(
                    10, 6, false);
            dayInt.setHour(7);
            ((VerticaPreparedStatement) pstmt).setObject(1, dayInt);
            ((VerticaPreparedStatement) pstmt).setObject(2, monthInt);
            pstmt.addBatch();
            // Set day interval in "days HH:MM:SS" format
            pstmt.setString(1, "10 10:10:10");
            // Set year to month value in "MM-YY" format
            pstmt.setString(2, "12-09");
            pstmt.addBatch();
            try {
                pstmt.executeBatch();
            } catch (SQLException e) {
                System.out.println("Error message: " + e.getMessage());
            }
            
            rs = stmt.executeQuery("SELECT * FROM interval_demo");
            md = rs.getMetaData();
            while (rs.next()) {
                for (int x = 1; x <= md.getColumnCount(); x++) {
                    // Get data type from metadata
                    int colDataType = md.getColumnType(x);
                    // You can get the type in a string:
                    System.out.println("Column " + x + " is a "
                            + md.getColumnTypeName(x));
                    // Normally, you'd have a switch statement here to
                    // handle all sorts of column types, but this example is
                    // simplified to just handle database-specific types
                    if (colDataType == Types.OTHER) {
                        // Column contains a database-specific type. Determine
                        // what type of interval it is. Assuming it is an
                        // interval...
                        Object columnVal = rs.getObject(x);
                        if (columnVal instanceof VerticaDayTimeInterval) {
                            // We know it is a date time interval
                            VerticaDayTimeInterval interval = 
                                    (VerticaDayTimeInterval) columnVal;
                            // You can use the getters to access the interval's
                            // data
                            System.out.print("Column " + x + "'s value is ");
                            System.out.print(interval.getDay() + " Days ");
                            System.out.print(interval.getHour() + " Hours ");
                            System.out.println(interval.getMinute()
                                    + " Minutes");
                        } else if (columnVal instanceof VerticaYearMonthInterval) {
                            VerticaYearMonthInterval interval = 
                                    (VerticaYearMonthInterval) columnVal;
                            System.out.print("Column " + x + "'s value is ");
                            System.out.print(interval.getYear() + " Years ");
                            System.out.println(interval.getMonth() + " Months");
                        } else {
                            System.out.println("Not an interval.");
                        }
                    }
                }
            }
            
            stmt.execute("DROP TABLE IF EXISTS UUID_TEST CASCADE;");
            stmt.execute("CREATE TABLE UUID_TEST (id UUID, description VARCHAR(25));");
            PreparedStatement ps = conn.prepareStatement("INSERT INTO UUID_TEST VALUES(?,?)");
            java.util.UUID uuid;
            for (Integer x = 0; x < 10; x++) {
                uuid = java.util.UUID.randomUUID();
                ps.setObject(1, uuid);
                ps.setString(2, "UUID #" + x);
                ps.execute();
            }
            rs = stmt.executeQuery("SELECT * FROM UUID_TEST ORDER BY description ASC");
            while (rs.next()) {
                System.out.println(rs.getString(2) + " : " +  rs.getObject(1));
            }
            
            
            // VARCHAR (which is limited to 65000)
            int length = 100000;
            stmt.execute("DROP TABLE IF EXISTS longtable CASCADE");
            stmt.execute("CREATE TABLE longtable (text LONG VARCHAR(" + length 
                            + "))");
            StringBuilder sb = new StringBuilder(length);
            for (int x = 0; x < length; x++)
            {
                sb.append(x % 10);
            }
            String value = sb.toString();
            System.out.println("String value is " + value.length() + 
                    " characters long.");
            pstmt = conn.prepareStatement(
                    "INSERT INTO longtable (text)" +
                    " VALUES(?)");
            try {
                System.out.println("Inserting LONG VARCHAR value");
                pstmt.setString(1, value);
                pstmt.addBatch();
                pstmt.executeBatch();
                
                rs = stmt.executeQuery("SELECT * FROM longtable");
                
                ResultSetMetaData rsmd = rs.getMetaData();
                System.out.println("Column #1 data type is: " + 
                                rsmd.getColumnTypeName(1));
                if (rsmd.getColumnType(1) == Types.LONGVARCHAR) {
                    System.out.println("It is a LONG VARCHAR");
                } else {
                    System.out.println("It is NOT a LONG VARCHAR");
                }
                
                while (rs.next()) {
                    System.out.println("Returned string length: " + 
                                    rs.getString(1).length());
                }
            } catch (SQLException e) {
                System.out.println("Error message: " + e.getMessage());
                return; // Exit if there was an error
            }
            
            
            conn.setAutoCommit(false);
            stmt.execute("DROP TABLE IF EXISTS customers CASCADE");
            stmt.execute("CREATE TABLE customers (CustID int, Last_Name"
                            + " char(50), First_Name char(50),Email char(50), "
                            + "Phone_Number char(12))");
            String[] firstNames = new String[] { "Anna", "Bill", "Cindy",
                            "Don", "Eric" };
            String[] lastNames = new String[] { "Allen", "Brown", "Chu",
                            "Dodd", "Estavez" };
            String[] emails = new String[] { "aang@example.com",
                            "b.brown@example.com", "cindy@example.com",
                            "d.d@example.com", "e.estavez@example.com" };
            String[] phoneNumbers = new String[] { "123-456-7890",
                            "555-444-3333", "555-867-53093453453",
                            "555-555-1212", "781-555-0000" };
            pstmt = conn.prepareStatement(
                            "INSERT INTO customers (CustID, Last_Name, " + 
                            "First_Name, Email, Phone_Number)" +
                            " VALUES(?,?,?,?,?)");
            for (int x = 0; x < firstNames.length; x++) {
                pstmt.setInt(1, x + 1);
                pstmt.setString(2, lastNames[x]);
                pstmt.setString(3, firstNames[x]);
                pstmt.setString(4, emails[x]);
                pstmt.setString(5, phoneNumbers[x]);
                pstmt.addBatch();
            }
	     
            int[] batchResults = null;
            try {
            	batchResults = pstmt.executeBatch();
            } catch (BatchUpdateException e) {
            	System.out.println("Error message: " + e.getMessage());
            	batchResults = e.getUpdateCounts();
            	System.out.println("Rolling back batch insertion");
            	conn.rollback();
            }catch (SQLException e) {
                System.out.println("Error message: " + e.getMessage());
                return; // Exit if there was an error
            }
            // Commit the transaction to close the COPY command
            conn.commit();
            
            System.out.println("Return value from inserting batch: "
                    + Arrays.toString(batchResults));
            rs = stmt.executeQuery("SELECT CustID, First_Name, "
                            + "Last_Name FROM customers ORDER BY CustID");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " - "
                                + rs.getString(2).trim() + " "
                                + rs.getString(3).trim());
            }
            
            
            boolean result = stmt.execute("COPY customers FROM LOCAL "
                    + " '/Users/tallong/JavaProjects/test/vertica-test/customers.txt' DIRECT ENFORCELENGTH");
            if (result) {
                System.out.println("Got result set");
            } else {
                System.out.println("Got count");
                int rowCount = stmt.getUpdateCount();
                System.out.println("Number of accepted rows = " + rowCount);
            }
            
            
            conn.close();
        } catch (SQLTransientConnectionException connException) {
            System.out.print("Network connection issue: ");
            System.out.print(connException.getMessage());
            System.out.println(" Try again later!");
            return;
        } catch (SQLInvalidAuthorizationSpecException authException) {
            System.out.print("Could not log into database: ");
            System.out.print(authException.getMessage());
            System.out.println(" Check the login credentials and try again.");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        }
	}
}
