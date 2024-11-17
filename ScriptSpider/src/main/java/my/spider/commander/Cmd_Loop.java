package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.NestManager;
import my.spider.utils.variables.SystemVarManager;

@Slf4j
public class Cmd_Loop extends Command
{
	private int loopStart;
	private int loopEnd;

	public Cmd_Loop()
	{
		this.commandEng = "Loop";
		this.commandCht = "進入迴圈";
		this.parsePattern = String.format("^\\s*(%s|%s)(\\s+(?<loopStart>\\S+)\\s+(?<loopEnd>\\S+))?\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			String lsStr = matcher.group("loopStart");
			String lsEnd = matcher.group("loopEnd");

			if (!StringUtils.hasText(lsStr) || !StringUtils.hasText(lsEnd))
			{
				loopStart = Integer.MIN_VALUE;
				loopEnd = Integer.MAX_VALUE;
			}else{
				lsStr = Tools.parseVariables(lsStr);
				lsEnd = Tools.parseVariables(lsEnd);
				loopStart = Integer.parseInt(lsStr);
				loopEnd = Integer.parseInt(lsEnd);
			}
			isMatch = true;
		}
		return isMatch;
	}

	// Loop:
	// 1. Push LOOP nested block with loop parameters
	public boolean execute(WebDriver driver)
	{
		try
		{
			// 要同步找到 Next 位置, 給 Break 用的. !!!
			// enter loop block
			NestManager.enterLoopBlock(loopStart, loopEnd, SystemVarManager.getCurrExecLine());
			this.execResultMsg = String.format("Enter Loop: %d ~ %d", loopStart, loopEnd);
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
