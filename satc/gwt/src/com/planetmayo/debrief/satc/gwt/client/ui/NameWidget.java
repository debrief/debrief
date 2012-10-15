package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NameWidget extends Composite {

	private static NameWidgetUiBinder uiBinder = GWT
			.create(NameWidgetUiBinder.class);

	interface NameWidgetUiBinder extends UiBinder<Widget, NameWidget> {
	}

	public NameWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label text;
	
	@UiField TextBox box;

	public void setText(String value) {
		text.setText(value);
	}

	public void setData(String name) {
		box.setText(name);

	}

}
