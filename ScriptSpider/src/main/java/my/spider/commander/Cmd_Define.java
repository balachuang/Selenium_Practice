package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.variables.CustomVarManager;

@Slf4j
public class Cmd_Define extends Command
{
	private String key;
	private String value;

	public Cmd_Define()
	{
		this.commandEng = "Define";
		this.commandCht = "定義變數";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<key>\\S+)\\s+(?<val>(\\S+|\\\".+\\\"))\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			key = matcher.group("key");
			value = Tools.parseVariables(matcher.group("val"));
			isMatch = true;
		}
		return isMatch;
	}

	// Define:
	// 1. Put Key/Value into variable array
	// 2. return true
	public boolean execute(WebDriver driver)
	{
		try
		{
			String res = CustomVarManager.put(this.key, this.value);
			this.execResultMsg = (res == null) ?
				String.format("Set: (%d) %s = %s", CustomVarManager.size(), this.key, this.value) :
				String.format("Set: (%d) %s = %s (replaced: )", CustomVarManager.size(), this.key, this.value, res) ;
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
