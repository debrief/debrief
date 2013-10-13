package com.planetmayo.debrief.satc.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.geom.util.NoninvertibleTransformationException;

public class StraightLineCulling
{	
	private static final double EPS = 0.000001;
	
	private List<LocationRange> ranges;
	private Coordinate[] line1;
	private Coordinate[] line2;
	
	private Geometry constrainedStart;
	private Geometry constrainedEnd;
	
	public StraightLineCulling(List<LocationRange> ranges)
	{
		this.ranges = ranges;
	}
	
	public void process() 
	{
		line1 = line2 = null;
		constrainedStart = constrainedEnd = null;
		if (ranges == null || ranges.size() < 3) 
		{
			return;
		}
		AffineTransformation transformation = transformWorldToMiddleLine();
		if (impossibleStartEndArea(transformation)) 
		{
			return;
		}
		CompliantLine[] crissCross = findCrissCrossLines(transformation);
		CompliantLine result1 = checkFullCompliant(processLine(crissCross[0], false, 1));
		CompliantLine result2 = checkFullCompliant(processLine(crissCross[1], true, 1));
		if (result1 == null || result2 == null)
		{
			return;
		}
		line1 = normalizeCoordinates(result1.getTransformedLine());
		line2 = normalizeCoordinates(result2.getTransformedLine());
		Point intersection = MathUtils.findIntersection(line1[0], line1[1], line2[0], line2[1]);		
		Geometry intersectPolygon = GeoSupport.getFactory().createPolygon(new Coordinate[] {
				line1[0], line2[0], intersection.getCoordinate(), line1[0]
		});
		constrainedStart = intersectPolygon.intersection(getStartLocation());
		
		intersectPolygon = GeoSupport.getFactory().createPolygon(new Coordinate[] {
				line1[1], line2[1], intersection.getCoordinate(), line1[1]
		});		
		constrainedEnd = getEndLocation().intersection(intersectPolygon);
	}
	
	public Coordinate[] getFirstCrissCrossLine()
	{
		return line1;
	}

	public Coordinate[] getSecondCrissCrossLine()
	{
		return line2;
	}
	
	public Geometry getConstrainedStart()
	{
		return constrainedStart;
	}

	public Geometry getConstrainedEnd()
	{
		return constrainedEnd;
	}
	
	public boolean hasResults()
	{
		return constrainedStart != null && constrainedEnd != null;
	}

	private AffineTransformation transformWorldToMiddleLine() 
	{
		AffineTransformation transformation = new AffineTransformation();
		Point startCenter = ranges.get(0).getGeometry().getCentroid();
		Point endCenter = ranges.get(ranges.size() - 1)
									.getGeometry().getCentroid();
		transformWorldToLine(transformation, startCenter.getCoordinate(), endCenter.getCoordinate());
		return transformation;
	}
	
	private boolean impossibleStartEndArea(AffineTransformation transformation) 
	{
		double xMax = Double.MIN_VALUE;
		Coordinate dest = new Coordinate();
		for (Coordinate startCoord : getStartLocation().getCoordinates()) 
		{
			transformation.transform(startCoord, dest);
			xMax = Math.max(xMax, dest.x);
		}
		for (Coordinate endCoord : getEndLocation().getCoordinates()) 
		{
			transformation.transform(endCoord, dest);
			if (dest.x <= xMax)
			{
				return true;
			}
		}
		return false;
	}
	
	private CompliantLine[] findCrissCrossLines(AffineTransformation transformation)
	{
		Geometry start = transformation.transform(getStartLocation());
		Geometry end = transformation.transform(getEndLocation());
		
		List<Coordinate> startGreatZero = new ArrayList<Coordinate>();
		List<Coordinate> startLessZero = new ArrayList<Coordinate>();
		List<Coordinate> endGreatZero = new ArrayList<Coordinate>();
		List<Coordinate> endLessZero = new ArrayList<Coordinate>();
		
		splitCoordinatesByZero(start, startGreatZero, startLessZero);
		splitCoordinatesByZero(end, endGreatZero, endLessZero);
		
		return new CompliantLine[] {
				new CompliantLine(transformation, findBestCoordinates(startLessZero, endGreatZero)),
				new CompliantLine(transformation, findBestCoordinates(startGreatZero, endLessZero))
		};
	}
	
	private void splitCoordinatesByZero(Geometry geometry, List<Coordinate> great, List<Coordinate> less)
	{
		for (Coordinate c : geometry.getCoordinates())
		{
			if (c.y > 0)
			{
				great.add(c);
			}
			else
			{
				less.add(c);
			}
		}		
	}
	
	private Coordinate[] findBestCoordinates(List<Coordinate> list1, List<Coordinate> list2)
	{
		Coordinate[] best = new Coordinate[2];
		double max = 0; 
		for (Coordinate x : list1) 
		{
			for (Coordinate y : list2)
			{
				double yDiff = Math.abs(x.y - y.y);
				double xDiff = Math.abs(x.x - y.x);
				double sin = yDiff / Math.hypot(xDiff, yDiff);
				if (sin > max) 
				{
					best[0] = x;
					best[1] = y;
					max = sin;
				}
			}
		}
		return best;
	}
	
