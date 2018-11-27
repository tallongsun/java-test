package com.dl.vertica.udx.udanf;

import com.vertica.sdk.AnalyticFunction;
import com.vertica.sdk.AnalyticFunctionFactory;
import com.vertica.sdk.AnalyticPartitionReader;
import com.vertica.sdk.AnalyticPartitionWriter;
import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.SizedColumnTypes;
import com.vertica.sdk.UdfException;

public class RankFactory extends AnalyticFunctionFactory {

	@Override
	public AnalyticFunction createAnalyticFunction(ServerInterface arg0) {
		return new Rank();
	}

	@Override
	public void getPrototype(ServerInterface arg0, ColumnTypes argTypes, ColumnTypes returnType) {
		returnType.addInt();
	}

	@Override
	public void getReturnType(ServerInterface arg0, SizedColumnTypes argTypes, SizedColumnTypes returnType)
			throws UdfException {
		returnType.addInt();
	}

	public class Rank extends AnalyticFunction {
		private int rank, numRowsWithSameOrder;

		@Override
		public void processPartition(ServerInterface srvInterface, AnalyticPartitionReader inputReader,
				AnalyticPartitionWriter outputWriter) throws UdfException, DestroyInvocation {
			rank = 0;
			numRowsWithSameOrder = 1;
			do {
				if (!inputReader.isNewOrderByKey()) {
					++numRowsWithSameOrder;
				} else {
					rank += numRowsWithSameOrder;
					numRowsWithSameOrder = 1;
				}
				outputWriter.setLong(0, rank);
				outputWriter.next();
			} while (inputReader.next());
		}
	}
}
