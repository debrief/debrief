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
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.debrief.pepys.controller.PepysImportController;
import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.ModelConfiguration;
import org.mwc.debrief.pepys.model.PepysConnectorBridge;
import org.mwc.debrief.pepys.view.AbstractViewSWT;
import org.mwc.debrief.pepys.view.PepysImportView;

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

		final Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay(),
				SWT.APPLICATION_MODAL | SWT.MIN | SWT.CLOSE | SWT.RESIZE | SWT.MAX);
		shell.setMinimumSize(600, 450);

		final AbstractConfiguration configurationModel = new ModelConfiguration();
		final AbstractViewSWT view = new PepysImportView(configurationModel, shell);

		try {
			configurationModel.loadDefaultDatabaseConfiguration();
		} catch (IOException | PropertyVetoException e) {
			// Invalid Database Configuration error
			e.printStackTrace();

			final MessageBox messageBox = new MessageBox(shell, SWT.ERROR | SWT.OK);
			messageBox.setMessage("Debrief has been unable to load the configuration file\n" + e.toString());
			messageBox.setText("Error in database configuration file.");
			messageBox.open();

			return;
		}

		new PepysImportController(shell, configurationModel, view, pepysBridge);

		shell.pack();
		shell.open();
	}

}
