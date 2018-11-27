package com.dl.sketch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

import com.yahoo.memory.NativeMemory;
import com.yahoo.sketches.ResizeFactor;
import com.yahoo.sketches.quantiles.DoublesSketch;
import com.yahoo.sketches.tuple.ArrayOfDoublesAnotB;
import com.yahoo.sketches.tuple.ArrayOfDoublesCombiner;
import com.yahoo.sketches.tuple.ArrayOfDoublesCompactSketch;
import com.yahoo.sketches.tuple.ArrayOfDoublesIntersection;
import com.yahoo.sketches.tuple.ArrayOfDoublesSetOperationBuilder;
import com.yahoo.sketches.tuple.ArrayOfDoublesSketch;
import com.yahoo.sketches.tuple.ArrayOfDoublesSketchIterator;
import com.yahoo.sketches.tuple.ArrayOfDoublesSketches;
import com.yahoo.sketches.tuple.ArrayOfDoublesUnion;
import com.yahoo.sketches.tuple.ArrayOfDoublesUpdatableSketch;
import com.yahoo.sketches.tuple.ArrayOfDoublesUpdatableSketchBuilder;
import com.yahoo.sketches.tuple.DoubleSummary;
import com.yahoo.sketches.tuple.SketchIterator;
import com.yahoo.sketches.tuple.TupleDoubleSketch;

public class TupleSketchTest {
	public static void doSerialize() throws Exception{
		  Random rand = new Random();

		  ArrayOfDoublesUpdatableSketch sketch1 = new ArrayOfDoublesUpdatableSketchBuilder().build();
		  for (int key = 0; key < 100000; key++) sketch1.update(key, new double[] {rand.nextGaussian()});
		  System.out.println(sketch1.getEstimate());
		  FileOutputStream out1 = new FileOutputStream(new File("TupleSketch1.bin"));
		  out1.write(sketch1.compact().toByteArray());
		  out1.close();

		  ArrayOfDoublesUpdatableSketch sketch2 = new ArrayOfDoublesUpdatableSketchBuilder().build();
		  for (int key = 50000; key < 150000; key++) sketch2.update(key, new double[] {rand.nextGaussian()});
		  System.out.println(sketch2.getEstimate());
		  FileOutputStream out2 = new FileOutputStream(new File("TupleSketch2.bin"));
		  out2.write(sketch2.compact().toByteArray());
		  out2.close();
		  
	}
	
	public static void doDeSerialize() throws Exception{
		  FileInputStream in1 = new FileInputStream(new File("TupleSketch1.bin"));
		  byte[] bytes1 = new byte[in1.available()];
		  in1.read(bytes1);
		  in1.close();
		  ArrayOfDoublesSketch sketch1 = ArrayOfDoublesSketches.wrapSketch(new NativeMemory(bytes1));

		  FileInputStream in2 = new FileInputStream(new File("TupleSketch2.bin"));
		  byte[] bytes2 = new byte[in2.available()];
		  in2.read(bytes2);
		  in2.close();
		  ArrayOfDoublesSketch sketch2 = ArrayOfDoublesSketches.wrapSketch(new NativeMemory(bytes2));

		  ArrayOfDoublesUnion union = new ArrayOfDoublesSetOperationBuilder().buildUnion();
		  union.update(sketch1);
		  union.update(sketch2);
		  ArrayOfDoublesSketch unionResult = union.getResult();

		  System.out.println("Union unique count estimate: " + unionResult.getEstimate());
		  System.out.println("Union unique count lower bound (95% confidence): " + unionResult.getLowerBound(2));
		  System.out.println("Union unique count upper bound (95% confidence): " + unionResult.getUpperBound(2));
		  
		  // Let's use Quantiles sketch to analyze the distribution of values
		  DoublesSketch quantilesSketch = DoublesSketch.builder().build();
		  ArrayOfDoublesSketchIterator it = unionResult.iterator();
		  while (it.next()) {
		    quantilesSketch.update(it.getValues()[0]);
		  }

		  System.out.println("Probability Histogram of values: estimated probability mass in 6 bins:\n"
		      + "(-inf, -2), [-2, -1), [-1, 0), [0, 1), [1, 2), [2, +inf)");
		  System.out.println(Arrays.toString(quantilesSketch.getPMF(new double[] {-2, -1, 0, 1, 2})));
	}
	
	public static void main(String[] args) throws Exception{
		
		
//		testTupleDoublesSketch();
//
//		testTupleDoubleSketch();
		
//		testTupleDoublesSketch2();
		
//		testTupleDoublesSketch3();
		
		testTupleDoublesSketch4();
	}
	
