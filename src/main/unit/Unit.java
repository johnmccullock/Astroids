package main.unit;

import java.util.UUID;

import main.unit.entity.Entity;

/**
 * Extends the Entity class with movement functions.
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public abstract class Unit extends Entity
{
	protected float mSpeed = 0f;
	protected float mDirection = 0f;
	
	public Unit(UUID id)
	{
		super(id);
		return;
	}
	
	public void setSpeed(float speed)
	{
		this.mSpeed = speed;
		return;
	}
	
	public float getSpeed()
	{
		return this.mSpeed;
	}
	
	public void setDirection(float direction)
	{
		this.mDirection = direction;
		return;
	}
	
	public float getDirection()
	{
		return this.mDirection;
	}
}
