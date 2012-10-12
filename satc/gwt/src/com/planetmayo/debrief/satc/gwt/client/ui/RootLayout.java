/**
 * 
 */
package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Akash-Gupta
 * 
 */
public class RootLayout extends Composite {
	
	@UiField
	TestHarness testHarness;
	
	@UiField
	ManageSolutionsView manageSolutionsView;

	private static RootLayoutUiBinder uiBinder = GWT
			.create(RootLayoutUiBinder.class);

	interface RootLayoutUiBinder extends UiBinder<Widget, RootLayout> {
	}

	public RootLayout() {
		initWidget(uiBinder.createAndBindUi(this));
		
		//HERE YOU CAN PUT YOUR LOGIC
		
		// thanks ;-)
	}

}
