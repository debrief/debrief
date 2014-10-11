/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;

public class MathUtils
{
	public static final double EPS = 0.00001;
	
	private static final double TWO_PI = 2 * Math.PI;
	private static final double PI_BY_TWO = Math.PI / 2;
	/**
	 * return angle from [0; 2*Math.PI)
	 * @param angle
	 * @return
	 */
	public static double normalizeAngle(double angle) 
	{
		double minus = angle < 0 ? 2 : -2;
		while (angle < 0 || angle >= TWO_PI) 
		{
			angle += minus * Math.PI; 
		}
		return angle;
	}
	
	/**
	 * returns angle from [-Math.PI; Math.PI]
	 * @param angle
	 * @return
	 */
	public static double normalizeAngle2(double angle) 
	{
		double res = angle;
		while (res > Math.PI)
			res -= TWO_PI;
		while (res < -Math.PI)
			res += TWO_PI;

		return res;
	}	
	
	
	public static double angleDiff(double angle1, double angle2, boolean normalized) 
	{
		if (! normalized)
		{
			angle1 = normalizeAngle(angle1);
			angle2 = normalizeAngle(angle2);
		}
		return Math.min(
				Math.abs(angle1 - angle2),
				Math.min(
						Math.abs(angle1 - angle2 + TWO_PI),
						Math.abs(angle1 - angle2 - TWO_PI)
		));
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
		if (control == null || control.length == 0) 
		{
			double x = invT * start.getX() + t * end.getX();
			double y = invT * start.getY() + t * end.getY();
			return GeoSupport.createPoint(x, y);
		}
		double invT2 = invT * invT;
		double t2 = t * t;
		if (control.length == 1) 
		{
			double x = invT2 * start.getX() + 2 * t * invT * control[0].getX() + t2 * end.getX();
			double y = invT2 * start.getY() + 2 * t * invT * control[0].getY() + t2 * end.getY();
			return GeoSupport.createPoint(x, y);
		}
		double invT3 = invT2 * invT;
		double t3 = t2 * t;
		
		double x = invT3 * start.getX() + 3 * t * invT2 * control[0].getX() + 3 * t2 * invT * control[1].getX() + t3 * end.getX();
		double y = invT3 * start.getY() + 3 * t * invT2 * control[0].getY() + 3 * t2 * invT * control[1].getY() + t3 * end.getY();			
		return GeoSupport.createPoint(x, y);
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
	
	
	public static double calcAbsoluteValue(Point vector)	
	{
		return calcAbsoluteValue(vector.getX(), vector.getY());
	}
	
	public static double calcAbsoluteValue(double x, double y)	
	{	
		return Math.sqrt(x * x + y * y);
	}
	
	public static double calcAngle(double x, double y)
	{
		double value = calcAbsoluteValue(x, y);
		double angle = Math.acos(x / value);
		if (y < 0)
		{
			angle = TWO_PI - angle;
		}
		return angle;		
	}
	
	public static double calcAngle(Point vector)
	{
		return calcAngle(vector.getX(), vector.getY());
	}
	
	public static double fast_atan2(double y, double x)
	{
			if (x == 0)
			{
				if ( y > 0) return PI_BY_TWO;
				if ( y == 0) return 0.;
				return -PI_BY_TWO;
			}
			double atan;
			double z = y/x;
			if (Math.abs(z) < 1.0)
			{
				atan = z/(1.0 + 0.28*z*z);
				if (x < 0)
				{
					if (y < 0) return atan - Math.PI;
					return atan + Math.PI;
				}
			}
			else
			{
				atan = PI_BY_TWO - z/(z*z + 0.28);
				if (y < 0) return atan - Math.PI;
			}
			return atan;
	}

	public static boolean rayTracing(Point point, Geometry polygon, AffineTransformation transform)
	{
		if (transform == null)
		{
			transform = new AffineTransformation();
		}
		double x = point.getX();
		double y = point.getY(); 
		
		Coordinate[] polygonCoords = polygon.getCoordinates();
		Coordinate start = transform.transform(polygonCoords[0], new Coordinate());
		Coordinate end = new Coordinate();
		
		double[] intersections = new double[polygonCoords.length];
		int index = 0;
		for (int i = 1; i < polygonCoords.length; i++)
		{
			end = transform.transform(polygonCoords[i], end);
			Coordinate a = start, b = end;
			if (end.y < start.y)
			{
				b = start;
				a = end;
			}
			if (y >= a.y && y < b.y) 
			{
				double diff = (y - a.y) / (b.y - a.y);
				intersections[index] = a.x + (b.x - a.x) * diff;
				index++;
			}
			start.x = end.x;
			start.y = end.y;
		}
		int sum = 0;
		for (int i = 0; i < index; i++) 
		{
			if (intersections[i] == x)
			{
				return true;
			} 
			else if (intersections[i] < x)
			{
				sum++;
			}			
		}
		return (sum % 2) != 0;
	}
}
