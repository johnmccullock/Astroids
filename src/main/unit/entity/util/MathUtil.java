package main.unit.entity.util;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Vector;

/**
 * General graph and trigonometry functions.
 * 
 * Version 1.7 Resurrects the old directionToFacePoint() method and renames to getShortestTurn2().
 * 
 * Version 1.6 adds angleDifference() and absAngleDifference() functions for better angle comparisons.
 * 
 * Version 1.5 replaces buggy directionToFacePoint() with getShortestTurn() which is easier to understand.
 * 
 * Version 1.4 reintroduced the "norm" function for normalizing radian values; necessary for radian value comparison
 * 
 * Version 1.3 adds getAngleA function.
 * 
 * Version 1.2 fixes buggy code by removing "normalizing" and "abs" functions that acted on radian values.
 * 
 * Version 1.1 includes function to find intersecting points of two circles.
 * 
 * @author John McCullock
 * @version 1.7 2015-06-07
 */
public class MathUtil
{
	public static final float PI = (float)Math.PI;
	public static final float PI2 = (float)Math.PI * 2f;
	
	/**
	 * Finds the distance between two points using Pythagorean Theorem.
	 * @param x1 float x value for first point.
	 * @param y1 float y value for first point.
	 * @param x2 float x value for second point.
	 * @param y2 float y value for second point.
	 * @return float distance between first and second point.
	 */
	public static float distance(float x1, float y1, float x2, float y2)
 	{
		return (float)Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
 	}
	
	/**
	 * Finds the difference between angles on the side less that Pi.  This function side-steps the problem
	 * where the angles span across the 0-2Pi divide.
	 * @param theta1 float first angle.
	 * @param theta2 float second angle.
	 * @return float
	 */
	public static float angleDifference(float theta1, float theta2)
	{
		return ((theta2 - theta1) + PI) % PI2 - PI;
	}
	
	/**
	 * Similar to angleDifference function, but return the absolute value.  This function side-steps the 
	 * problem where the angles span across the 0-2Pi divide.
	 * @param theta1 float first angle.
	 * @param theta2 float second angle.
	 * @return float
	 */
	public static float absAngleDifference(float theta1, float theta2)
	{
		return Math.abs(((theta2 - theta1) + PI) % PI2 - PI);
	}
	
	/**
	 * 
	 * @param x1 float x value for start point.
	 * @param y1 float y value for start point.
	 * @param x2 float x value for destination point.
	 * @param y2 float y value for destination point.
	 * @return float angle in radians.
	 */
	public static float getAngleFromPoints(float x1, float y1, float x2, float y2)
	{
		return (float)Math.atan2(-(y2 - y1), x2 - x1);
	}
	
	/**
	 * Determines if a clockwise or counterclockwise turn is shortest to a target angle.
	 * @param current float current angle in radians.
	 * @param target float target angle in radians.
	 * @return float Positive results means turn counterclockwise, negative means turn clockwise.
	 */
	public static float getShortestTurn(float current, float target)
	{
		float difference = target - current;
		while (difference < -PI) difference += PI2;
		while (difference > PI) difference -= PI2;
		return difference;
	}
	
	public static float getShortestTurn2(float targetX, float targetY, float currentX, float currentY, float currentAngle)
	{
		float rotX = (float)Math.cos(currentAngle);
		float rotY = -(float)Math.sin(currentAngle);
		float dx = targetX - currentX;
		float dy = targetY - currentY;
		return dx * rotY - dy * rotX;
	}
	
	
	public static float distanceToStartTurn(float radius, float radiusFactor, float turnAngle, float turnExponent)
	{
		//float diameter = radius * 2f;
		//float timesRadiusFactor = diameter * radiusFactor;
		//float exponent = (float)Math.pow(turnExponent, turnAngle);
		//float total = timesRadiusFactor * exponent;
		return ((radius * 2f) * radiusFactor) * (float)Math.pow(turnExponent, turnAngle);
	}
	
