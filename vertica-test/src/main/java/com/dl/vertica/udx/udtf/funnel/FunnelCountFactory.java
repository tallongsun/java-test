package com.dl.vertica.udx.udtf.funnel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.MultiPhaseTransformFunctionFactory;
import com.vertica.sdk.ParamReader;
import com.vertica.sdk.PartitionReader;
import com.vertica.sdk.PartitionWriter;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.SizedColumnTypes;
import com.vertica.sdk.TransformFunction;
import com.vertica.sdk.TransformFunctionPhase;
import com.vertica.sdk.UdfException;

//TODO: 1.漏斗的action定义 2.window限制为漏斗所有action范围 3.支持group by ，有点麻烦
public class FunnelCountFactory extends MultiPhaseTransformFunctionFactory{

	@Override
	public void getPhases(ServerInterface srvInterface, Vector<TransformFunctionPhase> phases) {
		FunnelPhase funnelPhase = new FunnelPhase();
		funnelPhase.setPrepass();
		phases.add(funnelPhase);
		
		CountPhase countPhase = new CountPhase();
		phases.add(countPhase);
	}

	@Override
	public void getPrototype(ServerInterface arg0, ColumnTypes argTypes, ColumnTypes returnType) {
        argTypes.addVarchar(); //sid
        argTypes.addVarchar(); //event
        argTypes.addDate();//timestamp
        
        returnType.addVarchar();//event
        returnType.addInt(); //count
	}
	
	@Override
	public void getParameterType(ServerInterface srvInterface, SizedColumnTypes parameterTypes) {
		parameterTypes.addInt("w");
	}
	
	public class FunnelPhase extends TransformFunctionPhase{

		@Override
		public TransformFunction createTransformFunction(ServerInterface arg0) {
			return new FunnelFunction();
		}

		@Override
		public void getReturnType(ServerInterface arg0, SizedColumnTypes inputTypes, SizedColumnTypes outputTypes) {
			ArrayList<Integer> argCols = new ArrayList<Integer>();
			inputTypes.getArgumentColumns(argCols);
			
			ArrayList<Integer> pByCols = new ArrayList<Integer>();
			inputTypes.getPartitionByColumns(pByCols);
			
			outputTypes.addVarcharPartitionColumn(inputTypes.getColumnType(pByCols.get(0)).getStringLength(), "event");
			outputTypes.addVarchar(inputTypes.getColumnType(argCols.get(0)).getStringLength(),"sid");
		}
		
	}
	
	public class CountPhase extends TransformFunctionPhase{

		@Override
		public TransformFunction createTransformFunction(ServerInterface arg0) {
			return new CountFunction();
		}

		@Override
		public void getReturnType(ServerInterface arg0, SizedColumnTypes inputTypes, SizedColumnTypes outputTypes) {
			ArrayList<Integer> pByCols = new ArrayList<Integer>();
			inputTypes.getPartitionByColumns(pByCols);
			
			outputTypes.addVarchar(inputTypes.getColumnType(pByCols.get(0)).getStringLength(),"event");
			outputTypes.addInt("count"); 
		}
		
	}

	
	public class FunnelFunction extends TransformFunction {

		@Override
		public void processPartition(ServerInterface srvInterface, PartitionReader inputReader, PartitionWriter outputWriter)
				throws UdfException, DestroyInvocation {
			try {
				SizedColumnTypes inTypes = inputReader.getTypeMetaData();
				
				ArrayList<Integer> argCols = new ArrayList<Integer>();
				inTypes.getArgumentColumns(argCols);

				SizedColumnTypes outTypes = outputWriter.getTypeMetaData();
				
				ArrayList<Integer> outArgsCols = new ArrayList<Integer>();
				outTypes.getArgumentColumns(outArgsCols);

				ArrayList<Integer> outPbyCols = new ArrayList<Integer>();
				outTypes.getPartitionByColumns(outPbyCols);

				ArrayList<Integer> outObyCols = new ArrayList<Integer>();
				outTypes.getOrderByColumns(outObyCols);
				
				ParamReader paramReader = srvInterface.getParamReader();
				long window = paramReader.getLong("w");
				
				Map<String,TreeSet<Long>> map = new HashMap<>(); 
				// Loop over all rows passed in in this partition.
				String sid = null;
				do {
					sid = inputReader.getString(argCols.get(0));
					String event = inputReader.getString(argCols.get(1));
					Date timestamp = inputReader.getDate(argCols.get(2));
					
					TreeSet<Long> set = map.get(event);
					if(set == null){
						set = new TreeSet<>();
						map.put(event, set);
					}
					set.add(timestamp.getTime());

					// Loop until there are no more input rows in partition.
				} while (inputReader.next());
				
				Entry<String,TreeSet<Long>> preEntry = null;
				for(Entry<String,TreeSet<Long>> entry : map.entrySet()){
					if(preEntry!=null){
						TreeSet<Long> value = entry.getValue();
						int cnt = 0;
						Iterator<Long> ite  = value.iterator();
						while(ite.hasNext()){
							long v = ite.next();
							TreeSet<Long> preValue = preEntry.getValue();
							if(!preValue.subSet(v-window, v).isEmpty()){
								cnt++;
							}else{
								ite.remove();
							}
						}
						if(cnt>0){
							outputWriter.setString(outPbyCols.get(0), entry.getKey());
							outputWriter.setString(outArgsCols.get(0), sid);
							outputWriter.next(); // Advance to next row of
						}
					}else{
						int cnt = entry.getValue().size();
						if(cnt>0){
							outputWriter.setString(outPbyCols.get(0), entry.getKey());
							outputWriter.setString(outArgsCols.get(0), sid);
							outputWriter.next(); // Advance to next row of
						}
					}
					preEntry = entry;
				}
			}catch (Exception e) {// Prevent exceptions from bubbling back up to server. Uncaught exceptions will cause a transaction rollback.
				// Use more robust error handling in your own UDTFs. This example just sends a message to the log.
				srvInterface.log("Exception: " + e.getClass().getSimpleName() + "Message: " + e.getMessage());
			}
		}
		
	}
	
	
	public class CountFunction extends TransformFunction{

		@Override
		public void processPartition(ServerInterface srvInterface, PartitionReader inputReader, PartitionWriter outputWriter)
				throws UdfException, DestroyInvocation {
			SizedColumnTypes inTypes = inputReader.getTypeMetaData();
			
			ArrayList<Integer> argCols = new ArrayList<Integer>();
			inTypes.getArgumentColumns(argCols);

			ArrayList<Integer> pByCols = new ArrayList<Integer>();
			inTypes.getPartitionByColumns(pByCols);

			ArrayList<Integer> oByCols = new ArrayList<Integer>();
			inTypes.getOrderByColumns(oByCols);
			
			SizedColumnTypes outTypes = outputWriter.getTypeMetaData();
			
			ArrayList<Integer> outArgsCols = new ArrayList<Integer>();
			outTypes.getArgumentColumns(outArgsCols);

			String event = inputReader.getString(pByCols.get(0));
			
			//vertica每个分区节点独立计算，sid在一个节点合并时可能重复，需要排重
			Set<String> sids = new HashSet<>();
			do{
				String sid = inputReader.getString(argCols.get(0));
				sids.add(sid);
			}while(inputReader.next());
			
			outputWriter.setString(outArgsCols.get(0), event);
			outputWriter.setLong(outArgsCols.get(1), sids.size());
			outputWriter.next();
		}
		
	}
}
