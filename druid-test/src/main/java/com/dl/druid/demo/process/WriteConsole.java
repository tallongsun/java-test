package com.dl.druid.demo.process;

import java.util.HashMap;
import java.util.List;

public class WriteConsole implements IProcess {
	
	public WriteConsole(){
	}
	
	
	@Override
	public void apply(List<HashMap<String, Object>> data) {
        for (HashMap<String, Object> obj : data) {
        	System.out.println(obj);
        }
	}

}
