package com.dl.vertica.udx.udtf.funnel;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.PartitionReader;
import com.vertica.sdk.PartitionWriter;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.SizedColumnTypes;
import com.vertica.sdk.TransformFunction;
import com.vertica.sdk.TransformFunctionFactory;
import com.vertica.sdk.UdfException;

public class FunnelFactory extends TransformFunctionFactory{

	@Override
	public TransformFunction createTransformFunction(ServerInterface arg0) {
		return new FunnelFunction();
	}

	@Override
	public void getPrototype(ServerInterface arg0, ColumnTypes argTypes, ColumnTypes returnType) {
        argTypes.addVarchar(); 
        argTypes.addVarchar(); 
        argTypes.addDate();
        
        returnType.addVarchar();
        returnType.addVarchar(); 
	}

	@Override
	public void getReturnType(ServerInterface arg0, SizedColumnTypes inputTypes, SizedColumnTypes outputTypes) throws UdfException {
		outputTypes.addVarchar(inputTypes.getColumnType(1).getStringLength(),"event");
		outputTypes.addVarchar(inputTypes.getColumnType(0).getStringLength(),"sid"); 
	}

	
	public class FunnelFunction extends TransformFunction {

		@Override
		public void processPartition(ServerInterface srvInterface, PartitionReader inputReader, PartitionWriter outputWriter)
				throws UdfException, DestroyInvocation {
			try {
				Map<String,TreeSet<Long>> map = new HashMap<>(); 
				// Loop over all rows passed in in this partition.
				String sid = null;
				do {
					sid = inputReader.getString(0);
					String event = inputReader.getString(1);
					Date timestamp = inputReader.getDate(2);
					
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
							if(!preValue.subSet(v-3, v).isEmpty()){
								cnt++;
							}else{
								ite.remove();
							}
						}
						if(cnt>0){
							outputWriter.setString(0, entry.getKey());
							outputWriter.setString(1, sid);
							outputWriter.next(); // Advance to next row of
						}
					}else{
						int cnt = entry.getValue().size();
						if(cnt>0){
							outputWriter.setString(0, entry.getKey());
							outputWriter.setString(1, sid);
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
}
