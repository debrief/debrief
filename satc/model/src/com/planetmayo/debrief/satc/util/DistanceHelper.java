package com.planetmayo.debrief.satc.util;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class DistanceHelper
{
	private Geometry geo1;
	private Geometry geo2;
	
	public DistanceHelper(Geometry geo1, Geometry geo2)
	{
		this.geo1 = geo1;
		this.geo2 = geo2;
	}
	
	private List<Geometry> geometryToList(Geometry geometry) 
	{
		List<Geometry> result = new ArrayList<Geometry>();
		if (geometry instanceof GeometryCollection) 
		{
			GeometryCollection collection = (GeometryCollection) geometry;
			for (int i = 0; i < collection.getNumGeometries(); i++) 
			{
				result.addAll(geometryToList(collection.getGeometryN(i)));
			}
		} 
		else if (geometry instanceof Polygon) 
		{
			result.add(((Polygon) geometry).getBoundary());
		} 
		else if (geometry instanceof LineString)
		{
			result.add(geometry);
		}
		else if (geometry instanceof Point)
		{
			result.add(geometry);
		}
		return result;
	}
	
	public void applyPointToPoint(Distance distance, Point point1, Point point2)
	{
		double currentDistance = point1.getCoordinate().distance(point2.getCoordinate());
		distance.apply(new Distance(currentDistance, currentDistance));
	}
	
	public void applyLineToLine(Distance distance, LineString line1, LineString line2)
	{
		for (int i = 0; i < line1.getNumPoints() - 1; i++) 
		{
			Coordinate A = line1.getCoordinateN(i);
			Coordinate B = line1.getCoordinateN(i + 1);
			for (int j = 0; j < line2.getNumPoints() - 1; j++)
			{
				Coordinate C = line2.getCoordinateN(j);
				Coordinate D = line2.getCoordinateN(j + 1);
				double possibleShortest = CGAlgorithms.distanceLineLine(A, B, C, D);
				double possibleLongest = Math.max(A.distance(C), A.distance(D));
				possibleLongest = Math.max(possibleLongest, Math.max(B.distance(C), B.distance(D)));
				
				distance.apply(new Distance(possibleShortest, possibleLongest));			
			}
		}
	}
	
	public void applyLineToPoint(Distance distance, LineString line, Point point)
	{
		Coordinate p = point.getCoordinate();
		for (int i = 0; i < line.getNumPoints() - 1; i++)
		{
			Coordinate A = line.getCoordinateN(i);
			Coordinate B = line.getCoordinateN(i + 1);
			
			double possibleShortest = CGAlgorithms.distancePointLine(p, A, B);
			double possibleLongest = Math.max(A.distance(p), B.distance(p));
			distance.apply(new Distance(possibleShortest, possibleLongest));	
		}
	}
	
	public Distance calculate() 
	{
		Distance distance = new Distance(Double.MAX_VALUE, 0);
		for (Geometry first : geometryToList(geo1)) 
		{
			for (Geometry second : geometryToList(geo2))
			{
				if ((first instanceof Point) && (second instanceof Point)) 
				{
					applyPointToPoint(distance, (Point) first, (Point) second);
				}
				if ((second instanceof Point) && (first instanceof LineString)) 
				{
					applyLineToPoint(distance, (LineString) first, (Point) second);
				}
				if ((first instanceof Point) && (second instanceof LineString))
				{
					applyLineToPoint(distance, (LineString) second, (Point) first);
				}
				if ((first instanceof LineString) && (second instanceof LineString))
				{
					applyLineToLine(distance, (LineString) second, (LineString) first);
				}				
			}
		}
		return distance;
	}
}
