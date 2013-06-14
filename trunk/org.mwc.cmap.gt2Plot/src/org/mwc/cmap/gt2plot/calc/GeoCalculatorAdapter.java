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
	private GeodeticCalculator _geoCalc;
	
	public GeoCalculatorAdapter() 
	{
		this(CRS_TYPE);
	}
	
	public GeoCalculatorAdapter(String crsType) {
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.decode(crsType);
		} catch (NoSuchAuthorityCodeException e) {
			GtActivator.logError(Status.ERROR, "Could not initialize CRS" + crsType, e);
		} catch (FactoryException e) {
			GtActivator.logError(Status.ERROR, "Could not initialize CRS" + crsType, e);
		}
		_geoCalc = new GeodeticCalculator(crs);
	}
	
	private void setLocations(WorldLocation from, WorldLocation to) {
		_geoCalc.setStartingGeographicPoint(from.getLong(), from.getLat());
		_geoCalc.setDestinationGeographicPoint(to.getLong(), to.getLat());
	}
		
	@Override
	public double rangeBetween(WorldLocation from, WorldLocation to) {
		this.setLocations(from, to);
		return Math.toDegrees(_geoCalc.getAzimuth());
	}

	@Override
	public double bearingBetween(WorldLocation from, WorldLocation to) {
		this.setLocations(from, to);
		return Math.toRadians(_geoCalc.getAzimuth());
	}

}