	private CompliantLine processLine(CompliantLine line, boolean reflect, int start)
	{
		AffineTransformation currentModification = new AffineTransformation();
		transformWorldToLine(currentModification, line.getLine()[0], line.getLine()[1]);		
		if (reflect)
		{
			currentModification.reflect(1, 0);
		}
		Coordinate[] transformedLine = 
		{
				currentModification.transform(line.getLine()[0], new Coordinate()),
				currentModification.transform(line.getLine()[1], new Coordinate()),
		};
		currentModification.composeBefore(line.getWorld());
		
		TreeSet<Coordinate> coordinates = new TreeSet<Coordinate>(
				new Comparator<Coordinate>()
				{

					@Override
					public int compare(Coordinate o1, Coordinate o2)
					{
						return (int) Math.signum(Math.abs(o1.y) - Math.abs(o2.y));
					}					
				}
		);
		int i;
		double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
		boolean lastCompliant = true;
		for (i = start; i < ranges.size() - 1 && lastCompliant; i++)
		{			
			Geometry geometry = getLocation(i);
			lastCompliant = false;
			coordinates.clear();

			min = Double.MAX_VALUE;
			max = -Double.MAX_VALUE;
			for (Coordinate coord : geometry.getCoordinates())
			{
				Coordinate temp = currentModification.transform(coord, new Coordinate());
				coordinates.add(temp);
				max = Math.max(max, temp.y);
				min = Math.min(min, temp.y);
				if (min <= EPS && max >= -EPS)
				{
					lastCompliant = true;
					break;
				}
			}	
		}
		if (! lastCompliant)
		{
			CompliantLine nextLine = null;
			if (min < 0)
			{
				nextLine = new CompliantLine(currentModification, 
						findCompliantLine(transformedLine[0], coordinates, true));
			}
			if (max > 0)
			{
				nextLine = new CompliantLine(currentModification, 
						findCompliantLine(transformedLine[1], coordinates, false));				
			}			
			return processLine(nextLine, false, i);
		}
		return line;
	}
	
	private Coordinate[] findCompliantLine(Coordinate point1, TreeSet<Coordinate> candidates, boolean order) 
	{
		double min = 2;
		Coordinate point2 = null;
		for (Coordinate c : candidates)
		{
			double xDiff = Math.abs(c.x - point1.x);
			double yDiff = Math.abs(c.y - point1.y);
			double sin = yDiff / Math.hypot(xDiff, yDiff);
			if (sin < min)
			{
				point2 = c;
				min = sin;
			}
		}
		Coordinate[] result = new Coordinate[2];
		result[0] = order ? point1 : point2;
		result[1] = order ? point2 : point1;
		return result;
	}
	
	private CompliantLine checkFullCompliant(CompliantLine line)
	{
		Coordinate temp = new Coordinate();
		AffineTransformation nextTransformation = new AffineTransformation();
		transformWorldToLine(nextTransformation, line.getLine()[0], line.getLine()[1]);
		
		AffineTransformation fullTransformation = new AffineTransformation(line.getWorld());
		fullTransformation.compose(nextTransformation);
		double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;		
		for (LocationRange range : ranges)
		{
			double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE; 
			for (Coordinate c : range.getGeometry().getCoordinates())
			{
				fullTransformation.transform(c, temp);
				minX = Math.min(minX, temp.x);
				maxX = Math.max(maxX, temp.x);
				minY = Math.min(minY, temp.y);
				maxY = Math.max(maxY, temp.y);				
			}
			if (! (minY <= EPS && maxY >= -EPS))
			{
				return null;
			}
		}
		double delta = 0.15 * (maxX - minX);
		return new CompliantLine(fullTransformation, new Coordinate[] {				
				new Coordinate(minX - delta, 0),
				new Coordinate(maxX + delta, 0)
		});
	}
	
	private void transformWorldToLine(AffineTransformation world,	Coordinate start, Coordinate end)
	{
		double xDiff = end.x - start.x;
		double yDiff = end.y - start.y;
		double length = Math.hypot(xDiff, xDiff);
		world.translate(-start.x, -start.y);
		world.rotate(-yDiff / length, xDiff / length);
	}
	
	private Coordinate[] normalizeCoordinates(Coordinate[] coordinates) 
	{
		for (Coordinate c : coordinates)
		{
			c.x = Math.rint(c.x / EPS) * EPS;
			c.y = Math.rint(c.y / EPS) * EPS;
		}
		return coordinates;
	}

	private Geometry getLocation(int num)
	{
		return ranges.get(num).getGeometry();
	}
	
	private Geometry getStartLocation() 
	{
		return ranges.get(0).getGeometry();
	}
	
	private Geometry getEndLocation() 
	{
		return ranges.get(ranges.size() - 1).getGeometry();
	}	
	
	static class CompliantLine 
	{
		private final AffineTransformation world;		
		private final Coordinate[] line;
		
		private Coordinate[] transformedLine;
		
		private CompliantLine(AffineTransformation world, Coordinate[] line)
		{
			this.world = world;
			this.line = line;
		}

		public AffineTransformation getWorld()
		{
			return world;
		}

		public Coordinate[] getLine()
		{
			return line;
		}
		
		public Coordinate[] getTransformedLine()
		{
			if (transformedLine != null)
			{
				return transformedLine;
			}
			try 
			{
				AffineTransformation inverse = world.getInverse();
				transformedLine = new Coordinate[2];
				transformedLine[0] = inverse.transform(line[0], new Coordinate());
				transformedLine[1] = inverse.transform(line[1], new Coordinate());
				return transformedLine;				
			}
			catch (NoninvertibleTransformationException ex)
			{
				throw new RuntimeException("We can't use noninvertable transformation", ex);
			}			
		}
	}
}
