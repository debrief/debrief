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
package org.mwc.cmap.naturalearth.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.wrapper.NELayer;

import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class InsertNaturalEarth extends AbstractHandler
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
		// check if we have a data path, and check it exists
		if (!NELayer.hasGoodPath())
		{
			System.err.println("Don't have good path assigned");
		}
		else
		{
			Layers layers = getLayers();
			if (layers != null)
			{
				//
				NELayer ne = new NELayer(Activator.getDefault().getDefaultStyleSet());
				layers.addThisLayer(ne);
			}
		}

		return null;
	}

}
