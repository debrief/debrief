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
package org.mwc.debrief.core.creators.chartFeatures;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.debrief.core.editors.painters.TimeDisplayPainter;

import MWC.GUI.Layers;

/**
 * @author snpe
 *
 */
public class InsertTimeDisplay extends AbstractHandler
{

	public Layers getLayers()
	{
		// nope, better generate it
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		return (Layers) editor.getAdapter(Layers.class);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Layers layers = getLayers();
		if (layers != null)
		{
			TimeDisplayPainter layer = new TimeDisplayPainter();;
			layers.addThisLayer(layer);
		}
		return null;
	}

}
