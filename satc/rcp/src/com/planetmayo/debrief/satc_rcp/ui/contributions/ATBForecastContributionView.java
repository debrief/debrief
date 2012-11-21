package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class ATBForecastContributionView extends BaseContributionView<ATBForecastContribution>
{

	public ATBForecastContributionView(Composite parent, ATBForecastContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		bindCommonHeaderWidgets(context, new PrefixSuffixLabelConverter(Object.class, " \u00B0"));
		bindCommonDates(context);
		
		IObservableValue minValue = BeansObservables.observeValue(contribution, ATBForecastContribution.MIN_ANGLE);
		IObservableValue minLabelValue = WidgetProperties.text().observe(minLabel);
		IObservableValue minSliderValue = WidgetProperties.selection().observe(minSlider);
		context.bindValue(minLabelValue, minValue, null,
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(Integer.class, " \u00B0")));
		context.bindValue(minSliderValue, minValue);
		
		IObservableValue maxValue = BeansObservables.observeValue(contribution, ATBForecastContribution.MAX_ANGLE);
		IObservableValue maxLabelValue = WidgetProperties.text().observe(maxLabel);
		IObservableValue maxSliderValue = WidgetProperties.selection().observe(maxSlider);
		context.bindValue(maxLabelValue, maxValue, null,
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(Integer.class, " \u00B0")));
		context.bindValue(maxSliderValue, maxValue);
		
		IObservableValue estimateValue = BeansObservables.observeValue(contribution, ATBForecastContribution.ESTIMATE);
		IObservableValue estimateLabelValue = WidgetProperties.text().observe(estimateDetailsLabel);
		IObservableValue estimateSliderValue = WidgetProperties.selection().observe(estimateSlider);
		context.bindValue(estimateLabelValue, estimateValue, null,
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(Integer.class, " \u00B0")));
		context.bindValue(estimateSliderValue, estimateValue);
		
		bindMaxMinEstimate(estimateValue, minValue, maxValue);
	}

	@Override
	protected void initializeWidgets()
	{
		minSlider.setMaximum(360);
		maxSlider.setMaximum(360);
		estimateSlider.setMaximum(360);
	}

	@Override
	protected String getTitlePrefix()
	{
		return "ATB Forecast - ";
	}	
}
