package com.dl.druid.demo.process;

import java.util.HashMap;
import java.util.List;

public interface IProcess {
	public void apply(List<HashMap<String, Object>> data);
}
