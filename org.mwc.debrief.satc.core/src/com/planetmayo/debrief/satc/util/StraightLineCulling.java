/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

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

public class StraightLineCulling {
	static class CompliantLine {
		private final AffineTransformation world;
		private final Coordinate[] line;

		private Coordinate[] transformedLine;

		private CompliantLine(final AffineTransformation world, final Coordinate[] line) {
			this.world = world;
			this.line = line;
		}

		public Coordinate[] getLine() {
			return line;
		}

		public Coordinate[] getTransformedLine() {
			if (transformedLine != null) {
				return transformedLine;
			}
			try {
				final AffineTransformation inverse = world.getInverse();
				transformedLine = new Coordinate[2];
				transformedLine[0] = inverse.transform(line[0], new Coordinate());
				transformedLine[1] = inverse.transform(line[1], new Coordinate());
				return transformedLine;
			} catch (final NoninvertibleTransformationException ex) {
				throw new RuntimeException("We can't use noninvertable transformation", ex);
			}
		}

		public AffineTransformation getWorld() {
			return world;
		}
	}

	class LineSolver {
		private final CompliantLine inputLine;
		private final boolean reflect;
		private final IdentityHashMap<Geometry, PolygonLocation> locations = new IdentityHashMap<Geometry, PolygonLocation>();

		private CompliantLine line;
		private int leftBoundary;
		private int rightBoundary;
		private int left;
		private int right;

		public LineSolver(final CompliantLine line, final boolean reflect) {
			this.inputLine = line;
			this.reflect = reflect;
		}

