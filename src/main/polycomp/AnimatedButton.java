package main.polycomp;

import java.awt.Dimension;
import java.awt.Polygon;

public class AnimatedButton extends PItem
{
	private boolean mIsExpanding = false;
	private int mNumStates = 0;
	private Dimension mNormalSize = new Dimension();
	private Dimension mExpandedSize = new Dimension();
	private double mIncrementX = 0.0;
	private double mIncrementY = 0.0;
	
	public AnimatedButton()
	{
		return;
	}
	
	public AnimatedButton(int[] xpoints, int[] ypoints, int npoints, String caption)
	{
		super(new Polygon(xpoints, ypoints, npoints), caption);
		return;
	}
	
	public void update()
	{
		if(this.mIsExpanding && this.getWidth() < this.mExpandedSize.width){
			this.resizePolygon((int)Math.round(this.getWidth() + this.mIncrementX), this.getHeight());
		}else if(this.mIsExpanding && this.getWidth() > this.mExpandedSize.width){
			this.resizePolygon(this.mExpandedSize.width, this.getHeight());
		}
		if(this.mIsExpanding && this.getHeight() < this.mExpandedSize.height){
			this.resizePolygon(this.getWidth(), (int)Math.round(this.getHeight() + this.mIncrementY));
		}else if(this.mIsExpanding && this.getHeight() > this.mExpandedSize.height){
			this.resizePolygon(this.getWidth(), this.mExpandedSize.height);
		}
		
		if(!this.mIsExpanding && this.getWidth() > this.mNormalSize.width){
			this.resizePolygon((int)Math.round(this.getWidth() - this.mIncrementX), this.getHeight());
		}else if(!this.mIsExpanding && this.getWidth() < this.mNormalSize.width){
			this.resizePolygon(this.mNormalSize.width, this.getHeight());
		}
		if(!this.mIsExpanding && this.getHeight() > this.mNormalSize.height){
			this.resizePolygon(this.getWidth(), (int)Math.round(this.getHeight() - this.mIncrementY));
		}else if(!this.mIsExpanding && this.getHeight() < this.mNormalSize.height){
			this.resizePolygon(this.getWidth(), this.mNormalSize.height);
		}
		return;
	}
	
	public void reset()
	{
		this.mIsExpanding = false;
		this.resizePolygon(this.mNormalSize.width, this.mNormalSize.height);
		return;
	}
	
	public void setIsExpanding(boolean value)
	{
		this.mIsExpanding = value;
		return;
	}
	
	public void setNumStates(int states)
	{
		this.mNumStates = states;
		return;
	}
	
	public int getNumStates()
	{
		return this.mNumStates;
	}
	
	public void setSizes(Dimension normal, Dimension expanded)
	{
		this.mNormalSize = normal;
		this.mExpandedSize = expanded;
		
		this.mIncrementX = (this.mExpandedSize.width - this.mNormalSize.width) / (double)this.mNumStates;
		this.mIncrementY = (this.mExpandedSize.width - this.mNormalSize.width) / (double)this.mNumStates;
		return;
	}
	
	public Dimension getNormalSize()
	{
		return this.mNormalSize;
	}
	
	public Dimension getExpandedSize()
	{
		return this.mExpandedSize;
	}
}
