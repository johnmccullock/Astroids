package main;

import main.unit.entity.util.GeomPath;
import main.unit.entity.util.Polygon2;

/**
 * A group of static functions for creating polygon shapes specific to the Asteroids game.
 * 
 * The points for each shape were mined from images of Asteroids game screen shots.  The image resolutions
 * were 640x480, so the factor values are in reference to that size.  Using the factors against the current
 * screen size will create asteroids and ships of comparable size.
 * 
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class EntityData
{
	public static final float ASTEROID_LARGE_WIDTH_FACTOR = 0.0625f; // by screen width.
	public static final float ASTEROID_LARGE_HEIGHT_FACTOR = 0.075f; // by screen height.
	public static final float ASTEROID_MEDIUM_WIDTH_FACTOR = 0.03125f; // by screen width.
	public static final float ASTEROID_MEDIUM_HEIGHT_FACTOR = 0.0375f; // by screen height.
	public static final float ASTEROID_SMALL_WIDTH_FACTOR = 0.015625f; // by screen width.
	public static final float ASTEROID_SMALL_HEIGHT_FACTOR = 0.01875f; // by screen height.
	public static final float UFO_LARGE_WIDTH_FACTOR = 0.040625f; // by screen width.
	public static final float UFO_LARGE_HEIGHT_FACTOR = 0.03125f; // by screen height.
	public static final float UFO_SMALL_WIDTH_FACTOR = 0.024375f; // by screen width.
	public static final float UFO_SMALL_HEIGHT_FACTOR = 0.01875f; // by screen height.
	
	public static Polygon2 getShape(int index)
	{
		if(index == 0){
			return EntityData.getAsteroid0Shape();
		}else if(index == 1){
			return EntityData.getAsteroid1Shape();
		}else if(index == 2){
			return EntityData.getAsteroid2Shape();
		}else if(index == 3){
			return EntityData.getAsteroid3Shape();
		}
		return null;
	}
	
	public static Polygon2 getBounds(int index)
	{
		if(index == 0){
			return EntityData.getAsteroid0Bounds();
		}else if(index == 1){
			return EntityData.getAsteroid1Bounds();
		}else if(index == 2){
			return EntityData.getAsteroid2Bounds();
		}else if(index == 3){
			return EntityData.getAsteroid3Bounds();
		}
		return null;
	}
	
	
	/*
	 * All of these came from 640x480 screen shots.
	 * 
	 */
	
	/*
	 * 40 / 640 = 0.0625
	 * 36 / 480 = 0.075
	 */
	public static Polygon2 getAsteroid0Shape()
	{
		int[] xpoints = new int[]{15, 29, 39, 39, 29, 20, 20, 10, 0, 10, 0};
		int[] ypoints = new int[]{0, 0, 13, 22, 35, 35, 22, 35, 22, 18, 13};
		return new Polygon2(xpoints, ypoints, 11);
	}
	
	public static Polygon2 getAsteroid0Bounds()
	{
		int[] xpoints = new int[]{15, 29, 39, 39, 29, 10, 0, 0};
		int[] ypoints = new int[]{0, 0, 13, 22, 35, 35, 22, 13};
		return new Polygon2(xpoints, ypoints, 8);
	}
	
	/*
	 * 40 / 640 = 0.0625
	 * 36 / 480 = 0.075
	 */
	public static Polygon2 getAsteroid1Shape()
	{
		int[] xpoints = new int[]{9, 24, 39, 39, 24, 39, 29, 24, 9, 0, 0, 14};
		int[] ypoints = new int[]{0, 0, 9, 13, 17, 26, 35, 30, 35, 22, 9, 9};
		return new Polygon2(xpoints, ypoints, 12);
	}
	
	public static Polygon2 getAsteroid1Bounds()
	{
		int[] xpoints = new int[]{9, 24, 39, 39, 29, 9, 0, 0};
		int[] ypoints = new int[]{0, 0, 9, 26, 35, 35, 22, 9};
		return new Polygon2(xpoints, ypoints, 8);
	}
	
	/*
	 * 40 / 640 = 0.0625
	 * 36 / 480 = 0.075
	 */
	public static Polygon2 getAsteroid2Shape()
	{
		int[] xpoints = new int[]{10, 19, 29, 39, 29, 39, 29, 14, 10, 0, 5, 0};
		int[] ypoints = new int[]{0, 5, 0, 9, 13, 22, 35, 31, 35, 27, 18, 9};
		return new Polygon2(xpoints, ypoints, 12);
	}
	
	public static Polygon2 getAsteroid2Bounds()
	{
		int[] xpoints = new int[]{10, 29, 39, 39, 29, 10, 0, 0};
		int[] ypoints = new int[]{0, 0, 9, 22, 35, 35, 27, 9};
		return new Polygon2(xpoints, ypoints, 8);
	}
	
	/*
	 * 41 / 640 = 0.0640625
	 * 36 / 480 = 0.075
	 */
	public static Polygon2 getAsteroid3Shape()
	{
		int[] xpoints = new int[]{10, 20, 30, 40, 35, 40, 25, 10, 0, 0};
		int[] ypoints = new int[]{0, 9, 0, 9, 17, 26, 35, 35, 26, 9};
		return new Polygon2(xpoints, ypoints, 10);
	}
	
	public static Polygon2 getAsteroid3Bounds()
	{
		int[] xpoints = new int[]{10, 30, 40, 40, 25, 10, 0, 0};
		int[] ypoints = new int[]{0, 0, 9, 26, 35, 35, 26, 9};
		return new Polygon2(xpoints, ypoints, 8);
	}
	
	/*
	 * 15 / 640 = 0.02343f
	 * 11 / 480 = 0.02291f
	 */
	public static Polygon2 getPlayerShape()
	{
		int[] xpoints = new int[]{0, 14, 0, 3, 3};
		int[] ypoints = new int[]{0, 5, 10, 8, 2};
		return new Polygon2(xpoints, ypoints, 5);
	}
	
	public static Polygon2 getPlayerBounds()
	{
		int[] xpoints = new int[]{0, 14, 0};
		int[] ypoints = new int[]{0, 5, 10};
		return new Polygon2(xpoints, ypoints, 3);
	}
	
	public static Polygon2 getExplosion1()
	{
		int[] xpoints = new int[]{0, 6, 13, 15, 13, 13, 9, 4, 0, 4};
		int[] ypoints = new int[]{0, 2, 0, 5, 10, 14, 12, 14, 10, 7};
		return new Polygon2(xpoints, ypoints, 10);
	}
	
	public static GeomPath getUFOShape()
	{
		int[] xpoints = new int[]{8, 9, 16, 17, 25, 17, 8, 0, 8, 17, 25, 0};
		int[] ypoints = new int[]{5, 0, 0, 5, 9, 14, 14, 9, 5, 5, 9, 9};
		return new GeomPath(xpoints, ypoints, 12);
	}
	
	public static Polygon2 getUFOBounds()
	{
		int[] xpoints = new int[]{9, 16, 17, 25, 17, 8, 0, 8};
		int[] ypoints = new int[]{0, 0, 5, 9, 14, 14, 9, 5};
		return new Polygon2(xpoints, ypoints, 8);
	}
}
