package my.spider.utils;

import lombok.Data;


@Data
public class NestInfo
{
	public static enum NestType {
		IF, LOOP
	}

	private NestType nestType;

	private boolean executable = false;
	private int startIdx = 0;
	private int finalIdx = 0;
	private int currentIdx = 0;
	private int startLine = 0;

	public NestInfo(boolean _executable)
	{
		this.nestType = NestType.IF;
		this.executable = _executable;
	}

	public NestInfo(int _startIdx, int _finalIdx, int _startLine)
	{
		this.nestType = NestType.LOOP;
		this.startIdx = _startIdx;
		this.finalIdx = _finalIdx;
		this.currentIdx = _startIdx;
		this.startLine = _startLine;
	}

	public boolean isIf()   { return (nestType == NestType.IF  ); }
	public boolean isLoop() { return (nestType == NestType.LOOP); }
}
