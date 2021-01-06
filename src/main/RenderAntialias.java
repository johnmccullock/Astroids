package main;

import java.awt.RenderingHints;

public enum RenderAntialias
{
	ON(RenderingHints.VALUE_ANTIALIAS_ON), 
	OFF(RenderingHints.VALUE_ANTIALIAS_OFF);
	
	private Object value = null;
	
	private RenderAntialias(Object value)
	{
		this.value = value;
		return;
	}
	
	public Object getValue()
	{
		return this.value;
	}
}
