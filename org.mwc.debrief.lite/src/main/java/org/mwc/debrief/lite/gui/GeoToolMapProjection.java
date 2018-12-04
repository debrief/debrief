package org.mwc.debrief.lite.gui;

import java.awt.Point;

import org.geotools.data.FileDataStore;
import org.mwc.cmap.gt2plot.proj.GtProjection;

import com.vividsolutions.jts.geom.GeometryFactory;

import MWC.Algorithms.PlainProjection;
import MWC.GenericData.WorldLocation;

public class GeoToolMapProjection  extends PlainProjection {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3398817999418475368L;

	private GtProjection gtProjection;
	
		
	public  GeoToolMapProjection(String theName) {
		super(theName);
		gtProjection = new GtProjection();
	}
	
	@Override
	public Point toScreen(WorldLocation val) {
		return gtProjection.toScreen(val);
	}

	@Override
	public WorldLocation toWorld(Point val) {
		return gtProjection.toWorld(val);
	}

	@Override
	public void zoom(double value) {
	}

	public void setFileStore() {
		
	}
}
