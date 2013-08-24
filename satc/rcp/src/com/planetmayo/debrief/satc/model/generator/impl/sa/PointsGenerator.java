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
	
	private double width;
	private double height;
	private double startX;
	private double startY;
	private double[][] edges;

	public PointsGenerator(Geometry geometry, Random random, SAParameters parameters)
	{
		this.rnd = random;
		this.geometry = geometry;
		this.envelope = geometry.getEnvelope();
		this.parameters = parameters;
		
		calcParameters();
	}
	
	private void calcParameters() 
	{
		double endX, endY;
		startX = startY = Double.MAX_VALUE;
		endX = endY = -Double.MAX_VALUE;
		Coordinate[] coords = envelope.getCoordinates();
		for (Coordinate c : coords)
		{
			startX = Math.min(startX, c.x);
			endX = Math.max(endX, c.x);
			startY = Math.min(startY, c.y);
			endY = Math.max(endY, c.y);			
		}
		width = endX - startX;
		height = endY - startY;
		
		edges = new double[coords.length][2];
		for (int i = 0; i < coords.length; i++) 
		{
			Coordinate a = coords[i];
			Coordinate b = coords[(i + 1) % coords.length];
			edges[i][0] = Math.min(a.y, b.y);
			edges[i][1] = Math.max(a.y, b.y);
		}
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
	
	private double distance(double T, double a, double b)
	{
		double x = distance(T);
		if (x < 0)
		{
			return x * (-a);
		}
		return x * (b);
	}
	
	public Point newPoint(Point p, double T) 
	{
		if (T < 0.3) 
		{
			return waltToVertexes(p, T);			
		}
		return walkThroughCoords(p, T);
	}

	public Point waltToVertexes(Point p, double T)
	{
		int coords = envelope.getNumPoints();
		Coordinate border1 = envelope.getCoordinates()[rnd.nextInt(coords)];
		Coordinate border2;
		do
		{
			border2 = envelope.getCoordinates()[rnd.nextInt(coords)];
		}
		while (border1 == border2);

		Point p1 = MathUtils.calculateBezier(Math.abs(distance(T * T)), p,
				toPoint(border1), null);
		Point p2 = MathUtils.calculateBezier(Math.abs(distance(T * T)), p,
				toPoint(border2), null);
		return MathUtils.calculateBezier(rnd.nextDouble(), p1, p2, null);
	}
	
	public double findX(int edgeNum, double y)
	{
		Coordinate a = envelope.getCoordinates()[edgeNum];
		Coordinate b = envelope.getCoordinates()[(edgeNum + 1) % envelope.getNumPoints()];
		if (b.y > a.y) 
		{
			Coordinate t = b;
			b = a;
			a = t;
		}
		double t = (y - a.y) / (b.y - a.y);
		return t * (b.x - a.x) + a.x;
	}
	
	public Point walkThroughCoords(Point p, double T)
	{
		double y1 = (startY - p.getY()) / height;
		double y2 = 1 + y1;
		double yCoef = distance(T, y1, y2);
		if (! (yCoef >= y1 && yCoef<= y2)) 
		{
			
		}
		double y = p.getY() + yCoef * height;
		int startEdge = -1, endEdge = -1;
		for (int i = 0; i < edges.length; i++)
		{
			if (y >= edges[i][0] && y <= edges[i][1] && edges[i][0] != edges[i][1])
			{
				if (startEdge == -1) 
				{
					startEdge = i;
				}
				else
				{
					endEdge = i;
					break;
				}
			}
		}		
		double xMin = findX(startEdge, y);
		double xMax = findX(endEdge, y);
		if (xMin > xMax) 
		{
			double t = xMin;
			xMin = xMax;
			xMax = t;
		}
		if (p.getX() >= xMin && p.getX() <= xMax) 
		{
			double x1 = (xMin - p.getX()) / width;
			double x2 = (xMax - p.getX()) / width;
			double xCoef = distance(T, x1, x2);
			return GeoSupport.createPoint(p.getX() + xCoef * width, y);
		} 
		return GeoSupport.createPoint(xMin + rnd.nextDouble() * (xMax - xMin), y);
	}

	public double getArea()
	{
		return geometry.getArea();
	}
}
