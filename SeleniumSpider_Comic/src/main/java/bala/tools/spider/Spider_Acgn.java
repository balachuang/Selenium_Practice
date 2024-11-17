package bala.tools.spider;

import java.io.IOException;
import java.io.InputStream;
// import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
// import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
// import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;

import bala.tools.model.ComicInfo;
import bala.tools.utils.AppConfig;
import bala.tools.utils.ComicConfig;
import bala.tools.utils.Common;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class Spider_Acgn
{
	@Autowired
	AppConfig appConfig;

	@Autowired
	ComicConfig comicConfig;

	public void run(WebDriver driver) throws IOException, URISyntaxException
	{
		// prepare config
		ComicInfo comicInfo = Common.loadComicInfo(comicConfig);
		String localPath = Common.prepareStoreLocation(appConfig, comicInfo.getName());

		// Login First -- no need

		// Goto overview page & click into the first chapter
		// 這個網站的所有圖片 url 都在同一頁, 不需要換頁. 只需要換下一部.
		logger.info("Navigate to coverpage of {}", comicInfo.getName());
		driver.navigate().to(comicInfo.getCoverUrl());

		// Find start chapter
		int currChap = getCurrChap(driver);
		if (currChap < 0) return;
		while(currChap < comicInfo.getChapStart())
		{
			// currChap = getCurrChap(driver);
			currChap = gotoNextChapter(driver);
			if (currChap < 0)
			{
				driver.close();
				return;
			}
		}

		// start download images
		while(currChap <= comicInfo.getChapEnd())
		{
			// get all images
			List<WebElement> imgElems = driver.findElements(By.cssSelector("#pic_list div.pic"));

			// find the last page number
			WebElement finalElem = imgElems.get(imgElems.size() - 1);
			int totalPage = getPageNumFromImageUrl(finalElem.getAttribute("_src"));

			// go each page
			Iterator<WebElement> imgItor = imgElems.iterator();
			while (imgItor.hasNext())
			{
				WebElement imgElem = imgItor.next();
				int currPage = getPageNumFromImageUrl(imgElem.getAttribute("_src"));
				if (currPage < comicInfo.getPageStart()) continue;
				if (currPage > comicInfo.getPageEnd()) break;

				// Don't break execution even got exception since it may takes lots of time to find specific chapter in Acgn
				try{
					logger.info(String.format("Download: [%s: %03d] - %03d / %03d.", comicInfo.getName(), currChap, currPage, totalPage));
					String imgFName = String.format("%s/CH_%03d__P_%03d.jpg", localPath, currChap, currPage);
					InputStream in = new URI(imgElem.getAttribute("_src")).toURL().openStream();
					Files.copy(in, Paths.get(imgFName), StandardCopyOption.REPLACE_EXISTING);
				}catch(Exception ex){
					logger.error("          Error: {}", ex.getMessage());
				}

				// sleep before next page
				Common.Sleep(appConfig.getRandomInterval());
			}
			logger.info("--- All pages downloaded.");

			// goto next chapter
			currChap = gotoNextChapter(driver);
			if (currChap < 0) break;
		}

		driver.quit();
		logger.info("=== All Chapters Downloaded.");
	}

	private int getCurrChap(WebDriver driver)
	{
		Pattern pattern = Pattern.compile("(\\d+)", Pattern.CASE_INSENSITIVE);
		WebElement chapElem = driver.findElement(By.cssSelector("#content table th div.display_middle"));
		Matcher matcher = pattern.matcher(chapElem.getText());
		int currChap = (matcher.find()) ? Integer.parseInt(matcher.group(1)) : -1;
		if (currChap < 0) logger.error("Find Current Chapter Error.");
		return currChap;
	}

	private int getPageNumFromImageUrl(String url)
	{
		Pattern pattern = Pattern.compile("/(\\d+)\\.jpg", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url);
		int pageNum = (matcher.find()) ? Integer.parseInt(matcher.group(1)) : -1;
		if (pageNum < 0) logger.error("Find Max Page Number Error.");
		return pageNum;
	}

	private int gotoNextChapter(WebDriver driver)
	{
		try{
			WebElement nextElem = driver.findElement(By.cssSelector("#content table th div.display_right a"));
			nextElem.click();
		}catch(Exception ex){
			logger.error("Find Next Chapter Error: {}", ex.getMessage());
			return -1;
		}

		return getCurrChap(driver);
	}
}
