package org.mwc.asset.netasset2;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.mwc.asset.netasset2.connect.ConnectRCPView;
import org.mwc.asset.netasset2.part.PartRCPView;
import org.mwc.asset.netasset2.plot.PlotRCPView;
import org.mwc.asset.netasset2.time.TimeRCPView;

public class Perspective implements IPerspectiveFactory
{

	public void createInitialLayout(IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT,
				0.4f, editorArea);
		topLeft.addView(ConnectRCPView.ID);

		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.6f,
				editorArea);
		right.addView(PlotRCPView.ID);

		// split the time one - so we can insert the track tote
		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout midLeft = layout.createFolder("midLeft", IPageLayout.BOTTOM,
				0.4f, "topLeft");
		midLeft.addView(TimeRCPView.ID);

		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft",
				IPageLayout.BOTTOM, 0.3f, "midLeft");
		bottomLeft.addView(PartRCPView.ID);

	}

}
