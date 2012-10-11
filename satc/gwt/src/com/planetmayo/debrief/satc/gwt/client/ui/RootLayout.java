/**
 * 
 */
package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Akash-Gupta
 * 
 */
public class RootLayout extends Composite {

	private static RootLayoutUiBinder uiBinder = GWT
			.create(RootLayoutUiBinder.class);

	interface RootLayoutUiBinder extends UiBinder<Widget, RootLayout> {
	}

	public RootLayout() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
