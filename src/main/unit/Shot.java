package main.unit;

import java.awt.Rectangle;
import java.util.UUID;
import main.unit.entity.util.MathUtil;

public class Shot extends Unit
{
	private Shot.Listener mListener = null;
	private float mStartX = 0f;
	private float mStartY = 0f;
	private float mLastX = 0f;
	private float mLastY = 0f;
	private float mRange = 0f;
	private float mTravelled = 0f;
	
	public Shot(UUID id)
	{
		super(id);
		return;
	}
	
	public Shot(UUID id, Shot.Listener listener, float x, float y, float direction, float speed, float range)
	{
		super(id);
		this.mListener = listener;
		this.x = x;
		this.y = y;
		this.setStart(x, y);
		this.setDirection(direction);
		this.setSpeed(speed);
		this.setRange(range);
		return;
	}
	
	@Override
	public void update()
	{
		if(this.isDisposing()){
			return;
		}
		if(this.mTravelled > this.mRange){
			this.mIsDisposing = true;
			return;
		}
		float dirX = (float)Math.cos(this.mDirection);
		float dirY = -(float)Math.sin(this.mDirection);
		float newX = this.x + (dirX * this.mSpeed);
		float newY = this.y + (dirY * this.mSpeed);
		
		this.mTravelled += MathUtil.distance(this.x, this.y, newX, newY);
		
		newX = newX < 0f ? this.mListener.getScreenBounds().width : newX;
		newX = newX > this.mListener.getScreenBounds().width ? 0f : newX;
		newY = newY < 0f ? this.mListener.getScreenBounds().height : newY;
		newY = newY > this.mListener.getScreenBounds().height ? 0f : newY;
		
		this.mLastX = this.x;
		this.mLastY = this.y;
		this.x = newX;
		this.y = newY;
		this.invalidate();
		return;
	}
	
	public void setStart(float x, float y)
	{
		this.mStartX = x;
		this.mStartY = y;
		return;
	}
	
	public float getStartX()
	{
		return this.mStartX;
	}
	
	public float getStartY()
	{
		return this.mStartY;
	}
	
	public float getLastX()
	{
		return this.mLastX;
	}
	
	public float getLastY()
	{
		return this.mLastY;
	}
	
	public void setRange(float range)
	{
		this.mRange = range;
		return;
	}
	
	public float getRange()
	{
		return this.mRange;
	}
	
	public interface Listener
	{
		abstract Rectangle getScreenBounds();
	}
}
