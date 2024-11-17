package my.spider.commander;

import org.openqa.selenium.WebDriver;

public abstract class Command
{
	public String commandEng = "";
	public String commandCht = "";
	public String parsePattern = "";
	public String execResultMsg = null;

	public abstract boolean parse(String cmdLine) throws Exception;
	public abstract boolean execute(WebDriver driver);
}