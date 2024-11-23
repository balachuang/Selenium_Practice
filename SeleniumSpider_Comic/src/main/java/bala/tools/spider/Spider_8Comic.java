package bala.tools.spider;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bala.tools.model.ChapInfo;
import bala.tools.model.ComicInfo;
import bala.tools.utils.AppConfig;
import bala.tools.utils.ComicConfig;
import bala.tools.utils.Common;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class Spider_8Comic
{
	@Autowired
	AppConfig appConfig;

	@Autowired
	ComicConfig comicConfig;

	public void run(WebDriver driver)
	{
		// prepare config
		WebElement webElem = null;
		ComicInfo comicInfo = Common.loadComicInfo(comicConfig);
		String localPath = "";
		try {
			localPath = Common.prepareStoreLocation(appConfig, comicInfo.getName());
		} catch (IOException ex) {
			logger.error("Prepare Local Path Fail: ({}) {}", ex.getClass().getName(), ex.getMessage());
			logger.error("Stop Spider due to this Exception, please check your config.");
			return;
		}

		// Login First
		// 用 navigate().to() 和 get() 一樣, 但是 navigate 可以有歷史記錄, 也可以保留 cookie.
		if (comicInfo.isNeedLogin())
		{
			logger.info("Login to 8-Comic");
			driver.navigate().to(comicInfo.getLoginUrl());

			webElem = driver.findElement(By.id("Form1")).findElement(By.name("username"));
			webElem.click();
			webElem.sendKeys(comicInfo.getUsername());

			webElem = driver.findElement(By.id("password"));
			webElem.click();
			webElem.sendKeys(comicInfo.getPassword());

			webElem = driver.findElement(By.id("Form1")).findElement(By.name("submit"));
			webElem.click();
		}

		// Goto overview page for all id pre=catch
		logger.info("Navigate to coverpage of {}", comicInfo.getName());
		driver.navigate().to(comicInfo.getCoverUrl());

		// prepare chapter element ID --> 不能先抓所有 element, 當點進漫畫頁再回到首頁, element 就不一樣了.
		ArrayList<ChapInfo> chapInfoArray = new ArrayList<ChapInfo>();
		// ArrayList<String> chapCssArray = new ArrayList<String>();
		if (comicInfo.getSpecialChaps().length <= 0)
		{
			int currChap = comicInfo.getChapStart();
			int finalChap = comicInfo.getChapEnd();
			while(currChap <= finalChap)
			{
				ChapInfo chapInfo = new ChapInfo();
				chapInfo.setCss(String.format("a#c%d", currChap));
				chapInfo.setName(String.format("%03d", currChap));
				chapInfoArray.add(chapInfo);
				// chapCssArray.add(String.format("a#c%d", currChap));
				++currChap;
			}
		}else{
			List<WebElement> chapList = driver.findElements(By.cssSelector("a.Ch"));
			for (String chapName : comicInfo.getSpecialChaps())
			{
				for (WebElement chapElem : chapList)
				{
					String chapText = chapElem.getText().trim();
					if (chapText.equals(chapName))
					{
						ChapInfo chapInfo = new ChapInfo();
						chapInfo.setCss(String.format("a#%s", chapElem.getAttribute("id")));
						chapInfo.setName(Common.removeIllegalFName(chapName));
						chapInfoArray.add(chapInfo);
						// chapCssArray.add(String.format("a#%s", chapElem.getAttribute("id")));
						break;
					}
				}
			}
		}

		// String localPath = localConfig.getPath().trim();
		ArrayList<String> skipPageArray = new ArrayList<String>();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		for (ChapInfo chapInfo : chapInfoArray)
		{
			// find and click into currChap
			WebElement chapElem = driver.findElement(By.cssSelector(chapInfo.getCss()));
			// String currChapName = chapElem.getText();
			chapElem.click();

			// all images are located in the same page, just scroll down...
			int totalPics = driver.findElements(By.cssSelector("div.comics-pic")).size();
			for (int currPage=comicInfo.getPageStart(); currPage<=Math.min(totalPics, comicInfo.getPageEnd()); ++currPage)
			{
				// find image
				webElem = driver.findElement(By.cssSelector(String.format("div.comics-pic a[name=\"%d\"]", currPage)));

				// scroll to image --> test Actions
				// js.executeScript(String.format("window.scrollTo(0,%d)", webElem.getLocation().getY()));
				new Actions(driver).scrollToElement(webElem);

				// download
				try{
					logger.info(String.format("Download: [%s: %s] - %03d / %03d.", comicInfo.getName(), chapInfo.getName(), currPage, totalPics));
					String imgFName = String.format("%s/CH%s_P%03d.jpg", localPath, chapInfo.getName(), currPage);
					InputStream in = new URI(webElem.findElement(By.xpath("following-sibling::*")).getAttribute("src")).toURL().openStream();
					Files.copy(in, Paths.get(imgFName), StandardCopyOption.REPLACE_EXISTING);
				}catch(Exception ex){
					logger.error("Download Image Fail: ({}) {}", ex.getClass().getName(), ex.getMessage());
					logger.error("Skip Page due to Exception: {}", currPage);
					skipPageArray.add(String.format("Chap: %s, Page: %d", chapInfo.getName(), currPage));
				}

				// sleep before next page
				Common.Sleep(appConfig.getRandomInterval());
			}

			// Goto overview page & click into the first chapter
			logger.info("Back to coverpage of {}", comicInfo.getName());
			driver.navigate().to(comicInfo.getCoverUrl());
		}

		// show all skip pages...
		for (String skip : skipPageArray) logger.info("Skip: {}", skip);

		driver.quit();
		logger.info("=== All Chapters Downloaded.");
	}
}
