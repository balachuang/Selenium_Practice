package my.spider.commander;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;

@Slf4j
public class Cmd_CloseTab extends Command
{
	private int tabIdx;

	public Cmd_CloseTab()
	{
		// to-do: addd CloseTab Current
		this.commandEng = "CloseTab";
		this.commandCht = "關閉頁籤";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<tab>\\d+)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			String find = Tools.parseVariables(matcher.group("tab"));
			tabIdx = Integer.parseInt(find);
			isMatch = true;
		}
		return isMatch;
	}

	// SwitchTab:
	// 1. Check if tab index is valid
	// 2. If valid, switch to target tab, close it
	// 3. If not valid, return false
	public boolean execute(WebDriver driver)
	{
		try
		{
			ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
			if (tabIdx >= tabs.size()) return Tools.logErr(logger, "Execution Fail: Index Not Found (" + tabIdx + ")");

			driver.switchTo().window(tabs.get(tabIdx));
			driver.close();
			// driver.switchTo().window(tabs.get(tabIdx - 1));
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
