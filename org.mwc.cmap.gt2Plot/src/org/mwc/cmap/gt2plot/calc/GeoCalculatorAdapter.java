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
package org.mwc.cmap.gt2plot.calc;

import org.eclipse.core.runtime.Status;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.mwc.cmap.gt2plot.GtActivator;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import MWC.Algorithms.EarthModel;
import MWC.Algorithms.EarthModels.FlatEarth;
import MWC.GenericData.WorldLocation;


public class GeoCalculatorAdapter extends FlatEarth implements EarthModel 
{
	
	/***
 	 * CoordinateReferenceSystem - specify this to configure your GeodeticCalculator to work with a specific Ellipsoid
	 */
	public static final String CRS_TYPE = "EPSG:4326";
	private final GeodeticCalculator _geoCalc;
	
	public GeoCalculatorAdapter() 
	{
		this(CRS_TYPE);
	}
	
	public GeoCalculatorAdapter(final String crsType) {
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.decode(crsType);
		} catch (final NoSuchAuthorityCodeException e) {
			GtActivator.logError(Status.ERROR, "Could not initialize CRS" + crsType, e);
		} catch (final FactoryException e) {
			GtActivator.logError(Status.ERROR, "Could not initialize CRS" + crsType, e);
		}
		_geoCalc = new GeodeticCalculator(crs);
	}
	
	private void setLocations(final WorldLocation from, final WorldLocation to) {
		_geoCalc.setStartingGeographicPoint(from.getLong(), from.getLat());
		_geoCalc.setDestinationGeographicPoint(to.getLong(), to.getLat());
	}
		
	@Override
	public double rangeBetween(final WorldLocation from, final WorldLocation to) {
		this.setLocations(from, to);
		return Math.toDegrees(_geoCalc.getAzimuth());
	}

	@Override
	public double bearingBetween(final WorldLocation from, final WorldLocation to) {
		this.setLocations(from, to);
		return Math.toRadians(_geoCalc.getAzimuth());
	}

}
