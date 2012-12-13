package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class LogPanel extends Composite
{

	private static LogPanelUiBinder uiBinder = GWT.create(LogPanelUiBinder.class);

	@UiField
	HTMLPanel container;

	interface LogPanelUiBinder extends UiBinder<Widget, LogPanel>
	{
	}

	public LogPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

}
