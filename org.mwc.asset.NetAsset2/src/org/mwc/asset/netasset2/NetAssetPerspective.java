/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.netasset2;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.mwc.asset.netasset2.connect.ConnectRCPView;
import org.mwc.asset.netasset2.part.PartRCPView;
import org.mwc.asset.netasset2.plot.PlotRCPView;
import org.mwc.asset.netasset2.sensor2.SensorRCPView2;
import org.mwc.asset.netasset2.time.TimeRCPView;

public class NetAssetPerspective implements IPerspectiveFactory
{

	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		// Get the editor area.
		final String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT,
				0.4f, editorArea);
		topLeft.addView(ConnectRCPView.ID);
		topLeft.addView(SensorRCPView2.ID);


		// Top left: Resource Navigator view and Bookmarks view placeholder
		final IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.6f,
				editorArea);
		right.addView(PlotRCPView.ID);

		// split the time one - so we can insert the track tote
		// Top left: Resource Navigator view and Bookmarks view placeholder
		final IFolderLayout midLeft = layout.createFolder("midLeft", IPageLayout.BOTTOM,
				0.4f, "topLeft");
		midLeft.addView(TimeRCPView.ID);

		// Bottom left: Outline view and Property Sheet view
		final IFolderLayout bottomLeft = layout.createFolder("bottomLeft",
				IPageLayout.BOTTOM, 0.3f, "midLeft");
		bottomLeft.addView(PartRCPView.ID);

	}

}