	/**
	 * Useful for radian comparisons.
	 * 
	 * Takes any radian value and shifts it to a value between 0.0 and 2PI.
	 * 
	 * @param angle float angle to be evaluated.
	 * @return float value between 0.0 and 2PI.
	 */
	public static float norm(float angle)
	{
		angle = angle % (PI * 2.0f);
		return angle = angle < 0 ? angle + (PI * 2.0f) : angle;
	}
	
	/**
	 * Finds the angleA opposite to sideA of a triangle where the lengths of all three sides are known.
	 * @param opposite float length of sideA, which is opposite the angle being searched for (angleA).
	 * @param sideB float length of sideB.
	 * @param sideC float length of sideC.
	 * @return float angle in radians of angleA, or zero if any parameters were zero.
	 */
	public static float getAngleA(float opposite, float sideB, float sideC)
	{
		if(opposite == 0.0f || sideB == 0.0f || sideC == 0.0f){
			return 0.0f;
		}
		return (float)Math.acos((opposite * opposite - sideB * sideB - sideC * sideC) / (-2.0f * sideB * sideC));
	}
	
	/**
	 * Generates a random radian between 0.0 and 2PI.
	 * @return float.
	 */
	public static float getRandomDirection()
	{
		return (float)Math.floor(Math.random() * PI2);
	}
	
	public static float getRandomDirection(float except)
	{
		float choice = 0f;
		for(int i = 0; i < 10; i++)
		{
			choice = (float)Math.floor(Math.random() * PI2);
			if(absAngleDifference(choice, except) > PI2 / 10f){
				return choice;
			}
		}
		return choice;
	}
	
	public static int getExclusiveRandom(int minimum, int maximum, int excluded)
	{
		int value = excluded;
		while(value == excluded)
		{
			value = (int)Math.floor(Math.random() * (maximum - minimum) + minimum);
		}
		return value;
	}
	
	public static int getExclusiveRandomInt(int high, int low, int[] excluding)
	{
		int result = 0;
		boolean done = false;
		while(!done)
		{
			boolean found = false;
			result = (int)Math.round(new Random().nextDouble() * (high - low)) + low;
			for(int i = 0; i < excluding.length; i++)
			{
				if(excluding[i] == result){
					found = true;
					break;
				}
			}
			if(!found){
				done = true;
			}
		}
		return result;
	}
	
	public static int getExclusiveRandomInt(int high, int low, Vector<Integer> excluding)
	{
		int result = 0;
		boolean done = false;
		while(!done)
		{
			boolean found = false;
			result = (int)Math.round(new Random().nextDouble() * (high - low)) + low;
			for(int i = 0; i < excluding.size(); i++)
			{
				if(excluding.get(i) == result){
					found = true;
					break;
				}
			}
			if(!found){
				done = true;
			}
		}
		return result;
	}
	
