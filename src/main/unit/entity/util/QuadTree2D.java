package main.unit.entity.util;

import java.util.Vector;



public class QuadTree2D
{
	private Node mRoot = null;
	private int mCapacity = 1; // bucket capacity.
	
	public QuadTree2D(int x, int y, int width, int height, int capacity)
	{
		this.mCapacity = capacity;
		this.mRoot = new Node(x, y, width, height);
		return;
	}
	
	public boolean insert(Rect2D rect)
	{
		return this.mRoot.insert(rect);
	}
	
	public Vector<Rect2D> query(int x, int y, int width, int height)
	{
		return this.mRoot.query(x, y, width, height);
	}
	
	public Vector<Rect2D> query(int x, int y, int width, int height, Vector<Rect2D> results)
	{
		return this.mRoot.query(x, y, width, height, results);
	}
	
	public void clear()
	{
		this.mRoot.clear();
	}
	
	private class Node
	{
		public int x = 0;
		public int y = 0;
		public int width = 0;
		public int height = 0;
		public Node northWest = null;
		public Node northEast = null;
		public Node southWest = null;
		public Node southEast = null;
		public Rect2D[] bucket = null;
		
		public Node(int x, int y, int width, int height)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.bucket = new Rect2D[mCapacity];
			return;
		}
		
		private boolean contains(float x, float y)
		{
			if((x >= this.x) && (x <= this.x + this.width) && (y >= this.y) && (y <= this.y + this.height)){
				return true;
			}else{
				return false;
			}
		}
		
		private boolean intersects(float x, float y, float width, float height)
		{
			if(x >= this.x && x <= this.x + this.width){
				return true;
			}
			if(this.x >= x && this.x <= x + width){
				return true;
			}
			if(x + width >= this.x && x + width <= this.x + this.width){
				return true;
			}
			if(this.x + this.width >= x && this.x + this.width <= x + width){
				return true;
			}
			if(y >= this.y && y <= this.y + this.height){
				return true;
			}
			if(this.y >= y && this.y <= y + height){
				return true;
			}
			if(y + height >= this.y && y + height <= this.y + this.height){
				return true;
			}
			if(this.y + this.height >= y && this.y + this.height <= y + height){
				return true;
			}
			return false;
		}
		
		public boolean insert(Rect2D rect)
		{
			if(!this.contains(rect.x, rect.y)){
				return false;
			}
			
			// If there's room for an item, insert it and return true.
			for(int i = 0; i < mCapacity; i++)
			{
				if(this.bucket[i] == null){
					this.bucket[i] = rect;
					return true;
				}
			}
			
			// If there isn't enough room, subdivide.
			if(this.northWest == null){
				this.subdivide();
			}
			
			if(this.northWest.insert(rect)) { return true; }
			if(this.northEast.insert(rect)) { return true; }
			if(this.southWest.insert(rect)) { return true; }
			if(this.southEast.insert(rect)) { return true; }
			
			return false;
		}
		
		private void subdivide()
		{
			// Use floor() instead of round().  Rounding errors could cause boundary overlap.
			int halfWidth = (int)Math.floor(this.width / 2.0);
			int halfHeight = (int)Math.floor(this.height / 2.0);
			this.northWest = new Node(this.x, this.y, halfWidth, halfHeight);
			this.northEast = new Node(this.x + halfWidth, this.y, this.width - halfWidth, halfHeight);
			this.southWest = new Node(this.x, this.y + halfHeight, halfWidth, this.height - halfHeight);
			this.southEast = new Node(this.x + halfWidth, this.y + halfHeight, this.width - halfWidth, this.height - halfHeight);
			return;
		}
		
		public Vector<Rect2D> query(int x, int y, int width, int height)
		{
			Vector<Rect2D> results = new Vector<Rect2D>();
			
			if(!this.intersects(x, y, width, height)){
				return results;
			}
			
			for(int i = 0; i < mCapacity; i++)
			{
				if(this.bucket[i] == null){
					continue;
				}
				if(this.bucket[i].x > x && this.bucket[i].x < x + width && this.bucket[i].y > y && this.bucket[i].y < y + height){
					results.add(this.bucket[i]);
				}
			}
			
			if(this.northWest != null){
				results.addAll(this.northWest.query(x, y, width, height));
			}
			if(this.northEast != null){
				results.addAll(this.northEast.query(x, y, width, height));
			}
			if(this.southWest != null){
				results.addAll(this.southWest.query(x, y, width, height));
			}
			if(this.southEast != null){
				results.addAll(this.southEast.query(x, y, width, height));
			}
			
			return results;
		}
		
		public Vector<Rect2D> query(int x, int y, int width, int height, Vector<Rect2D> results)
		{
			if(!this.intersects(x, y, width, height)){
				return results;
			}
			
			for(int i = 0; i < mCapacity; i++)
			{
				if(this.bucket[i] == null){
					continue;
				}
				if(this.bucket[i].x > x && this.bucket[i].x < x + width && this.bucket[i].y > y && this.bucket[i].y < y + height){
					results.add(this.bucket[i]);
				}
			}
			
			if(this.northWest != null){
				results.addAll(this.northWest.query(x, y, width, height));
			}
			if(this.northEast != null){
				results.addAll(this.northEast.query(x, y, width, height));
			}
			if(this.southWest != null){
				results.addAll(this.southWest.query(x, y, width, height));
			}
			if(this.southEast != null){
				results.addAll(this.southEast.query(x, y, width, height));
			}
			
			return results;
		}
		
		public void clear()
		{
			this.bucket = new Rect2D[mCapacity];
			this.northWest = null;
			this.northEast = null;
			this.southWest = null;
			this.southEast = null;
			return;
		}
	}
}
