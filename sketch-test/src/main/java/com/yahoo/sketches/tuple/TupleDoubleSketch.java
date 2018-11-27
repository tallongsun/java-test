package com.yahoo.sketches.tuple;

import com.yahoo.memory.Memory;

public class TupleDoubleSketch extends UpdatableSketch<Double, DoubleSummary>{

	public TupleDoubleSketch(int nomEntries, int lgResizeFactor, float samplingProbability,
			SummaryFactory<DoubleSummary> summaryFactory) {
		super(nomEntries, lgResizeFactor, samplingProbability, summaryFactory);
	}

    public TupleDoubleSketch(Memory memory){
        super(memory);
    }
    
    public static TupleDoubleSketch build(){
        return new TupleDoubleSketch(16384,1,1,new DoubleSummaryFactory());
    }
    
    public static void merge(TupleDoubleSketch s1,TupleDoubleSketch s2){
    	SketchIterator< DoubleSummary> ite = s2.iterator();
    	while(ite.next()){
    		s1.merge(ite.getKey(), ite.getSummary());
    	}
    }
}