	/**
	 * Do line segments (x1, y1)--(x2, y2) and (x3, y3)--(x4, y4) intersect?
  	 * Found at: http://ptspts.blogspot.com/2010/06/how-to-determine-if-two-line-segments.html
  	 * This design avoids as many multiplications and divisions as possible.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @param x4
	 * @param y4
	 * @return boolean true if lines intersect, false otherwise.
	 */
	public static boolean LineSegmentsIntersect(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4)
 	{
 	  int d1 = ComputeDirection(x3, y3, x4, y4, x1, y1);
 	  int d2 = ComputeDirection(x3, y3, x4, y4, x2, y2);
 	  int d3 = ComputeDirection(x1, y1, x2, y2, x3, y3);
 	  int d4 = ComputeDirection(x1, y1, x2, y2, x4, y4);
 	  return (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) &&
 	          ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) ||
 	         (d1 == 0 && IsOnSegment(x3, y3, x4, y4, x1, y1)) ||
 	         (d2 == 0 && IsOnSegment(x3, y3, x4, y4, x2, y2)) ||
 	         (d3 == 0 && IsOnSegment(x1, y1, x2, y2, x3, y3)) ||
 	         (d4 == 0 && IsOnSegment(x1, y1, x2, y2, x4, y4));
 	}
 	
 	private static int ComputeDirection(float xi, float yi, float xj, float yj, float xk, float yk)
 	{
		float a = (xk - xi) * (yj - yi);
		float b = (xj - xi) * (yk - yi);
		return a < b ? -1 : a > b ? 1 : 0;
	}
 	
 	private static boolean IsOnSegment(float xi, float yi, float xj, float yj, float xk, float yk)
 	{
 		return (xi <= xk || xj <= xk) && (xk <= xi || xk <= xj) && (yi <= yk || yj <= yk) && (yk <= yi || yk <= yj);
 	}
 	
 	/**
 	 * Finds possible intersecting points of two circles.  The length of the resulting array describes how many intersecting points were found.
 	 * 
 	 * Will return empty array if:
 	 *  - the circles do not intersect.
 	 *  - the circles coincide.
 	 *  - one circle contains the other.
 	 * 
 	 * Will return only one intersection point if the circle edges merely touch.
 	 * 
 	 * Will return two intersection points if either circle overlaps the other.
 	 * 
 	 * @param radius1 float radius of the first circle.
 	 * @param center1x float x-coordinate of first circle's center.
 	 * @param center1y float y-coordinate of first circle's center.
 	 * @param radius2 float radius of the second circle.
 	 * @param center2x float x-coordinate of second circle's center.
 	 * @param center2y float y-coordinate of second circle's center.
 	 * @return Point2D.Double[] array containing any possible intersecting points.
 	 */
 	public static Point2D.Float[] circleIntersects(float radius1, final float center1x, final float center1y, float radius2, final float center2x, final float center2y)
	{
		Point2D.Float[] results = null;
		
		float d = MathUtil.distance(center1x, center1y, center2x, center2y);
		
		// Determine possible solutions:
		if(d > radius1 + radius2){
			// Circles do not intersect.
			return new Point2D.Float[0];
		}else if(d == 0.0f && radius1 == radius2){
			// Circles coincide.
			return new Point2D.Float[0];
		}else if(d + Math.min(radius1, radius2) < Math.max(radius1, radius2)){
			// One circle contains the other.
			return new Point2D.Float[0];
		}else{
			float a = ((radius1 * radius1) - (radius2 * radius2) + (d * d)) / (2.0f * d);
			float h = (float)Math.sqrt((radius1 * radius1) - (a * a));
			
			// Find p2
			Point2D.Float p2 = new Point2D.Float(center1x + (a * (center2x - center1x)) / d,
												center1y + (a * (center2y - center1y)) / d);
			
			results = new Point2D.Float[2];
			
			results[0] = new Point2D.Float(p2.x + (h * (center2y - center1y) / d),
											p2.y - (h * (center2x - center1x) / d));
			
			results[1] = new Point2D.Float(p2.x - (h * (center2y - center1y) / d),
											p2.y + (h * (center2x - center1x) / d));
			
		}
		return results;
	}
 	
 	/**
 	 * Compares the distance between the first point and two others, returning the closest.
 	 * @param x1 float x-value of the first point.
 	 * @param y1 float y-value of the first point.
 	 * @param x2 float x-value of a second point to compare to the first.
 	 * @param y2 float y-value of a second point to compare to the first.
 	 * @param x3 float x-value of a third point to compare to the first.
 	 * @param y3 float y-value of a third point to compare to the first.
 	 * @return java.awt.Point of the closest: either the second or third point.
 	 */
 	public static Point2D.Float minPoint(float x1, float y1, float x2, float y2, float x3, float y3)
	{
		if(MathUtil.distance(x1, y1, x2, y2) < MathUtil.distance(x1, y1, x3, y3)){
			return new Point2D.Float(x2, y2);
		}else{
			return new Point2D.Float(x3, y3);
		}
	}
 	
 	public static Point2D.Float rotatePoint(float x, float y, float centerX, float centerY, float radians)
 	{
 		float dx = x - centerX;
 		float dy = y - centerY;
 		float newX = centerX + (float)(dx * Math.cos(radians) - dy * Math.sin(radians));
 		float newY = centerY + (float)(dx * Math.sin(radians) + dy * Math.cos(radians));
 		return new Point2D.Float(newX, newY);
 	}
}
