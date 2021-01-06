package main.polycomp;

import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 * A general purpose polygon class based on the java.awt.Polygon, with some added fields and methods.
 * 
 * Note: As with the java.awt.Polygon, this class's invalidate() method needs to be called any time the 
 * xpoints or ypoints arrays change.
 * 
 * Version 1.1 includes constructor to create from existing polygon.
 * 
 * Version 1.2 includes PFocusable and PHoverable implementations.
 * 
 * Version 1.3 includes the resizePolygon method, added to remedy issues with the polygon's reference being
 * orphaned after using the getResizedPolygon method.
 * 
 * Version 1.4 includes the setLocation method.
 * 
 * Version 1.5 includes width-only resize and height-only resize methods.
 * 
 * Version 1.6 rewrite of resize methods for overall resizing, not stretching from the center.
 * 
 * Version 1.7 moves PHoverable implementation to PItem which would usually extends PolyShape for its purposes.
 * PFucusable is removed altogether.
 * 
 * @author John McCullock
 * @version 1.7 2015-05-25
 */
public class PolyShape
{
	private Polygon mPolygon = new Polygon();
	private int mCenterX = 0;
	private int mCenterY = 0;
	private int mWidth = 0;
	private int mHeight = 0;
	private int mTop = 0;
	private int mBottom = 0;
	private int mLeft = 0;
	private int mRight = 0;
	
	public PolyShape() { return; }
	
	public PolyShape(int[] xpoints, int[] ypoints, int npoints)
	{
		this.setPolygon(xpoints, ypoints, npoints);
		return;
	}
	
	public PolyShape(Polygon polygon)
	{
		int[] xpoints = new int[polygon.npoints];
		int[] ypoints = new int[polygon.npoints];
		for(int i = 0; i < polygon.npoints; i++)
		{
			xpoints[i] = polygon.xpoints[i];
			ypoints[i] = polygon.ypoints[i];
		}
		this.setPolygon(xpoints, ypoints, polygon.npoints);
		return;
	}
	
	public void invalidate()
	{
		this.mPolygon.invalidate();
		this.mCenterX = this.approxAverage(this.mPolygon.xpoints);
		this.mCenterY = this.approxAverage(this.mPolygon.ypoints);
		this.mWidth = this.max(this.mPolygon.xpoints) - this.min(this.mPolygon.xpoints);
		this.mHeight = this.max(this.mPolygon.ypoints) - this.min(this.mPolygon.ypoints);
		this.mTop = this.min(this.mPolygon.ypoints);
		this.mBottom = this.max(this.mPolygon.ypoints);
		this.mLeft = this.min(this.mPolygon.xpoints);
		this.mRight = this.max(this.mPolygon.xpoints);
		return;
	}
	
	/**
	 * General purpose function to find the mathematical average of all values within an Integer array, and
	 * return a result rounded to an integer.
	 * @param values Integer array.
	 * @return Integer
	 */
	private int approxAverage(final int[] values)
	{
		int sum = 0;
		for(int i = 0; i < values.length; i++)
		{
			sum += values[i];
		}
		return (int)Math.round(sum / (double)values.length);
	}
	
	/**
	 * General purpose function to find maximum value within an Integer array.
	 * @param values Integer array.
	 * @return Integer
	 */
	private int max(final int[] values)
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
	
	/**
	 * General purpose function to find minimum value within an Integer array.
	 * @param values Integer array
	 * @return Integer
	 */
	private int min(final int[] values)
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
	
	private int minIndex(final int[] values)
	{
		int index = 0;
		for(int i = 0; i < values.length; i++)
		{
			if(values[i] < values[index]){
				index = i;
			}
		}
		return index; 
	}
	
	/**
	 * Renders a polygon by drawing it one line at a time, between all points.
	 * @param g2d Graphics2D reference used for drawing.
	 * @param polygon Polygon shape to draw.
	 */
	public static void drawPolygon(Graphics2D g2d, Polygon polygon)
	{
		for(int i = 0; i < polygon.npoints; i++)
		{
			if(i == polygon.npoints - 1){
				g2d.drawLine(polygon.xpoints[i], polygon.ypoints[i], polygon.xpoints[0], polygon.ypoints[0]);
			}else{
				g2d.drawLine(polygon.xpoints[i], polygon.ypoints[i], polygon.xpoints[i + 1], polygon.ypoints[i + 1]);
			}
		}
		return;
	}
	
