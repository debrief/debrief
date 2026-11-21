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
package org.mwc.debrief.lite.gui;

import java.awt.Rectangle;

import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class MapUtils {
	
	/**
	 * Converts the Projection Area from lat/long to a referenced envelope
	 * 
	 * @param projectionArea
	 * @param crs
	 * @return
	 */
	public static ReferencedEnvelope convertToPaneArea(final WorldArea projectionArea,final CoordinateReferenceSystem crs) {
		final WorldLocation tl = projectionArea.getTopLeft();
		final WorldLocation br = projectionArea.getBottomRight();
		double long1 = tl.getLong();
		double lat1 = tl.getLat();
		double long2 = br.getLong();
		double lat2 = br.getLat();
		if (crs != DefaultGeographicCRS.WGS84) {
			try {
				final MathTransform degsToWorld = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs);
				final Position2D tlDegs = new Position2D(long1, lat1);
				final Position2D brDegs = new Position2D(long2, lat2);
				degsToWorld.transform(tlDegs, tlDegs);
				degsToWorld.transform(brDegs, brDegs);
				long1 = tlDegs.x;
				lat1 = tlDegs.y;
				long2 = brDegs.x;
				lat2 = brDegs.y;

			} catch (final FactoryException | MismatchedDimensionException | TransformException e) {
				Application.logError2(ToolParent.ERROR, "Failure in projection transform", e);
			}
		}
		final ReferencedEnvelope bounds = new ReferencedEnvelope(long1, long2, lat1, lat2, crs);
		return bounds;
	}
	
	public static WorldArea convertToWorldArea(final Rectangle currentViewArea,final CoordinateReferenceSystem crs) {
		
		WorldArea retval = null;
		try {
			final MathTransform degsToWorld = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs);
			Position2D tldegs = new Position2D(currentViewArea.getX(),currentViewArea.getY());
			degsToWorld.transform(tldegs, tldegs);
			double long1 = tldegs.x;
			double lat1 = tldegs.y;

			Position2D brDegs = new Position2D(currentViewArea.getMaxX(),currentViewArea.getMaxY());
			degsToWorld.transform(brDegs, brDegs);
			double long2 = brDegs.x;
			double lat2 = brDegs.y;
			WorldLocation topLeftVal = new WorldLocation(lat1, long1, 0);
			WorldLocation bottomRightVal = new WorldLocation(lat2,long2,0);
			retval = new WorldArea(topLeftVal,bottomRightVal);

		} catch (final FactoryException | MismatchedDimensionException | TransformException e) {
			Application.logError2(ToolParent.ERROR, "Failure in projection transform", e);
		}
		return retval;
	}

}
