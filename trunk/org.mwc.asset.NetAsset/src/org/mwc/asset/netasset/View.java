package org.mwc.asset.netasset;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset.view.HolderPane;

public class View extends ViewPart {
	public static final String ID = "org.mwc.asset.NetAsset.view";

	private HolderPane _control;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		_control = new HolderPane(parent, SWT.NONE);
		_control.setActCourse("12.3");
		_control.setActSpeed("2.3");
		_control.setActDepth("1.3");
		
		_control.logEvent(new Date().getTime(), "Event", "Start");
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	//	viewer.getControl().setFocus();
	}
}