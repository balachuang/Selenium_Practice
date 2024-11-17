package my.spider.commander;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;

@Slf4j
public class Cmd_SwitchTab extends Command
{
	private int tabIdx;

	public Cmd_SwitchTab()
	{
		this.commandEng = "SwitchTab";
		this.commandCht = "切換頁籤";
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
			if ("First".equalsIgnoreCase(find)) find = "0";
			if ("Latest".equalsIgnoreCase(find)) find = "-1";
			tabIdx = Integer.parseInt(find);
			isMatch = true;
		}
		return isMatch;
	}

	// SwitchTab:
	// 1. Check if tab index is valid
	// 2. If valid, switch to target tab then return true
	// 3. If not valid, return false
	public boolean execute(WebDriver driver)
	{
		try
		{
			ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
			if (tabIdx < 0) tabIdx = tabs.size() - 1;
			if (tabIdx >= tabs.size()) return Tools.logErr(logger, "Execution Fail: Index Not Found (" + tabIdx + ")");

			driver.switchTo().window(tabs.get(tabIdx));
			this.execResultMsg = String.format("Switch to Tab: %d", tabIdx);
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
