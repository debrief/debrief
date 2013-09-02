package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;

public class StraightLegForecastContributionView extends BaseContributionView<StraightLegForecastContribution>
{
	
	public StraightLegForecastContributionView(Composite parent, StraightLegForecastContribution contribution,
			IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
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
