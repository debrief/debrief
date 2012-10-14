package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class ContributionPanelHeader extends Composite {

	private static ContributionPanelHeaderUiBinder uiBinder = GWT
			.create(ContributionPanelHeaderUiBinder.class);

	interface ContributionPanelHeaderUiBinder extends
			UiBinder<Widget, ContributionPanelHeader> {
	}

	public ContributionPanelHeader() {
		initWidget(uiBinder.createAndBindUi(this));
		header.setCellWidth(active, "20%");
		header.setCellWidth(estimate, "30%");
		header.setCellWidth(hardConstraints, "30%");
		header.setCellWidth(weighting, "20%");
	}

	@UiField
	HorizontalPanel header;

	@UiField
	CheckBox active;

	@UiField
	InlineLabel estimate;

	@UiField
	InlineLabel hardConstraints;

	@UiField
	NumberSpinner weighting;

	@UiHandler("active")
	void onClick(ClickEvent e) {
		e.stopPropagation();
	}

	public void setActive(boolean value) {
		active.setValue(value);
	}

	public void setEstimate(String value) {
		estimate.setText(value);
	}

	public void setHardConstraints(String value) {
		hardConstraints.setText(value);
	}

	public void setWeighting(int value) {
		weighting.setValue(value);
	}

	public void setData(boolean active, String hardConstraints, int weight) {
		this.active.setValue(active);
		this.hardConstraints.setText(hardConstraints);
		this.weighting.setValue(weight);
		
	}
}
