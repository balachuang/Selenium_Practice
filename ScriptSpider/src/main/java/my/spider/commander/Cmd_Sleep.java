package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;

@Slf4j
public class Cmd_Sleep extends Command
{
	private int sec;

	public Cmd_Sleep()
	{
		this.commandEng = "Sleep";
		this.commandCht = "中場休息";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<sec>-?\\d+)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			String find = Tools.parseVariables(matcher.group("sec"));
			sec = Integer.parseInt(find);
			isMatch = true;
		}
		return isMatch;
	}

	// Sleep:
	// 1. Just Sleep
	public boolean execute(WebDriver driver)
	{
		try
		{
			long sleepMs = (sec < 0) ? Math.round(-sec * 1000 * Math.random()) : (sec * 1000);
			this.execResultMsg = String.format("Slept %.2f secs", 0.001 * sleepMs);
			Thread.sleep(sleepMs);
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
