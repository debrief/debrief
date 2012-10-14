package com.planetmayo.debrief.satc.gwt.client.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
		ContributionData, PropertyChangeListener
{

	private static CourseForecastContributionViewUiBinder uiBinder = GWT
			.create(CourseForecastContributionViewUiBinder.class);

	interface CourseForecastContributionViewUiBinder extends
			UiBinder<Widget, CourseForecastContributionView>
	{
	}

	public CourseForecastContributionView()
	{
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
	public void setData(BaseContribution contribution)
	{
		// initialise the UI components
		min.setData(((CourseForecastContribution) contribution).getMinCourse());
		max.setData(((CourseForecastContribution) contribution).getMaxCourse());
		estimate.setData(((CourseForecastContribution) contribution).getEstimate());
		name.setData(contribution.getName());
		startFinish.setData(contribution.getStartDate(),
				contribution.getFinishDate());
		header.setData(contribution.isActive(), contribution.getHardConstraints(),
				contribution.getWeight());

		// ok, now listen for changes in the contribution
		contribution.addPropertyChangeListener(
				CourseForecastContribution.MIN_COURSE, this);

		// and also the base attributes
		contribution.addPropertyChangeListener(BaseContribution.HARD_CONSTRAINTS,
				this);

		// TODO: and the other attributes. Some of them are base, some in
		// course-forecast
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0)
	{
		final String attr = arg0.getPropertyName();
		if (attr.equals(CourseForecastContribution.MIN_COURSE))
			min.setData((Integer) arg0.getNewValue());

		// TODO: extend the if construct to handle changes to the other properties

		// TODO: the header.setData() method will have to be refactored, so that we
		// can set the Active, HardConstraint, EStimate, and WEight individually in
		// their relevant if block

	}

}
