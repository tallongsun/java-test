package com.dl.selenium.script;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaiduSearchScript extends BaseScript{

	private String pageUrl="http://www.baidu.com/";
	
	public BaiduSearchScript(WebDriver webDriver) {
		super(webDriver);
	}

	
	@Override
	public void run() {
		driver.get(pageUrl);
		
		WebElement element = driver.findElement(By.name("wd"));

		element.sendKeys("Cheese!");

		element.submit();

		System.out.println("Page title is: " + driver.getTitle());

		// Wait for the page to load, timeout after 10 seconds
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().toLowerCase().startsWith("cheese!");
			}
		});

		System.out.println("Page title is: " + driver.getTitle());
	}

}
