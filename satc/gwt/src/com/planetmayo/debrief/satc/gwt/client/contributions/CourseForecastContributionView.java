package com.planetmayo.debrief.satc.gwt.client.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.planetmayo.debrief.satc.gwt.client.ui.ContributionPanelHeader;
import com.planetmayo.debrief.satc.gwt.client.ui.NameWidget;
import com.planetmayo.debrief.satc.gwt.client.ui.Slider2BarWidget;
import com.planetmayo.debrief.satc.gwt.client.ui.StartFinishWidget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

public class CourseForecastContributionView extends Composite implements
		ContributionView, PropertyChangeListener
{

	interface CourseForecastContributionViewUiBinder extends
			UiBinder<Widget, CourseForecastContributionView>
	{
	}

	private static CourseForecastContributionViewUiBinder uiBinder = GWT
			.create(CourseForecastContributionViewUiBinder.class);

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

	private CourseForecastContribution _myData;

	public CourseForecastContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));

		// TODO: Akash - we need to respond to other UI changes aswell.

		// respond to the UI
		max.addBarValueChangedHandler(new BarValueChangedHandler()
		{
			@Override
			public void onBarValueChanged(BarValueChangedEvent event)
			{
				_myData.setMaxCourse(event.getValue());
			}
		});
		
		// TODO: Akash - we also need to listen for changes in the headerfor Active plus weighting

	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0)
	{
		final String attr = arg0.getPropertyName();
		if (attr.equals(CourseForecastContribution.MIN_COURSE))
			min.setData((Integer) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.MAX_COURSE))
			max.setData((Integer) arg0.getNewValue());
		else if (attr.equals(BaseContribution.ESTIMATE))
		{
			estimate.setData((Integer) arg0.getNewValue());
			header.setEstimateData((String) arg0.getNewValue());
		}
		else if (attr.equals(BaseContribution.NAME))
			name.setData((String) arg0.getNewValue());
		else if (attr.equals(BaseContribution.START_DATE))
			startFinish.setStartData((Date) arg0.getNewValue());
		else if (attr.equals(BaseContribution.FINISH_DATE))
			startFinish.setFinishData((Date) arg0.getNewValue());
		else if (attr.equals(BaseContribution.WEIGHT))
			header.setWeightData((Integer) arg0.getNewValue());
		else if (attr.equals(BaseContribution.ACTIVE))
			header.setActiveData((Boolean) arg0.getNewValue());
		else if (attr.equals(BaseContribution.HARD_CONSTRAINTS))
			header.setHardConstraintsData((String) arg0.getNewValue());
	}

	@Override
	public void setData(BaseContribution contribution)
	{

		_myData = (CourseForecastContribution) contribution;

		// initialise the UI components
		min.setData(((CourseForecastContribution) contribution).getMinCourse());
		max.setData(((CourseForecastContribution) contribution).getMaxCourse());
		estimate.setData(((CourseForecastContribution) contribution).getEstimate());
		name.setData(contribution.getName());
		startFinish.setData(contribution.getStartDate(),
				contribution.getFinishDate());
		int estimate = ((CourseForecastContribution) contribution).getEstimate();
		header.setData(contribution.isActive(), contribution.getHardConstraints(),
				"" + estimate, contribution.getWeight());

		contribution.addPropertyChangeListener(
				CourseForecastContribution.MIN_COURSE, this);

		contribution.addPropertyChangeListener(
				CourseForecastContribution.MAX_COURSE, this);

		contribution.addPropertyChangeListener(BaseContribution.ESTIMATE, this);

		contribution.addPropertyChangeListener(BaseContribution.NAME, this);

		contribution.addPropertyChangeListener(BaseContribution.START_DATE, this);

		contribution.addPropertyChangeListener(BaseContribution.FINISH_DATE, this);

		contribution.addPropertyChangeListener(BaseContribution.WEIGHT, this);

		contribution.addPropertyChangeListener(BaseContribution.ACTIVE, this);

		contribution.addPropertyChangeListener(BaseContribution.HARD_CONSTRAINTS,
				this);

	}

}
