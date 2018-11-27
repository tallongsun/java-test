package com.dl.vertica.udx.udtf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.MultiPhaseTransformFunctionFactory;
import com.vertica.sdk.PartitionReader;
import com.vertica.sdk.PartitionWriter;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.SizedColumnTypes;
import com.vertica.sdk.TransformFunction;
import com.vertica.sdk.TransformFunctionPhase;
import com.vertica.sdk.UdfException;

public class InvertedIndexFactory extends MultiPhaseTransformFunctionFactory {

	/**
	 * Extracts terms from documents.
	 */
	public class ForwardIndexPhase extends TransformFunctionPhase {

		@Override
		public TransformFunction createTransformFunction(ServerInterface arg0) {
			return new ForwardIndexBuilder();
		}

		@Override
		public void getReturnType(ServerInterface arg0,
				SizedColumnTypes inputTypes, SizedColumnTypes outputTypes) {

			// Sanity checks on input we've been given.
			// Expected input: (doc_id INTEGER, text VARCHAR)
			ArrayList<Integer> argCols = new ArrayList<Integer>();

			inputTypes.getArgumentColumns(argCols);

			if (argCols.size() < 2
					|| !inputTypes.getColumnType(argCols.get(0)).isInt()
					|| !inputTypes.getColumnType(argCols.get(1)).isVarchar()) {
				throw new UdfException(0,
						"Funciton only accepts two arguments (INTEGER, VARCHAR)");
			}

			// Output of this phase is:
			// (term_freq INTEGER) OVER(PBY term VARCHAR OBY doc_id INTEGER)

			// Number of times term appears within a document.
			outputTypes.addInt("term_freq");

			// Add analytic clause columns: (PARTITION BY term ORDER BY doc_id).

			// The length of any term is at most the size of the entire
			// document.
			outputTypes.addVarcharPartitionColumn(
					inputTypes.getColumnType(argCols.get(1)).getStringLength(),
					"term");

			// Add order column on the basis of the document id's data type.
			outputTypes.addOrderColumn(
					inputTypes.getColumnType(argCols.get(0)), "doc_id");
		}

	}

	public class InvertedIndexPhase extends TransformFunctionPhase {

		@Override
		public TransformFunction createTransformFunction(
				ServerInterface srvInterface) {
			return new InvertedIndexBuilder();
		}

		@Override
		public void getReturnType(ServerInterface srvInterface,
				SizedColumnTypes inputTypes, SizedColumnTypes outputTypes) {
			// Sanity checks on input we've been given.
			// Expected input:
			// (term_freq INTEGER) OVER(PBY term VARCHAR OBY doc_id INTEGER)
			ArrayList<Integer> argCols = new ArrayList<Integer>();
			inputTypes.getArgumentColumns(argCols);

			ArrayList<Integer> pByCols = new ArrayList<Integer>();
			inputTypes.getPartitionByColumns(pByCols);

			ArrayList<Integer> oByCols = new ArrayList<Integer>();
			inputTypes.getOrderByColumns(oByCols);

			if (argCols.size() != 1 || pByCols.size() != 1
					|| oByCols.size() != 1
					|| !inputTypes.getColumnType(argCols.get(0)).isInt()
					|| !inputTypes.getColumnType(pByCols.get(0)).isVarchar()
					|| !inputTypes.getColumnType(oByCols.get(0)).isInt()) {
				throw new UdfException(
						0,
						"Function expects an argument (INTEGER) with "
								+ "analytic clause OVER(PBY VARCHAR OBY INTEGER)");
			}

			// Output of this phase is:
			// (term VARCHAR, doc_id INTEGER, term_freq INTEGER, corp_freq
			// INTEGER).
			outputTypes.addVarchar(inputTypes.getColumnType(pByCols.get(0))
					.getStringLength(), "term");
			outputTypes.addInt("doc_id");

			// Number of times term appears within the document.
			outputTypes.addInt("term_freq");

			// Number of documents where the term appears in.
			outputTypes.addInt("corp_freq");

		}
	}

