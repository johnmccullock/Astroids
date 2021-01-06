package main.polycomp;

import java.awt.Font;
import java.awt.Graphics2D;

public class ContentToolkit
{
	public static int getContentWidth(Graphics2D g2d, Font font, Object contents)
	{
		int width = 0;
		if(contents instanceof String){
			width = g2d.getFontMetrics(font).stringWidth((String)contents);
		}
		if(contents instanceof Integer){
			width = g2d.getFontMetrics(font).stringWidth(String.valueOf((Integer)contents));
		}
		
		return width;
	}
	
	public static int getContentHeight(Graphics2D g2d, Font font, Object contents)
	{
		int height = 0;
		if(contents instanceof String || contents instanceof Integer){
			height = g2d.getFontMetrics(font).getHeight();
		}
		
		return height;
	}
}
