/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.gui;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.MapPane;
import org.mwc.debrief.lite.DebriefLiteApp;

import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldArea;

/**
 * @author Ayesha
 *
 */
public class ViewAction implements Action {

	private WorldArea _lastProjectionArea;
	private WorldArea _newProjectionArea;
	private final MapPane _map;
	private String _actionName;

	public ViewAction(final MapPane map, final String actionName) {
		_map = map;
		_actionName = actionName;
	}

	@Override
	public void execute() {
		resetProjectionArea(_newProjectionArea);
	}

	public WorldArea getLastProjectionArea() {
		return _lastProjectionArea;
	}

	public WorldArea getNewProjectionArea() {
		return _newProjectionArea;
	}

	@Override
	public boolean isRedoable() {
		return true;
	}

	@Override
	public boolean isUndoable() {
		return true;
	}

	public void resetProjectionArea(final WorldArea projectionArea) {
		// System.out.println("Setting projection area to:"+projectionArea);
		final ReferencedEnvelope bounds = MapUtils.convertToPaneArea(projectionArea,
				_map.getMapContent().getCoordinateReferenceSystem());
		_map.getMapContent().getViewport().setBounds(bounds);
		// force repaint
		final ReferencedEnvelope paneArea = _map.getDisplayArea();
		_map.setDisplayArea(paneArea);

		DebriefLiteApp.getInstance().getProjection().setDataArea(projectionArea);
		DebriefLiteApp.getInstance().updateProjectionArea();
	}

	public void setActionName(final String _actionName) {
		this._actionName = _actionName;
	}

	public void setLastProjectionArea(final WorldArea lastProjectionArea) {
		_lastProjectionArea = lastProjectionArea;
	}

	public void setNewProjectionArea(final WorldArea newProjectionArea) {
		_newProjectionArea = newProjectionArea;
	}

	@Override
	public String toString() {
		final String res = _actionName;
		return res;
	}

	@Override
	public void undo() {
		resetProjectionArea(_lastProjectionArea);
	}

}
