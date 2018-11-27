package com.dl.selenium;

import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;

public class IosTest {
	//没好用啊o(╯□╰)o,改用appium了
	public static void main(String[] args) throws Exception {
	    DesiredCapabilities safari = IOSCapabilities.iphone("Safari");
	    
	    safari.setCapability("platformVersion", "9.3");
	    
	    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"), safari);
	    

	    driver.get("http://www.ebay.co.uk/");

	    System.out.println(driver.getTitle());
	    driver.quit();
	}
	    
}
