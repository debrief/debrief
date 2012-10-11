package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class BearingMeasurement extends Composite {

	private static CourseForecastUiBinder uiBinder = GWT
			.create(CourseForecastUiBinder.class);

	interface CourseForecastUiBinder extends UiBinder<Widget, BearingMeasurement> {
	}

	public BearingMeasurement() {
		initWidget(uiBinder.createAndBindUi(this));
		header.setCellWidth(active, "20%");
		header.setCellWidth(estimate, "30%");
		header.setCellWidth(hardConstraints, "30%");
		header.setCellWidth(weighting, "20%");
	}
	
	@UiField
	HorizontalPanel header;
	
	@UiHandler("active")
	void onClick(ClickEvent e) {
		e.stopPropagation();
	}
	
	@UiField
	CheckBox active;
	
	@UiField
	InlineLabel estimate;
	
	@UiField
	InlineLabel hardConstraints;
	
	@UiField
	NumberSpinner weighting;

}
