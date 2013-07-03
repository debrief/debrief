package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;

import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class PointExtension
{
	private final Point point;
	private double precision;
	
	public PointExtension(Point point, double precision)
	{
		super();
		this.point = point;
		this.precision = precision;
	}
	
	public List<Point> extend() 
	{		
		final List<Point> result = new ArrayList<Point>(8);
		final Coordinate coordinate = point.getCoordinate();
		precision = precision / 2;
		final double delta = GeoSupport.m2deg(precision);
		result.add(createPoint(coordinate, -delta, -delta));
		result.add(createPoint(coordinate, 0, -delta));
		result.add(createPoint(coordinate, delta, -delta));
		result.add(createPoint(coordinate, -delta, 0));
		result.add(createPoint(coordinate, delta, 0));		
		result.add(createPoint(coordinate, -delta, delta));
		result.add(createPoint(coordinate, 0, delta));
		result.add(createPoint(coordinate, delta, delta));
		return result;
	}
	
	private Point createPoint(Coordinate c, double xDelta, double yDelta)
	{
		return GeoSupport.getFactory().createPoint(
				new Coordinate(c.x + xDelta, c.y + yDelta));
	}
}
