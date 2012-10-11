package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class SATC_GWT_AK implements EntryPoint {

	@Override
	public void onModuleLoad() {
		
		RootPanel.get().add(new RootLayout());

	}

}
