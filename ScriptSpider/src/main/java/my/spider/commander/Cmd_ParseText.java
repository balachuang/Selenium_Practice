package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.variables.SystemVarManager;

@Slf4j
public class Cmd_ParseText extends Command
{
	private String execText;
	private String execPattern;

	public Cmd_ParseText()
	{
		this.commandEng = "ParseText";
		this.commandCht = "剖析字串";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<text>(\\S+|\\\".+\\\"))\\s+(?<pattern>(\\S+|\\\".+\\\"))\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			execPattern = matcher.group("pattern");
			if (execPattern == null) return Tools.logErr(logger, "Pattern Not Found");
			execPattern = StringUtils.trimLeadingCharacter(execPattern, '"');
			execPattern = StringUtils.trimTrailingCharacter(execPattern, '"');

			execText = Tools.parseVariables(matcher.group("text"));
			execPattern = Tools.parseVariables(execPattern);
			isMatch = true;
		}
		return isMatch;
	}

	// ParseText:
	// 1. Put Key/Value into variable array
	// 2. return true
	public boolean execute(WebDriver driver)
	{
		try
		{
			Pattern pattern = Pattern.compile(this.execPattern);
			Matcher matcher = pattern.matcher(this.execText);
			if (matcher.find())
			{
				String result = "";
				try{
					result = matcher.group("target");
				}catch(Exception e1){
					try{
						result = matcher.group(1);
					}catch(Exception e2){
						result = matcher.group(0);
					}
				}

				SystemVarManager.setPrevResult(result);
				this.execResultMsg = String.format("Got [%s] from [%s]", result, this.execText);
			}
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
