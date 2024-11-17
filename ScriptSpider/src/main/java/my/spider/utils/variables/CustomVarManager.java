package my.spider.utils.variables;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CustomVarManager
{
    private static HashMap<String, String> variables = new HashMap<String, String>();

    public static Pattern pattern = Pattern.compile("\\$\\((?<varkey>[^\\s\\(\\)]+)\\)", Pattern.CASE_INSENSITIVE);
    public static String put(String key, String value) { return CustomVarManager.variables.put(key.toLowerCase(), value); }
    public static String get(String key) { return CustomVarManager.containsKey(key) ? variables.get(key.toLowerCase()) : ""; }
    public static int size() { return CustomVarManager.variables.size(); }
    public static boolean containsKey(String key) { return CustomVarManager.variables.containsKey(key.toLowerCase()); }

    public static String parseCustVars(String paramWtVar) throws Exception
    {
        // variable in script: $(varName)
        String paramWoVar = paramWtVar;
		Matcher matcher = pattern.matcher(paramWoVar);
		while (matcher.find())
		{
            // get match variable key
            String varKey = matcher.group("varkey");
            if (!CustomVarManager.containsKey(varKey))
            {
                logger.error("Variable not found in GlocalVariables: {}", varKey);
                throw new Exception();
            }

            // replace all variable key to value
            String varValue = CustomVarManager.get(varKey);
            varKey = String.format("\\$\\(%s\\)", varKey);
            paramWoVar = paramWoVar.replaceAll(varKey, varValue);

            // re-match new string
            matcher.reset();
            matcher = pattern.matcher(paramWoVar);
        }

        return paramWoVar;
    }
}
