package bala.tools.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.util.StringUtils;

import bala.tools.model.ComicInfo;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Common
{
	public static void Sleep(int waitInv)
	{
		if (waitInv == 0) return;

		long sleepMs = 0;
		if (waitInv < 0)
		{
			sleepMs = Math.round(- waitInv * 1000 * Math.random());
		}else if (waitInv > 0){
			sleepMs = waitInv * 1000;
		}

		try {
			logger.info("   wait for {} secs.", String.format("%.3f", (sleepMs / 1000.0)));
			Thread.sleep(sleepMs);
		} catch (InterruptedException e) {
			logger.error("   ERROR when delay: {}.", e.getMessage());
		}
	}

	public static ComicInfo loadComicInfo(ComicConfig comicConfig)
	{
		ComicInfo info = new ComicInfo();

		info.setName(comicConfig.getComicName());
		info.setId(comicConfig.getComicId());

		info.setChapStart(comicConfig.getComicChStr());
		info.setChapEnd(comicConfig.getComicChEnd());

		// String sc = comicConfig.getComicSpecialChs();
		// info.setSpecialChaps(StringUtils.hasText(sc) ? StringUtils.split(sc, "<SEP>") : null);
		info.setSpecialChaps(comicConfig.getComicSpecialChs());

		info.setLoginUrl(comicConfig.getLoginUrl());
		info.setCoverUrl(comicConfig.getCoverUrl().replace("{{comic-id}}", String.format("%d", info.getId())));
		info.setUsername(comicConfig.getUsername());
		info.setPassword(comicConfig.getPassword());

		int pageStr = comicConfig.getComicPageStr();
		int pageEnd = comicConfig.getComicPageEnd();
		if (pageStr <= 0) pageStr = 1;
		if (pageEnd <= 0) pageEnd = Integer.MAX_VALUE;
		info.setPageStart(pageStr);
		info.setPageEnd(pageEnd);

		info.setNeedLogin(comicConfig.isNeedLogin());

		return info;
	}

	public static String prepareStoreLocation(AppConfig appConfig, String comicName) throws IOException
	{
		String localPath = appConfig.getLocalBasePath().trim();

		localPath = StringUtils.trimTrailingCharacter(localPath, '/').trim();
		localPath = StringUtils.trimTrailingCharacter(localPath, '\\').trim();
		if (appConfig.isLocalSaveToSubFolder())
		{
			// save image to bus-folder: create sub folder if not exist
			localPath += "/" + comicName;
			Files.createDirectories(Paths.get(localPath));
		}

		return localPath;
	}

	public static String removeIllegalFName(String fname)
	{
		String[] replace = {",","/","\\","\"","'","+","*","?","!",">","<"};
		String res = fname;
		for (String r : replace) res = res.replace(r, "");
		return res;
	}
}
