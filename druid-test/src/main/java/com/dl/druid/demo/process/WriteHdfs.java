package com.dl.druid.demo.process;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.codehaus.jackson.map.ObjectMapper;

public class WriteHdfs implements IProcess {
	FileSystem filesystem;
	FSDataOutputStream fw;

	ObjectMapper objmapper;

	public WriteHdfs(String fiename) throws Exception {
		filesystem = FileSystem.get(new URI("hdfs://hdfs-namenode:9000"), new Configuration());
		Path path = new Path(String.format("/%s.txt", fiename));
		filesystem.delete(path, true);
		fw = filesystem.create(path);

		objmapper = new ObjectMapper();
	}

	@Override
	public void apply(List<HashMap<String, Object>> data) {
		try {
			for (HashMap<String, Object> obj : data) {
				fw.write(objmapper.writeValueAsBytes(obj));
				fw.write("\n".getBytes());
			}
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
