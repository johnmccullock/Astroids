package main;

import java.awt.RenderingHints;

public enum RenderQuality
{
	HIGH(RenderingHints.VALUE_RENDER_QUALITY), 
	LOW(RenderingHints.VALUE_RENDER_SPEED);
	
	private Object value = null;
	
	private RenderQuality(Object value)
	{
		this.value = value;
		return;
	}
	
	public Object getValue()
	{
		return this.value;
	}
}
