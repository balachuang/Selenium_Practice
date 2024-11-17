package my.spider.commander;

import org.openqa.selenium.WebDriver;
import org.springframework.util.StringUtils;

public class Cmd_Remark extends Command
{
	public Cmd_Remark()
	{
		this.commandEng = "---";
		this.commandCht = "---";
		this.parsePattern = "";
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		// check Remark / Empty line
		cmdLine = cmdLine.trim();
		return (cmdLine.startsWith(this.commandEng) || !StringUtils.hasText(cmdLine));
	}

	// Remark:
	// 1. Just return true
	public boolean execute(WebDriver driver)
	{
		// Remark, do nothing
		return true;
	}
}