	@Override
	public void getPhases(ServerInterface srvInterface,
			Vector<TransformFunctionPhase> phases) {
		ForwardIndexPhase fwardIdxPh;
		InvertedIndexPhase invIdxPh;
		
		fwardIdxPh = new ForwardIndexPhase();
		invIdxPh = new InvertedIndexPhase();
		
		fwardIdxPh.setPrepass();
		phases.add(fwardIdxPh);
		phases.add(invIdxPh);
	}

	@Override
	public void getPrototype(ServerInterface srvInterface,
			ColumnTypes argTypes, ColumnTypes returnTypes) {

		// Expected input: (doc_id INTEGER, text VARCHAR).
		argTypes.addInt();
		argTypes.addVarchar();

		// Output is: (term VARCHAR, doc_id INTEGER, term_freq INTEGER,
		// corp_freq INTEGER)
		returnTypes.addVarchar();
		returnTypes.addInt();
		returnTypes.addInt();
		returnTypes.addInt();
	}
	
	
	public class ForwardIndexBuilder extends TransformFunction {

		@Override
		public void processPartition(ServerInterface srvInterface,
				PartitionReader inputReader, PartitionWriter outputWriter)
				throws UdfException, DestroyInvocation {
			
	        // Sanity checks on input/output we've been given.
	        // Expected input: (doc_id INTEGER, text VARCHAR)		
			SizedColumnTypes inTypes = inputReader.getTypeMetaData();
			ArrayList<Integer> argCols = new ArrayList<Integer>();
			inTypes.getArgumentColumns(argCols);
			srvInterface.log("f:in:arg:"+argCols+"");
			
			ArrayList<Integer> pByCols = new ArrayList<Integer>();
			inTypes.getPartitionByColumns(pByCols);
			srvInterface.log("f:in:part:"+pByCols+"");

			ArrayList<Integer> oByCols = new ArrayList<Integer>();
			inTypes.getOrderByColumns(oByCols);
			srvInterface.log("f:in:order:"+oByCols+"");

			if (argCols.size() < 2
					|| !inTypes.getColumnType(argCols.get(0)).isInt()
					|| !inTypes.getColumnType(argCols.get(1)).isVarchar()) {
				throw new UdfException(0,
						"Function Expects two arguments(Integer, Varchar)");
			}

			SizedColumnTypes outTypes = outputWriter.getTypeMetaData();
			ArrayList<Integer> outArgsCols = new ArrayList<Integer>();
			outTypes.getArgumentColumns(outArgsCols);
			srvInterface.log("f:out:arg:"+outArgsCols+"");

			ArrayList<Integer> outPbyCols = new ArrayList<Integer>();
			outTypes.getPartitionByColumns(outPbyCols);
			srvInterface.log("f:out:arg:"+outPbyCols+"");

			ArrayList<Integer> outObyCols = new ArrayList<Integer>();
			outTypes.getOrderByColumns(outObyCols);
			srvInterface.log("f:out:arg:"+outObyCols+"");

			if (outArgsCols.size() != 1
					|| !outTypes.getColumnType(outArgsCols.get(0)).isInt()
					|| outPbyCols.size() != 1
					|| !outTypes.getColumnType(outPbyCols.get(0)).isVarchar()
					|| outObyCols.size() != 1
					|| !outTypes.getColumnType(outObyCols.get(0)).isInt()) {
				throw new UdfException(0,
						"Function expects to emit an (INTEGER) argument"
								+ " with OVER(PBY (VARCHAR) OBY (INTEGER)) clause.");
			}

			Map<String, Long> docTerms = new HashMap<String,Long>();

			//Extract terms from Documents
			do {
				docTerms.clear();
				long docID = inputReader.getLong(argCols.get(0));

				String text = inputReader.getString(argCols.get(1)).toLowerCase();

				if (text != null && !text.isEmpty()) {
					String[] terms = text.split(" ");
					int i = 0;
					while (i < terms.length) {
						if (!docTerms.containsKey(terms[i])) {
							docTerms.put(terms[i], 1L); // new document term
						} else {
							docTerms.put(terms[i], docTerms.get(terms[i]) + 1); // term was already seen. Increment its count.
						}
						i++;
					}
				}
				
				// output: (term_freq) OVER(PBY term OBY doc_id).
				for(Entry<String,Long> docTerm : docTerms.entrySet()){
					outputWriter.setLong(outArgsCols.get(0), docTerm.getValue()); //term_freq
					outputWriter.setString(outPbyCols.get(0), docTerm.getKey()); // term
					outputWriter.setLong(outObyCols.get(0),docID); // doc_id
					outputWriter.next();
				}

			} while (inputReader.next());
		}

	}

	
	public class InvertedIndexBuilder extends TransformFunction {

