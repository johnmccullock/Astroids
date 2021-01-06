package main.unit.entity.util;

import java.awt.geom.Point2D;
import java.util.UUID;

public class Rect2D implements Comparable<Rect2D>
{
	public float x = 0f;
	public float y = 0f;
	public float width = 0f;
	public float height = 0f;
	protected float mXOffset = 0f;
	protected float mYOffset = 0f;
	private UUID mID = null;
	
	public Rect2D(UUID id)
	{
		this.mID = id;
		return;
	}
	
	public Rect2D(UUID id, float x, float y, float width, float height)
	{
		this.mID = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.invalidate();
		return;
	}
	
	public void invalidate()
	{
		this.mXOffset = width / 2.0f;
		this.mYOffset = height / 2.0f;
		return;
	}
	
	public int x1()
	{
		return (int)Math.round(this.x - this.mXOffset);
	}
	
	public int y1()
	{
		return (int)Math.round(this.y - this.mYOffset);
	}
	
	public int x2()
	{
		return (int)Math.round(this.x + this.mXOffset);
	}
	
	public int y2()
	{
		return (int)Math.round(this.y + this.mYOffset);
	}
	
	public float getX1()
	{
		return this.x - this.mXOffset;
	}
	
	public float getY1()
	{
		return this.y - this.mYOffset;
	}
	
	public float getX2()
	{
		return this.x + this.mXOffset;
	}
	
	public float getY2()
	{
		return this.y + this.mYOffset;
	}
	
	public float getXOffset()
	{
		return this.mXOffset;
	}
	
	public float getYOffset()
	{
		return this.mYOffset;
	}
	
	public UUID getID()
	{
		return this.mID;
	}
	
	public boolean contains(float x, float y)
	{
		boolean result = true;
		if(x < this.x - this.mXOffset || x > this.x + this.mXOffset){
			return false;
		}
		if(y < this.y - this.mYOffset || y > this.y + this.mYOffset){
			return false;
		}
		return result;
	}
	
	public boolean contains(Point2D.Float point)
	{
		boolean result = true;
		if(point.x < this.x - this.mXOffset || point.x > this.x + this.mXOffset){
			return false;
		}
		if(point.y < this.y - this.mYOffset || point.y > this.y + this.mYOffset){
			return false;
		}
		return result;
	}
	
	public boolean contains(Rect2D that)
	{
		boolean result = true;
		if(that.getX2() < this.x - this.mXOffset || that.getX1() > this.x + this.mXOffset){
			return false;
		}
		if(that.getY2() < this.y - this.mYOffset || that.getY1() > this.y + this.mYOffset){
			return false;
		}
		return result;
	}
	
	public int compareTo(Rect2D that)
	{
		float thisArea = this.width * this.height;
		float thatArea = that.width * that.height;
		if(thisArea < thatArea){
			return -1;
		}else if(thisArea > thatArea){
			return 1;
		}
		return 0;
	}
}
