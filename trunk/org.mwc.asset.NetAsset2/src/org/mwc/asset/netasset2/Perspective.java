package org.mwc.asset.netasset2;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.4f,
			editorArea);
		topLeft.addView(ConnectView.ID);
		
		// split the time one - so we can insert the track tote
		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout midLeft = layout.createFolder("midLeft", IPageLayout.BOTTOM, 0.4f,
				"topLeft");
		midLeft.addView(TimeView.ID);
		
		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.2f,
			"midLeft");
		bottomLeft.addView(PartView.ID);

		
	}

}
