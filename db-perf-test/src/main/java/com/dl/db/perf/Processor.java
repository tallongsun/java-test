package com.dl.db.perf;

import java.util.List;

public interface Processor {
	public  void write(List<SimpleObject> allData) ;

	public List<SimpleObject> read();
	
	public void clear();
}
