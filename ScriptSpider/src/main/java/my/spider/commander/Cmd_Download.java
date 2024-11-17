package my.spider.commander;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import lombok.extern.slf4j.Slf4j;
import my.spider.utils.Tools;

@Slf4j
public class Cmd_Download extends Command
{
	private String url;
	private String filepath;

	public Cmd_Download()
	{
		this.commandEng = "Download";
		this.commandCht = "下載檔案";
		this.parsePattern = String.format("^\\s*(%s|%s)\\s+(?<url>(\\S+|\\\".+\\\"))\\s+(?<filepath>(\\S+|\\\".+\\\"))\\s*$", this.commandEng, this.commandCht);
		this.execResultMsg = null;
	}

	public boolean parse(String cmdLine) throws Exception
	{
		boolean isMatch = false;
		Pattern pattern = Pattern.compile(this.parsePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cmdLine);
		if (matcher.find())
		{
			url = matcher.group("url");
			if (url == null) return Tools.logErr(logger, "Url Not Found");
			url = Tools.TrimDoubleQuote(url);
			url = Tools.parseVariables(url);

			filepath = matcher.group("filepath");
			if (filepath == null) return Tools.logErr(logger, "FilePath Not Found");
			filepath = Tools.TrimDoubleQuote(filepath);
			filepath = Tools.parseVariables(filepath);

			isMatch = true;
		}
		return isMatch;
	}

	// Download:
	// 1. 
	// 2. 
	public boolean execute(WebDriver driver)
	{
		try
		{
			InputStream in = new URI(this.url).toURL().openStream();
			Files.copy(in, Paths.get(this.filepath), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception ex)
		{
			logger.error("Execution Exception: {}", ex.getMessage());
			return false;
		}

		return true;
	}
}
