package com.dl.vertica.udx.udtf;

import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.PartitionReader;
import com.vertica.sdk.PartitionWriter;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.SizedColumnTypes;
import com.vertica.sdk.TransformFunction;
import com.vertica.sdk.TransformFunctionFactory;
import com.vertica.sdk.UdfException;

public class TokenFactory extends TransformFunctionFactory {

	@Override
	public TransformFunction createTransformFunction(ServerInterface arg0) {
		return new TokenizeString();
	}

	@Override
	public void getPrototype(ServerInterface arg0, ColumnTypes argTypes, ColumnTypes returnType) {
        // Define two input columns: an INTEGER and a VARCHAR 
        argTypes.addInt(); // Row id
        argTypes.addVarchar(); // Line of text
        
        // Define the output columns
        returnType.addVarchar(); // The token
        returnType.addInt(); // The row in which this token occurred
        returnType.addInt(); // The position in the row of the token
	}

	@Override
	public void getReturnType(ServerInterface arg0, SizedColumnTypes inputTypes, SizedColumnTypes outputTypes) throws UdfException {
        // Set the maximum width of the token return column to the width 
        // of the input text column and name the output column "Token"
        outputTypes.addVarchar(
            inputTypes.getColumnType(1).getStringLength(), "token");
        // Name the two INTEGER output columns
        outputTypes.addInt("row_id");
        outputTypes.addInt("token_position");
	}

	public class TokenizeString extends TransformFunction {
		@Override
		public void processPartition(ServerInterface srvInterface, PartitionReader inputReader,
				PartitionWriter outputWriter) throws UdfException, DestroyInvocation {
			try {
				// Loop over all rows passed in in this partition.
				do {
					// Test if the row ID is null. If so, then do not
					// process any further. Skip to next row of input.
					if (inputReader.isLongNull(0)) {
						srvInterface.log("Skipping row with null id.");
						continue; // Move on to next row of input
					}
					// Get the row ID now that we know it has a value
					long rowId = inputReader.getLong(0);

					// Test if the input string is NULL. If so, return NULL for token and string position.
					if (inputReader.isStringNull(1)) {
						outputWriter.setStringNull(0);
						outputWriter.setLong(1, rowId);
						outputWriter.setLongNull(2);
						outputWriter.next(); // Move to next line of output
					} else {
						// Break string into tokens. Output each word as its own value.
						String[] tokens = inputReader.getString(1).split("\\s+");
						// Output each token on a separate row.
						for (int i = 0; i < tokens.length; i++) {
							outputWriter.getStringWriter(0).copy(tokens[i]);
							outputWriter.setLong(1, rowId);
							outputWriter.setLong(2, i);
							outputWriter.next(); // Advance to next row of
													// output
						}
					}
					// Loop until there are no more input rows in partition.
				} while (inputReader.next());
			}catch (Exception e) {// Prevent exceptions from bubbling back up to server. Uncaught exceptions will cause a transaction rollback.
				// Use more robust error handling in your own UDTFs. This example just sends a message to the log.
				srvInterface.log("Exception: " + e.getClass().getSimpleName() + "Message: " + e.getMessage());
			}
		}
	}
}
