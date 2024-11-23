package bala.tools.spider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
// import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
// import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import bala.tools.utils.AppConfig;
import bala.tools.utils.CookieManager;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@Order(1)
public class Router implements ApplicationRunner
{
	// @Autowired
	// private Environment env;

	@Autowired
	AppConfig appConfig;

	@SuppressWarnings("deprecation")
	public void run(ApplicationArguments args) throws MalformedURLException, IOException, URISyntaxException, InterruptedException
	{
		// === initialize WebDriver

		// hide chrome by chrome option
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");

		// Initialize WebDriver
		WebDriver driver = appConfig.isHideBrowser() ? new ChromeDriver(options) : new ChromeDriver();

		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.MINUTES);
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.MINUTES);
		// if (appConfig.isMinimizeBrowser()) driver.manage().window().minimize();

		// === Connect to Youtube Watch-Later list
		List<Cookie> ytCookies = CookieManager.ReadCookiesFromFile(appConfig.getYoutubeCookiePath());
		driver.navigate().to(appConfig.getYoutubeHomeUrl());
		CookieManager.SetCookiesToWebFriver(driver, ytCookies);
		driver.navigate().to(appConfig.getYoutubeWatchUrl());

		// === Refresh Youtube Watch-Later list every X seconds
		boolean running = true;
		while(running)
		{

			// get video list.
			List<WebElement> videoList = driver.findElements(By.tagName("ytd-playlist-video-renderer"));
			if (videoList.size() > 0)
			{
				// download video by yt-dlp
				// 同一個 cookie 檔要同時給 selenium 和 yt-dlp 用, 
				// 而且 yt-dlp 用完還要砍掉. 看起來不可行.

				// https://www.youtube.com/watch?v=JAg_t678U3w&list=WL&index=1&pp=gAQBiAQB

				// https://www.youtube.com/playlist?list=WL
				// /watch?v=JAg_t678U3w&amp;list=WL&amp;index=1&amp;pp=gAQBiAQB
				
				// find: <a id="video-title"
				// merge url & urldecode(href)
				// .\yt-dlp.exe --config-location myDownload.conf https://www.youtube.com/playlist?list=WL

				// remove downloaded videos
				// check video category
				// rename video
				logger.info("download video...");
			}

			// sleep 5 secs
			Thread.sleep(5000);
		}
	}
}
