package org.mwc.cmap.NarrativeViewer.actions;

import org.mwc.cmap.NarrativeViewer.Column;
import org.mwc.cmap.NarrativeViewer.NarrativeViewer;

public class OpenFilterAction extends AbstractDynamicAction {
	private final NarrativeViewer myViewer;
	private final Column myColumn;

	public OpenFilterAction(NarrativeViewer viewer, Column column, String name){
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
