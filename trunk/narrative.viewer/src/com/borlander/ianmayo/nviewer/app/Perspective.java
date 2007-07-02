package com.borlander.ianmayo.nviewer.app;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		IFolderLayout folder = layout.createFolder("root", IPageLayout.TOP, 0.5f, editorArea);

		folder.addView(NViewerView.VIEW_ID);
		layout.getViewLayout(NViewerView.VIEW_ID).setCloseable(false);
		layout.getViewLayout(NViewerView.VIEW_ID).setMoveable(false);
	}
}
