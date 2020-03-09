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
package info.limpet.operations.spatial;

import java.awt.geom.Point2D;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotools.referencing.crs.DefaultGeographicCRS;

import info.limpet.impl.SampleData;

public class GeoSupport {
	private static class Calculator2D implements IGeoCalculator {

		@Override
		public Point2D calculatePoint(final Point2D pos1, final double angle, final double distance) {
			final double dx = Math.sin(Math.toRadians(angle)) * distance;
			final double dy = Math.cos(Math.toRadians(angle)) * distance;
			return new Point2D.Double(pos1.getX() + dx, pos1.getY() + dy);
		}

		@Override
		public Point2D createPoint(final double x, final double y) {
			return new Point2D.Double(x, y);
		}

		@Override
		public double getAngleBetween(final Point2D locA, final Point2D locB) {
			final double dx = locB.getX() - locA.getX();
			final double dy = locB.getY() - locA.getY();
			double angle = Math.toDegrees(Math.atan2(dy, dx));
			if (angle < 0) {
				angle += 360;
			}
			return angle;
		}

		@Override
		public double getDistanceBetween(final Point2D locA, final Point2D locB) {
			return locA.distance(locB);
		}

	}

	private static IGeoCalculator calcWGS84;

	private static IGeoCalculator calc2D;

	public static IGeoCalculator calculatorFor(final Unit<?> units) {
		final IGeoCalculator res;
		if (units.equals(SampleData.DEGREE_ANGLE)) {
			res = getCalculatorWGS84();
		} else if (units.equals(SI.METRE)) {
			res = getCalculatorGeneric2D();
		} else {
			throw new IllegalArgumentException("Don't have calculator for:" + units);
		}
		return res;
	}

	public static IGeoCalculator getCalculatorGeneric2D() {
		if (calc2D == null) {
			calc2D = new Calculator2D();
		}

		return calc2D;
	}

	public static IGeoCalculator getCalculatorWGS84() {
		if (calcWGS84 == null) {
			calcWGS84 = new GeotoolsCalculator(DefaultGeographicCRS.WGS84);
		}

		return calcWGS84;
	}

	/**
	 * protected constructor, to prevent inadvertent instantiation
	 *
	 */
	protected GeoSupport() {
	}

}
