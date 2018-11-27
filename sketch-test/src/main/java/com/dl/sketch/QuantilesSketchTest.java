package com.dl.sketch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

import com.yahoo.memory.NativeMemory;
import com.yahoo.sketches.quantiles.DoublesSketch;
import com.yahoo.sketches.quantiles.DoublesUnion;

public class QuantilesSketchTest {

	private static void doSerialize() throws Exception{
		  Random rand = new Random();

		  DoublesSketch sketch1 = DoublesSketch.builder().build(); // default k=128
		  for (int i = 0; i < 10000; i++) {
		    sketch1.update(rand.nextGaussian()); // mean=0, stddev=1
		  }
		  FileOutputStream out1 = new FileOutputStream(new File("QuantilesDoublesSketch1.bin"));
		  out1.write(sketch1.toByteArray());
		  out1.close();

		  DoublesSketch sketch2 = DoublesSketch.builder().build(); // default k=128
		  for (int i = 0; i < 10000; i++) {
		    sketch2.update(rand.nextGaussian() + 1); // shift the mean for the second sketch
		  }
		  FileOutputStream out2 = new FileOutputStream(new File("QuantilesDoublesSketch2.bin"));
		  out2.write(sketch2.toByteArray());
		  out2.close();
	}
	
	private static void doDeSerialize() throws Exception{
		  FileInputStream in1 = new FileInputStream(new File("QuantilesDoublesSketch1.bin"));
		  byte[] bytes1 = new byte[in1.available()];
		  in1.read(bytes1);
		  in1.close();
		  DoublesSketch sketch1 = DoublesSketch.heapify(new NativeMemory(bytes1));

		  FileInputStream in2 = new FileInputStream(new File("QuantilesDoublesSketch2.bin"));
		  byte[] bytes2 = new byte[in2.available()];
		  in2.read(bytes2);
		  in2.close();
		  DoublesSketch sketch2 = DoublesSketch.heapify(new NativeMemory(bytes2));

		  DoublesUnion union = DoublesUnion.builder().build(); // default k=128
		  union.update(sketch1);
		  union.update(sketch2);
		  DoublesSketch result = union.getResult();
		  // Debug output from the sketch
		  System.out.println(result.toString());

		  System.out.println("Min, Median, Max values");
		  System.out.println(Arrays.toString(result.getQuantiles(new double[] {0, 0.5, 1})));

		  System.out.println("Probability Histogram: estimated probability mass in 4 bins: (-inf, -2), [-2, 0), [0, 2), [2, +inf)");
		  System.out.println(Arrays.toString(result.getPMF(new double[] {-2, 0, 2})));

		  System.out.println("Frequency Histogram: estimated number of original values in the same bins");
		  double[] histogram = result.getPMF(new double[] {-2, 0, 2});
		  for (int i = 0; i < histogram.length; i++) {
		    histogram[i] *= result.getN(); // scale the fractions by the total count of values
		  }
		  System.out.println(Arrays.toString(histogram));
	}
	
	public static void main(String[] args)  throws Exception{

		  doSerialize();
		  
		  doDeSerialize();

	}

}
