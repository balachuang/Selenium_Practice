package bala.tools.spider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

// import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import bala.tools.utils.AppConfig;
import bala.tools.utils.ComicConfig;
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
	ComicConfig comicConfig;

	@Autowired
	Spider_8Comic spdr_8comic;

	@Autowired
	Spider_Cartoonmad spdr_Cartoonmad;

	@Autowired
	Spider_Acgn spdr_Acgn;

	@Autowired
	Spider_Komiic spdr_Komiic;

	@Autowired
	Spider_Apexmh spdr_Apexmh;

	@SuppressWarnings("deprecation")
	public void run(ApplicationArguments args) throws MalformedURLException, IOException, URISyntaxException, InterruptedException
	{
		// hide chrome by chrome option
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");

		// Initialize WebDriver
		WebDriver driver = null;

		switch (appConfig.getBrowserDriver()) {
			case "Chrome":
				driver = appConfig.isHideBrowser() ? new ChromeDriver(options) : new ChromeDriver();
				break;
			default:
				driver = appConfig.isHideBrowser() ? new ChromeDriver(options) : new ChromeDriver();
				break;
		}

		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.MINUTES);
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.MINUTES);
		if (appConfig.isMinimizeBrowser()) driver.manage().window().minimize();
		// if (appConfig.isHideBrowser()) driver.manage().window().setPosition(new Point(
		// 	driver.manage().window().getSize().getWidth() * 3,
		// 	driver.manage().window().getPosition().getY()
		// ));

		// Lauch responding Spider
		String[] activeProfs = env.getActiveProfiles();
		String activeProf = (activeProfs.length >= 1) ? activeProfs[0] : "";
		switch (activeProf) {
			case "8comic":  // 無限動漫
			case "8-comic": // 無限動漫
				spdr_8comic.run(driver);
				break;
			case "cartoonmad": // 動漫狂
				spdr_Cartoonmad.run(driver);
				break;
			case "acgn": // 動漫戲說
				spdr_Acgn.run(driver);
				break;
			case "apexmh": // 紳士漫畫
				spdr_Apexmh.run(driver);
				break;
			case "komiic": // 紳士漫畫
				spdr_Komiic.run(driver);
				break;
			default:
				logger.error("No target spider defined, please check your property file.");
				break;
		}
	}
}
