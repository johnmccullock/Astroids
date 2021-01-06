package main;

import java.awt.RenderingHints;

public enum RenderAlpha
{
	HIGH(RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY), 
	LOW(RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
	
	private Object value = null;
	
	private RenderAlpha(Object value)
	{
		this.value = value;
		return;
	}
	
	public Object getValue()
	{
		return this.value;
	}
}
