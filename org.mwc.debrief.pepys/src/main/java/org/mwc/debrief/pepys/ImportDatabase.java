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
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.debrief.pepys.model.PepysConnectorBridge;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.PostgresDatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
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
		final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
		try {
			DatabaseConnection.loadDatabaseConfiguration(databaseConfiguration, DatabaseConnection.DEFAULT_DATABASE_FILE);

			final HashMap<String, String> category = databaseConfiguration
					.getCategory(DatabaseConnection.CONFIGURATION_TAG);
			if (category != null && category.containsKey(DatabaseConnection.CONFIGURATION_DATABASE_TYPE)) {
				final String databaseType = category.get(DatabaseConnection.CONFIGURATION_DATABASE_TYPE);
				if (databaseType != null) {
					final DatabaseConnection conn;
					switch(databaseType) {
					case DatabaseConnection.POSTGRES:
						conn = new PostgresDatabaseConnection();
						break;
					case DatabaseConnection.SQLITE:					
						conn = new SqliteDatabaseConnection();
						break;					
					default:
						conn = null;
						break;
					}
					if(conn != null) {
						conn.createInstance(databaseConfiguration);
					}
				}
			}
		} catch (IOException | PropertyVetoException e) {
			// Invalid Database Configuration error
			e.printStackTrace();

			final MessageBox messageBox = new MessageBox(shell, SWT.ERROR | SWT.OK);
			messageBox.setMessage(e.toString());
			messageBox.setText("Error in database configuration file.");
			messageBox.open();
		}

		if (DatabaseConnection.getInstance() != null) {
			new PepysImportPresenter(shell, pepysBridge, getChart().getLayers());

			shell.pack();
			shell.open();
		}
	}

}
