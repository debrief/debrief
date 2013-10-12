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
import com.vividsolutions.jts.io.WKTReader;

public class StraightLineCulling
{	
	private List<LocationRange> ranges;
	
	public StraightLineCulling(List<LocationRange> ranges)
	{
		this.ranges = ranges;
	}
	
	public void process() 
	{
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
		CompliantLine a1 = processLine(crissCross[0], false, 1);
		System.out.println(a1.getTransformedLine()[0] + " : " + a1.getTransformedLine()[1]);
		CompliantLine a2 = processLine(crissCross[1], true, 1);
		System.out.println(a2.getTransformedLine()[0] + " : " + a2.getTransformedLine()[1]);
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
		for (Coordinate startCoord : getStartArea().getCoordinates()) 
		{
			transformation.transform(startCoord, dest);
			xMax = Math.max(xMax, dest.x);
		}
		for (Coordinate endCoord : getEndArea().getCoordinates()) 
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
		Geometry start = transformation.transform(getStartArea());
		Geometry end = transformation.transform(getEndArea());
		
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
				double sin = yDiff / Math.sqrt(yDiff * yDiff + xDiff * xDiff);
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
			Geometry geometry = getArea(i);
			lastCompliant = false;
			coordinates.clear();
			
			for (Coordinate coord : geometry.getCoordinates())
			{
				Coordinate temp = currentModification.transform(coord, new Coordinate());
				coordinates.add(temp);
				max = Math.max(max, temp.y);
				min = Math.min(min, temp.y);
				if (min < 0 && max > 0)
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
			return processLine(nextLine, false, i + 1);
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
			double sin = yDiff / Math.sqrt(xDiff * xDiff + yDiff * yDiff);
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
	
	private void transformWorldToLine(AffineTransformation world,	Coordinate start, Coordinate end)
	{
		double xDiff = end.x - start.x;
		double yDiff = end.y - start.y;
		double length = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		world.translate(-start.x, -start.y);
		world.rotate(-yDiff / length, xDiff / length);
	}

	private Geometry getArea(int num)
	{
		return ranges.get(num).getGeometry();
	}
	
	private Geometry getStartArea() 
	{
		return ranges.get(0).getGeometry();
	}
	
	private Geometry getEndArea() 
	{
		return ranges.get(ranges.size() - 1).getGeometry();
	}	
	
	private static class CompliantLine 
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
	
	public static void main(String[] args) throws Exception
	{
		WKTReader reader = new WKTReader();

		List<LocationRange> ranges = new ArrayList<LocationRange>();
		
		//ranges.add(new LocationRange(reader.read("POLYGON ((17.2 3.1, 17.6 3.2, 20.4 2.9, 21.0 -1.2, 18.5 0.4, 17.2 3.1))")));		
		//ranges.add(new LocationRange(reader.read("POLYGON ((3.28 -3.02, 4.02 2.16, 5.66 2.42, 6.1 -0.76, 5.7 -1.84, 3.28 -3.02))")));
		//ranges.add(new LocationRange(reader.read("POLYGON ((1.58 1.78, 0.66 4.74, 0.36 5.08, 2.12 4.3, 2.54 1.76, 1.58 1.78))")));
		
		ranges.add(new LocationRange(reader.read("POLYGON ((1.58 1.78, 0.66 4.74, 0.36 5.08, 2.12 4.3, 2.54 1.76, 1.58 1.78))")));		
		ranges.add(new LocationRange(reader.read("POLYGON ((3.28 -3.02, 4.02 2.16, 5.66 2.42, 6.1 -0.76, 5.7 -1.84, 3.28 -3.02))")));
		ranges.add(new LocationRange(reader.read("POLYGON ((17.2 3.1, 17.6 3.2, 20.4 2.9, 21.0 -1.2, 18.5 0.4, 17.2 3.1))")));
		
		//ranges.add(new LocationRange(reader.read("POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))")));
		//ranges.add(new LocationRange(reader.read("POLYGON ((3.28 -3.02, 4.02 2.16, 5.66 2.42, 6.1 -0.76, 5.7 -1.84, 3.28 -3.02))")));
		//ranges.add(new LocationRange(reader.read("POLYGON ((0 3, 1 3, 1 4, 0 4, 0 3))")));		
		
		//ranges.add(new LocationRange(reader.read("POLYGON ((0 3, 1 3, 1 4, 0 4, 0 3))")));		
		//ranges.add(new LocationRange(reader.read("POLYGON ((3.28 -3.02, 4.02 2.16, 5.66 2.42, 6.1 -0.76, 5.7 -1.84, 3.28 -3.02))")));
		//ranges.add(new LocationRange(reader.read("POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))")));
		
		StraightLineCulling culling = new StraightLineCulling(ranges);
		culling.process();
	}

}
