package my.spider.utils;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;


@Data
@Slf4j
@Component
public class BrowserManager
{
	@Autowired
	AppConfig appConfig;

	public WebDriver InitBrowser()
	{
		String driverType = appConfig.getBrowserDriver();
		WebDriver driver = Tools.createBrowser(driverType, null);

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(appConfig.getGlobalTimeout()));
		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(appConfig.getScriptTimeout()));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(appConfig.getPageTimeout()));
		Tools.resizeBrowser(driver);

		// InitContent(driver);
		return driver;
	}

	// private void InitContent(WebDriver driver)
	// {
	// 	driver.navigate().to("about:blank");
	// 	JavascriptExecutor jsExec = (JavascriptExecutor)driver;
	// 	jsExec.executeScript(appConfig.getSpiderBrowserJs());
	// }

	// public void showExecMessage(WebDriver driver)
	// {
	// 	JavascriptExecutor jsExec = (JavascriptExecutor)driver;
	// 	jsExec.executeScript(appConfig.getMessageBrowserJs());
	// }
}
