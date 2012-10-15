package com.planetmayo.debrief.satc.gwt.client.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

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
		ContributionView, PropertyChangeListener {

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
		// initialise the UI components
		min.setData(((CourseForecastContribution) contribution).getMinCourse());
		max.setData(((CourseForecastContribution) contribution).getMaxCourse());
		estimate.setData(((CourseForecastContribution) contribution)
				.getEstimate());
		name.setData(contribution.getName());
		startFinish.setData(contribution.getStartDate(),
				contribution.getFinishDate());
		header.setData(contribution.isActive(),
				contribution.getHardConstraints(), contribution.getWeight());

		contribution.addPropertyChangeListener(
				CourseForecastContribution.MIN_COURSE, this);

		contribution.addPropertyChangeListener(
				CourseForecastContribution.MAX_COURSE, this);

		contribution.addPropertyChangeListener(
				CourseForecastContribution.ESTIMATE, this);

		contribution.addPropertyChangeListener(CourseForecastContribution.NAME,
				this);

		contribution.addPropertyChangeListener(
				CourseForecastContribution.START_DATE, this);

		contribution.addPropertyChangeListener(
				CourseForecastContribution.FINISH_DATE, this);

		contribution.addPropertyChangeListener(
				CourseForecastContribution.WEIGHT, this);

		contribution.addPropertyChangeListener(
				CourseForecastContribution.ACTIVE, this);

		contribution.addPropertyChangeListener(
				BaseContribution.HARD_CONSTRAINTS, this);

	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		final String attr = arg0.getPropertyName();
		if (attr.equals(CourseForecastContribution.MIN_COURSE))
			min.setData((Integer) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.MAX_COURSE))
			max.setData((Integer) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.ESTIMATE)) {
			estimate.setData((Integer) arg0.getNewValue());
			header.setEstimateData((String) arg0.getNewValue());
		} else if (attr.equals(CourseForecastContribution.NAME))
			name.setData((String) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.START_DATE))
			startFinish.setStartData((Date) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.FINISH_DATE))
			startFinish.setFinishData((Date) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.WEIGHT))
			header.setWeightData((Integer) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.ACTIVE))
			header.setActiveData((Boolean) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.HARD_CONSTRAINTS))
			header.setHardConstraintsData((String) arg0.getNewValue());
	}

}
