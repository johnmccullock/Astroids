package main;

import java.awt.RenderingHints;

public enum RenderColor
{
	HIGH(RenderingHints.VALUE_COLOR_RENDER_QUALITY), 
	LOW(RenderingHints.VALUE_COLOR_RENDER_SPEED);
	
	private Object value = null;
	
	private RenderColor(Object value)
	{
		this.value = value;
		return;
	}
	
	public Object getValue()
	{
		return this.value;
	}
}
