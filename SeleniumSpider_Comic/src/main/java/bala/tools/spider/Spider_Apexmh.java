package bala.tools.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
public class Spider_Apexmh
{
	@Autowired
	AppConfig appConfig;

	@Autowired
	ComicConfig comicConfig;

	public void run(WebDriver driver) throws MalformedURLException, IOException, URISyntaxException, InterruptedException
	{
		// prepare config
		WebElement webElem = null;
		ComicInfo comicInfo = Common.loadComicInfo(comicConfig);
		String localPath = Common.prepareStoreLocation(appConfig, comicInfo.getName());

		// Login First

		// Goto cover page (the cover page here is also the first page)
		logger.info("Navigate to page 1 of {}", comicInfo.getName());
		driver.navigate().to(comicInfo.getCoverUrl());

		// String localPath = localConfig.getPath().trim();
		Pattern pattern = Pattern.compile("(\\d+)", Pattern.CASE_INSENSITIVE);

		// read pages
		int currPage = 1;
		while (true)
		{
			int totalPage = 0;
			webElem = driver.findElements(By.cssSelector("div.c_l p")).get(2);
			Matcher matcher = pattern.matcher(webElem.getText());
			if (matcher.find())
			{
				totalPage = Integer.parseInt(matcher.group(1));
			}else{
				// page number not found
				logger.error("Find page number ERROR, please check HTML content.");
				break;
			}

			// check page in range and download image
			if ((currPage >= comicInfo.getPageStart()) && (currPage <= comicInfo.getPageEnd()))
			{
				logger.info(String.format("Download: [%s] - %03d / %03d.", comicInfo.getName(), currPage, totalPage));
				String imgFName = String.format("%s/%03d.jpg", localPath, currPage);
				webElem = driver.findElement(By.cssSelector("#showimg img"));
				InputStream in = new URI(webElem.getAttribute("src")).toURL().openStream();
				Files.copy(in, Paths.get(imgFName), StandardCopyOption.REPLACE_EXISTING);
			}

			// go next page (no way to directly jump to start page)
			if ((currPage < totalPage) && (currPage <= comicInfo.getPageEnd()))
			{
				// sleep before next page
				Common.Sleep(appConfig.getRandomInterval());

				List<WebElement> webElems = driver.findElements(By.cssSelector("#page a"));
				webElem = webElems.get(webElems.size() - 1);
				webElem.click();
			}else{
				logger.info("--- All pages downloaded.");
				break;
			}
			++currPage;
		}

		driver.quit();
		logger.info("=== All Pages Downloaded.");
	}
}