		@Override
		public void processPartition(ServerInterface srvInterface,
				PartitionReader inputReader, PartitionWriter outputWriter)
				throws UdfException, DestroyInvocation {

	        // Sanity checks on input/output we've been given.
	        // Expected input: (term_freq INTEGER) OVER(PBY term VARCHAR OBY doc_id INTEGER)	
			SizedColumnTypes inTypes = inputReader.getTypeMetaData();

			ArrayList<Integer> argCols = new ArrayList<Integer>();
			inTypes.getArgumentColumns(argCols);
			srvInterface.log("i:in:arg:"+argCols+"");

			ArrayList<Integer> pByCols = new ArrayList<Integer>();
			inTypes.getPartitionByColumns(pByCols);
			srvInterface.log("i:in:part:"+pByCols+"");

			ArrayList<Integer> oByCols = new ArrayList<Integer>();
			inTypes.getOrderByColumns(oByCols);
			srvInterface.log("i:in:order:"+oByCols+"");

			if (argCols.size() != 1 || pByCols.size() != 1 || oByCols.size() != 1
					|| !inTypes.getColumnType(argCols.get(0)).isInt()
					|| !inTypes.getColumnType(pByCols.get(0)).isVarchar()
					|| !inTypes.getColumnType(oByCols.get(0)).isInt()) {
				throw new UdfException(
						0,
						"Function expects an argument (INTEGER) with analytic clause OVER(PBY VARCHAR OBY INTEGER)");
			}
			
			SizedColumnTypes outTypes = outputWriter.getTypeMetaData();
			ArrayList<Integer> outArgCols = new ArrayList<Integer>();
			outTypes.getArgumentColumns(outArgCols);
			srvInterface.log("i:out:arg:"+outArgCols+"");
			
			ArrayList<Integer> outPbyCols = new ArrayList<Integer>();
			outTypes.getPartitionByColumns(outPbyCols);
			srvInterface.log("i:out:part:"+outPbyCols+"");

			ArrayList<Integer> outObyCols = new ArrayList<Integer>();
			outTypes.getOrderByColumns(outObyCols);
			srvInterface.log("i:out:order:"+outObyCols+"");
			
			if(outArgCols.size() != 4 || 
					!outTypes.getColumnType(outArgCols.get(0)).isVarchar() ||
					!outTypes.getColumnType(outArgCols.get(1)).isInt() ||
					!outTypes.getColumnType(outArgCols.get(2)).isInt() ||
					!outTypes.getColumnType(outArgCols.get(3)).isInt()){
				throw new UdfException(0,"Function expects to emit four columns " +
						"(VARCHAR, INTEGER, INTEGER, INTEGER)");
			}

			String term = inputReader.getString(pByCols.get(0));
			int corpFreq = 0;
			
	        // Count the number of documents the term appears in.
			do{
				// Output: (term VARCHAR, doc_id INTEGER, term_freq INTEGER, NULL).
				
				outputWriter.setString(outArgCols.get(0), term); // term
				outputWriter.setLong(outArgCols.get(1), inputReader.getLong(oByCols.get(0))); // doc_id
				outputWriter.setLong(outArgCols.get(2), inputReader.getLong(argCols.get(0))); // term_freq
				outputWriter.setLongNull(outArgCols.get(3)); // corp_freq
				outputWriter.next();
				corpFreq++;
			}while(inputReader.next());

			// Piggyback term's corpus frequency in the output tuple as:
	        //   (term VARCHAR, NULL, NULL, corp_freq INTEGER)
			outputWriter.setString(outArgCols.get(0), term); // term
			outputWriter.setLongNull(outArgCols.get(1)); // doc_id
			outputWriter.setLongNull(outArgCols.get(2)); // term_freq
			outputWriter.setLong(outArgCols.get(3), corpFreq); // corp_freq
			outputWriter.next();

		}
	}
}
