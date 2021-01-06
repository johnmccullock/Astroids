package main.unit;

import java.util.UUID;
import main.unit.entity.util.Polygon2;

public class Asteroid extends Unit
{
	public enum Size
	{
		LARGE(50), 
		MEDIUM(75), 
		SMALL(100);
		
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
	
	private Asteroid.Size mSize = null;
	private Polygon2 mShape = null;
	
	public Asteroid(UUID id)
	{
		super(id);
		return;
	}
	
	public Asteroid(UUID id, Asteroid.Size size)
	{
		super(id);
		this.setSize(size);
		return;
	}
	
	public void setSize(Asteroid.Size size)
	{
		this.mSize = size;
		return;
	}
	
	public Asteroid.Size getSize()
	{
		return this.mSize;
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
}
