package com.planetmayo.debrief.satc_rcp.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SATC_Perspective implements IPerspectiveFactory {
	private static final String HARNESS_VIEW = "com.planetmayo.debrief.satc_rcp.views.TestHarnessView";
	private static final String MAINTAIN_VIEW = "com.planetmayo.debrief.satc_rcp.views.MaintainContributionsView";
	private static final String STATES_VIEW = "com.planetmayo.debrief.satc_rcp.views.TrackStatesView";
	private static final String SPATIAL_VIEW = "com.planetmayo.debrief.satc_rcp.views.SpatialView";
	
	public static final String LAYER_MANAGER = "org.mwc.cmap.layer_manager.views.LayerManagerView";

	
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
		bottomRight.addView(SPATIAL_VIEW);
		bottomRight.addView(LAYER_MANAGER);

		// upper states panel
		final IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.TOP, 0.4f, SPATIAL_VIEW);
		topRight.addView(STATES_VIEW);
		topRight.addView(IPageLayout.ID_PROP_SHEET);

		
		// make all of teh views closeable
		layout.getViewLayout(SPATIAL_VIEW).setCloseable(true);
		layout.getViewLayout(MAINTAIN_VIEW).setCloseable(true);
		layout.getViewLayout(HARNESS_VIEW).setCloseable(true);
		layout.getViewLayout(STATES_VIEW).setCloseable(true);

		layout.getViewLayout(SPATIAL_VIEW).setMoveable(true);
		layout.getViewLayout(MAINTAIN_VIEW).setMoveable(true);
		layout.getViewLayout(HARNESS_VIEW).setMoveable(true);
		layout.getViewLayout(STATES_VIEW).setMoveable(true);
		
		// ok - try to show the Debrief shotrcuts (will fail in pure SATC)
		layout.addActionSet("org.mwc.debrief.core");		
	}
}