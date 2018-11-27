package com.dl.sketch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.yahoo.memory.NativeMemory;
import com.yahoo.sketches.theta.AnotB;
import com.yahoo.sketches.theta.Intersection;
import com.yahoo.sketches.theta.SetOperation;
import com.yahoo.sketches.theta.Sketch;
import com.yahoo.sketches.theta.Sketches;
import com.yahoo.sketches.theta.Union;
import com.yahoo.sketches.theta.UpdateSketch;

public class ThetaSketchTest {
	public static void doSerialize() throws Exception{
		  UpdateSketch sketch1 = UpdateSketch.builder().build();
		  for (int key = 0; key < 100000; key++) sketch1.update(key);
		  System.out.println(sketch1.getEstimate());
		  FileOutputStream out1 = new FileOutputStream(new File("ThetaSketch1.bin"));
		  out1.write(sketch1.compact().toByteArray());
		  out1.close();

		  // 100000 unique keys
		  // the first 50000 unique keys overlap with sketch1
		  UpdateSketch sketch2 = UpdateSketch.builder().build();
		  for (int key = 50000; key < 150000; key++) sketch2.update(key);
		  System.out.println(sketch2.getEstimate());
		  FileOutputStream out2 = new FileOutputStream(new File("ThetaSketch2.bin"));
		  out2.write(sketch2.compact().toByteArray());
		  out2.close();
	}
	
	public static void doDeSerialize() throws Exception{
		  FileInputStream in1 = new FileInputStream(new File("ThetaSketch1.bin"));
		  byte[] bytes1 = new byte[in1.available()];
		  in1.read(bytes1);
		  in1.close();
		  Sketch sketch1 = Sketches.wrapSketch(new NativeMemory(bytes1));
		  System.out.println(sketch1.getEstimate());
		  FileInputStream in2 = new FileInputStream(new File("ThetaSketch2.bin"));
		  byte[] bytes2 = new byte[in2.available()];
		  in2.read(bytes2);
		  in2.close();
		  Sketch sketch2 = Sketches.wrapSketch(new NativeMemory(bytes2));
		  System.out.println(sketch2.getEstimate());

		  Union union = SetOperation.builder().buildUnion();
		  union.update(sketch1);
		  union.update(sketch2);
		  Sketch unionResult = union.getResult();

		  // debug summary of the union result sketch
		  System.out.println(unionResult.toString());

		  System.out.println("Union unique count estimate: " + unionResult.getEstimate());
		  System.out.println("Union unique count lower bound 95% confidence: " + unionResult.getLowerBound(2));
		  System.out.println("Union unique count upper bound 95% confidence: " + unionResult.getUpperBound(2));

		  Intersection intersection = SetOperation.builder().buildIntersection();
		  intersection.update(sketch1);
		  intersection.update(sketch2);
		  Sketch intersectionResult = intersection.getResult();

		  // debug summary of the intersection result sketch
		  System.out.println(intersectionResult.toString());

		  System.out.println("Intersection unique count estimate: " + intersectionResult.getEstimate());
		  System.out.println("Intersection unique count lower bound 95% confidence: " + intersectionResult.getLowerBound(2));
		  System.out.println("Intersection unique count upper bound 95% confidence: " + intersectionResult.getUpperBound(2));
		  
		  AnotB anotb = SetOperation.builder().buildANotB();
		  anotb.update(sketch1, sketch2);
		  Sketch anotbResult = anotb.getResult();
		  
		  System.out.println(anotbResult.toString());
	}
	
	public static void main(String[] args)  throws Exception{

//		  doSerialize();
//		  
//		  doDeSerialize();
		
		  Union union = SetOperation.builder().buildUnion();
		  union.update(1);
		  union.update(2);
		  union.update(1);
		  Sketch unionResult = union.getResult();

		  // debug summary of the union result sketch
		  System.out.println(unionResult.toString());

		  System.out.println("Union unique count estimate: " + unionResult.getEstimate());

	}
}
