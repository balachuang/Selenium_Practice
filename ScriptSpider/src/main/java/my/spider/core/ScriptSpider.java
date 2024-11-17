package my.spider.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.openqa.selenium.WebDriver;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import my.spider.commander.*;
import my.spider.utils.AppConfig;
import my.spider.utils.BrowserManager;
import my.spider.utils.Tools;
import my.spider.utils.NestManager;
import my.spider.utils.variables.SystemVarManager;


@Slf4j
@Component
@Order(1)
public class ScriptSpider implements CommandLineRunner
{
	@Autowired
	AppConfig appConfig;

	@Autowired
	BrowserManager browserFactory;

	private ArrayList<Command> commands = new ArrayList<Command>(){{
		add(new Cmd_Break());
		add(new Cmd_Click());
		add(new Cmd_CloseTab());
		add(new Cmd_Define());
		add(new Cmd_Download());
		add(new Cmd_End());
		add(new Cmd_Find());
		add(new Cmd_Get());
		add(new Cmd_If());
		add(new Cmd_Input());
		add(new Cmd_Loop());
		add(new Cmd_Navigate());
		add(new Cmd_Next());
		add(new Cmd_ParseText());
		add(new Cmd_Remark());
		add(new Cmd_Sleep());
		add(new Cmd_SwitchTab());
	}};

	public void run(String... args)
	{
		// Initialize WebDriver
		WebDriver driver = browserFactory.InitBrowser();

		// Read Script
		ArrayList<String> scriptLins = Tools.readScriptFile(args[0]);
		if (!NestManager.preloadLoopBlock(scriptLins))
		{
			logger.error("Pre-load Loop Bloacks Error");
			return;
		}
		logger.info("<<< Script Start >>>");

		// Start Execution
		SystemVarManager.resetCurrExecLine();
		while(SystemVarManager.isValidExecLine(scriptLins.size()))
		{
			String line = scriptLins.get(SystemVarManager.getCurrExecLine()).trim();
			String lineNum = String.format("Line %d: ", SystemVarManager.getCurrExecLine());
			logger.info("{}{}", lineNum, line);

			boolean isIllegalCommand = true;
			for (Command cmd : commands)
			{
				try {
					// find matching command
					if (!cmd.parse(line)) continue;
				}
				catch (Exception ex)
				{
					// matching command found, but parse error
					logger.error("Parse Command Error: {}", ex.getMessage());
					logger.error("INTERRUPT due to Command Parse Error in: {}", line);
					return;
				}
				isIllegalCommand = false;

				// command found, go execution
				if (!NestManager.isInExecutableIf())
				{
					if (cmd.getClass().getName().indexOf("Cmd_End") < 0)
					{
						// break due to in (If False)
						logger.info("{}--- Inside IF(false), skip: {}", lineNum.replaceAll(".", " "), line);
						break;
					}
				}

				// execute command !!!
				if (!cmd.execute(driver))
				{
					// script execution error, stop.
					logger.error("Script TERMINATED due to Execution ERROR.");
					return;
				}
				if (StringUtils.hasText(cmd.execResultMsg)) logger.info("{}{}", lineNum.replaceAll(".", " "), cmd.execResultMsg);
				break;
			}

			// check if this command is not execute
			if (isIllegalCommand)
			{
				// script execution error, stop.
				logger.error("Script TERMINATED Command NOT FOUND.");
				break;
			}

			SystemVarManager.gotoNextExecLine();
		}

		// close WebDriver then End
		driver.close();
		logger.info("<<< Script Finish >>>");
	}
}
