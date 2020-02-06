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
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.PanTool;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

/**
 * @author Ayesha
 *
 */
public class PanCommandAction extends PanAction implements CommandAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PanCommandAction(final MapPane mapPane) {
		super(mapPane);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		getMapPane().setCursorTool(new PanTool() {

			@Override
			public void onMouseReleased(final MapMouseEvent ev) {
				super.onMouseReleased(ev);
				DebriefLiteApp.getInstance().updateProjectionArea();
			}

		});
	}

	@Override
	public void commandActivated(final CommandActionEvent e) {
		actionPerformed(e);
	}
}
