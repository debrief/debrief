package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.gwt.client.Gwt;
import com.planetmayo.debrief.satc.gwt.client.event.ErrorEvent;
import com.planetmayo.debrief.satc.gwt.client.event.ErrorEventHandler;

public class ErrorPanelView extends Composite implements ErrorEventHandler {

	private static ErrorPanelViewUiBinder uiBinder = GWT
			.create(ErrorPanelViewUiBinder.class);

	@UiField
	HTMLPanel errorPanel;

	interface ErrorPanelViewUiBinder extends UiBinder<Widget, ErrorPanelView> {
	}

	public ErrorPanelView() {
		initWidget(uiBinder.createAndBindUi(this));
		Gwt.getInstance().getEventBus().addHandler(ErrorEvent.TYPE, this);
	}

	@Override
	public void error(ErrorEvent event) {
		errorPanel.clear();
		if (event.getErrors() == null) {
			errorPanel.setVisible(false);
			return;
		}
		for (String error : event.getErrors()) {
			errorPanel.setVisible(true);
			errorPanel.add(new Label(error));
		}

	}

}
