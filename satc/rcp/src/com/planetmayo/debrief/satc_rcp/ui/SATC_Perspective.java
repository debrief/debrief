package com.planetmayo.debrief.satc_rcp.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SATC_Perspective implements IPerspectiveFactory {
	public void createInitialLayout(IPageLayout layout) {
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		layout.addView("com.planetmayo.debrief.satc_rcp.views.TestHarnessView", IPageLayout.LEFT, 0.4f, editorArea);
		layout.addView("com.planetmayo.debrief.satc_rcp.views.MaintainContributionsView", IPageLayout.BOTTOM, 0.3f, "com.planetmayo.debrief.satc_rcp.views.TestHarnessView");

		// lower spatial panel
		layout.addView("com.planetmayo.debrief.satc_rcp.views.SpatialView", IPageLayout.BOTTOM, 0.3f, editorArea);

		// upper states panel
		layout.addView("com.planetmayo.debrief.satc_rcp.views.TrackStatesView", IPageLayout.RIGHT, 0.4f, editorArea);
		
		layout.setEditorAreaVisible(false);
	}
}