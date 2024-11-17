package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.NestManager;

@Slf4j
public class Cmd_End extends Command
{
	public Cmd_End()
	{
		this.commandEng = "End";
		this.commandCht = "結束判斷";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		return (matcher.find());
	}

	// Define:
	// 1. Check if in IF nested block
	// 2. If yes, Exist IF nested block then return true
	// 3. If no, return false
	public boolean execute(WebDriver driver)
	{
		if (!NestManager.isInIfBlock()) return Tools.logErr(logger, "Execution Fail: NO responding IF");

		return NestManager.exitIfBlock();
	}
}
