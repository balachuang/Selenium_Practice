package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.variables.SystemVarManager;

@Slf4j
public class Cmd_Input extends Command
{
	private String text;

	public Cmd_Input()
	{
		this.commandEng = "Input";
		this.commandCht = "輸入文字";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<text>.+)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			text = Tools.parseVariables(matcher.group("text"));
			isMatch = true;
		}
		return isMatch;
	}

	// Input:
	// 1. Get previous element
	// 2. If found, send text then return true
	// 3. If not found, return false
	public boolean execute(WebDriver driver)
	{
		try
		{
			WebElement webElem = SystemVarManager.getPrevWebElement();
			if (webElem == null) return Tools.logErr(logger, "Execution Fail: Previous Element Not Found");

			if ("[ENTER]".equals(text))
				webElem.sendKeys(Keys.RETURN);
			else
				webElem.sendKeys(text);
			this.execResultMsg = String.format("Text input: %s", text);
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
