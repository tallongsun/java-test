package com.dl.basic;

import java.util.ArrayList;
import java.util.List;

public class MemoryTest {

    public static void main(String[] args) throws Exception{
            consumeMem(100*1024*1024);
    }

    static List<byte[]> bytesList = new ArrayList<>();
    private static void consumeMem(int byteArraySize) throws Exception{
            long heapSize = Runtime.getRuntime().maxMemory();
            System.out.println(heapSize);

            Thread.sleep(60000);
            System.out.println("new");
            byte[] bytes = new byte[byteArraySize];
            System.out.println("new end");
            bytesList.add(bytes);
            System.out.println("add end");
            Thread.sleep(60000);

            System.out.println(bytes.length);

    }
} 
