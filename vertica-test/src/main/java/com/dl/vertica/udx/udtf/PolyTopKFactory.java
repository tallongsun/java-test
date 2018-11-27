package com.dl.vertica.udx.udtf;

import java.util.ArrayList;

import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.PartitionReader;
import com.vertica.sdk.PartitionWriter;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.SizedColumnTypes;
import com.vertica.sdk.TransformFunction;
import com.vertica.sdk.TransformFunctionFactory;
import com.vertica.sdk.UdfException;
import com.vertica.sdk.VerticaType;

public class PolyTopKFactory extends TransformFunctionFactory {
	@Override
	public TransformFunction createTransformFunction(ServerInterface srvInterface) {
		return new PolyTopKPerPartition();
	}

	@Override
	public void getReturnType(ServerInterface srvInterface, SizedColumnTypes inputTypes, SizedColumnTypes outputTypes) {
		ArrayList<Integer> argCols = new ArrayList<Integer>();
		inputTypes.getArgumentColumns(argCols);
		int colIdx = 0;
		for (int i = 1; i < argCols.size(); ++i) {
			StringBuilder cname = new StringBuilder();
			cname.append("col").append(colIdx++);
			outputTypes.addArg(inputTypes.getColumnType(argCols.get(i)), cname.toString());
		}
	}

	@Override
	public void getPrototype(ServerInterface srvInterface, ColumnTypes argTypes, ColumnTypes returnType) {
		argTypes.addAny();
		returnType.addAny();
	}

	public class PolyTopKPerPartition extends TransformFunction {
		@Override
		public void processPartition(ServerInterface srvInterface, PartitionReader inputReader,
				PartitionWriter outputWriter) throws UdfException, DestroyInvocation {
			// Sanity check the input we've been given
			SizedColumnTypes inTypes = inputReader.getTypeMetaData();
			ArrayList<Integer> argCols = new ArrayList<Integer>(); // Argument
																	// column
																	// indexes.
			inTypes.getArgumentColumns(argCols);
			srvInterface.log(argCols+"");
			
			ArrayList<Integer> pByCols = new ArrayList<Integer>();
			inTypes.getPartitionByColumns(pByCols);
			srvInterface.log(pByCols+"");

			ArrayList<Integer> oByCols = new ArrayList<Integer>();
			inTypes.getOrderByColumns(oByCols);
			srvInterface.log(oByCols+"");
			
			SizedColumnTypes outTypes = outputWriter.getTypeMetaData();
			ArrayList<Integer> outArgsCols = new ArrayList<Integer>();
			outTypes.getArgumentColumns(outArgsCols);
			srvInterface.log(outArgsCols+"");

			ArrayList<Integer> outPbyCols = new ArrayList<Integer>();
			outTypes.getPartitionByColumns(outPbyCols);
			srvInterface.log(outPbyCols+"");

			ArrayList<Integer> outObyCols = new ArrayList<Integer>();
			outTypes.getOrderByColumns(outObyCols);
			srvInterface.log(outObyCols+"");

			if (argCols.size() < 2)
				throw new UdfException(0, "Function takes at least 2 arguments, the first of which must be 'k'");

			VerticaType t = inTypes.getColumnType(argCols.get(0)); // first
																	// argument
			if (!t.isInt())
				throw new UdfException(0, "First argument must be an integer (the 'k' value)");

			long cnt = 0;
			do {
				long num = inputReader.getLong(argCols.get(0));

				// If we're already produced num tuples, then break
				if (cnt >= num)
					break;

				// Write the remaining arguments to output
				int owColIdx = 0;
				for (int i = 1; i < argCols.size(); ++i)
					outputWriter.copyFromInput(owColIdx++, inputReader, argCols.get(i));

				outputWriter.next();
				cnt++;
			} while (inputReader.next());
		}
	}
}
