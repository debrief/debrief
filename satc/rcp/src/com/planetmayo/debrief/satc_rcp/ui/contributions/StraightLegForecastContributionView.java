package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;

public class StraightLegForecastContributionView extends AnalystContributionView<StraightLegForecastContribution>
{
	
	public StraightLegForecastContributionView(Composite parent, StraightLegForecastContribution contribution)
	{
		super(parent, contribution);
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
		bindCommonHeaderWidgets(context, null);
		bindCommonDates(context);
	}
}
