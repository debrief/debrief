package org.mwc.cmap.NarrativeViewer.app;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(final IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		final IFolderLayout folder = layout.createFolder("root", IPageLayout.TOP, 0.5f, editorArea);

		folder.addView(NViewerView.VIEW_ID);
		layout.getViewLayout(NViewerView.VIEW_ID).setCloseable(false);
		layout.getViewLayout(NViewerView.VIEW_ID).setMoveable(false);
	}
}