		private void constraintLine() {
			boolean stepLeft = false;
			leftBoundary = 0;
			rightBoundary = filtered.size() - 1;
			left = leftBoundary + 1;
			right = rightBoundary - 1;
			while (!(left == rightBoundary && right == leftBoundary) && leftBoundary < rightBoundary) {
				stepLeft = doStep(stepLeft);
				if (stepLeft) {
					final PolygonLocation location = getPolygonLocation(line.getWorld(), getLocation(left));
					switch (location.getLocation()) {
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
				} else {
					final PolygonLocation location = getPolygonLocation(line.getWorld(), getLocation(right));
					switch (location.getLocation()) {
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
			if (rightBoundary <= leftBoundary) {
				line = null;
			}
		}

		private boolean doStep(boolean stepLeft) {
			stepLeft = !stepLeft;
			if (!stepLeft && right == leftBoundary) {
				stepLeft = true;
			}
			if (stepLeft && left == rightBoundary) {
				stepLeft = false;
			}
			return stepLeft;
		}

		private void extendLine() {
			if (line == null) {
				return;
			}
			final Coordinate temp = new Coordinate();
			double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
			for (final Geometry geometry : filtered) {
				double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
				for (final Coordinate c : geometry.getCoordinates()) {
					line.getWorld().transform(c, temp);
					minX = Math.min(minX, temp.x);
					maxX = Math.max(maxX, temp.x);
					minY = Math.min(minY, temp.y);
					maxY = Math.max(maxY, temp.y);
				}
				if (!(minY <= EPS && maxY >= -EPS)) {
					line = null;
					return;
				}
			}
			line = new CompliantLine(line.getWorld(),
					new Coordinate[] { new Coordinate(minX, 0), new Coordinate(maxX, 0) });
		}

		private PolygonLocation getPolygonLocation(final AffineTransformation modification, final Geometry geometry) {
			if (locations.containsKey(geometry)) {
				return locations.get(geometry);
			}
			return calculatePolygonLocation(modification, geometry);
		}

		private CompliantLine nextCompliantLine(final TreeSet<Coordinate> candidates, final boolean isChangeLeft) {
			double min = 2;
			final Coordinate point = isChangeLeft ? line.getLine()[1] : line.getLine()[0];
			Coordinate point2 = null;
			for (final Coordinate c : candidates) {
				if ((c.x >= point.x && isChangeLeft) || (c.x <= point.x && !isChangeLeft)) {
					continue;
				}
				final double xDiff = Math.abs(c.x - point.x);
				final double yDiff = Math.abs(c.y - point.y);
				final double sin = yDiff / Math.hypot(xDiff, yDiff);
				if (sin < min) {
					point2 = c;
					min = sin;
				}
			}
			final Coordinate[] result = new Coordinate[2];
			result[0] = !isChangeLeft ? point : point2;
			result[1] = !isChangeLeft ? point2 : point;

			final AffineTransformation transformation = new AffineTransformation();
			transformWorldToLine(transformation, result[0], result[1]);
			return new CompliantLine(new AffineTransformation(transformation).composeBefore(line.getWorld()),
					new Coordinate[] { transformation.transform(result[0], new Coordinate()),
							transformation.transform(result[1], new Coordinate()) });
		}

		private void prepareLine() {
			final AffineTransformation currentModification = new AffineTransformation();
			transformWorldToLine(currentModification, inputLine.getLine()[0], inputLine.getLine()[1]);
			if (reflect) {
				currentModification.reflect(1, 0);
			}
			line = new CompliantLine(new AffineTransformation(inputLine.getWorld()).compose(currentModification),
					new Coordinate[] { currentModification.transform(inputLine.getLine()[0], new Coordinate()),
							currentModification.transform(inputLine.getLine()[1], new Coordinate()), });
		}

		public CompliantLine process() {
			prepareLine();
			constraintLine();
			extendLine();
			return line;
		}
	}

	static enum Location {
		INTERSECT, UNDER, OVER
	}

	static class PolygonLocation {
		private final Location location;
		private final TreeSet<Coordinate> coordinates;

		public PolygonLocation(final Location location, final TreeSet<Coordinate> coordinates) {
			this.location = location;
			this.coordinates = coordinates;
		}

		public TreeSet<Coordinate> getCoordinates() {
			return coordinates;
		}

		public Location getLocation() {
			return location;
		}
	}

	private static final double EPS = 0.000001;

	private final List<LocationRange> ranges;
	private List<Geometry> filtered;

	private Coordinate[] line1;

	private Coordinate[] line2;

	private Geometry constrainedStart;

	private Geometry constrainedEnd;

	public StraightLineCulling(final List<LocationRange> ranges) {
		this.ranges = ranges;
	}

	private PolygonLocation calculatePolygonLocation(final AffineTransformation modification, final Geometry geometry) {
		final TreeSet<Coordinate> coordinates = new TreeSet<Coordinate>(new Comparator<Coordinate>() {

			@Override
			public int compare(final Coordinate o1, final Coordinate o2) {
				return (int) Math.signum(Math.abs(o1.y) - Math.abs(o2.y));
			}
		});
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		for (final Coordinate coord : geometry.getCoordinates()) {
			final Coordinate temp = modification.transform(coord, new Coordinate());
			coordinates.add(temp);
			max = Math.max(max, temp.y);
			min = Math.min(min, temp.y);
			if (min <= EPS && max >= -EPS) {
				return new PolygonLocation(Location.INTERSECT, null);
			}
		}
		if (min < 0) {
			return new PolygonLocation(Location.UNDER, coordinates);
		}
		return new PolygonLocation(Location.OVER, coordinates);
	}

	private boolean checkCompliantCrissCross(final AffineTransformation transformation,
			final CompliantLine[] crissCross) {
		final AffineTransformation crissTransform = new AffineTransformation(transformation);
		transformWorldToLine(crissTransform, crissCross[0].getLine()[0], crissCross[0].getLine()[1]);
		crissTransform.translate(0, -3 * EPS);
		PolygonLocation first = calculatePolygonLocation(crissTransform, getEndLocation());
		crissTransform.translate(0, 6 * EPS);
		PolygonLocation second = calculatePolygonLocation(crissTransform, getStartLocation());
		if (first.location == Location.INTERSECT || second.location == Location.INTERSECT) {
			return false;
		}

		final AffineTransformation crossTransform = new AffineTransformation(transformation);
		transformWorldToLine(crossTransform, crissCross[1].getLine()[0], crissCross[1].getLine()[1]);
		crossTransform.translate(0, -3 * EPS);
		first = calculatePolygonLocation(crossTransform, getStartLocation());
		crossTransform.translate(0, 6 * EPS);
		second = calculatePolygonLocation(crossTransform, getEndLocation());
		return !(first.location == Location.INTERSECT || second.location == Location.INTERSECT);
	}

	private void filterRanges() {
		filtered = new ArrayList<Geometry>();
		Geometry previous = ranges.get(0).getGeometry();
		filtered.add(previous);
		for (final LocationRange range : ranges) {
			// if (! range.getGeometry().intersects(previous))
			// {
			previous = range.getGeometry();
			filtered.add(previous);
			// }
		}
		/*
		 * if (! filtered.contains(ranges.get(ranges.size() - 1).getGeometry())) {
		 * filtered.remove(filtered.size() - 1); filtered.add(ranges.get(ranges.size() -
		 * 1).getGeometry()); }
		 */
	}

	private Coordinate[] findBestCoordinates(final List<Coordinate> list1, final List<Coordinate> list2) {
		final Coordinate[] best = new Coordinate[2];
		double max = 0;
		for (final Coordinate x : list1) {
			for (final Coordinate y : list2) {
				final double yDiff = Math.abs(x.y - y.y);
				final double xDiff = Math.abs(x.x - y.x);
				final double sin = yDiff / Math.hypot(xDiff, yDiff);
				if (sin > max) {
					best[0] = x;
					best[1] = y;
					max = sin;
				}
			}
		}
		return best;
	}

	private CompliantLine[] findCrissCrossLines(final AffineTransformation transformation) {
		final Geometry start = transformation.transform(getStartLocation());
		final Geometry end = transformation.transform(getEndLocation());

		final List<Coordinate> startGreatZero = new ArrayList<Coordinate>();
		final List<Coordinate> startLessZero = new ArrayList<Coordinate>();
		final List<Coordinate> endGreatZero = new ArrayList<Coordinate>();
		final List<Coordinate> endLessZero = new ArrayList<Coordinate>();

		splitCoordinatesByZero(start, startGreatZero, startLessZero);
		splitCoordinatesByZero(end, endGreatZero, endLessZero);

		final CompliantLine[] crissCross = new CompliantLine[] {
				new CompliantLine(transformation, findBestCoordinates(startLessZero, endGreatZero)),
				new CompliantLine(transformation, findBestCoordinates(startGreatZero, endLessZero)) };
		if (!checkCompliantCrissCross(transformation, crissCross)) {
			return null;
		}
		return crissCross;
	}

	public Geometry getConstrainedEnd() {
		return constrainedEnd;
	}

	public Geometry getConstrainedStart() {
		return constrainedStart;
	}

	private Geometry getEndLocation() {
		return filtered.get(filtered.size() - 1);
	}

	public List<Geometry> getFiltered() {
		return filtered;
	}

	public Coordinate[] getFirstCrissCrossLine() {
		return line1;
	}

	private Geometry getLocation(final int num) {
		return filtered.get(num);
	}

	public Coordinate[] getSecondCrissCrossLine() {
		return line2;
	}

	private Geometry getStartLocation() {
		return filtered.get(0);
	}

	public boolean hasResults() {
		return constrainedStart != null && constrainedEnd != null;
	}

	private boolean impossibleStartEndArea(final AffineTransformation transformation) {
		double xMax = Double.MIN_VALUE;
		final Coordinate dest = new Coordinate();
		for (final Coordinate startCoord : getStartLocation().getCoordinates()) {
			transformation.transform(startCoord, dest);
			xMax = Math.max(xMax, dest.x);
		}
		for (final Coordinate endCoord : getEndLocation().getCoordinates()) {
			transformation.transform(endCoord, dest);
			if (dest.x <= xMax) {
				return true;
			}
		}
		return false;
	}

	public void process() {
		line1 = line2 = null;
		constrainedStart = constrainedEnd = null;
		if (ranges == null || ranges.size() < 3) {
			return;
		}
		final AffineTransformation transformation = transformWorldToMiddleLine();
		filterRanges();
		if (impossibleStartEndArea(transformation)) {
			return;
		}
		final CompliantLine[] crissCross = findCrissCrossLines(transformation);
		if (crissCross == null) {
			return;
		}
		final CompliantLine result1 = new LineSolver(crissCross[0], false).process();
		final CompliantLine result2 = new LineSolver(crissCross[1], true).process();
		if (result1 == null || result2 == null) {
			return;
		}
		line1 = result1.getTransformedLine();
		line2 = result2.getTransformedLine();
		final Point intersection = MathUtils.findIntersection(line1[0], line1[1], line2[0], line2[1]);
		final Coordinate[] scaledLine1 = scaleAndNormalizeLine(5, line1);
		final Coordinate[] scaledLine2 = scaleAndNormalizeLine(5, line2);
		Geometry intersectPolygon = GeoSupport.getFactory().createPolygon(
				new Coordinate[] { scaledLine1[0], scaledLine2[0], intersection.getCoordinate(), scaledLine1[0] });
		constrainedStart = intersectPolygon.intersection(getStartLocation());

		intersectPolygon = GeoSupport.getFactory().createPolygon(
				new Coordinate[] { scaledLine1[1], scaledLine2[1], intersection.getCoordinate(), scaledLine1[1] });
		constrainedEnd = getEndLocation().intersection(intersectPolygon);
	}

	private Coordinate[] scaleAndNormalizeLine(final double scaleFactor, Coordinate[] coordinates) {
		coordinates = Arrays.copyOf(coordinates, 2);
		final double centreX = (coordinates[0].x + coordinates[1].x) / 2;
		final double centreY = (coordinates[0].y + coordinates[1].y) / 2;
		coordinates[0] = new Coordinate(centreX + (coordinates[0].x - centreX) * scaleFactor,
				centreY + (coordinates[0].y - centreY) * scaleFactor);
		coordinates[1] = new Coordinate(centreX + (coordinates[1].x - centreX) * scaleFactor,
				centreY + (coordinates[1].y - centreY) * scaleFactor);
		for (final Coordinate c : coordinates) {
			c.x = Math.rint(c.x / EPS) * EPS;
			c.y = Math.rint(c.y / EPS) * EPS;
		}
		return coordinates;
	}

	private void splitCoordinatesByZero(final Geometry geometry, final List<Coordinate> great,
			final List<Coordinate> less) {
		for (final Coordinate c : geometry.getCoordinates()) {
			if (c.y > 0) {
				great.add(c);
			} else {
				less.add(c);
			}
		}
	}

	private void transformWorldToLine(final AffineTransformation world, final Coordinate start, final Coordinate end) {
		final double xDiff = end.x - start.x;
		final double yDiff = end.y - start.y;
		final double length = Math.hypot(xDiff, yDiff);
		world.translate(-start.x, -start.y);
		world.rotate(-yDiff / length, xDiff / length);
	}

	private AffineTransformation transformWorldToMiddleLine() {
		final AffineTransformation transformation = new AffineTransformation();
		final Point startCenter = ranges.get(0).getGeometry().getCentroid();
		final Point endCenter = ranges.get(ranges.size() - 1).getGeometry().getCentroid();
		transformWorldToLine(transformation, startCenter.getCoordinate(), endCenter.getCoordinate());
		return transformation;
	}
}
