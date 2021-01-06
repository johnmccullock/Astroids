package main.unit.entity.util;

import java.awt.Point;

/**
 * Generic geometric path with convenience methods for resizing and setting location.
 * 
 * Best drawn with java.awt.Graphics drawPolyline() method.
 * 
 * @author John McCullock
 * @version 1.0 2015-07-23
 */
public class GeomPath
{
	public int[] xpoints = null;
	public int[] ypoints = null;
	public int npoints = 0;
	private int[] mBaselineX = null;
	private int[] mBaselineY = null;
	private int x = 0; // center x-coord.
	private int y = 0; // center y-coord.
	
	public GeomPath(int[] xpoints, int[] ypoints, int npoints)
	{
		this.xpoints = xpoints;
		this.ypoints = ypoints;
		this.npoints = npoints;
		this.mBaselineX = xpoints;
		this.mBaselineY = ypoints;
		this.invalidate();
		return;
	}
	
	public void invalidate()
	{
		for(int i = 0; i < this.npoints; i++)
		{
			this.xpoints[i] = this.mBaselineX[i];
			this.ypoints[i] = this.mBaselineY[i];
		}
		int left = this.min(this.xpoints);
		int top = this.min(this.ypoints);
		this.x = left + (int)Math.round((this.max(this.xpoints) - left) / 2f);
		this.y = top + (int)Math.round((this.max(this.ypoints) - top) / 2f);
		return;
	}
	
	public void rotate(float radians)
	{
		Point results = new Point();
		for(int i = 0; i < this.npoints; i++)
		{
			this.rotatePoint(this.mBaselineX[i], this.mBaselineY[i], radians, results);
			this.xpoints[i] = results.x;
			this.ypoints[i] = results.y;
		}
		return;
	}
	
	private void rotatePoint(int pointX, int pointY, float radians, Point results)
	{
		float dx = pointX - this.x;
		float dy = pointY - this.y;
		
		results.x = this.x + (int)Math.round(dx * Math.cos(-radians) - dy * Math.sin(-radians));
		results.y = this.y + (int)Math.round(dx * Math.sin(-radians) + dy * Math.cos(-radians));
		return;
	}
	
	public void setLocation(int x, int y)
	{
		int xDiff = (x - this.x);
		int yDiff = (y - this.y);
		for(int i = 0; i < this.npoints; i++)
		{	
			this.mBaselineX[i] += xDiff;
			this.mBaselineY[i] += yDiff;
		}
		this.invalidate();
		return;
	}
	
	public void resize(float newWidth, int newHeight)
	{
		int left = this.min(this.xpoints);
		int top = this.min(this.ypoints);
		int width = this.max(this.xpoints) - this.min(this.xpoints);
		int height = this.max(this.ypoints) - this.min(this.ypoints);
		int[] xpoints = new int[this.npoints];
		int[] ypoints = new int[this.npoints];
		
		for(int i = 0; i < npoints; i++)
		{
			float xFactor = (this.xpoints[i] - left) / (float)width;
			float yFactor = (this.ypoints[i] - top) / (float)height;
			xpoints[i] = left + (int)Math.round(newWidth * xFactor);
			ypoints[i] = top + (int)Math.round(newHeight * yFactor);
		}
		this.mBaselineX = xpoints;
		this.mBaselineY = ypoints;
		this.invalidate();
		return;
	}
	
	private int max(int[] values)
	{
		int maximum = -1000000; // just any small number.
		for(int i = 0; i < values.length; i++)
		{
			if(values[i] > maximum){
				maximum = values[i];
			}
		}
		return maximum;
	}
	
	private int min(int[] values)
	{
		int minimum = 1000000; // just any big number.
		for(int i = 0; i < values.length; i++)
		{
			if(values[i] < minimum){
				minimum = values[i];
			}
		}
		return minimum;
	}
	
	public int getLeft()
	{
		return this.min(this.xpoints);
	}
	
	public int getRight()
	{
		return this.max(this.xpoints);
	}
	
	public int getTop()
	{
		return this.min(this.ypoints);
	}
	
	public int getBottom()
	{
		return this.max(this.ypoints);
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
}
