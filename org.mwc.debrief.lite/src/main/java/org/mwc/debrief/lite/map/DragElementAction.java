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
package org.mwc.debrief.lite.map;

import java.awt.event.ActionEvent;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.MapAction;
import org.geotools.swing.tool.CursorTool;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

public class DragElementAction extends MapAction implements CommandAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 8514990334295403151L;

	private final CursorTool cursorTool;

	public DragElementAction(final MapPane mapPane, final CursorTool _cursorTool) {
		this.cursorTool = _cursorTool;
		super.init(mapPane, null, GenericDragTool.TOOL_TIP, GenericDragTool.ICON_IMAGE);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		getMapPane().setCursorTool(cursorTool);
	}

	@Override
	public void commandActivated(final CommandActionEvent e) {
		actionPerformed(e);

	}

}
