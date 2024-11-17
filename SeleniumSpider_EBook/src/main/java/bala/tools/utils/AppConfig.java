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

	@Value("${spider.local-save.default-path}")
    private String localDefaultPath;

	@Value("${spider.local-save.target-path}")
    private String localTargetPath;

    @Value("${download.timeout}")
    private int waitTimeout;

    @Value("${download.urls}")
    private String url;

    public String[] getUrls()
    {
        String[] urls = this.getUrl().split("\\n");
        return urls;
    }
}
