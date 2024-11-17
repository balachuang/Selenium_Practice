package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.NestManager;
import my.spider.utils.variables.SystemVarManager;

@Slf4j
public class Cmd_Next extends Command
{
	public Cmd_Next()
	{
		this.commandEng = "Next";
		this.commandCht = "回到開頭";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		return (matcher.find());
	}

	// Next:
	// 1. Check if in LOOP nested block
	// 2. If yes, Exist LOOP nested block then return true
	// 3. If no, return false
	public boolean execute(WebDriver driver)
	{
		if (!NestManager.isInLoopBlock()) return Tools.logErr(logger, "Execution Fail: NO responding LOOP");

		// 先把 Loop Index +1, 如果已經超過 final-index, 會回傳 -1
		if (!NestManager.gotoNextLoopIndex())
		{
			this.execResultMsg = String.format("Exit Loop");
			return (NestManager.getCurrLoopIndex() < 0);
		}

		// Loop Index 改成功了再把目前的執行位置重置到 Loop start
		int nStartLine = NestManager.getLoopStartLine();
		if (nStartLine < 0) return Tools.logErr(logger, "Execution Fail: Error Loop Start Line (" + nStartLine + ")");

		SystemVarManager.setCurrExecLine(nStartLine);
		this.execResultMsg = String.format("Go back to line: %d", nStartLine);

		return true;
	}
}
