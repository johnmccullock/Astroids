package main.unit;

import main.unit.entity.util.Polygon2;

@SuppressWarnings("serial")
public class Explosion extends Polygon2
{
	private Polygon2 mPoints = null;
	private int mSteps = 0;
	private int mCurrentStep = 0;
	private int mRate = 0; // the velocity of the moving debris.
	private boolean mIsDisposing = false;
	
	public Explosion(int[] xpoints, int[] ypoints, int npoints, int steps, int rate)
	{
		super(xpoints, ypoints, npoints);
		this.setSteps(steps);
		this.setRate(rate);
		return;
	}
	
	public void update()
	{
		if(this.mCurrentStep >= this.mSteps){
			this.mIsDisposing = true;
			return;
		}
		int xDiff = (int)Math.round(((this.getBounds().width + this.mRate) - this.getBounds().width) / 2f);
		int yDiff = (int)Math.round(((this.getBounds().height + this.mRate) - this.getBounds().height) / 2f);
		this.setLocation(this.getX() - xDiff, this.getY() - yDiff);
		this.resizePolygon(this.getBounds().width + this.mRate, this.getBounds().height + this.mRate);
		this.mCurrentStep++;
		return;
	}
	
	public void setPoints(Polygon2 points)
	{
		this.mPoints = points;
		return;
	}
	
	public Polygon2 getPoints()
	{
		return this.mPoints;
	}
	
	public void setSteps(int steps)
	{
		this.mSteps = steps;
		return;
	}
	
	public int getSteps()
	{
		return this.mSteps;
	}
	
	public void setRate(int rate)
	{
		this.mRate = rate;
		return;
	}
	
	public int getRate()
	{
		return this.mRate;
	}
	
	public void setDisposing(boolean isDisposing)
	{
		this.mIsDisposing = isDisposing;
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
