package my.spider.utils.variables;

// import java.util.Stack;
import org.openqa.selenium.WebElement;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.NestManager;


@Slf4j
public class SystemVarManager
{
	// Variables / Functions for Execution Line
	private static int currentExecLine = 0;
	public static void resetCurrExecLine(){ currentExecLine = 0; }
	public static void gotoNextExecLine(){ ++currentExecLine; }
	public static void setCurrExecLine(int _line){ currentExecLine = _line; }
	public static int getCurrExecLine(){ return currentExecLine; }
	public static boolean isValidExecLine(int totalLines){ return ((currentExecLine >= 0) && (currentExecLine < totalLines)); }


	// Variables / Functions for Last Execution Result
	private static Object lastResult;
	private static ResultType lastResultType;
	private static enum ResultType {
		STRING, WEBELEMENT
	}

	public static void setPrevResult(String value) {
		lastResultType = ResultType.STRING;
		lastResult = value;
	}

	public static void setPrevResult(WebElement value) {
		lastResultType = ResultType.WEBELEMENT;
		lastResult = value;
	}

	public static String getPrevString() {
		if (lastResultType == ResultType.STRING) return (String)lastResult;
		return null;
	}

	public static WebElement getPrevWebElement() {
		if (lastResultType == ResultType.WEBELEMENT) return (WebElement)lastResult;
		return null;
	}

	// Case-Sensitive
	public static String parseSysVars(String input) throws Exception
	{
		String output = input;

		// ${loop}
		if (output.contains("${loop}"))
		{
			Integer currLoop = NestManager.getCurrLoopIndex();
			output = output.replaceAll("\\$\\{loop\\}", ((currLoop == null) ? "" : Integer.toString(currLoop)));
		}

		// ${prevText}
		if (output.contains("${prevText}"))
		{
			String text = SystemVarManager.getPrevString();
			output = output.replaceAll("\\$\\{prevText\\}", ((text == null) ? "" : text));
		}

		return output;
	}
}
