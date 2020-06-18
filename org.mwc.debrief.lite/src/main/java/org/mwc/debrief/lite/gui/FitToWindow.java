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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.map.LiteMapPane;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

import Debrief.GUI.Frames.Application;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class FitToWindow extends AbstractAction implements CommandAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private ViewAction viewActionDetails;

	public static void fitToWindow(final Layers layers, final JMapPane map, final PlainProjection projection) {
		final WorldArea area = layers.getBounds();
		if (area != null) {
			// check it's not the default area that gets returned when
			// no data is loaded

			if (area.equals(Layers.getDebriefOrigin())) {
				// ok, don't bother resizing. Leave it as-is
			} else if (LiteMapPane.isViewportAcceptable(area)) {
				// ok, let's introduce a 5% border
				area.grow(area.getWidth() * 0.05, 0);

				final WorldLocation tl = area.getTopLeft();
				final WorldLocation br = area.getBottomRight();
				final CoordinateReferenceSystem crs = map.getMapContent().getCoordinateReferenceSystem();
				double long1 = tl.getLong();
				double lat1 = tl.getLat();
				double long2 = br.getLong();
				double lat2 = br.getLat();
				// TODO: Ian Turton
				// Ideally, I'd like to make use of the GTProjection object here but I'm not
				// sure how to
				// find it
				if (crs != DefaultGeographicCRS.WGS84) {
					try {
						final MathTransform degsToWorld = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs);
						final DirectPosition2D tlDegs = new DirectPosition2D(long1, lat1);
						final DirectPosition2D brDegs = new DirectPosition2D(long2, lat2);
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
				map.getMapContent().getViewport().setBounds(bounds);

				// force repaint
				final ReferencedEnvelope paneArea = map.getDisplayArea();
				map.setDisplayArea(paneArea);

				projection.setDataArea(area);
			}
		}
	}

	private final Layers _layers;

	private final JMapPane _map;

	private final PlainProjection _projection;

	private ToolParent _toolParent;

	public FitToWindow(final Layers layers, final JMapPane map, final PlainProjection projection,final ToolParent parent) {
		_layers = layers;
		_map = map;
		_projection = projection;
		_toolParent = parent;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		viewActionDetails = new ViewAction(_map,"Fit to window");
		viewActionDetails.setLastProjectionArea(DebriefLiteApp.getInstance().getProjectionArea());
		//System.out.println("Last projectionArea:"+viewActionDetails.getLastProjectionArea());
		fitToWindow(_layers, _map, _projection);
		viewActionDetails.setNewProjectionArea(DebriefLiteApp.getInstance().getProjectionArea());
		
	}

	@Override
	public void commandActivated(final CommandActionEvent e) {
		actionPerformed(e);
		if(viewActionDetails.isUndoable() && _toolParent != null) {
			_toolParent.addActionToBuffer(viewActionDetails);
		}
	}
}
