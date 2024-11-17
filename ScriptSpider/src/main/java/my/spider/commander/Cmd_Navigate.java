package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

// import bala.tools.utils.variables.CustomVarManager;
import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;


@Slf4j
public class Cmd_Navigate extends Command
{
	private String url;

	public Cmd_Navigate()
	{
		this.commandEng = "Navigate";
		this.commandCht = "瀏覽網頁";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<url>\\S+)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			String find = matcher.group("url");
			url = Tools.parseVariables(find);
			isMatch = true;
		}
		return isMatch;
	}

	// Navigate:
	// 1. Navigate to url
	public boolean execute(WebDriver driver)
	{
		try
		{
			driver.navigate().to(url);
			this.execResultMsg = String.format("Navigate: %s", url);
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
