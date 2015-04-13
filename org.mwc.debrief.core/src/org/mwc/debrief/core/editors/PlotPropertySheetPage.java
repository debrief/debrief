/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.editors;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.properties.PropertySheetPage;

public class PlotPropertySheetPage extends PropertySheetPage
{

	private PlotEditor _plotEditor;

	public PlotPropertySheetPage(PlotEditor plotEditor)
	{
		this._plotEditor = plotEditor;
	}

	@Override
	public void setActionBars(IActionBars actionBars)
	{
		super.setActionBars(actionBars);
		actionBars.setGlobalActionHandler(
				ActionFactory.UNDO.getId(), _plotEditor.getUndoAction());
		actionBars.setGlobalActionHandler(
				ActionFactory.REDO.getId(), _plotEditor.getRedoAction());
		actionBars.updateActionBars();
	}

}
