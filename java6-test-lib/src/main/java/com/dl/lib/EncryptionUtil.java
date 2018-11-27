package com.dl.lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class EncryptionUtil {
    public static void main(String[] args) {  
    		encryptJar("/Users/tallong/Downloads/test.jar");
//        encryptClass("/Users/tallong/JavaProjects/test/java6-test-lib/target/classes/com/dl/classloader/TestClass.class");  
    }  

	public static String encryptJar(String path) {
		File jarFile = new File(path);
		if (!jarFile.exists()) {
            System.out.println("encryptJar() File:" + path + " not found!");  
			return null;
		}

        System.out.println("encryptJar() path=" + path);  
        
		String cipheredJar = jarFile.getParent() + File.separator  + "enc_"+jarFile.getName();
		File cipheredJarFile = new File(cipheredJar);

		ZipInputStream is = null;
		ZipOutputStream out = null;
		try {
			is = new ZipInputStream(new FileInputStream(jarFile));
			out = new ZipOutputStream(new FileOutputStream(cipheredJarFile));
			ZipEntry zipEntry = is.getNextEntry();
			for (; zipEntry != null; zipEntry = is.getNextEntry()) {
				String s = zipEntry.getName();
				ZipEntry entry = new ZipEntry(s);
				out.putNextEntry(entry);
				BufferedInputStream bis = new BufferedInputStream(is);
				BufferedOutputStream bout = new BufferedOutputStream(out);
				int data = 0;
				while ((data = bis.read()) != -1) {
					bout.write(data ^ 0xFF);
				}
				bout.flush();
				is.closeEntry();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)is.close();
				if (out != null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return path;
	}
    
	public static String encryptClass(String path) {
        File classFile = new File(path);  
        if (!classFile.exists()) {  
            System.out.println("encryptClass() File:" + path + " not found!");  
            return null;  
        }  
        
        System.out.println("encryptClass() path=" + path);  
        
        String cipheredClass = classFile.getParent() + File.separator +  "enc_" + classFile.getName();  
        File cipheredClassFile = new File(cipheredClass);
        
        BufferedInputStream is = null;  
        BufferedOutputStream out = null;
        try {  
	        	is = new BufferedInputStream(new FileInputStream(classFile));  
	        	out = new BufferedOutputStream(new FileOutputStream(cipheredClassFile));
            int data = 0;  
            while ((data = is.read()) != -1) {  
                out.write(data ^ 0xFF);  
            }  
  
            out.flush();  
            
            cipheredClassFile.renameTo(classFile);
 
        } catch (IOException e) {  
            e.printStackTrace();  
        }  finally {
            try {
            		if(is!=null)is.close();
	            if(out!=null)out.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }
        return path; 
	}
	
	public static byte[] decryptClass(String path) {
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("decryptClass() File:" + path + " not found!");
			return null;
		}

		System.out.println("decryptClass() path=" + path);

		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			out = new ByteArrayOutputStream();
			int data = 0;
			while ((data = in.read()) != -1) {
				out.write(data ^ 0xFF);
			}
			in.close();
			out.flush();
			out.close();

			return out.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)in.close();
				if (out != null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
	public static byte[] decryptClass(byte[] bytes) {
		byte[] result = new byte[bytes.length];
		for(int i=0;i<bytes.length;i++) {
			result[i] = (byte) (bytes[i] ^ 0xFF);
		}
		return result;
	}
}
