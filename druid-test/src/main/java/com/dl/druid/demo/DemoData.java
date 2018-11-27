package com.dl.druid.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.dl.druid.demo.process.IProcess;
import com.dl.druid.demo.process.WriteConsole;
import com.dl.druid.demo.process.WriteDruid;
import com.dl.druid.demo.process.WriteFile;
import com.dl.druid.demo.process.WriteHdfs;
import com.dl.druid.demo.process.WriteKafka;
import com.dl.druid.demo.process.WriteMongo;

public class DemoData {
	public static int USER_COUNT = 1860480;
	public static int MAX_ROWS = 1;
	
	public static String DESTINATION = "druid";
	
	public static String DATASOURCE = "demo";
	
	public static void main(String[] args) throws Exception{
        DianShangRand dsdata=new DianShangRand();
        dsdata.init();
	
        ArrayList<IProcess> processers=new ArrayList<>();
        IProcess prcossor = null;
        switch(DESTINATION){
        case "kafka":
        	prcossor = new WriteKafka(DATASOURCE);
        	processers.add(prcossor);
        	break;
        case "druid":        	
        	prcossor = new WriteDruid(DATASOURCE);
        	processers.add(prcossor);
        	break;
        case "file":
        	prcossor = new WriteFile(DATASOURCE);
        	processers.add(prcossor);
            break;
        case "hdfs":
        	prcossor = new WriteHdfs(DATASOURCE);
        	processers.add(prcossor);
            break;
        case "mongo":
        	prcossor = new WriteMongo(DATASOURCE);
        	processers.add(prcossor);
            break;
        case "mysql"://TODO
        case "es":
        case "redis":
        case "hbase":
        default:
        	prcossor = new WriteConsole();
        	processers.add(prcossor);
        	break;
        }
        
        
        List<String> users=dsdata.genUsers(USER_COUNT);
        System.out.println(users.size());
        System.out.println(new HashSet<String>(users).size());
        
        long start = System.currentTimeMillis();
        
        //TODO：从某一天开始按天
        dsdata.genData(users, MAX_ROWS, processers,null);
        
        System.out.println((System.currentTimeMillis()-start)/1000);
	
	}

}
