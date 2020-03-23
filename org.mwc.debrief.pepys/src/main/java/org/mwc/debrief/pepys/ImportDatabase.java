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

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.debrief.pepys.model.PepysConnectorBridge;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.presenter.PepysImportPresenter;

import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.WorldArea;

public class ImportDatabase extends CoreEditorAction {

	@Override
	protected void execute() {

		final PlainChart theChart = getChart();

		final PepysConnectorBridge pepysBridge = new PepysConnectorBridge() {

			@Override
			public WorldArea getCurrentArea() {
				return new WorldArea(theChart.getCanvas().getProjection().getVisibleDataArea());
			}

			@Override
			public Layers getLayers() {
				return theChart.getLayers();
			}
		};

		final Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		try {
			new SqliteDatabaseConnection().createInstance();
			// new PostgresDatabaseConnection().createInstance();
		} catch (final PropertyVetoException | FileNotFoundException e) {
			e.printStackTrace();
		}

		new PepysImportPresenter(shell, pepysBridge, getChart().getLayers());

		shell.pack();
		shell.open();
	}

}
