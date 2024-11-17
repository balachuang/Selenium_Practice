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
public class Cmd_Find extends Command
{
	private String searchTerm;

	// To-Do: check difference between Find / Get
	public Cmd_Find()
	{
		this.commandEng = "Find";
		this.commandCht = "搜尋元件";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<term>.+)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			searchTerm = Tools.parseVariables(matcher.group("term"));
			isMatch = true;
		}
		return isMatch;
	}

	// Find:
	// 1. Get the first element by CSS selector
	// 2. If found, set the element as PrevObject, then return true
	// 3. If not found, return false
	public boolean execute(WebDriver driver)
	{
		try
		{
			WebElement webElem = driver.findElement(By.cssSelector(this.searchTerm));
			if (webElem == null) return Tools.logErr(logger, "Execution Fail: Element Not Found");

			SystemVarManager.setPrevResult(webElem);
			this.execResultMsg = String.format("Element found: %s", webElem.getTagName());
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
