package bala.tools.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;


@Data
@Configuration
public class AppConfig
{
	@Value("${browser.driver}")
    private String browserDriver;

	@Value("${browser.minimize}")
    private boolean minimizeBrowser;

	@Value("${browser.hide}")
    private boolean hideBrowser;

	@Value("${spider.random-interval}")
    private int randomInterval;

	@Value("${spider.local-save.base-path}")
    private String localBasePath;

	@Value("${spider.local-save.save-to-sub-folder}")
    private boolean localSaveToSubFolder;
}
