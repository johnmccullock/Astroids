package main.unit.entity;

import java.util.UUID;

import main.unit.entity.util.Rect2D;

/**
 * Basis for most game entities (i.e.; Unit, Ship, UFO, etc.).
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class Entity extends Rect2D
{
	protected final float PI = (float)Math.PI;
	
	protected Object mType = null; // Arbitrary identifier.
	protected float mRotation = 0f; // Radians.
	protected boolean mIsDisposing = false;
	
	public Entity(UUID id)
	{
		super(id);
		return;
	}
	
	public Entity(UUID id, float x, float y, float width, float height, float rotation)
	{
		super(id, x, y, width, height);
		this.mRotation = rotation;
		return;
	}
	
	public void update()
	{
		
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
	
	public void setSize(float width, float height)
	{
		this.width = width;
		this.height = height;
		this.invalidate();
		return;
	}
	
	public void setRotation(float radians)
	{
		this.mRotation = radians;
		return;
	}
	
	public float getRotation()
	{
		return this.mRotation;
	}
	
	public void setDisposing(boolean value)
	{
		this.mIsDisposing = value;
		return;
	}
	
	public boolean getDisposing()
	{
		return this.mIsDisposing;
	}
	
	public boolean isDisposing()
	{
		return this.mIsDisposing;
	}
}
