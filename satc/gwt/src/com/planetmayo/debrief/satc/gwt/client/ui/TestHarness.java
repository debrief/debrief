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
public class TestHarness extends Composite {

	private static TestHarnessUiBinder uiBinder = GWT
			.create(TestHarnessUiBinder.class);

	interface TestHarnessUiBinder extends UiBinder<Widget, TestHarness> {
	}

	public TestHarness() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
