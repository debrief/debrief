package com.planetmayo.debrief.satc.gwt.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class StartFinishWidget extends Composite {

	private static StartFinishWidgetUiBinder uiBinder = GWT
			.create(StartFinishWidgetUiBinder.class);

	interface StartFinishWidgetUiBinder extends
			UiBinder<Widget, StartFinishWidget> {
	}

	public StartFinishWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	DateBox startDateBox;

	@UiField
	DateBox finishDateBox;

	@UiField
	Label startDateLabel;

	@UiField
	Label finishDateLabel;

	@SuppressWarnings("deprecation")
	public void setLoaded(boolean value) {
		if (value) {
			startDateBox.setVisible(false);
			finishDateBox.setVisible(false);
			startDateLabel.setVisible(true);
			finishDateLabel.setVisible(true);
			// TODO remove when implementing backend
			startDateLabel.setText(new Date().toLocaleString());
			finishDateLabel.setText(new Date().toLocaleString());
		} else {
			startDateBox.setVisible(true);
			finishDateBox.setVisible(true);
			startDateLabel.setVisible(false);
			finishDateLabel.setVisible(false);
		}
	}

	public void setData(Date startDate, Date finishDate) {
		startDateBox.setValue(startDate);
		finishDateBox.setValue(finishDate);
	}

	public void setStartData(Date startDate) {
		startDateBox.setValue(startDate);
	}

	public void setFinishData(Date finishDate) {
		finishDateBox.setValue(finishDate);
	}

}
