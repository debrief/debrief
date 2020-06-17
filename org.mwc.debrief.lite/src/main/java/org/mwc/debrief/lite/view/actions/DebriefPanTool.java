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
package org.mwc.debrief.lite.view.actions;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.PanTool;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.gui.ViewAction;

import MWC.GUI.ToolParent;

/**
 * @author Ayesha
 *
 */
public class DebriefPanTool extends PanTool{
	
	private ToolParent _toolParent;
	int curIndex;
	private ViewAction actionDetails;
	DebriefPanTool(ToolParent parent){
		_toolParent = parent;
	}
	
	@Override
	public void onMouseReleased(final MapMouseEvent ev) {
		super.onMouseReleased(ev);
		DebriefLiteApp.getInstance().updateProjectionArea();
		
		actionDetails.setNewProjectionArea(DebriefLiteApp.getInstance().getProjectionArea());
		System.out.println("new projectionArea:"+actionDetails.getLastProjectionArea());
		if(_toolParent!=null){
			_toolParent.addActionToBuffer(actionDetails);
		}
	}
	
	@Override
	public void onMousePressed(MapMouseEvent ev) {
		super.onMousePressed(ev);
		actionDetails = new ViewAction(getMapPane());
		actionDetails.setLastProjectionArea(DebriefLiteApp.getInstance().getProjectionArea());
		System.out.println("Last projectionArea:"+actionDetails.getNewProjectionArea());
	}

	
}
