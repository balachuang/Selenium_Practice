package bala.tools.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
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
public class Spider_Cartoonmad
{
	@Autowired
	AppConfig appConfig;

	@Autowired
	ComicConfig comicConfig;

	// 電鋸人 Chap1 直接抓不能抓? 但是用 debug mode 就可以? 要 wait ?
	public void run(WebDriver driver) throws MalformedURLException, IOException, URISyntaxException, InterruptedException
	{
		// prepare config
		WebElement webElem = null;
		ComicInfo comicInfo = Common.loadComicInfo(comicConfig);
		String localPath = Common.prepareStoreLocation(appConfig, comicInfo.getName());

		// Login First --> 目前不需要. 等用到再寫.

		// Goto overview page & click into the first chapter
		logger.info("Navigate to coverpage of {}", comicInfo.getName());
		driver.navigate().to(comicInfo.getCoverUrl());

		Pattern pattern = Pattern.compile("(\\d+)", Pattern.CASE_INSENSITIVE);
		int currChap = comicInfo.getChapStart();
		int finalChap = comicInfo.getChapEnd();
		while(currChap <= finalChap)
		{
			// find and click into currChap
			ArrayList<String> tabs = new ArrayList<String>();
			List<WebElement> webElems = driver.findElements(By.cssSelector("#info a"));
			String chText = String.format("第 %03d 話", currChap);
			webElem = getElementbyInnerText(webElems, chText);
			if (webElem != null)
			{
				webElem.click();
				tabs = new ArrayList<String> (driver.getWindowHandles());
				driver.switchTo().window(tabs.get(1));
			}else{
				logger.error("Find chapter number ERROR, please check HTML content.");
				break;
			}

			// read pages, get all images before
			while (true)
			{
				// get current page and total page
				int totalPage = driver.findElements(By.cssSelector("select option")).size() - 1;
				if (totalPage <= 0) break; // total page not found.

				webElem = driver.findElement(By.cssSelector("select option[selected]"));
				Matcher matcher = pattern.matcher(webElem.getText());
				int currPage = (matcher.find()) ? Integer.parseInt(matcher.group(1)) : -1;
				if (currPage < 0) break; // page number not found

				// check page in range and download image
				if ((currPage >= comicInfo.getPageStart()) && (currPage <= comicInfo.getPageEnd()))
				{
					webElem = findMainImage(driver, currPage);
					if (webElem != null)
					{
						logger.info(String.format("Download: [%s: %03d] - %03d / %03d.", comicInfo.getName(), currChap, currPage, totalPage));
						String imgFName = String.format("%s/CH_%03d__P_%03d.jpg", localPath, currChap, currPage);
						InputStream in = new URI(webElem.getAttribute("src")).toURL().openStream();
						Files.copy(in, Paths.get(imgFName), StandardCopyOption.REPLACE_EXISTING);
					}
				}

				// go next page or directly jump to start page
				if ((currPage < totalPage) && (currPage < comicInfo.getPageEnd()))
				{
					// sleep before next page
					Common.Sleep(appConfig.getRandomInterval());

					if (currPage < comicInfo.getPageStart())
					{
						// current page < start page, -> jump to Start Page
						logger.info(String.format("Jump to page: {}", comicInfo.getPageStart()));
						webElem = findPageLink(driver, comicInfo.getPageStart());
						if (webElem == null) break;
						webElem.click();
					}else{
						// current page >= start page, -> go Next Page
						webElem = findNextLink(driver);
						if (webElem == null) break;
						webElem.click();
					}
				}else{
					logger.info("--- All pages downloaded.");
					break;
				}
			}

			// this chapter done. go next chapter
			driver.close();
			driver.switchTo().window(tabs.get(0));

			// go next chapter
			++currChap;
		}

		driver.quit();
		logger.info("=== All Chapters Downloaded.");
	}

	private WebElement getElementbyInnerText(List<WebElement> imgElems, String text)
	{
		for (WebElement webElem : imgElems)
		{
			String elemText = webElem.getText();
			if (elemText.equals(text)) return webElem;
		}
		return null;
	}

	private WebElement findMainImage(WebDriver driver, int currPage)
	{
		String srcText = String.format("%03d.jpg", currPage);
		List<WebElement> imgElems = driver.findElements(By.tagName("img"));
		for (WebElement webElem : imgElems)
		{
			String elemSrc = webElem.getAttribute("src");
			if (elemSrc.indexOf(srcText) >= 0) return webElem;
		}
		return null;
	}

	private WebElement findNextLink(WebDriver driver)
	{
		List<WebElement> linkElems = driver.findElements(By.tagName("a"));
		for (WebElement linkElem : linkElems)
		{
			try{
				// check if contains NextPage image
				WebElement webElem = linkElem.findElement(By.tagName("img"));
				if (webElem == null) continue;

				String elemSrc = webElem.getAttribute("src");
				if (elemSrc.indexOf("/image/panen.png") >= 0) return webElem;
			}catch(Exception ex){
				// no child element
				continue;
			}
		}
		return null;
	}

	private WebElement findPageLink(WebDriver driver, int page)
	{
		Select dropdown = new Select(driver.findElement(By.name("jump")));
		String pageText = String.format("第 %d 頁", page);
		// dropdown.selectByValue(String.format("第 %d 頁", pageStr));

		List<WebElement> optElems = dropdown.getOptions();
		for (WebElement optElem : optElems)
		{
			try{
				String optText = optElem.getText();
				if (optText.indexOf(pageText) >= 0) return optElem;
			}catch(Exception ex){
				// no child element
				continue;
			}
		}
		return null;
	}
}
