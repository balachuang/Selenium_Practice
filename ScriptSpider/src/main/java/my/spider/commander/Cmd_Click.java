package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.variables.SystemVarManager;

@Slf4j
public class Cmd_Click extends Command
{
	private String css;

	public Cmd_Click()
	{
		this.commandEng = "Click";
		this.commandCht = "點擊元件";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<css>.+)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			String find = matcher.group("css");
			css = Tools.parseVariables(find);
			isMatch = true;
		}
		return isMatch;
	}

	// Click:
	// 1. Get the first element by CSS selector
	// 2. If found, set the element as PrevObject, then click it and return true
	// 3. If not found, return false
	public boolean execute(WebDriver driver)
	{
		try
		{
			// find WebElement and click
			WebElement webElem = driver.findElement(By.cssSelector(css));
			if (webElem == null) return Tools.logErr(logger, "Execution Fail: Element Not Found");

			SystemVarManager.setPrevResult(webElem);
			webElem.click();
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
