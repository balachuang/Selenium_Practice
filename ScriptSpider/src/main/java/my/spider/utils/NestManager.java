package my.spider.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import my.spider.commander.Cmd_Loop;
import my.spider.commander.Cmd_Next;


@Slf4j
// support nested If / Loop
public class NestManager
{
	private static ArrayList<NestInfo> nestObjs = new ArrayList<NestInfo>();
	private static HashMap<Integer, Integer> loopBlocks = new HashMap<Integer, Integer>();

	public static void enterIfBlock(boolean condition)
	{
		nestObjs.add(new NestInfo(condition));
	}

	public static void enterLoopBlock(int startIdx, int finalIdx, int currLine)
	{
		// swap
		if (startIdx > finalIdx)
		{
			int temp = startIdx;
			startIdx = finalIdx;
			finalIdx = temp;
		}

		nestObjs.add(new NestInfo(startIdx, finalIdx, currLine));
	}

	public static boolean isInIfBlock()
	{
		if (nestObjs.size() == 0) return false;
		return peekObj().isIf();
	}

	public static boolean isInLoopBlock()
	{
		if (nestObjs.size() == 0) return false;
		return peekObj().isLoop();
	}

	public static boolean isInExecutableIf()
	{
		// not in If, executable
		if (nestObjs.size() == 0) return true;
		if (!isInIfBlock()) return true;

		// in If, decide by condition.
		return peekObj().isExecutable();
	}

	public static Integer getLoopStartLine()
	{
		if (!isInLoopBlock()) return null;
		return peekObj().getStartLine();
	}

	public static Integer getCurrLoopIndex()
	{
		if (!isInLoopBlock()) return null;
		return peekObj().getCurrentIdx();
	}

	public static boolean gotoNextLoopIndex()
	{
		if (!isInLoopBlock()) return false;

		int idx = peekObj().getCurrentIdx();
		if (idx >= peekObj().getFinalIdx())
		{
			peekObj().setCurrentIdx(-1);
			return false;
		}

		peekObj().setCurrentIdx(idx + 1);
		return true;
	}

	public static boolean exitIfBlock()
	{
		// no in If, cannot exit
		if (!isInIfBlock()) return false;

		popObj();
		return true;
	}

	public static boolean exitLoopBlock()
	{
		// no in Loop, cannot exit
		if (!isInLoopBlock()) return false;

		popObj();
		return true;
	}

	public static int exitRecentLoopBlock()
	{
		if (nestObjs.size() == 0) return -1;

		int idx;
		boolean foundLoop = false;
		for (idx=nestObjs.size()-1; idx>=0; --idx)
		{
			if(nestObjs.get(idx).isLoop())
			{
				foundLoop = true;
				break;
			}
		}

		if (!foundLoop) return -1;
		int loopStartPos = nestObjs.get(idx).getStartLine();

		// revmoe element nestObjs[n]
		for (int n=nestObjs.size()-1; n>=idx; --n) nestObjs.remove(n);

		return loopStartPos;
	}

	public static boolean preloadLoopBlock(ArrayList<String> scripts)
	{
		if (scripts == null) return false;
		if (scripts.size() == 0) return false;

		Pattern loopPattern = Pattern.compile("\\b(Loop|進入迴圈)\\b");
		Pattern nextPattern = Pattern.compile("\\b(Next|回到開頭)\\b");
		Stack<Integer> loopPos = new Stack<Integer>();

		try{
			for (int idx=0; idx<scripts.size(); ++idx)
			{
				String script = scripts.get(idx);

				Matcher loopMatcher = loopPattern.matcher(script);
				if (loopMatcher.find()) loopPos.push(idx);

				Matcher nextMatcher = nextPattern.matcher(script);
				if (nextMatcher.find())
				{
					// store loop start/end position
					int loopStart = loopPos.pop();
					loopBlocks.put(loopStart, idx);
				}
			}

			if (loopPos.size() > 0)
			{
				// no end map to start
				logger.error("Pre-load Loop Block Fail: Loop not found for Next");
				return false;
			}
		}catch(Exception ex){
			logger.error("Pre-load Loop Block Exceotion: {}", ex.getMessage());
			return false;
		}

		return true;
	}

	public static int getLoopEndPos(int loopStartPos)
	{
		if (!loopBlocks.containsKey(loopStartPos))
		{
			logger.error("Loop End Position Not Found for: {}", loopStartPos);
			return -1;
		}

		return loopBlocks.get(loopStartPos);
	}

	private static NestInfo peekObj()
	{
		if (nestObjs.size() == 0) return null;
		return nestObjs.get(nestObjs.size() - 1);
	}

	private static NestInfo popObj()
	{
		if (nestObjs.size() == 0) return null;

		int idx = nestObjs.size() - 1;
		NestInfo obj = nestObjs.get(idx);
		nestObjs.remove(idx);

		return obj;
	}
}
