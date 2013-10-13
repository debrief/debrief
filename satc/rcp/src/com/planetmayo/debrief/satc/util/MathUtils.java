package com.planetmayo.debrief.satc.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class MathUtils
{
	
	public static double normalizeAngle(double angle) 
	{
		double minus = angle < 0 ? 2 : -2;
		while (angle < 0 || angle >= 2 * Math.PI) 
		{
			angle += minus * Math.PI; 
		}
		return angle;
	}
	
	public static Point calculateBezierDerivative(double t, Point start, Point end, Point[] control) {
		if (control == null || control.length == 0) 
		{
			return GeoSupport.createPoint(end.getX() - start.getX(), end.getY() - start.getY());
		}	
		else if (control.length == 1) 
		{
			double x = 2 * t * (start.getX() - 2 * control[0].getX() + end.getX()) + 2 * (control[0].getX() - start.getX());
			double y = 2 * t * (start.getY() - 2 * control[0].getY() + end.getY()) + 2 * (control[0].getY() - start.getY());
			return GeoSupport.createPoint(x, y);
		}
		else
		{
			double p0x = start.getX(), p1x = control[0].getX(), p2x = control[1].getX(), p3x = end.getX();
			double p0y = start.getY(), p1y = control[0].getY(), p2y = control[1].getY(), p3y = end.getY();
			double t2 = t * t;
			double x = 3 * (t2 * (p3x - 3 * p2x + 3 * p1x - p0x) + 2 * t * (p2x - 2 * p1x + p0x) + (p1x - p0x));
			double y = 3 * (t2 * (p3y - 3 * p2y + 3 * p1y - p0y) + 2 * t * (p2y - 2 * p1y + p0y) + (p1y - p0y));
			return GeoSupport.createPoint(x, y);
		}
	}
	
	public static Point calculateBezier(double t, Point start, Point end, Point[] control) 
	{
		double invT = 1 - t;
		double invT2 = invT * invT;
		double invT3 = invT2 * invT;
		double t2 = t * t;
		double t3 = t2 * t;
		if (control == null || control.length == 0) 
		{
			double x = invT * start.getX() + t * end.getX();
			double y = invT * start.getY() + t * end.getY();
			return GeoSupport.createPoint(x, y);
		}
		else if (control.length == 1) 
		{
			double x = invT2 * start.getX() + 2 * t * invT * control[0].getX() + t2 * end.getX();
			double y = invT2 * start.getY() + 2 * t * invT * control[0].getY() + t2 * end.getY();
			return GeoSupport.createPoint(x, y);
		}
		else
		{
			double x = invT3 * start.getX() + 3 * t * invT2 * control[0].getX() + 3 * t2 * invT * control[1].getX() + t3 * end.getX();
			double y = invT3 * start.getY() + 3 * t * invT2 * control[0].getY() + 3 * t2 * invT * control[1].getY() + t3 * end.getY();			
			return GeoSupport.createPoint(x, y);
		}
	}
	
	/** 
	 * defines straight route in y(x) = k * x + b shape and returns [k, b]. 
	 * @param start and end point
	 * @return [k, b]
	 */
	public static double[] findStraightLineCoef(Point startPoint, Point endPoint)
	{
		double k = (startPoint.getY() - endPoint.getY()) / (startPoint.getX() - endPoint.getX());
		double b = startPoint.getY() - k * startPoint.getX();
		return new double[] {k, b};
	}	
	
	/**
	 * 
	 * @param line1 - first straight line coeffs: y(x) = line1[0] * x + line1[1]
	 * @param line2 - second straight line coeffs: y(x) = line2[0] * x + line2[1]
	 * @return intersection point between two line1 and line2
	 */	
	public static Point findIntersection(double[] line1, double[] line2)
	{
		if (Math.abs(line1[0] - line2[0]) < 0.0001) 
		{
			return null;
		}
		double x = (line1[1] - line2[1]) / (line2[0] - line1[0]);
		double y = line1[0] * x + line1[1];
		return GeoSupport.getFactory().createPoint(new Coordinate(x, y));
	}
	
	public static Point findIntersection(Coordinate a, Coordinate b, Coordinate c, Coordinate d)
	{
		double dx1 = b.x - a.x,
		    		dy1 = b.y - a.y,
		  			dx2 = d.x - c.x,
  		 			dy2 = d.y - c.y;
		double c1 = ((a.x + a.y) - (c.x + c.y)) / (dx2 + dy2);
		double c2 = (dx1 + dy1) / (dx2 + dy2);
		double t1;
		if (Math.abs(dx2) > Math.abs(dy2))
		{
			t1 = (a.x - c.x - dx2 * c1) / (dx2 * c2 - dx1);
		}
		else
		{
			t1 = (a.y - c.y - dy2 * c1) / (dy2 * c2 - dy1);
		}
		Coordinate res = new Coordinate(a.x + dx1 * t1, a.y + dy1 * t1);
		return GeoSupport.getFactory().createPoint(res);
	}
	
	/**
	 * returns distance between two points in 2D 
	 * 	
	 * @param point1
	 * @param point2
	 * @return
	 */
	public static double calcFlatDistance(Point point1, Point point2) 
	{
		double a = (point1.getX() - point2.getX());
		double b = (point1.getY() - point2.getY());
		return Math.hypot(a, b);		
	}	
	
	public static double calcAbsoluteValue(Point vector)	
	{
		return Math.hypot(vector.getX(), vector.getY());
	}
	
	public static double calcAngle(Point vector)
	{
		double value = calcAbsoluteValue(vector);
		double angle = Math.acos(vector.getX() / value);
		if (vector.getY() < 0)
		{
			angle = 2 * Math.PI - angle;
		}
		return angle;
	}
}
