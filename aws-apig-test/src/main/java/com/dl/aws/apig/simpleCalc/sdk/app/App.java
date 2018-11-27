package com.dl.aws.apig.simpleCalc.sdk.app;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.opensdk.config.ConnectionConfiguration;
import com.amazonaws.opensdk.config.TimeoutConfiguration;
import com.dl.aws.apig.simpleCalc.sdk.SimpleCalcSdk;
import com.dl.aws.apig.simpleCalc.sdk.SimpleCalcSdkClientBuilder;
import com.dl.aws.apig.simpleCalc.sdk.model.GetTestapigatewayRequest;
import com.dl.aws.apig.simpleCalc.sdk.model.GetTestapigatewayResult;

public class App 
{
    SimpleCalcSdk sdkClient;

    public App() {
        initSdk();
    }

    // The configuration settings are for illustration purposes and may not be a recommended best practice.
    private void initSdk() {
    		AWSStaticCredentialsProvider iamCredentials = new AWSStaticCredentialsProvider(
    				new BasicAWSCredentials("AKIAJOUTF4KJ64JBRXSQ", "bCPf4e4DB6xPa0N3Ud7/sGWz/6J1PiK6Ih5vV2Pg"));
    	
    		SimpleCalcSdkClientBuilder sdkClientBuilder = SimpleCalcSdk.builder();
    		sdkClientBuilder.setIamCredentials(iamCredentials);
        sdkClient = 
        		sdkClientBuilder.connectionConfiguration(
                  new ConnectionConfiguration()
                        .maxConnections(100)
                        .connectionMaxIdleMillis(1000))
              .timeoutConfiguration(
                  new TimeoutConfiguration()
                        .httpRequestTimeout(3000)
                        .totalExecutionTimeout(10000)
                        .socketTimeout(2000))
             
        .build();
        
    		

    }
    // Calling shutdown is not necessary unless you want to exert explicit control of this resource.
    public void shutdown() {
        sdkClient.shutdown();
    }
     


    public static void main( String[] args )
    {
        System.out.println( "Simple calc" );
        App calc = new App();
        
        
		GetTestapigatewayResult res = calc.sdkClient.getTestapigateway(new GetTestapigatewayRequest());
        System.out.printf("%s\n", res);


        
    }
}
