package com.planetmayo.debrief.satc_rcp.ui.contributions;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;

public class StraightLegForecastContributionView extends
		BaseContributionView<StraightLegForecastContribution>
{

	private PropertyChangeListener _colorListener;

	public StraightLegForecastContributionView(Composite parent,
			StraightLegForecastContribution contribution, IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();

		_colorListener = new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				setContributionColor((Color) evt.getNewValue());
			}
		};
		// listen out for colro changes
		contribution.addPropertyChangeListener(
				StraightLegForecastContribution.COLOR, _colorListener);
	}

	@Override
	public void dispose()
	{
		contribution.removePropertyChangeListener(
				StraightLegForecastContribution.COLOR, _colorListener);

		super.dispose();
	}

	@Override
	protected void createLimitAndEstimateSliders()
	{
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Straight Leg Forecast - ";
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		bindCommonHeaderWidgets(context, null, null, null);
		bindCommonDates(context);
	}

	@Override
	protected void initializeWidgets()
	{
		hardConstraintLabel.setText("n/a");
		estimateLabel.setText("n/a");
	}
}
