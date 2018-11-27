package com.dl.selenium.script;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CaasAdminLoginScript extends BaseScript {

	private String pageUrl = "http://caas-admin-dev/";

	public CaasAdminLoginScript(WebDriver webDriver) {
		super(webDriver);
	}

	public void run() throws Exception{

		this.driver.get(this.pageUrl);

		TimeUnit.SECONDS.sleep(2);

		WebElement userName = driver.findElement(By.name("email"));
		WebElement passWord = driver.findElement(By.name("password"));
		WebElement submit = driver.findElement(By.id("login"));

		userName.sendKeys("admin@caas.rongcaptical.cn");
		passWord.sendKeys("123456");

		submit.click();
	}

}
