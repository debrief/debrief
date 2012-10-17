package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NameWidget extends Composite
{

	interface NameWidgetUiBinder extends UiBinder<Widget, NameWidget>
	{
	}

	private static NameWidgetUiBinder uiBinder = GWT
			.create(NameWidgetUiBinder.class);

	@UiField
	Label text;

	@UiField
	TextBox box;

	public NameWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		box.addValueChangeHandler(handler);
	}

	public void setData(String name)
	{
		box.setText(name);

	}

	public void setText(String value)
	{
		text.setText(value);
	}

}
