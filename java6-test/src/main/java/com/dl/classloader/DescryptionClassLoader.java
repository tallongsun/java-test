package com.dl.classloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DescryptionClassLoader extends ClassLoader{
	private final Map<String, byte[]> bytesMap;
	private final Map<String, Class<?>> clazzMap;
	
	public DescryptionClassLoader() {
		super();
		bytesMap = new HashMap<String, byte[]>();
		clazzMap = new HashMap<String, Class<?>>();
		try {
			setJar();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void setJar() throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File("/Users/tallong/Downloads/enc_test.jar"));
		ZipInputStream zipIs = new ZipInputStream(fileInputStream);
		ZipEntry zipEntry = zipIs.getNextEntry();
		ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		for (; zipEntry != null; zipEntry = zipIs.getNextEntry()) {
			String s = zipEntry.getName();
			if (s.indexOf(".class") > 0) {
				//将class文件由路径符替换成为java标准的以.分隔的括号
				s = s.substring(0, s.lastIndexOf('.'));
				if (s.indexOf('\\') > 0) {
					s = s.replace('\\', '.');
				} else if (s.indexOf('/') > 0) {
					s = s.replace('/', '.');
				}
			}
			bout.reset();
			do {
				int i = zipIs.read(buffer, 0, buffer.length);
				if (i <= 0) {
					break;
				}
				bout.write(buffer, 0, i);
			} while (true);
			byte[] fileData = bout.toByteArray();
			bytesMap.put(s, fileData);
			zipIs.closeEntry();
		}
		zipIs.close();
	}
	
	@Override
	/**
	 * 打破双亲委托，由该classloader加载的类，会先通过自定义classloader加载，加载不到再找父亲classloader加载
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		System.out.println("loadClass() name = "+name);
		
		Class<?> clz = null;
		clz = clazzMap.get(name);
		if(clz == null) {
			byte[] bytes = bytesMap.get(name);
			if(bytes != null) {
				clz = findClass(name,bytes);
			}
			if (clz != null) {
				clazzMap.put(name, clz);
			}
		}
		if(clz == null) {
        		clz = this.getParent().loadClass(name);
		}
		if (clz != null && resolve) {
			this.resolveClass(clz);
		}
		return clz;
	}
	
	protected Class<?> findClass(String name,byte[] bytes) throws ClassNotFoundException {
        System.out.println("findClass() name = " + name);  
        
        byte[] data = null;
		try {
	        data = decryptClass(bytes);  
		}catch(Exception e) {
			throw new ClassNotFoundException(name,e);
		}
        Class<?> clz = defineClass(name, data, 0, data.length);  
        return clz;  
	}

	
	private byte[]  decryptClass(byte[] bytes)  throws Exception{
		byte[] result = new byte[bytes.length];
		for(int i=0;i<bytes.length;i++) {
			result[i] = (byte) (bytes[i] ^ 0xFF);
		}
		return result;
	}
	

	@Override
	protected URL findResource(String name) {
		if (this.bytesMap.get(name) != null) {
			try {
				return new URL("jar:" + new File("/Users/tallong/Downloads/enc_test.jar").toURI().toURL() + "!/" + name);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		Vector<URL> _urls = new Vector<URL>();
		URL resource = this.findResource(name);
		if (resource != null) {
			_urls.add(resource);
		}
		return _urls.elements();
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		if (bytesMap.get(name) != null) {
			return new ByteArrayInputStream(bytesMap.get(name));
		}
		return this.getParent().getResourceAsStream(name);
	}
}
