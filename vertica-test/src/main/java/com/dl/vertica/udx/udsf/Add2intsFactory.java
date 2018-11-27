package com.dl.vertica.udx.udsf;

import com.vertica.sdk.BlockReader;
import com.vertica.sdk.BlockWriter;
import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.ScalarFunction;
import com.vertica.sdk.ScalarFunctionFactory;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.UdfException;

public class Add2intsFactory extends ScalarFunctionFactory {

	@Override
	public ScalarFunction createScalarFunction(ServerInterface srvInterface) {
		return new Add2ints();
	}

	@Override
	public void getPrototype(ServerInterface srvInterface, ColumnTypes argTypes, ColumnTypes returnType) {
		argTypes.addInt();
		argTypes.addInt();
		returnType.addInt();
	}

	public class Add2ints extends ScalarFunction {
		@Override
		public void processBlock(ServerInterface srvInterface, BlockReader argReader, BlockWriter resWriter)
				throws UdfException, DestroyInvocation {
			do {
                if (argReader.isLongNull(0) || argReader.isLongNull(1) ) {
                    // No nulls allowed. Throw exception
                    throw new UdfException(1234, "Cannot add a NULL value");
                }
				// The input and output objects have already loaded
				// the first row, so you can start reading and writing
				// values immediately.

				// Get the two integer arguments from the BlockReader
				long a = argReader.getLong(0);
				long b = argReader.getLong(1);
				srvInterface.log("Got values a=%d and b=%d", a, b);

				// Process the arguments and come up with a result. For this
				// example, just add the two arguments together.
				long result = a + b;

				// Write the integer output value.
				resWriter.setLong(result);

				// Advance the output BlocKWriter to the next row.
				resWriter.next();

				// Continue processing input rows until there are no more.
			} while (argReader.next());
		}
	}

}
