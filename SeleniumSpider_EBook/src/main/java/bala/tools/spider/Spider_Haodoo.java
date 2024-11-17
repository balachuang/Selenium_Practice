package bala.tools.spider;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import bala.tools.utils.AppConfig;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class Spider_Haodoo
{
	@Autowired
	AppConfig appConfig;

	public void run(WebDriver driver) throws InterruptedException
	{
		// prepare config
		String defaultDownloadPath = StringUtils.trimTrailingCharacter(appConfig.getLocalDefaultPath(), '\\') ;
		String targetFilePath = StringUtils.trimTrailingCharacter(appConfig.getLocalTargetPath(), '\\') ;

		String[] urls = appConfig.getUrls();
		for (String url : urls)
		{
			// Goto Haodoo EBook url
			logger.info("Navigate to EBook page...");
			driver.navigate().to(url);

			// Find Main Content && Download Button
			List<WebElement> tableElems = driver.findElements(By.tagName("table"));
			Iterator<WebElement> tableItor = tableElems.iterator();
			while (tableItor.hasNext())
			{
				WebElement tableElem = tableItor.next();
				WebElement inputElem = null;

				boolean containsButton = false;
				List<WebElement> inputElems = tableElem.findElements(By.tagName("input"));
				Iterator<WebElement> inputItor = inputElems.iterator();
				while (inputItor.hasNext())
				{
					inputElem = inputItor.next();
					if (!inputElem.getAttribute("value").contains("下載 epub 檔")) continue;
		
					// button found
					containsButton = true;
					break;
				}

				if (!containsButton) continue;

				// button found, get Author & Book Name
				// WebDriver cannot get pure text node...
				WebElement tdElem = tableElem.findElement(By.tagName("td"));
				String targetFName = tdElem.getText().split("\\n")[0].replace("說明", "").trim() + ".epub";

				// find download file name: DownloadEpub('P1010490239')
				String downloadName = inputElem.getAttribute("onclick");
				downloadName = downloadName.substring(15, downloadName.length() - 2) + ".epub";

				// click button
				logger.info("Start download: {}", targetFName);
				inputElem.click();

				// prepare file onjects
				File f1 = new File(defaultDownloadPath + "\\" + downloadName);
				File f2 = new File(targetFilePath + "\\" + targetFName);

				// Haodoo's download cannot stuck WebDrive, wait 10 secs
				logger.info("Download...");
				int waitTimes = 0;
				while (!f1.exists() && (++waitTimes <= appConfig.getWaitTimeout())) Thread.sleep(1000);
				if(!f1.exists()) {
					logger.error("Download file not found: {}", defaultDownloadPath + "\\" + downloadName);
					break;
				}

				// wait download and rename download file
				logger.info("Rename: {} ---> {}", downloadName, targetFName);
				boolean isCopyOk = false;
				if (f1.exists())
				{
					// remove org file and move
					if (f2.exists()) f2.delete();
					isCopyOk = f1.renameTo(f2);
				}else{
					logger.error("Download file not found: {}", defaultDownloadPath + "\\" + downloadName);
				}

				if (!isCopyOk) logger.error("Move file ERROR");
				break;
			}
		}

		logger.info("=== Downloaded.");
	}
}
