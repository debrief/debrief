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

package org.mwc.debrief.lite.view.actions;

import java.awt.event.ActionEvent;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.PanAction;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;

/**
 * @author Ayesha
 *
 */
public class PanCommandAction extends PanAction implements CommandAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private ToolParent _toolParent;
	private DebriefPanTool _panTool;

	public PanCommandAction(final MapPane mapPane,ToolParent parent) {
		super(mapPane);
		_toolParent = parent;
		_panTool = new DebriefPanTool(_toolParent);
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		getMapPane().setCursorTool(_panTool);
	}

	@Override
	public void commandActivated(final CommandActionEvent e) {
		actionPerformed(e);
	}

	@Override
	public final String toString() {
		return _panTool.toString();
	}
}