	public static void testTupleDoublesSketch4(){
		int size = 16384;
		ArrayOfDoublesUpdatableSketch sketch = new ArrayOfDoublesUpdatableSketchBuilder().setNominalEntries(size).setResizeFactor(ResizeFactor.X2).setNumberOfValues(1).build();
		sketch.update("a", new double[]{1});
		sketch.update("b", new double[]{1});
		ArrayOfDoublesUpdatableSketch sketch2 = new ArrayOfDoublesUpdatableSketchBuilder().setNominalEntries(size).setResizeFactor(ResizeFactor.X2).setNumberOfValues(2).build();
		double[] o1 = new double[]{2};
		double[] o2 = new double[]{1};
		double[] n1 = new double[2];
		double[] n2 = new double[2];
		System.arraycopy(o1, 0, n1, 0, o1.length);
		System.arraycopy(o2, 0, n2, 0, o1.length);
		sketch2.update("a", n1);
		sketch2.update("c", n2);
		
		ArrayOfDoublesUnion union = new ArrayOfDoublesSetOperationBuilder().setNominalEntries(size).setNumberOfValues(2).buildUnion();
		union.update(sketch2);
	}
	
	
	public static void testTupleDoublesSketch3() {
		int size = 16384;
		int valuesCount = 1;
		ArrayOfDoublesUpdatableSketch sketch = new ArrayOfDoublesUpdatableSketchBuilder().setNominalEntries(size).setResizeFactor(ResizeFactor.X2).setNumberOfValues(valuesCount).build();
		sketch.update("a", new double[]{1});
		sketch.update("b", new double[]{1});
		ArrayOfDoublesUpdatableSketch sketch2 = new ArrayOfDoublesUpdatableSketchBuilder().setNominalEntries(size).setResizeFactor(ResizeFactor.X2).setNumberOfValues(valuesCount).build();
		sketch2.update("a", new double[]{2});
		sketch2.update("c", new double[]{1});
		
		
		ArrayOfDoublesUnion union = new ArrayOfDoublesSetOperationBuilder().setNominalEntries(size).setNumberOfValues(valuesCount).buildUnion();
		union.update(sketch);
		
		ArrayOfDoublesIntersection intersection = new ArrayOfDoublesSetOperationBuilder().setNominalEntries(size).setNumberOfValues(valuesCount).buildIntersection();
		intersection.update(sketch2, max);
		intersection.update(union.getResult(), max);
		
		ArrayOfDoublesAnotB anotb = new ArrayOfDoublesSetOperationBuilder().setNominalEntries(size).setNumberOfValues(valuesCount).buildAnotB();
		anotb.update(union.getResult(), sketch2);
		
		ArrayOfDoublesAnotB bnota = new ArrayOfDoublesSetOperationBuilder().setNominalEntries(size).setNumberOfValues(valuesCount).buildAnotB();
		bnota.update(sketch2,union.getResult());
		
		union.reset();
		union.update(intersection.getResult());
		union.update(anotb.getResult());
		union.update(bnota.getResult());
		
		System.out.println("---union");
		System.out.println(union.getResult().getEstimate() + " , " + union.getResult().getTheta() + " , " + union.getResult().getRetainedEntries());
		ArrayOfDoublesSketchIterator it = union.getResult().iterator();
		while (it.next()) {
			System.out.println(it.getKey() + "," + Arrays.toString(it.getValues()));
		}
	}
	
    public static ArrayOfDoublesCombiner max = new ArrayOfDoublesCombiner(){
		@Override
		public double[] combine(double[] a, double[] b) {
			if(a==null || a.length==0){
				return b;
			}
			double[] c = new double[a.length];
			for(int i=0;i<c.length;i++){
				c[i] = Math.max(a[i], b[i]);
			}
			return c;
		}
    };

