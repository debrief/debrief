package com.planetmayo.debrief.satc.model.generator.impl.sa;

import java.util.Random;

import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PointsGenerator
{
	private final Random rnd;
	private final Geometry geometry;
	private final Geometry envelope;
	private final SAParameters parameters;

	public PointsGenerator(Geometry geometry, Random random, SAParameters parameters)
	{
		this.rnd = random;
		this.geometry = geometry;
		this.envelope = geometry.getEnvelope();
		this.parameters = parameters;
	}

	public Point startPoint()
	{
		return envelope.getCentroid();
	}

	public Point toPoint(Coordinate coord)
	{
		return GeoSupport.getFactory().createPoint(coord);
	}
	
	private double distance(double T) 
	{
		return parameters.getSaFuntions().neighborDistance(parameters, rnd, T);
	}

	public Point newPoint(Point p, double T)
	{
		int coords = envelope.getNumPoints();
		Coordinate border1 = envelope.getCoordinates()[rnd.nextInt(coords)];
		Coordinate border2;
		do
		{
			border2 = envelope.getCoordinates()[rnd.nextInt(coords)];
		}
		while (border1 == border2);

		Point p1 = MathUtils.calculateBezier(Math.abs(distance(T)), p,
				toPoint(border1), null);
		Point p2 = MathUtils.calculateBezier(Math.abs(distance(T)), p,
				toPoint(border2), null);
		return MathUtils.calculateBezier(rnd.nextDouble(), p1, p2, null);
	}
	
	/*public Point newPoint(Point p, double T)
	{
		double dx = distance(T);
		double dy = distance(T);
		if (dx == 0 || dy == 0)
		{
			return p;
		}
		Coordinate border1;
		do 
		{
			border1 = envelope.getCoordinates()[rnd.nextInt(envelope
					.getNumPoints())];
		}
		while (Math.signum(border1.x - p.getX()) != Math.signum(dx));
		Coordinate border2 = border1;
		while (Math.signum(border2.y - p.getY()) != Math.signum(dy)) 
		{
			border2 = envelope.getCoordinates()[rnd.nextInt(envelope
					.getNumPoints())]; 
		}
		return GeoSupport.createPoint(
				p.getX() + Math.abs(border1.x - p.getX()) * dx, 
				p.getY() + Math.abs(border2.y - p.getY()) * dy
		); 
	}*/	

	public double getArea()
	{
		return geometry.getArea();
	}
}
