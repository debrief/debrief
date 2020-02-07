/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations.spatial;

import java.awt.geom.Point2D;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.crs.SingleCRS;

public class GeotoolsCalculator implements IGeoCalculator {
	private final GeodeticCalculator calc;

	/**
	 * protected constructor - to prevent declaration of GeoSupport
	 *
	 */
	public GeotoolsCalculator(final SingleCRS system) {
		calc = new GeodeticCalculator(system);
	}

	@Override
	public Point2D calculatePoint(final Point2D pos1, final double angle, final double distance) {
		calc.setStartingGeographicPoint(pos1);
		calc.setDirection(angle, distance);
		return calc.getDestinationGeographicPoint();
	}

	@Override
	public Point2D createPoint(final double dLong, final double dLat) {
		return new Point2D.Double(dLong, dLat);
	}

	@Override
	public double getAngleBetween(final Point2D txLoc, final Point2D rxLoc) {
		calc.setStartingGeographicPoint(txLoc);
		calc.setDestinationGeographicPoint(rxLoc);
		return calc.getAzimuth();
	}

	@Override
	public double getDistanceBetween(final Point2D locA, final Point2D locB) {
		calc.setStartingGeographicPoint(locA);
		calc.setDestinationGeographicPoint(locB);
		return calc.getOrthodromicDistance();
	}
}