	public static void testTupleDoublesSketch2() {
		//sketch1
		ArrayOfDoublesUpdatableSketch sketch = new ArrayOfDoublesUpdatableSketchBuilder().setNominalEntries(16384).setResizeFactor(ResizeFactor.X2).setNumberOfValues(1).build();
		sketch.update(123, new double[] { 0.01});
		sketch.update(123, new double[] { 0.03});
		sketch.update("aaa", new double[] { 0.01});
		sketch.update("bb", new double[] { 0.01});
		
		//sketch2
		ArrayOfDoublesUpdatableSketch sketch2 = new ArrayOfDoublesUpdatableSketchBuilder().setNominalEntries(16384).setResizeFactor(ResizeFactor.X2).setNumberOfValues(1).build();
		sketch2.update("321", new double[] {0.02});
		sketch2.update(123, new double[]{0.02});
		sketch2.update("bb", new double[] { 0.02});
		
		
		//union
		ArrayOfDoublesUnion union = new ArrayOfDoublesSetOperationBuilder().buildUnion();
		union.update(sketch);
		union.update(sketch2);
		
		
		System.out.println("---union");
		System.out.println(union.getResult().getEstimate() + " , " + union.getResult().getTheta() + " , " + union.getResult().getRetainedEntries());

		//summarys
		ArrayOfDoublesSketchIterator it = union.getResult().iterator();
		while (it.next()) {
			System.out.println(it.getKey() + "," + Arrays.toString(it.getValues()));
		}
		
		//union2
//		ArrayOfDoublesUnion union2 = new ArrayOfDoublesSetOperationBuilder().setNominalEntries(16384).setNumberOfValues(2).buildUnion();
		ArrayOfDoublesUpdatableSketch sketch3 = new ArrayOfDoublesUpdatableSketchBuilder().setNominalEntries(16384).setResizeFactor(ResizeFactor.X2).setNumberOfValues(1).build();
		sketch3.update("321", new double[] {0.02});
//		union2.update(sketch3);
//		
//		System.out.println("---union2");
//		System.out.println(union2.getResult().getEstimate() + " , " + union2.getResult().getTheta() + " , " + union2.getResult().getRetainedEntries());
//		//summarys
//		ArrayOfDoublesSketchIterator it2 = union2.getResult().iterator();
//		while (it2.next()) {
//			System.out.println(it2.getKey() + "," + Arrays.toString(it2.getValues()));
//		}
		
		//intersection
		ArrayOfDoublesIntersection intersection = new ArrayOfDoublesSetOperationBuilder().buildIntersection();
		intersection.update(sketch,(double[] a, double[] b) ->{
			System.out.println("--1");
			System.out.println(a);
			System.out.println(b);
			if(a==null || a.length==0){
				return b;
			}
			double[] c = new double[a.length];
			for(int i=0;i<c.length;i++){
				c[i] = Math.max(a[i], b[i]);
			}
			return c;
			});
		intersection.update(sketch2,(double[] a, double[] b) ->{
			System.out.println("--2");
			System.out.println(a);
			System.out.println(b);
			if(a==null || a.length==0){
				return b;
			}
			double[] c = new double[a.length];
			for(int i=0;i<c.length;i++){
				c[i] = Math.max(a[i], b[i]);
			}
			return c;
		});
		
		intersection.update(intersection.getResult(),(double[] a, double[] b) ->{
			System.out.println("--3");
			System.out.println(a);
			System.out.println(b);
			if(a==null || a.length==0){
				return b;
			}
			double[] c = new double[a.length];
			for(int i=0;i<c.length;i++){
				c[i] = Math.max(a[i], b[i]);
			}
			return c;
		});
		
		System.out.println("---intersection");
		System.out.println(intersection.getResult().getEstimate() + " , " + intersection.getResult().getTheta()+" , "+intersection.getResult().getRetainedEntries());
		
		//summarys
		it = intersection.getResult().iterator();
		while (it.next()) {
			System.out.println(it.getKey() + "," + Arrays.toString(it.getValues()));
		}
		
		//anotb
		ArrayOfDoublesAnotB anotb = new ArrayOfDoublesSetOperationBuilder().buildAnotB();
		anotb.update(sketch,sketch2);
		
		System.out.println("---anotb");
		//after getting result, sketch will reset cause of stateless 
		ArrayOfDoublesCompactSketch compactSketch = anotb.getResult();
		System.out.println(compactSketch.getEstimate() + " , " + compactSketch.getTheta()+" , "+compactSketch.getRetainedEntries());
		
		//summarys
		it = compactSketch.iterator();
		while (it.next()) {
			System.out.println(it.getKey() + "," + Arrays.toString(it.getValues()));
		}
		
	}
	
	
	
	
	public static void testTupleDoublesSketch() {
		ArrayOfDoublesUpdatableSketch sketch = new ArrayOfDoublesUpdatableSketchBuilder().setNumberOfValues(2).build();
		sketch.update(123, new double[] { 0.01, 0.1 });
		sketch.update(123, new double[] { 0.03, 0.3 });
		sketch.update("aaa", new double[] { 0.01, 0.1 });
		System.out.println(sketch.getEstimate() + " , " + sketch.getTheta() + " , " + sketch.getRetainedEntries()
				+ " , " + sketch.getNominalEntries() + " , " + sketch.getNumValues());

		for (double[] values : sketch.getValues()) {
			System.out.println(Arrays.toString(values));
		}

		System.out.println("---");

		ArrayOfDoublesSketchIterator it = sketch.iterator();
		while (it.next()) {
			System.out.println(it.getKey() + "," + Arrays.toString(it.getValues()));
		}

		System.out.println("---");
	}
	
	public static void testTupleDoubleSketch() {
		TupleDoubleSketch tdSketch = TupleDoubleSketch.build();
		tdSketch.update(123, 0.01);
		tdSketch.update(123, 0.03);
		tdSketch.update("aaa", 0.01);
		
		TupleDoubleSketch tdSketch2 = TupleDoubleSketch.build();
		tdSketch2.update("321", 0.02);
		
		TupleDoubleSketch.merge(tdSketch, tdSketch2);
		
		System.out.println(tdSketch.getEstimate() + " , " + tdSketch.getTheta() + " , " + tdSketch.getRetainedEntries());
		
		System.out.println("---");
		
		SketchIterator<DoubleSummary> tdIt = tdSketch.iterator();
		while(tdIt.next()){
			System.out.println(tdIt.getKey() + "," + tdIt.getSummary().getValue());
		}
		
		System.out.println("---");
	}




	
	

}
