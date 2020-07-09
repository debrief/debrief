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
package Debrief.Wrappers.DynamicTrackShapes;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import MWC.GUI.CanvasType;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.ShapeCanvasType;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class DynamicTrackCoverageWrapper extends DynamicTrackShapeWrapper {
	/**
	 * utility class used to store a single sensor coverage arc
	 *
	 * @author ian
	 *
	 */
	public static class DynamicCoverageShape implements DynamicShape {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Create a donut or donut section. A full donut results if the start and end
		 * mod 360 are within 0.1 degree, so don't call this if the difference between
		 * minAngle and maxAngle should be 0.
		 *
		 * @param innerRadius of donut section
		 * @param outerRadius of donut section
		 * @param minAngle    compass angle of section start
		 * @param maxAngle    compass angle of section end
		 */
		public static Area makeDonutSectionArea(final double innerRadius, final double outerRadius,
				final double minAngle, final double maxAngle, final double course) {

			final double dO = 2 * outerRadius, dI = 2 * innerRadius;
			// Angles: From degrees clockwise from the positive y axis,
			// convert to degress counter-clockwise from positive x axis.
			final double aBeg = 90 - (maxAngle + course);
			final double aExt = maxAngle - minAngle;
			// X and y are upper left corner of bounding rectangle of full circle.
			// Subtract 0.5 so that center is between pixels and drawn width is dO
			// (rather than dO + 1).
			final double xO = -dO / 2 - 0.5, yO = -dO / 2 - .5;
			final double xI = -dI / 2 - 0.5, yI = -dI / 2 - .5;
			if (Math.abs(minAngle % 360 - maxAngle % 360) < 0.1) {
				final Area outer = new Area(new Ellipse2D.Double(xO, yO, dO, dO));
				final Area inner = new Area(new Ellipse2D.Double(xI, yI, dI, dI));
				outer.subtract(inner);
				return outer;
			} else {
				final Area outer = new Area(new Arc2D.Double(xO, yO, dO, dO, aBeg, aExt, Arc2D.PIE));
				final Area inner = new Area(new Ellipse2D.Double(xI, yI, dI, dI));
				// Area inner = new Area(new Arc2D.Double(xI, yI, dI, dI, aBeg, aExt,
				// Arc2D.PIE));
				outer.subtract(inner);
				return outer;
			}
		}

		final public int minYds, maxYds, minAngleDegs, maxAngleDegs;

		public DynamicCoverageShape(final int MinAngleDegs, final int MaxAngleDegs, final int minYds,
				final int maxYds) {
			this.minAngleDegs = MinAngleDegs;
			this.maxAngleDegs = MaxAngleDegs;
			this.minYds = minYds;
			this.maxYds = maxYds;
		}

		@Override
		public void paint(final CanvasType dest, final Color color, final boolean semiTransparent,
				final WorldLocation originWd, final double courseDegs, final boolean filled) {
			// update the color
			dest.setColor(color);

			// get the host origin in screen coords
			final Point originPt = dest.toScreen(originWd);

			// handle unable to gen screen coords (if off visible area)
			if (originPt == null)
				return;

			final WorldDistance minDist = new WorldDistance(minYds, WorldDistance.YARDS);
			final WorldDistance maxDist = new WorldDistance(maxYds, WorldDistance.YARDS);

			// sort out the sizes in pixels
			final WorldVector minOffset = new WorldVector(0.001d, minDist, null);
			final WorldVector maxOffset = new WorldVector(0.001d, maxDist, null);

			final WorldLocation minWd = originWd.add(minOffset);
			final WorldLocation maxWd = originWd.add(maxOffset);

			final Point minPt = dest.toScreen(minWd);
			final Point maxPt = dest.toScreen(maxWd);

			final long minOffsetPt = originPt.y - minPt.y;
			final long maxOffsetPt = originPt.y - maxPt.y;

			final Area area = makeDonutSectionArea(minOffsetPt, maxOffsetPt, minAngleDegs, maxAngleDegs, courseDegs);

			final AffineTransform af = AffineTransform.getTranslateInstance(originPt.x, originPt.y);
			final Shape shape = af.createTransformedShape(area);

			if (dest instanceof ExtendedCanvasType) {
				if (!filled) {
					((ExtendedCanvasType) dest).emptyShape(shape);
				} else if (semiTransparent) {
					((ExtendedCanvasType) dest).semiFillShape(shape);
				} else {
					((ExtendedCanvasType) dest).fillShape(shape);
				}
			} else if (dest instanceof ShapeCanvasType) {
				((ShapeCanvasType) dest).fillShape(shape);
			}
		}

		@Override
		public String toString() {
			return minAngleDegs + " " + maxAngleDegs + " " + minYds + " " + maxYds + " ";
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DynamicTrackCoverageWrapper() {
		super();
	}

	/**
	 * build a new sensorarc contact wrapper
	 *
	 */

	public DynamicTrackCoverageWrapper(final String theTrack, final HiResDate startDTG, final HiResDate endDTG,
			final List<DynamicShape> values, final Color theColor, final int theStyle, final String coverageName) {
		super(theTrack, startDTG, endDTG, values, theColor, theStyle, coverageName);
	}

	@Override
	public String getConstraints() {
		final StringBuilder builder = new StringBuilder();
		for (final DynamicShape valueD : _values) {
			final DynamicCoverageShape value = (DynamicCoverageShape) valueD;
			builder.append(value.minAngleDegs);
			builder.append(" ");
			builder.append(value.maxAngleDegs);
			builder.append(" ");
			builder.append(value.minYds);
			builder.append(" ");
			builder.append(value.maxYds);
			builder.append(" ");
		}
		return builder.toString().trim();
	}

	@Override
	public void setConstraints(String arcs) {
		if (arcs == null) {
			throw new RuntimeException("Error parsing arcs");
		}
		arcs = arcs.trim();

		final StringTokenizer tokens = new StringTokenizer(arcs, " ");
		final ArrayList<String> elements = new ArrayList<String>();
		while (tokens.hasMoreTokens()) {
			final String token = tokens.nextToken();
			elements.add(token);
		}

		if (elements.size() % 4 != 0) {
			throw new IllegalArgumentException("Wrong number of elements in arcs property");
		}
		final List<DynamicShape> values = new ArrayList<DynamicShape>();
		int index = 0;
		while (index < elements.size()) {
			final int minAngleDegs = getValue(elements, index++);
			final int maxAngleDegs = getValue(elements, index++);
			final int minYds = getValue(elements, index++);
			final int maxYds = getValue(elements, index++);
			final DynamicCoverageShape value = new DynamicCoverageShape(minAngleDegs, maxAngleDegs, minYds, maxYds);
			values.add(value);
		}
		this._values = values;
	}
}
