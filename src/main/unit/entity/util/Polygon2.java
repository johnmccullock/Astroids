package main.unit.entity.util;

import java.awt.Point;
import java.awt.Polygon;

/**
 * An extension of the java.awt.Polygon class which includes convenience methods for relocating, resizing, 
 * and rotating the polygon points.
 * 
 * Version 1.1 introduces baseline arrays to keep rounding errors from destroying original shape during 
 * rotate procedures.
 * 
 * Version 1.2 includes finiteLineIntersects() method.
 * 
 * @author John McCullock
 * @version 1.2 2015-07-15
 */
@SuppressWarnings("serial")
public class Polygon2 extends Polygon
{
	private int[] mBaselineX = null;
	private int[] mBaselineY = null;
	private int x = 0; // center x-coord.
	private int y = 0; // center y-coord.
	
	public Polygon2(int[] xPoints, int[] yPoints, int nPoints)
	{
		super(xPoints, yPoints, nPoints);
		this.mBaselineX = xPoints;
		this.mBaselineY = yPoints;
		this.invalidate();
		return;
	}
	
	@Override
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
		super.invalidate();
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
	
	public void resizePolygon(float newWidth, int newHeight)
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
	
	/**
	 * Checks if a specified finite line segment intersects and of this polygon's line segments.
	 * @param x1 float x-coordinate of line start.
	 * @param y1 float y-coordinate of line start.
	 * @param x2 float x-coordinate of line end.
	 * @param y2 float y-coordinate of line end.
	 * @return boolean true of intersection occurs, false otherwise.
	 */
	public boolean finiteLineIntersects(float x1, float y1, float x2, float y2)
	{
		for(int i = 0; i < this.npoints; i++)
		{
			if(i == this.npoints - 1){
				if(MathUtil.LineSegmentsIntersect(this.xpoints[i], this.ypoints[i], this.xpoints[0], this.ypoints[0], x1, y1, x2, y2)){
					return true;
				}
			}else{
				if(MathUtil.LineSegmentsIntersect(this.xpoints[i], this.ypoints[i], this.xpoints[i + 1], this.ypoints[i + 1], x1, y1, x2, y2)){
					return true;
				}
			}
		}
		return false;
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
