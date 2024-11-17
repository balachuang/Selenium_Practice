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
public class Cmd_Get extends Command
{
	private String cssSelector;
	private String target;

	// To-Do: Find --> Get DOM / Attraibute / innerText / innerHtml/ ....
	public Cmd_Get()
	{
		this.commandEng = "Get";
		this.commandCht = "取得元件";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<cssSelector>\\S+)(\\s+(?<target>\\S+))?\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			// parse
			cssSelector = matcher.group("cssSelector");
			target = matcher.group("target");

			// check
			if (cssSelector == null)
			{
				logger.error("Parse Command Error");
				return false;
			}

			// post-processing
			cssSelector = Tools.parseVariables(cssSelector);
			target = (target == null) ? "" : Tools.parseVariables(target);
			this.execResultMsg = String.format("Got: %s", target);

			isMatch = true;
		}
		return isMatch;
	}

	// Get:
	// 1. Get the first element by CSS selector
	// 2. If found, get element attribute then return true
	// 3. If not found, return false
	public boolean execute(WebDriver driver)
	{
		try
		{
			WebElement webElem = driver.findElement(By.cssSelector(cssSelector));
			if (webElem == null) return Tools.logErr(logger, "Execution Fail: Element Not Found");
	
			if ("".equals(target))
			{
				SystemVarManager.setPrevResult(webElem);
				this.execResultMsg = String.format("Element got: %s", webElem.getTagName());
			} else {
				SystemVarManager.setPrevResult(webElem.getAttribute(target));
				this.execResultMsg = String.format("Attribute got: %s", webElem.getAttribute(target));
			}
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
