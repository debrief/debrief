package org.mwc.cmap.NarrativeViewer.actions;

import org.mwc.cmap.NarrativeViewer.Column;
import org.mwc.cmap.NarrativeViewer.NarrativeViewer;

public class OpenFilterAction extends AbstractDynamicAction {
	private final NarrativeViewer myViewer;
	private final Column myColumn;

	public OpenFilterAction(final NarrativeViewer viewer, final Column column, final String name){
		myViewer = viewer;
		myColumn = column;
		setText(name);
	}
	
	@Override
	public void refresh() {
		//
	}
	
	@Override
	public void run() {
		myViewer.showFilterDialog(myColumn);
	}

}
