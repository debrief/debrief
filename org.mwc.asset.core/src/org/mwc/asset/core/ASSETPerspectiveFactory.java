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

package org.mwc.asset.core;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.mwc.cmap.core.CorePlugin;

/**
 * @author ian.mayo
 *
 */
public class ASSETPerspectiveFactory implements IPerspectiveFactory {

	/**
	 * @param layout
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void createInitialLayout(final IPageLayout layout) {
		// Get the editor area.
		final String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.4f, editorArea);
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);

		// split the time one - so we can insert the track tote
		// Top left: Resource Navigator view and Bookmarks view placeholder
		// // 0.2f, "topLeft");

		// Bottom left: Outline view and Property Sheet view
		final IFolderLayout upperMidLeft = layout.createFolder("upperMidLeft", IPageLayout.BOTTOM, 0.2f, "topLeft");
		upperMidLeft.addView(ASSETPlugin.SCENARIO_CONTROLLER2);
		upperMidLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		upperMidLeft.addPlaceholder(CorePlugin.OVERVIEW_PLOT);
		upperMidLeft.addPlaceholder(CorePlugin.POLYGON_EDITOR);
		upperMidLeft.addPlaceholder(ASSETPlugin.SENSOR_MONITOR);

		// Bottom left: Outline view and Property Sheet view
		final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.6f, "upperMidLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);

		// bottom: placeholder for the xyplot
		final IPlaceholderFolderLayout bottomPanel = layout.createPlaceholderFolder("bottom", IPageLayout.BOTTOM, 0.6f,
				editorArea);
		bottomPanel.addPlaceholder(CorePlugin.XY_PLOT + ":*");
		bottomPanel.addPlaceholder(CorePlugin.LIVE_DATA_MONITOR);
		bottomPanel.addPlaceholder(IPageLayout.ID_TASK_LIST);

		// and our view shortcuts
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(CorePlugin.BULK_NARRATIVE_VIEWER);
		layout.addShowViewShortcut(CorePlugin.OVERVIEW_PLOT);
		layout.addShowViewShortcut(ASSETPlugin.SCENARIO_CONTROLLER2);
		layout.addShowViewShortcut(ASSETPlugin.VESSEL_MONITOR);
		layout.addShowViewShortcut(ASSETPlugin.SENSOR_MONITOR);

		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		layout.addShowViewShortcut(CorePlugin.LIVE_DATA_MONITOR);

		// and the error log
		layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

		// hey - try to add the 'new plot' to the New menu
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		// layout.addNewWizardShortcut("org.mwc.debrief.core.wizards.NewPlotWizard");

		// ok - make sure the debrief action sets are visible
		layout.addActionSet("org.mwc.debrief.core");
		// layout.addActionSet("org.mwc.debrief.track_shift");
	}

}
