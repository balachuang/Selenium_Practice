package bala.tools.spider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import bala.tools.utils.AppConfig;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@Order(1)
public class Router implements ApplicationRunner
{
	@Autowired
	private Environment env;

	@Autowired
	AppConfig appConfig;

	@Autowired
	Spider_Haodoo spdr_Haodoo;

	@SuppressWarnings("deprecation")
	public void run(ApplicationArguments args) throws InterruptedException
	{
		// Initialize WebDriver
		WebDriver driver = null;

		switch (appConfig.getBrowserDriver()) {
			case "Chrome":
				// ChromeOptions options = new ChromeOptions();
				// options.addArguments("--disable-dev-shm-usage");
				driver = new ChromeDriver();
				break;
			default:
				driver = new ChromeDriver();
				break;
		}

		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.MINUTES);
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.MINUTES);
		if (appConfig.isMinimizeBrowser()) driver.manage().window().minimize();
		if (appConfig.isHideBrowser()) driver.manage().window().setPosition(new Point(
			driver.manage().window().getSize().getWidth() * 3,
			driver.manage().window().getPosition().getY()
		));

		// Lauch responding Spider
		String[] activeProfs = env.getActiveProfiles();
		String activeProf = (activeProfs.length >= 1) ? activeProfs[0] : "";
		switch (activeProf) {
			case "Haodoo": // 好讀
				// for (int n=0; n<args.length; ++n) spdr_Haodoo.run(driver, args[n]);
				spdr_Haodoo.run(driver);
				break;
			default:
				logger.error("No target spider defined, please check your property file.");
				break;
		}

		if (driver != null) driver.close();
	}
}
