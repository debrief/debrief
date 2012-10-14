package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.gwt.client.ui.ContributionPanelHeader;
import com.planetmayo.debrief.satc.gwt.client.ui.NameWidget;
import com.planetmayo.debrief.satc.gwt.client.ui.Slider2BarWidget;
import com.planetmayo.debrief.satc.gwt.client.ui.StartFinishWidget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

public class CourseForecastContributionView extends Composite implements
		ContributionData {

	private static CourseForecastContributionViewUiBinder uiBinder = GWT
			.create(CourseForecastContributionViewUiBinder.class);

	interface CourseForecastContributionViewUiBinder extends
			UiBinder<Widget, CourseForecastContributionView> {
	}

	public CourseForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}
	
	@UiField
	Slider2BarWidget min;
	
	@UiField
	Slider2BarWidget max;
	
	@UiField
	Slider2BarWidget estimate;
	
	@UiField
	NameWidget name;
	
	@UiField
	StartFinishWidget startFinish;
	
	@UiField
	ContributionPanelHeader header;

	@Override
	public void setData(BaseContribution contribution) {
		min.setData(((CourseForecastContribution)contribution).getMinCourse());
		max.setData(((CourseForecastContribution)contribution).getMaxCourse());
		estimate.setData(((CourseForecastContribution)contribution).getEstimate());
		name.setData(contribution.getName());
		startFinish.setData(contribution.getStartDate(),contribution.getFinishDate());
		header.setData(contribution.isActive(),contribution.getHardConstraints(),contribution.getWeight());
		
	}

}
