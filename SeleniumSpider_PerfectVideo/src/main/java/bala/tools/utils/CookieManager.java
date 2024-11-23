package bala.tools.utils;

import java.io.BufferedReader;
import java.io.FileReader;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// import org.springframework.util.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

// import bala.tools.model.ComicInfo;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CookieManager
{
	public static List<Cookie> ReadCookiesFromFile(String cookieFilePath)
	{
		ArrayList<Cookie> cookies = new ArrayList<Cookie>();
		FileReader fr = null;
		BufferedReader br = null;

		try
		{
			fr = new FileReader(cookieFilePath);
			br = new BufferedReader(fr);

			// StringBuilder sb = new StringBuilder();
			// String line = br.readLine();
			String line = "";

			while ((line = br.readLine()) != null)
			{
				if (line.trim().startsWith("#")) continue;
				if (line.trim().length() <= 0) continue;

				// 設太多反而加不進去, 只要 name, value 就好了.
				String[] c = line.split("\t");
				String name = c[5];
				String value = c[6];
				// String domain = c[0];
				// String path = c[2];
				// Date date = new Date(Long.parseLong(c[4]));
				// boolean secure = "TRUE".equals(c[3]);
				// boolean httpOnly = "TRUE".equals(c[1]);
				// Cookie o = new Cookie(name, value);
				cookies.add(new Cookie(name, value));

				// line = br.readLine();
			}
		}
		catch(Exception ex)
		{
			logger.error("Read Cookie Error: {}", ex.getMessage());
			cookies.clear();
		}
		finally
		{
			try{
				if (fr != null) fr.close();
				if (br != null) br.close();
			}catch(Exception ex){
				logger.error("Close File Error: {}", ex.getMessage());
			}
		}

		return cookies;
	}

	public static void SetCookiesToWebFriver(WebDriver driver, List<Cookie> cookies)
	{
		if (driver == null) return;
		if (cookies == null) return;
		if (cookies.size() <= 0) return;

		int cookieCnt = 0;
		for (Cookie cookie : cookies)
		{
			try{
				driver.manage().addCookie(cookie);
				++cookieCnt;
			}catch(Exception ex){
				logger.error("Set Cookie Error:");
				logger.error("  Cookie Name: {}", cookie.getName());
				logger.error("  Cookie value: {}", cookie.getValue());
				logger.error("  Error: {}", ex.getMessage());
			}
		}

		logger.info("Set {} cookies to driver", cookieCnt);
	}
}
