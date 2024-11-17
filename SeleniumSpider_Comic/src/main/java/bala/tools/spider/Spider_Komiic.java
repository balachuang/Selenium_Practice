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
import org.openqa.selenium.JavascriptExecutor;
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
public class Spider_Komiic
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

		// Login - implement by demand later

		JavascriptExecutor js = (JavascriptExecutor) driver;
		int currChap = comicInfo.getChapStart();
		int finalChap = comicInfo.getChapEnd();
		while(currChap <= finalChap)
		{
			// Goto overview page & click into the first chapter
			logger.info("Navigate to coverpage of {}", comicInfo.getName());
			driver.navigate().to(comicInfo.getCoverUrl());

			// find and click into currChap
			webElem = driver.findElement(By.cssSelector("div.v-card-text a.v-btn"));
			webElem.click();

			// click 連續 button
			int bs = driver.findElements(By.tagName("button")).size();
			webElem = driver.findElements(By.tagName("button")).get(bs-1);
			webElem.click();

			// all images are located in the same page, just scroll down...
			List<WebElement> pics = driver.findElements(By.cssSelector("div.ComicImageContainer > div"));
			int totalPics = pics.size();
			for (int currPage=comicInfo.getPageStart(); currPage<=Math.min(totalPics, comicInfo.getPageEnd()); ++currPage)
			{
				// find image
				webElem = pics.get(currPage);
				webElem = webElem.findElement(By.cssSelector("img.comicImage"));
				//String ss = webElem.getAttribute("src"); --> blob:https:// ???

				// scroll to image
				js.executeScript(String.format("window.scrollTo(0,%d)", webElem.getLocation().getY()));

				// download
				// blob type image
				// through javascript: https://stackoverflow.com/questions/47424245/how-to-download-an-image-with-python-3-selenium-if-the-url-begins-with-blob
				// better: https://superuser.com/questions/1364468/downloading-blob-image

				// var image = document.querySelector('img');       // Image you want to save
				// var saveImg = document.createElement('a');       // New link we use to save it with
				// saveImg.href = image.src                         // Assign image src to our link target
				// saveImg.download = "imagename.jpg";              // set filename for download
				// saveImg.innerHTML = "Click to save image";       // Set link text
				// document.body.appendChild(saveImg);              // Add link to page

				logger.info(String.format("Download: [%s: %03d] - %03d / %03d.", comicInfo.getName(), currChap, currPage, totalPics));
				String imgFName = String.format("%s/CH_%03d__P_%03d.jpg", localPath, currChap, currPage);
				InputStream in = new URI(webElem.findElement(By.cssSelector("img.comicImage")).getAttribute("src")).toURL().openStream();
				Files.copy(in, Paths.get(imgFName), StandardCopyOption.REPLACE_EXISTING);

				// sleep before next page
				Common.Sleep(appConfig.getRandomInterval());
			}

			// go next chapter
			++currChap;
		}

		driver.quit();
		logger.info("=== All Chapters Downloaded.");
	}
}
