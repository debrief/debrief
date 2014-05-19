package org.mwc.cmap.media.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.mwc.cmap.media.views.ImagesView;
import org.mwc.cmap.media.views.TestHarnessView;
import org.mwc.cmap.media.views.VideoPlayerView;

public class PlanetmayoPerspective implements IPerspectiveFactory {

	public final static String ID = "org.mwc.cmap.media.perspective.PlanetmayoPerspective";
	
	@Override
	public void createInitialLayout(final IPageLayout layout) {

		String editorArea = layout.getEditorArea();

		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(VideoPlayerView.ID + ":1");
		topLeft.addPlaceholder(VideoPlayerView.ID + ":*");

		IFolderLayout bottomLeft = layout.createFolder("bottomLeft",
				IPageLayout.BOTTOM, 0.50f, "topLeft");
		bottomLeft.addView(TestHarnessView.ID);

		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
				0.75f, editorArea);
		right.addView(ImagesView.ID + ":1");
		right.addPlaceholder(ImagesView.ID + ":*");
		layout.setEditorAreaVisible(false);
		
		layout.addShowViewShortcut(VideoPlayerView.ID);
		layout.addShowViewShortcut(TestHarnessView.ID);
		layout.addShowViewShortcut(ImagesView.ID);
		layout.addPerspectiveShortcut(ID);
	}

}
