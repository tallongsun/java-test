package com.dl.vertica.udx.udtf;

import java.util.ArrayList;

import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.ParamReader;
import com.vertica.sdk.PartitionReader;
import com.vertica.sdk.PartitionWriter;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.SizedColumnTypes;
import com.vertica.sdk.TransformFunction;
import com.vertica.sdk.TransformFunctionFactory;
import com.vertica.sdk.UdfException;

public class TopKPerPartitionWithParamsFactory extends TransformFunctionFactory {
	@Override
	public TransformFunction createTransformFunction(ServerInterface srvInterface) {
		return new TopKPerPartitionWithParams();
	}

	@Override
	public void getReturnType(ServerInterface srvInterface, SizedColumnTypes inputTypes, SizedColumnTypes outputTypes) {
		ArrayList<Integer> argCols = new ArrayList<Integer>();
		inputTypes.getArgumentColumns(argCols);
		int colIdx = 0;
		for (int i = 0; i < argCols.size(); ++i) {
			StringBuilder cname = new StringBuilder();
			cname.append("col").append(colIdx++);
			outputTypes.addArg(inputTypes.getColumnType(argCols.get(i)), cname.toString());
		}
	}

	@Override
	public void getPrototype(ServerInterface srvInterface, ColumnTypes argTypes, ColumnTypes returnType) {
		argTypes.addInt();
		argTypes.addInt();
		returnType.addInt();
		returnType.addInt();
	}

	@Override
	public void getParameterType(ServerInterface srvInterface, SizedColumnTypes parameterTypes) {
		parameterTypes.addInt("k");
	}

	public class TopKPerPartitionWithParams extends TransformFunction {

		@Override
		public void processPartition(ServerInterface srvInterface, PartitionReader inputReader,
				PartitionWriter outputWriter) throws UdfException, DestroyInvocation {
			// Sanity check the input we've been given
			SizedColumnTypes inTypes = inputReader.getTypeMetaData();
			ArrayList<Integer> argCols = new ArrayList<Integer>(); // Argument
																	// column
																	// indexes.
			inTypes.getArgumentColumns(argCols);
			ParamReader paramReader = srvInterface.getParamReader();
			long num = paramReader.getLong("k");
			long cnt = 0;
			do {

				// If we're already produced num tuples, then break
				if (cnt >= num)
					break;

				// Write the remaining arguments to output
				int owColIdx = 0;
				for (int i = 0; i < argCols.size(); ++i)
					outputWriter.copyFromInput(owColIdx++, inputReader, argCols.get(i));

				outputWriter.next();
				cnt++;
			} while (inputReader.next());
		}
	}
}
