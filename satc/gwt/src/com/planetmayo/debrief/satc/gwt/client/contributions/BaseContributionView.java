package com.planetmayo.debrief.satc.gwt.client.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.planetmayo.debrief.satc.gwt.client.ui.ContributionPanelHeader;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

abstract public class BaseContributionView extends Composite implements
		ContributionView, PropertyChangeListener
{

	@UiField
	public ContributionPanelHeader header;

	public BaseContributionView()
	{

	}

	abstract protected BaseContribution getData();

	@Override
	public void initHandlers()
	{
		header.setHandlers(new ValueChangeHandler<Boolean>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event)
			{
				getData().setActive(event.getValue());

			}
		}, new ValueChangeHandler<Integer>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				getData().setWeight(event.getValue());

			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0)
	{

		final String attr = arg0.getPropertyName();

		if (attr.equals(BaseContribution.ESTIMATE))
		{
			header.setEstimateData(getData().getEstimateStr());
		}
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

		// initialise the UI components
		header.setData(contribution.isActive(), contribution.getHardConstraints(),
				contribution.getEstimateStr(), contribution.getWeight());

		// TODO: Akash - this method should only register listeners for the
		// BaseContribution attributes that it knows about. The course-specific
		// ones should be declared in CourseForecastContribution

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
