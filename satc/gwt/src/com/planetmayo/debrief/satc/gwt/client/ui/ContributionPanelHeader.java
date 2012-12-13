package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class ContributionPanelHeader extends Composite
{

	interface ContributionPanelHeaderUiBinder extends
			UiBinder<Widget, ContributionPanelHeader>
	{
	}

	private static ContributionPanelHeaderUiBinder uiBinder = GWT
			.create(ContributionPanelHeaderUiBinder.class);

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

	public ContributionPanelHeader()
	{
		initWidget(uiBinder.createAndBindUi(this));
		header.setCellWidth(active, "20%");
		header.setCellWidth(estimate, "30%");
		header.setCellWidth(hardConstraints, "30%");
		header.setCellWidth(weighting, "20%");
	}

	@UiHandler("active")
	void onClick(ClickEvent e)
	{
		e.stopPropagation();
	}

	public void setActive(boolean value)
	{
		active.setValue(value);
	}

	public void setActiveData(boolean active)
	{
		this.active.setValue(active);

	}

	public void setData(boolean active, int weight)
	{
		setActive(active);
		setWeighting(weight);
	}

	public void setEstimate(String value)
	{
		estimate.setText(value);
	}

	public void setEstimateData(String value)
	{
		estimate.setText(value);

	}

	public void setHandlers(ValueChangeHandler<Boolean> activeValueChangeHandler,
			ValueChangeHandler<Integer> weightingValueChangeHandler)
	{
		active.addValueChangeHandler(activeValueChangeHandler);
		weighting.addValueChangeHandler(weightingValueChangeHandler);
	}

	public void setHardConstraints(String value)
	{
		hardConstraints.setText(value);
	}

	public void setHardConstraintsData(String hardConstraints)
	{
		this.hardConstraints.setText(hardConstraints);

	}

	public void setWeightData(Integer weight)
	{
		this.weighting.setValue(weight);

	}

	public void setWeighting(int value)
	{
		weighting.setValue(value);
	}
}
