package main.unit;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.UUID;

import main.unit.entity.util.GeomPath;
import main.unit.entity.util.MathUtil;
import main.unit.entity.util.Polygon2;

public class UFO extends Unit
{
	public enum Size
	{
		LARGE(250), 
		SMALL(500);
		
		private int mScore = 0;
		
		private Size(int score)
		{
			this.mScore = score;
			return;
		}
		
		public int getScore()
		{
			return this.mScore;
		}
	};
	
	private UFO.Listener mListener = null;
	private UFO.Size mSize = null;
	/*
	 * The UFO class has both mShape and mBounds fields because of the geometric path being used
	 * for the shape.  It's easier to check for collisions with a polygon than with a path.
	 */
	private GeomPath mShape = null;
	private Polygon2 mBounds = null;
	private Point2D.Float mDestination = new Point2D.Float();
	
	private Moving mMoving = new Moving();
	private ChangeCourse mChangeCourse = new ChangeCourse();
	private WeaponIdle mWeaponIdle = new WeaponIdle();
	private PreparingToFire mPreparingToFire = new PreparingToFire();
	private Firing mFiring = new Firing();
	private Behavable mMotionState = this.mMoving;
	private Behavable mWeaponState = this.mPreparingToFire;
	
	public UFO(UUID id, UFO.Listener listener)
	{
		super(id);
		this.mListener = listener;
		this.initialize();
		return;
	}
	
	public UFO(UUID id, UFO.Listener listener, UFO.Size size)
	{
		super(id);
		this.mListener = listener;
		this.setSize(size);
		this.initialize();
		return;
	}
	
	private void initialize()
	{
		this.mPreparingToFire.reset();
		return;
	}
	
	@Override
	public void update()
	{
		this.mMotionState.update();
		this.mWeaponState.update();
		super.update();
		return;
	}
	
	public void setToWeaponIdle()
	{
		this.mWeaponIdle.reset();
		this.mWeaponState = this.mWeaponIdle;
		return;
	}
	
	public void initializeDestination()
	{
		float min = this.mListener.getScreenBounds().width * 0.2f;
		float max = this.mListener.getScreenBounds().width * 0.8f;
		this.mDestination.x = ((float)Math.random() * (max - min)) + min;
		this.mDestination.y = this.y;
		return;
	}
	
	public void setSize(UFO.Size size)
	{
		this.mSize = size;
		return;
	}
	
	public UFO.Size getSize()
	{
		return this.mSize;
	}
	
	public void setShape(GeomPath shape)
	{
		this.mShape = shape;
		return;
	}
	
	public GeomPath getShape()
	{
		return this.mShape;
	}
	
	public void setBounds(Polygon2 bounds)
	{
		this.mBounds = bounds;
		return;
	}
	
	public Polygon2 getBounds()
	{
		return this.mBounds;
	}
	
	private interface Behavable
	{
		abstract void reset();
		abstract void update();
	}
	
	private class Moving implements Behavable
	{
		public void reset() { return; }

		public void update()
		{
			float dirX = (float)Math.cos(getDirection());
			float dirY = -(float)Math.sin(getDirection());
			float newX = x + (dirX * getSpeed());
			float newY = y + (dirY * getSpeed());
			
			newY = newY < 0f ? mListener.getScreenBounds().height : newY;
			newY = newY > mListener.getScreenBounds().height ? 0f : newY;
			
			if(MathUtil.distance(newX, newY, mDestination.x, mDestination.y) <= getSpeed()){
				x = mDestination.x;
				y = mDestination.y;
				mMotionState = mChangeCourse;
				return;
			}else{
				x = newX;
				y = newY;
			}
			return;
		}
	}
	
	private class ChangeCourse implements Behavable
	{
		public void reset() { return; }

		public void update()
		{
			if(getDirection() == 0f || getDirection() == PI){
				if(Math.random() < 0.5){
					setDirection(getDirection() + (PI * 0.333f));
				}else{
					setDirection(getDirection() - (PI * 0.333f));
				}
			}else{
				if(getDirection() > PI * 0.5f && getDirection() < PI * 1.5f){
					setDirection(PI);
				}else{
					setDirection(0f);
				}
			}
			float distance = ((float)Math.random() * ((mListener.getScreenBounds().width * 0.5f) - (mListener.getScreenBounds().width * 0.125f))) + (mListener.getScreenBounds().width * 0.125f);
			float dirX = (float)Math.cos(getDirection());
			float dirY = -(float)Math.sin(getDirection());
			mDestination.x = x + (dirX * distance);
			mDestination.y = y + (dirY * distance);
			
			mMotionState = mMoving;
			return;
		}
	}
	
	private class WeaponIdle implements Behavable
	{
		public void reset() { return; }
		
		public void update() { return; }
	}
	
	private class PreparingToFire implements Behavable
	{
		long delayStart = 0;
		long delay = 1500;
		
		public void reset()
		{
			this.delayStart = System.currentTimeMillis();
			return;
		}
		
		public void update()
		{
			if(System.currentTimeMillis() - this.delayStart > this.delay){
				mFiring.reset();
				mWeaponState = mFiring;
				return;
			}
			return;
		}
	}
	
	private class Firing implements Behavable
	{
		public void reset() { return; }
		
		public void update()
		{
			mListener.UFOFirePerformed(UFO.this);
			mPreparingToFire.reset();
			mWeaponState = mPreparingToFire;
			return;
		}
	}
	
	public interface Listener
	{
		abstract Rectangle getScreenBounds();
		abstract void UFOFirePerformed(UFO u);
	}
}
