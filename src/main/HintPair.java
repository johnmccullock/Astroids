package main;

import java.awt.RenderingHints;

/**
 * General data class containing RenderingHints for use with Graphics and Graphics2D drawing methods.
 * @author John McCullock
 * @version 1.0 2014-11-28
 */
public class HintPair
{
	public RenderingHints.Key key = null;
	public Object value = null;
	
	public HintPair() { return; }
	
	public HintPair(RenderingHints.Key key, Object value)
	{
		this.key = key;
		this.value = value;
		return;
	}
}