	/**
	 * One way to get a resized polygon based on this class's polygon.
	 * 
	 * @param newWidth Integer value specifying overall width.
	 * @param newHeight Integer value specifying overall height.
	 * @return Polygon
	 */
	public Polygon getResizedPolygon(int newWidth, int newHeight)
	{
		int npoints = this.mPolygon.npoints;
		int[] xpoints = new int[npoints];
		int[] ypoints = new int[npoints];
		
		for(int i = 0; i < npoints; i++)
		{
			double xFactor = this.mPolygon.xpoints[i] / (double)this.mWidth;
			double yFactor = this.mPolygon.ypoints[i] / (double)this.mHeight;
			xpoints[i] = (int)Math.round(newWidth * xFactor);
			ypoints[i] = (int)Math.round(newHeight *yFactor);
		}
		return new Polygon(xpoints, ypoints, npoints);
	}
	
	/**
	 * Resizes the width and height of an existing polygon.
	 * @param newWidth int value of the new width.
	 * @param newHeight int value of the new height;
	 */
	public void resizePolygon(int newWidth, int newHeight)
	{
		int npoints = this.mPolygon.npoints;
		int[] xpoints = new int[npoints];
		int[] ypoints = new int[npoints];
		
		for(int i = 0; i < npoints; i++)
		{
			double xFactor = (this.mPolygon.xpoints[i] - this.mLeft) / (double)this.mWidth;
			double yFactor = (this.mPolygon.ypoints[i] - this.mTop) / (double)this.mHeight;
			xpoints[i] = this.mLeft + (int)Math.round(newWidth * xFactor);
			ypoints[i] = this.mTop + (int)Math.round(newHeight * yFactor);
		}
		this.mPolygon.xpoints = xpoints;
		this.mPolygon.ypoints = ypoints;
		this.invalidate();
		return;
	}
	
	public void resizePolygonWidth(int newWidth)
	{
		int npoints = this.mPolygon.npoints;
		int[] xpoints = new int[npoints];
		for(int i = 0; i < npoints; i++)
		{
			if(this.mPolygon.xpoints[i] > this.mCenterX){
				xpoints[i] = newWidth - (this.getWidth() - this.getPolygon().xpoints[i]);
			}else{
				xpoints[i] = this.mPolygon.xpoints[i];
			}
		}
		this.mPolygon.xpoints = xpoints;
		this.invalidate();
		return;
	}
	
	public void resizePolygonHeight(int newHeight)
	{
		int npoints = this.mPolygon.npoints;
		int[] ypoints = new int[npoints];
		
		for(int i = 0; i < npoints; i++)
		{
			if(this.mPolygon.ypoints[i] > this.mCenterY){
				ypoints[i] = newHeight - (this.getHeight() - this.mPolygon.ypoints[i]);
			}else{
				ypoints[i] = this.mPolygon.ypoints[i];
			}
		}
		this.mPolygon.ypoints = ypoints;
		this.invalidate();
		return;
	}
	
	/**
	 * Set the polygon's upper-left-most coordinates to match the new location.  
	 * @param x int value of the new horizontal location.
	 * @param y int value of the new vertical location.
	 */
	public void setLocation(int x, int y)
	{
		int xDiff = x - this.mLeft;
		int yDiff = y - this.mTop;
		for(int i = 0; i < this.mPolygon.npoints; i++)
		{	
			this.mPolygon.xpoints[i] += xDiff;
			this.mPolygon.ypoints[i] += yDiff;
		}
		this.invalidate();
		return;
	}
	
	public void setPolygon(Polygon p)
	{
		int[] xpoints = new int[p.npoints];
		int[] ypoints = new int[p.npoints];
		for(int i = 0; i < p.npoints; i++)
		{
			xpoints[i] = p.xpoints[i];
			ypoints[i] = p.ypoints[i];
		}
		this.mPolygon = new Polygon(xpoints, ypoints, p.npoints);
		this.invalidate();
		return;
	}
	
	public void setPolygon(int[] xpoints, int[] ypoints, int npoints)
	{
		this.mPolygon = new Polygon(xpoints, ypoints, npoints);
		this.invalidate();
		return;
	}
	
	public Polygon getPolygon()
	{
		return this.mPolygon;
	}
	
	public int getCenterX()
	{
		return this.mCenterX;
	}
	
	public int getCenterY()
	{
		return this.mCenterY;
	}
	
	public int getWidth()
	{
		return this.mWidth;
	}
	
	public int getHeight()
	{
		return this.mHeight;
	}
	
	public int getTop()
	{
		return this.mTop;
	}
	
	public int getBottom()
	{
		return this.mBottom;
	}
	
	public int getLeft()
	{
		return this.mLeft;
	}
	
	public int getRight()
	{
		return this.mRight;
	}
}
