package com.borlander.ianmayo.nviewer.actions;

import com.borlander.ianmayo.nviewer.Column;

public class SwitchColumnVisibilityAction extends AbstractDynamicAction {
	private final Column myColumn;

	public SwitchColumnVisibilityAction(Column column, String name){
		myColumn = column;
		setText(name);
	}
	
	public void refresh(){
		setChecked(myColumn.isVisible());
	}
	
	@Override
	public void run() {
		boolean wasVisible = myColumn.isVisible();
		myColumn.setVisible(!wasVisible);
	}

}
