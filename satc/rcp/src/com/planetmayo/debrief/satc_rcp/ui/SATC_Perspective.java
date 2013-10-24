package com.planetmayo.debrief.satc_rcp.ui;

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
		layout.addView(HARNESS_VIEW, IPageLayout.LEFT, 0.4f, editorArea);
		layout.addView(MAINTAIN_VIEW, IPageLayout.BOTTOM, 0.4f, HARNESS_VIEW);

		// lower spatial panel
		layout.addView(SPATIAL_VIEW, IPageLayout.RIGHT, 0.3f, editorArea);

		// upper states panel
		layout.addView(STATES_VIEW, IPageLayout.TOP, 0.4f, SPATIAL_VIEW);
		
		// make all of teh views closeable
		layout.getViewLayout(SPATIAL_VIEW).setCloseable(true);
		layout.getViewLayout(MAINTAIN_VIEW).setCloseable(true);
		layout.getViewLayout(HARNESS_VIEW).setCloseable(true);
		layout.getViewLayout(STATES_VIEW).setCloseable(true);

		
	}
}