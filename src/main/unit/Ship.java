package main.unit;

import java.awt.Rectangle;
import java.util.UUID;

import main.unit.entity.util.MathUtil;
import main.unit.entity.util.Polygon2;

public class Ship extends Unit
{
	private Ship.Listener mListener = null;
	private float mAcceleration = 0.0f;
	private float mMass = 0.0f;
	private float mMaxSpeed = 0.0f;
	private float mRotation = 0f; // Radians.
	private float mRotationIncrement = 0.0f;
	private Polygon2 mShape = null;
	
	public Ship(UUID id, Ship.Listener listener)
	{
		super(id);
		this.mListener = listener;
		return;
	}
	
	@Override
	public void update()
	{
		this.updatePosition();
		this.updateRotation();
		return;
	}
	
	private void updatePosition()
	{
		if(this.mSpeed > 0.0f){
			this.mSpeed -= (this.mSpeed / this.mMass) / 10.0f;
		}
		float dirX = (float)Math.cos(this.mDirection);
		float dirY = -(float)Math.sin(this.mDirection);
		float newX = this.x + (dirX * this.mSpeed);
		float newY = this.y + (dirY * this.mSpeed);
		newX = newX < 0f ? this.mListener.getScreenBounds().width : newX;
		newX = newX > this.mListener.getScreenBounds().width ? 0f : newX;
		newY = newY < 0f ? this.mListener.getScreenBounds().height : newY;
		newY = newY > this.mListener.getScreenBounds().height ? 0f : newY;
		this.x = newX;
		this.y = newY;
		this.invalidate();
		this.mShape.setLocation((int)Math.round(this.x), (int)Math.round(this.y));
		
		super.update();
		return;
	}
	
	private void updateRotation()
	{
		this.mShape.rotate(this.mRotation);
		return;
	}
	
	public void reset(float x, float y, float rotation)
	{
		this.mSpeed = 0f;
		this.x = x;
		this.y = y;
		this.mShape.setLocation((int)Math.round(x), (int)Math.round(y));
		this.mRotation = rotation;
		this.mDirection = rotation;
		this.updateRotation();
		return;
	}
	
	public void applyThrust(float speedLimit)
	{
		if(this.mDirection != this.mRotation){
			float dRotX = 0.0f;
			float dRotY = 0.0f;
			dRotX = (this.mAcceleration / this.mMass) * (float)Math.cos(this.mRotation);
			dRotY = (this.mAcceleration / this.mMass) * (float)Math.sin(this.mRotation);
			float dDestX = this.mSpeed * (float)Math.cos(this.mDirection);
			float dDestY = this.mSpeed * (float)Math.sin(this.mDirection);
			
			float dNewX = dDestX + dRotX;
			float dNewY = dDestY + dRotY;
			
			this.mSpeed = (float)Math.sqrt((dNewX * dNewX) + (dNewY * dNewY));
			if(this.mSpeed > this.mMaxSpeed){
				this.mSpeed = this.mMaxSpeed;
			}
			
			this.mDirection = (float)Math.atan2(dNewY, dNewX);
		}else{
			if(this.mSpeed + (this.mAcceleration / this.mMass) <= this.mMaxSpeed){
				this.mSpeed += (this.mAcceleration / this.mMass);
			}else{
				this.mSpeed = this.mMaxSpeed;
			}
		}
		return;
	}
	
	public void rotateRight()
	{
		// Normalizing is necessary here for radian comparisons.
		this.setRotation(MathUtil.norm(this.getRotation() - this.getRotationIncrement()));
		return;
	}
	
	public void rotateLeft()
	{
		// Normalizing is necessary here for radian comparisons.
		this.setRotation(MathUtil.norm(this.getRotation() + this.getRotationIncrement()));
		return;
	}
	
	public void setAccelerationRate(float rate)
	{
		this.mAcceleration = rate;
		return;
	}
	
	public float getAccelerationRate()
	{
		return this.mAcceleration;
	}
	
	public void setMaxSpeed(float maxSpeed)
	{
		this.mMaxSpeed = maxSpeed;
		return;
	}
	
	public float getMaxSpeed()
	{
		return this.mMaxSpeed;
	}
	
	public void setMass(float mass)
	{
		this.mMass = mass;
		return;
	}
	
	public float getMass()
	{
		return this.mMass;
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
	
	public void setRotationIncrement(float increment)
	{
		this.mRotationIncrement = increment;
		return;
	}
	
	public float getRotationIncrement()
	{
		return this.mRotationIncrement;
	}
	
	public void setShape(Polygon2 shape)
	{
		this.mShape = shape;
		return;
	}
	
	public Polygon2 getShape()
	{
		return this.mShape;
	}
	
	public interface Listener
	{
		abstract Rectangle getScreenBounds();
	}
}
