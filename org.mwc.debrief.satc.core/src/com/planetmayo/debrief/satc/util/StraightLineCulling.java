/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IdentityHashMap;
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
	private List<Geometry> filtered;
	
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
		filterRanges();		
		if (impossibleStartEndArea(transformation)) 
		{
			return;
		}
		CompliantLine[] crissCross = findCrissCrossLines(transformation);
		if (crissCross == null)
		{
			return;
		}
		CompliantLine result1 = new LineSolver(crissCross[0], false).process();
		CompliantLine result2 = new LineSolver(crissCross[1], true).process();
		if (result1 == null || result2 == null)
		{
			return;
		}
		line1 = result1.getTransformedLine();
		line2 = result2.getTransformedLine();
		Point intersection = MathUtils.findIntersection(line1[0], line1[1], line2[0], line2[1]);
		Coordinate[] scaledLine1 = scaleAndNormalizeLine(5, line1);
		Coordinate[] scaledLine2 = scaleAndNormalizeLine(5, line2);
		Geometry intersectPolygon = GeoSupport.getFactory().createPolygon(new Coordinate[] {
				scaledLine1[0], scaledLine2[0], intersection.getCoordinate(), scaledLine1[0]
		});
		constrainedStart = intersectPolygon.intersection(getStartLocation());
		
		intersectPolygon = GeoSupport.getFactory().createPolygon(new Coordinate[] {
				scaledLine1[1], scaledLine2[1], intersection.getCoordinate(), scaledLine1[1]
		});		
		constrainedEnd = getEndLocation().intersection(intersectPolygon);
	}
	
	public List<Geometry> getFiltered()
	{
		return filtered;
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

	private void filterRanges() 
	{
		filtered = new ArrayList<Geometry>();
		Geometry previous = ranges.get(0).getGeometry();
		filtered.add(previous);
		for (LocationRange range : ranges) 
		{
			//if (! range.getGeometry().intersects(previous)) 
			//{
				previous = range.getGeometry();
				filtered.add(previous);
			//}			
		}
		/*if (! filtered.contains(ranges.get(ranges.size() - 1).getGeometry())) 
		{
			filtered.remove(filtered.size() - 1);
			filtered.add(ranges.get(ranges.size() - 1).getGeometry());
		}*/
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
		
		CompliantLine[] crissCross = new CompliantLine[] {
				new CompliantLine(transformation, findBestCoordinates(startLessZero, endGreatZero)),
				new CompliantLine(transformation, findBestCoordinates(startGreatZero, endLessZero))
		};
		if (! checkCompliantCrissCross(transformation, crissCross)) 
		{
			return null;
		}
		return crissCross;
	}
	
	private boolean checkCompliantCrissCross(AffineTransformation transformation, CompliantLine[] crissCross)
	{
		AffineTransformation crissTransform = new AffineTransformation(transformation); 
		transformWorldToLine(crissTransform, crissCross[0].getLine()[0], crissCross[0].getLine()[1]);
		crissTransform.translate(0, -3 * EPS);
		PolygonLocation first = calculatePolygonLocation(crissTransform, getEndLocation());
		crissTransform.translate(0, 6 * EPS);
		PolygonLocation second = calculatePolygonLocation(crissTransform, getStartLocation());
		if (first.location == Location.INTERSECT || second.location == Location.INTERSECT) 
		{
			return false;
		}		
		
		AffineTransformation crossTransform = new AffineTransformation(transformation); 
		transformWorldToLine(crossTransform, crissCross[1].getLine()[0], crissCross[1].getLine()[1]);
		crossTransform.translate(0, -3 * EPS);
		first = calculatePolygonLocation(crossTransform, getStartLocation());
		crossTransform.translate(0, 6 * EPS);
		second = calculatePolygonLocation(crossTransform, getEndLocation());
		return ! (first.location == Location.INTERSECT || second.location == Location.INTERSECT); 
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
	
	private void transformWorldToLine(AffineTransformation world,	Coordinate start, Coordinate end)
	{
		double xDiff = end.x - start.x;
		double yDiff = end.y - start.y;
		double length = Math.hypot(xDiff, yDiff);
		world.translate(-start.x, -start.y);
		world.rotate(-yDiff / length, xDiff / length);
	}
	
	private Coordinate[] scaleAndNormalizeLine(double scaleFactor, Coordinate[] coordinates) 
	{
		coordinates = Arrays.copyOf(coordinates, 2);		
		double centreX = (coordinates[0].x + coordinates[1].x) / 2;
		double centreY = (coordinates[0].y + coordinates[1].y) / 2;
		coordinates[0] = new Coordinate(
				centreX + (coordinates[0].x - centreX) * scaleFactor,
				centreY + (coordinates[0].y - centreY) * scaleFactor
		);
		coordinates[1] = new Coordinate(
				centreX + (coordinates[1].x - centreX) * scaleFactor,
				centreY + (coordinates[1].y - centreY) * scaleFactor
		);		
		for (Coordinate c : coordinates)
		{
			c.x = Math.rint(c.x / EPS) * EPS;
			c.y = Math.rint(c.y / EPS) * EPS;
		}
		return coordinates;
	}	
	
	private PolygonLocation calculatePolygonLocation(AffineTransformation modification, Geometry geometry) 
	{
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
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		for (Coordinate coord : geometry.getCoordinates())
		{
			Coordinate temp = modification.transform(coord, new Coordinate());
			coordinates.add(temp);
			max = Math.max(max, temp.y);
			min = Math.min(min, temp.y);
			if (min <= EPS && max >= -EPS)
			{
				return new PolygonLocation(Location.INTERSECT, null);
			}
		}
		if (min < 0) 
		{
			return new PolygonLocation(Location.UNDER, coordinates);		
		}
		return new PolygonLocation(Location.OVER, coordinates);
	}		
	
	private Geometry getLocation(int num)
	{
		return filtered.get(num);
	}
	
	private Geometry getStartLocation() 
	{
		return filtered.get(0);
	}
	
	private Geometry getEndLocation() 
	{
		return filtered.get(filtered.size() - 1);
	}
	
	class LineSolver 
	{
		private final CompliantLine inputLine;
		private final boolean reflect;
		private final IdentityHashMap<Geometry, PolygonLocation> locations 
						= new IdentityHashMap<Geometry, PolygonLocation>();
		
		private CompliantLine line;
		private int leftBoundary;
		private int rightBoundary;
		private int left;
		private int right;
		
		public LineSolver(CompliantLine line, boolean reflect)
		{
			this.inputLine = line;
			this.reflect = reflect;
		}

		public CompliantLine process() 
		{
			prepareLine();
			constraintLine();
			extendLine();
			return line;
		}
		
		private void prepareLine() 
		{
			AffineTransformation currentModification = new AffineTransformation();
			transformWorldToLine(currentModification, inputLine.getLine()[0], inputLine.getLine()[1]);		
			if (reflect)
			{
				currentModification.reflect(1, 0);
			}
			line = new CompliantLine(
					new AffineTransformation(inputLine.getWorld()).compose(currentModification),
					new Coordinate[] {
						currentModification.transform(inputLine.getLine()[0], new Coordinate()),
						currentModification.transform(inputLine.getLine()[1], new Coordinate()),
					}				
			);			
		}		
		
		private void constraintLine() 
		{
			boolean stepLeft = false;
			leftBoundary = 0; 
			rightBoundary = filtered.size() - 1;
			left = leftBoundary + 1;
			right = rightBoundary - 1;
			while (!(left == rightBoundary && right == leftBoundary) && leftBoundary < rightBoundary) 
			{
				stepLeft = doStep(stepLeft);
				if (stepLeft)
				{
					PolygonLocation location = getPolygonLocation(line.getWorld(), getLocation(left));
					switch (location.getLocation())
					{
						case OVER:
							line = nextCompliantLine(location.getCoordinates(), true);
							locations.clear();
							leftBoundary = left;
							right = rightBoundary - 1;
							break;
						default:
							locations.put(getLocation(left), location);							
					}
					left++;
				}
				else
				{
					PolygonLocation location = getPolygonLocation(line.getWorld(), getLocation(right));
					switch (location.getLocation())
					{
						case UNDER:
							line = nextCompliantLine(location.getCoordinates(), false);
							locations.clear();
							rightBoundary = right;
							left = leftBoundary + 1;
							break;
						default:
							locations.put(getLocation(right), location);							
					}
					right--;					
				}
			}
			if (rightBoundary <= leftBoundary)
			{
				line = null;
			}
		}
		
		private boolean doStep(boolean stepLeft)
		{
			stepLeft = !stepLeft;
			if (! stepLeft && right == leftBoundary)
			{
				stepLeft = true;
			}
			if (stepLeft && left == rightBoundary)
			{
				stepLeft = false;
			}
			return stepLeft;
		}
		
		private PolygonLocation getPolygonLocation(AffineTransformation modification, Geometry geometry) 
		{
			if (locations.containsKey(geometry)) 
			{
				return locations.get(geometry);
			}
			return calculatePolygonLocation(modification, geometry);
		}	
		
		private CompliantLine nextCompliantLine(TreeSet<Coordinate> candidates, boolean isChangeLeft) 
		{
			double min = 2;
			Coordinate point = isChangeLeft ? line.getLine()[1] : line.getLine()[0];
			Coordinate point2 = null;
			for (Coordinate c : candidates)
			{
				if ((c.x >= point.x && isChangeLeft) || (c.x <= point.x && !isChangeLeft)) 
				{
					continue;
				}
				double xDiff = Math.abs(c.x - point.x);
				double yDiff = Math.abs(c.y - point.y);
				double sin = yDiff / Math.hypot(xDiff, yDiff);
				if (sin < min)
				{
					point2 = c;
					min = sin;
				}
			}
			Coordinate[] result = new Coordinate[2];
			result[0] = !isChangeLeft ? point : point2;
			result[1] = !isChangeLeft ? point2 : point;
			
			AffineTransformation transformation = new AffineTransformation();
			transformWorldToLine(transformation, result[0], result[1]);			
			return new CompliantLine(
					new AffineTransformation(transformation).composeBefore(line.getWorld()),
					new Coordinate[] {
						transformation.transform(result[0], new Coordinate()),
						transformation.transform(result[1], new Coordinate())
			});
		}
		
		private void extendLine()
		{
			if (line == null)
			{
				return;
			}
			Coordinate temp = new Coordinate();
			double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;		
			for (Geometry geometry : filtered)
			{
				double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE; 
				for (Coordinate c : geometry.getCoordinates())
				{
					line.getWorld().transform(c, temp);
					minX = Math.min(minX, temp.x);
					maxX = Math.max(maxX, temp.x);
					minY = Math.min(minY, temp.y);
					maxY = Math.max(maxY, temp.y);				
				}
				if (! (minY <= EPS && maxY >= -EPS))
				{
					line = null;
					return;
				}
			}
			line = new CompliantLine(line.getWorld(), new Coordinate[] {				
					new Coordinate(minX, 0),
					new Coordinate(maxX, 0)
			});
		}		
	}
	
	static class PolygonLocation 
	{
		private final Location location;
		private final TreeSet<Coordinate> coordinates;

		public PolygonLocation(Location location, TreeSet<Coordinate> coordinates)
		{
			this.location = location;
			this.coordinates = coordinates;
		}

		public Location getLocation()
		{
			return location;
		}

		public TreeSet<Coordinate> getCoordinates()
		{
			return coordinates;
		}
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
	
	static enum Location 
	{
		INTERSECT, UNDER, OVER
	}
}
