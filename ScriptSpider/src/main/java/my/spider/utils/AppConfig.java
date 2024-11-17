package my.spider.utils;

// import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;


@Data
@Configuration
public class AppConfig
{
	@Value("${browser.driver}")
	private String browserDriver;

	@Value("${browser.timeout.global}")
	private long globalTimeout;

	@Value("${browser.timeout.script}")
	private long scriptTimeout;

	@Value("${browser.timeout.pageLoad}")
	private long pageTimeout;

	// @Value("${content.spider.js}")
	// private String spiderBrowserJs;

	// @Value("${content.message.js}")
	// private String messageBrowserJs;
}
