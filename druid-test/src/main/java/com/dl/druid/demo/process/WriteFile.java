package com.dl.druid.demo.process;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

public class WriteFile implements IProcess {
    FileWriter fw;
    ObjectMapper objmapper;
    
	public WriteFile(String fiename) throws Exception{
        fw = new FileWriter(fiename,false);
        objmapper=new ObjectMapper();
	}
	
	
	@Override
	public void apply(List<HashMap<String, Object>> data) {
		try{
	        for(HashMap<String, Object> obj:data){
	        	fw.write(objmapper.writeValueAsString(obj+"\n"));
	        }
	        fw.flush();
		}catch (IOException e) {
            e.printStackTrace();
       }
	}

}
