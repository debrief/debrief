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
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.JMapPane;
import org.locationtech.jts.geom.Coordinate;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

import MWC.GUI.ToolParent;
import MWC.GenericData.WorldArea;

public class ZoomOut extends AbstractAction implements CommandAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final JMapPane _map;
	private WorldArea _currentViewArea;
	private ToolParent _toolParent;
	private ViewAction actionDetails;
	public ZoomOut(final JMapPane map,ToolParent parent) {
		_map = map;
		_toolParent = parent;
		
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		_currentViewArea = DebriefLiteApp.getInstance().getProjectionArea();
		actionDetails = new ViewAction(_map,"Zoom out");
		actionDetails.setLastProjectionArea(_currentViewArea);
		Rectangle paneArea = _map.getVisibleRect();
		// get the centre of the viewport
		final Coordinate centre = _map.getMapContent().getViewport().getBounds().centre();
		final Point2D mapPos = new Point2D.Double(centre.x, centre.y);

		// decide on a new scale
		final double scale = _map.getWorldToScreenTransform().getScaleX();
		final double newScale = scale / 1.5;

		// don't bother zooming out too far
		if (newScale > 2.5E-5) {
			final DirectPosition2D corner = new DirectPosition2D(centre.x - 0.5d * paneArea.getWidth() / newScale,
					centre.y + 0.5d * paneArea.getHeight() / newScale);

			final Envelope2D newMapArea = new Envelope2D();
			newMapArea.setFrameFromCenter(mapPos, corner);
			_map.setDisplayArea(newMapArea);
		}
		
		DebriefLiteApp.getInstance().updateProjectionArea();
		actionDetails.setNewProjectionArea(DebriefLiteApp.getInstance().getProjectionArea());
		System.out.println("Projection area after zoom/redo:"+DebriefLiteApp.getInstance().getProjectionArea());
	}

	@Override
	public void commandActivated(final CommandActionEvent e) {
		actionPerformed(e);
		if (actionDetails.isUndoable()) {
			// store the event
			// put it on the buffer
			if (_toolParent != null)
				_toolParent.addActionToBuffer(actionDetails);
		}
	}

//	@Override
//	public void execute() {
//		actionPerformed(null);
//	}
//
//	@Override
//	public boolean isRedoable() {
//		return true;
//	}
//
//	@Override
//	public boolean isUndoable() {
//		return true;
//	}
//
//	@Override
//	public void undo() {
//		System.out.println("CurrentViewArea:"+_currentViewArea);
//		_map.setDisplayArea(MapUtils.convertToPaneArea(_currentViewArea, _map.getMapContent().getCoordinateReferenceSystem()));
//		DebriefLiteApp.getInstance().updateProjectionArea();
//		System.out.println("After undo:"+DebriefLiteApp.getInstance().getProjectionArea());
//	}
	
}
