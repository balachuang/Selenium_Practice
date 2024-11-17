package bala.tools.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;


@Data
@Configuration
public class ComicConfig
{
	@Value("${web.url.login}")
	private String loginUrl;

	@Value("${web.url.cover}")
	private String coverUrl;

	@Value("${web.username}")
	private String username;

	@Value("${web.password}")
	private String password;

	@Value("${comic.name}")
	private String comicName;

	@Value("${comic.id}")
	private int comicId;

	@Value("${comic.need-login}")
	private boolean needLogin;

	@Value("${comic.special-chaps}")
	private String[] comicSpecialChs;

	@Value("${comic.chap-str}")
	private int comicChStr;

	@Value("${comic.chap-end}")
	private int comicChEnd;

	@Value("${comic.page-str}")
	private int comicPageStr;

	@Value("${comic.page-end}")
	private int comicPageEnd;
}
