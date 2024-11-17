package my.spider.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.variables.CustomVarManager;
import my.spider.utils.variables.SystemVarManager;

@Slf4j
public class Tools
{
	public static void sleep(int waitInv)
	{
		if (waitInv == 0) return;

		long sleepMs = (waitInv < 0) ? Math.round(-waitInv * 1000 * Math.random()) : (waitInv * 1000);

		try {
			logger.info("   sleep {} secs.", String.format("%.3f", (sleepMs / 1000.0)));
			Thread.sleep(sleepMs);
		} catch (InterruptedException e) {
			logger.error("   ERROR when delay: {}.", e.getMessage());
		}
	}

	public static WebDriver createBrowser(String driverType, ChromeOptions option)
	{
		// WebDriver driver = null;

		// if (option == null) driver = new ChromeDriver();
		// else                driver = new ChromeDriver(option);

		return (option == null) ? new ChromeDriver() : new ChromeDriver(option);
	}

	public static void resizeBrowser(WebDriver driver)
	{
		// WebDriver dimension is not equal to Screen dimension
		driver.manage().window().maximize();
		Dimension size = driver.manage().window().getSize();

		int currW = (int)size.getWidth();
		int currH = (int)size.getHeight();
		int targetW = 600;
		int targetH = 400;

		driver.manage().window().setSize(new Dimension(targetW, targetH));
		driver.manage().window().setPosition(new Point(currW - targetW - 20, currH - targetH - 20));
	}

	public static ArrayList<String> readScriptFile(String scriptFilePath)
	{
		ArrayList<String> scriptLines = new ArrayList<String>();

		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFilePath), "UTF-8")))
		{
			String line = br.readLine();
			while (line != null) 
			{
				scriptLines.add(line);
				line = br.readLine();
			}
		}catch(Exception ex){
			logger.error("Read script {} error, please check your parameter: {}", scriptFilePath, ex);
		}

		return scriptLines;
	}

	public static String parseVariables(String input) throws Exception
	{
		String output = input;
		output = CustomVarManager.parseCustVars(output);
		output = SystemVarManager.parseSysVars(output);
		return output;
	}

	public static String TrimDoubleQuote(String input)
	{
		// remove double quote pair
		String output = input;
		while (output.startsWith("\"") && output.endsWith("\"")) output = output.substring(1, output.length()-2);
		return output;
	}

	public static boolean logErr(Logger _logger, String _msg)
	{
		_logger.error(_msg);
		return false;
	}
}

// enable here if there are another Browser
// switch (driverType)
// {
// case "Chrome":
// 	if (option == null) driver = new ChromeDriver();
// 	else                driver = new ChromeDriver(option);
// 	break;
// default:
// 	if (option == null) driver = new ChromeDriver();
// 	else                driver = new ChromeDriver(option);
// 	break;
// }
