package main.polycomp;

public class PChar
{
	public char code = 0;
	public int width = 0;
	public int height = 0;
	
	public PChar(char code)
	{
		this.code = code;
		return;
	}
	
	public PChar(char code, int width, int height)
	{
		this.code = code;
		this.width = width;
		this.height = height;
		return;
	}
}
