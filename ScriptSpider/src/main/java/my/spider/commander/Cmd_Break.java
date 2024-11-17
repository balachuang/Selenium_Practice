package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

// import bala.tools.utils.NestManager;
import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.NestManager;
import my.spider.utils.variables.SystemVarManager;

@Slf4j
public class Cmd_Break extends Command
{
	public Cmd_Break()
	{
		this.commandEng = "Break";
		this.commandCht = "跳出迴圈";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		return (matcher.find());
	}

	// Break:
	// 1. Find the lastest Loop in NestBlock
	// 2. If found, pop all Nested Objects after Loop, jump to the next line of Loop block.
	// 3. If not found, return false
	public boolean execute(WebDriver driver)
	{
		// return false;
		// 有可能 Breadk 在 Loop > If 中
		// ==> 找到最近的一個 Loop, 然後以下全部中斷
		int loopStartPos = NestManager.exitRecentLoopBlock();
		if (loopStartPos < 0) return Tools.logErr(logger, "Execution Fail: Break Fail");

		// 跳到對應的 Next 後一行....
		int loopEndPos = NestManager.getLoopEndPos(loopStartPos);
		SystemVarManager.setCurrExecLine(loopEndPos);
		return true;
	}
}
