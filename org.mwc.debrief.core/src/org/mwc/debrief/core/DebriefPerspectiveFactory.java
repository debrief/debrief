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
package org.mwc.debrief.core;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.mwc.cmap.core.CorePlugin;

/**
 * @author ian.mayo
 *
 */
public class DebriefPerspectiveFactory implements IPerspectiveFactory
{

	/**
	 * @param layout
	 */
	@SuppressWarnings("deprecation")
	public void createInitialLayout(final IPageLayout layout)
	{
		// Get the editor area.
		final String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f,
			editorArea);
		topLeft.addView(IPageLayout.ID_RES_NAV);
		topLeft.addView(CorePlugin.TIME_CONTROLLER);
		topLeft.addPlaceholder(CorePlugin.TIME_BAR);
		
		// split the time one - so we can insert the track tote
		// Top left: Resource Navigator view and Bookmarks view placeholder
		final IFolderLayout midLeft = layout.createFolder("midLeft", IPageLayout.BOTTOM, 0.3f,
				"topLeft");
		midLeft.addView(IPageLayout.ID_PROP_SHEET);		
		midLeft.addView(CorePlugin.TOTE);
		midLeft.addView(CorePlugin.OVERVIEW_PLOT);
		midLeft.addPlaceholder(CorePlugin.DIS_LISTENER_VIEW);
		midLeft.addPlaceholder(CorePlugin.POLYGON_EDITOR);		
		
		// Bottom left: Outline view and Property Sheet view
		final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.40f,
			"midLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		bottomLeft.addPlaceholder(DebriefPlugin.MULTI_PATH);
		
		// bottom: placeholder for the xyplot
		final IPlaceholderFolderLayout bottomPanel = layout.createPlaceholderFolder("bottom", IPageLayout.BOTTOM, 0.7f, editorArea);
		bottomPanel.addPlaceholder(CorePlugin.XY_PLOT + ":*");
		bottomPanel.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		bottomPanel.addPlaceholder(IPageLayout.ID_TASK_LIST);
		bottomPanel.addPlaceholder(CorePlugin.NARRATIVES2);

		// RIGHT: SATC contributions
		final IPlaceholderFolderLayout right = layout.createPlaceholderFolder("right", IPageLayout.RIGHT, 0.6f,
			editorArea);
		right.addPlaceholder(DebriefPlugin.SATC_MAINTAIN_CONTRIBUTIONS);		
		right.addPlaceholder(CorePlugin.GRID_EDITOR);
		right.addPlaceholder(CorePlugin.STACKED_DOTS);
		right.addPlaceholder(CorePlugin.FREQ_RESIDUALS);
		
		// and our view shortcuts
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(CorePlugin.NARRATIVES2);
		layout.addShowViewShortcut(CorePlugin.TIME_CONTROLLER);
		layout.addShowViewShortcut(CorePlugin.TIME_BAR);
		layout.addShowViewShortcut(CorePlugin.TOTE);
		layout.addShowViewShortcut(CorePlugin.STACKED_DOTS);
		layout.addShowViewShortcut(CorePlugin.FREQ_RESIDUALS);
		layout.addShowViewShortcut(CorePlugin.GRID_EDITOR);
		layout.addShowViewShortcut(CorePlugin.OVERVIEW_PLOT);
		layout.addShowViewShortcut(CorePlugin.DIS_LISTENER_VIEW);
		
		layout.addShowViewShortcut(DebriefPlugin.SENSOR_FUSION);
		layout.addShowViewShortcut(DebriefPlugin.MULTI_PATH);
		layout.addShowViewShortcut(DebriefPlugin.TIME_BAR);
		layout.addShowViewShortcut(DebriefPlugin.SATC_MAINTAIN_CONTRIBUTIONS);
		
		// it via action (so we can populate it)  

		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);

		// and the error log
		layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
		
		// hey - try to add the 'new plot' to the New menu
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.mwc.debrief.core.wizards.NewPlotWizard");
		
		// ok - make sure the debrief action sets are visible
		// The actionSets ext point has been moved to commands/menus
		// See https://www.assembla.com/spaces/Debrief/tickets/517
		// layout.addActionSet("org.mwc.debrief.core");
	}

}
