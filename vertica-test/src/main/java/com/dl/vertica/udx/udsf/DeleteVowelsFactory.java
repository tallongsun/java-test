package com.dl.vertica.udx.udsf;

import com.dl.vertica.udx.libs.VowelRemover;
import com.vertica.sdk.BlockReader;
import com.vertica.sdk.BlockWriter;
import com.vertica.sdk.ColumnTypes;
import com.vertica.sdk.DestroyInvocation;
import com.vertica.sdk.ScalarFunction;
import com.vertica.sdk.ScalarFunctionFactory;
import com.vertica.sdk.ServerInterface;
import com.vertica.sdk.SizedColumnTypes;
import com.vertica.sdk.UdfException;

public class DeleteVowelsFactory extends ScalarFunctionFactory {

    @Override
    public ScalarFunction createScalarFunction(ServerInterface arg0) {
        return new DeleteVowels();
    }

    @Override
    public void getPrototype(ServerInterface arg0, ColumnTypes argTypes,
            ColumnTypes returnTypes) {
        // Accept a single string and return a single string.
        argTypes.addVarchar();
        returnTypes.addVarchar();
    }
    
    @Override
    public void getReturnType(ServerInterface srvInterface,
            SizedColumnTypes argTypes,
            SizedColumnTypes returnType){
        returnType.addVarchar(argTypes.getColumnType(0).getStringLength(), "RemovedVowels");
    }
    
    public class DeleteVowels extends ScalarFunction
    {
        @Override
        public void processBlock(ServerInterface arg0, BlockReader argReader,
                BlockWriter resWriter) throws UdfException, DestroyInvocation {
            
            // Create an instance of the  VowelRemover object defined in 
            // the library.
            VowelRemover remover = new VowelRemover();
            
            do {
                String instr = argReader.getString(0);
                // Call the removevowels method defined in the library.
                resWriter.setString(remover.removevowels(instr));
                resWriter.next();
            } while (argReader.next());
        }
    }

}
