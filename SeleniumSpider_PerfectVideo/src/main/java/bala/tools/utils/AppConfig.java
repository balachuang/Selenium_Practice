package bala.tools.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;


@Data
@Configuration
public class AppConfig
{
	@Value("${browser.minimize}")
    private boolean minimizeBrowser;

	@Value("${browser.hide}")
    private boolean hideBrowser;

	@Value("${youtube.home-url}")
    private String youtubeHomeUrl;

	@Value("${youtube.watchlater-url}")
    private String youtubeWatchUrl;

	@Value("${youtube.cookie-file-path}")
    private String youtubeCookiePath;
}
