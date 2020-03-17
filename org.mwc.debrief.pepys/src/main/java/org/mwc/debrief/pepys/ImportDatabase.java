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

package org.mwc.debrief.pepys;

import java.util.Enumeration;

import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.debrief.pepys.model.PepysConnectorBridge;
import org.mwc.debrief.pepys.presenter.PepysImportPresenter;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.WorldArea;

public class ImportDatabase extends CoreEditorAction {

	@Override
	protected void execute() {

		final PlainChart theChart = getChart();

		final PepysConnectorBridge pepysBridge = new PepysConnectorBridge() {

			@Override
			public Layers getLayers() {
				return theChart.getLayers();
			}

			@Override
			public WorldArea getCurrentArea() {
				return new WorldArea(theChart.getCanvas().getProjection().getVisibleDataArea());
			}
		};

		final PepysImportPresenter pepysImportPresenter = new PepysImportPresenter(
				PlatformUI.getWorkbench().getModalDialogShellProvider().getShell());
		
		final Layers _layers = theChart.getLayers();
		final Enumeration<Editable> items = _layers.elements();
		while (items.hasMoreElements()) {
			final Editable thisE = items.nextElement();
			System.out.println(thisE.getName());
		}

		System.out.println("Area = " + pepysBridge.getCurrentArea());
	}

}
