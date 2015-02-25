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
package com.planetmayo.debrief.satc_rcp.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SATC_Perspective implements IPerspectiveFactory {
	private static final String HARNESS_VIEW = "com.planetmayo.debrief.satc_rcp.views.TestHarnessView";
	private static final String MAINTAIN_VIEW = "com.planetmayo.debrief.satc_rcp.views.MaintainContributionsView";
	private static final String STATES_VIEW = "com.planetmayo.debrief.satc_rcp.views.TrackStatesView";
	private static final String SPATIAL_VIEW = "com.planetmayo.debrief.satc_rcp.views.SpatialView";
	
	public void createInitialLayout(IPageLayout layout) {
		// keep the editor open - once integrated it will contain the plot
		layout.setEditorAreaVisible(true);
		
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		
		final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.4f, editorArea);
		topLeft.addView(HARNESS_VIEW);

		final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.4f, HARNESS_VIEW);
		bottomLeft.addView(MAINTAIN_VIEW);

		// lower spatial panel
		final IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.RIGHT, 0.3f, editorArea);
		bottomRight.addPlaceholder(SPATIAL_VIEW);
		bottomLeft.addView(IPageLayout.ID_OUTLINE);

		// upper states panel
		final IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.TOP, 0.4f, SPATIAL_VIEW);
		topRight.addView(STATES_VIEW);
		topRight.addView(IPageLayout.ID_PROP_SHEET);
		
		// ok - try to show the Debrief shotrcuts (will fail in pure SATC)
		layout.addActionSet("org.mwc.debrief.core");		
	}
}