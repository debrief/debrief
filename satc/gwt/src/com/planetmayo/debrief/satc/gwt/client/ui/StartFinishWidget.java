package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StartFinishWidget extends Composite {

	private static StartFinishWidgetUiBinder uiBinder = GWT
			.create(StartFinishWidgetUiBinder.class);

	interface StartFinishWidgetUiBinder extends
			UiBinder<Widget, StartFinishWidget> {
	}

	public StartFinishWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
