package main.polycomp;

import java.awt.Polygon;

/**
 * A general purpose, polygon-based data class for use with PTable, PPopupMenu and others.
 * 
 * Version 1.1 includes the action command field, similar to that used with javax.swing.JButton.
 * 
 * Version 1.2 rewritten to extend PolyShape
 * 
 * Version 1.3 adds PHoverable implementation, and uses an Object "mType" to identify the item instead of ActionCommand.
 * 
 * @author John McCullock
 * @version 1.3 2015-04-25
 */
public class PItem extends PolyShape implements PHoverable, PSelectable
{
	private Object mContents = null;
	private boolean mIsVisible = true;
	private Object mType = null;
	private boolean mComponentIsHovered = false;
	private boolean mComponentIsSelected = false;
	
	public PItem() { return; }
	
	public PItem(Polygon poly, Object contents)
	{
		int[] xpoints = new int[poly.npoints];
		int[] ypoints = new int[poly.npoints];
		for(int i = 0; i < poly.npoints; i++)
		{
			xpoints[i] = poly.xpoints[i];
			ypoints[i] = poly.ypoints[i];
		}
		this.setPolygon(new Polygon(xpoints, ypoints, poly.npoints));
		this.mContents = contents;
		return;
	}
	
	public PItem(Polygon poly, Object contents, Object type)
	{
		int[] xpoints = new int[poly.npoints];
		int[] ypoints = new int[poly.npoints];
		for(int i = 0; i < poly.npoints; i++)
		{
			xpoints[i] = poly.xpoints[i];
			ypoints[i] = poly.ypoints[i];
		}
		this.setPolygon(xpoints, ypoints, poly.npoints);
		this.mContents = contents;
		this.mType = type;
		return;
	}
	
	public void setType(Object type)
	{
		this.mType = type;
		return;
	}
	
	public Object getType()
	{
		return this.mType;
	}
	
	public void setContents(Object contents)
	{
		this.mContents = contents;
		return;
	}
	
	public Object getContents()
	{
		return this.mContents;
	}
	
	public void setVisible(boolean value)
	{
		this.mIsVisible = value;
		return;
	}
	
	public boolean getVisible()
	{
		return this.mIsVisible;
	}
	
	public boolean isVisible()
	{
		return this.mIsVisible;
	}
	
	public void setComponentHovered(boolean state)
	{
		this.mComponentIsHovered = state;
		return;
	}

	public boolean componentIsHovered()
	{
		return this.mComponentIsHovered;
	}
	
	public void setComponentSelected(boolean state)
	{
		this.mComponentIsSelected = state;
		return;
	}
	
	public boolean componentIsSelected()
	{
		return this.mComponentIsSelected;
	}
}
