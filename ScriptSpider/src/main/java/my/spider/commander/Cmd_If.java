package my.spider.commander;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;
import my.spider.utils.NestManager;

@Slf4j
public class Cmd_If extends Command
{
	private String condition;

	public Cmd_If()
	{
		this.commandEng = "If";
		this.commandCht = "條件判斷";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<condition>.+)\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			condition = matcher.group("condition");
			if (condition == null) return Tools.logErr(logger, "Condition Not Found");

			condition = Tools.parseVariables(condition);
			isMatch = true;
		}
		return isMatch;
	}

	// If:
	// 1. Evaluate condition by ScriptEngineManager
	// 2. Push IF nested block with condition evaluation result
	public boolean execute(WebDriver driver)
	{
		try
		{
			// evaluate condition result
			ExpressionParser expressionParser = new SpelExpressionParser();
			Expression exp = expressionParser.parseExpression(condition);
			boolean isTrue = "true".equals(exp.getValue().toString());

			// condition is true, put status to stack
			NestManager.enterIfBlock(isTrue);
			this.execResultMsg = String.format("Enter If: %s", isTrue);
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
