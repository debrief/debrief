package com.borlander.ianmayo.nviewer.actions;

import com.borlander.ianmayo.nviewer.Column;
import com.borlander.ianmayo.nviewer.NarrativeViewer;

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
