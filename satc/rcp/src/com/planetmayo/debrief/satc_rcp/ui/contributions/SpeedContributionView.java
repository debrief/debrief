package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class SpeedContributionView extends BaseContributionView<SpeedForecastContribution>
{

	public SpeedContributionView(Composite parent, SpeedForecastContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(
				Object.class, " kts");
		bindCommonHeaderWidgets(context, labelsConverter);
		bindCommonDates(context);

		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.ESTIMATE);
		IObservableValue estimateLabel = WidgetProperties.text().observe(
				this.estimateLabel);
		context.bindValue(estimateLabel, estimateValue, null,
				UIUtils.converterStrategy(labelsConverter));

		IObservableValue minSpeedValue = BeansObservables.observeValue(
				contribution, SpeedForecastContribution.MIN_SPEED);
		IObservableValue minSpeedSlider = WidgetProperties.selection().observe(
				minSlider);
		IObservableValue minSpeedLabel = WidgetProperties.text().observe(minLabel);
		context.bindValue(minSpeedSlider, minSpeedValue);
		context.bindValue(minSpeedLabel, minSpeedValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class, " kts")));

		IObservableValue maxSpeedValue = BeansObservables.observeValue(
				contribution, SpeedForecastContribution.MAX_SPEED);
		IObservableValue maxSpeedSlider = WidgetProperties.selection().observe(
				maxSlider);
		IObservableValue maxSpeedLabel = WidgetProperties.text().observe(maxLabel);
		context.bindValue(maxSpeedSlider, maxSpeedValue);
		context.bindValue(maxSpeedLabel, maxSpeedValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class, " kts")));

		IObservableValue estimateSliderValue = WidgetProperties.selection()
				.observe(estimateSlider);
		IObservableValue estimateSpeedDetailsLabel = WidgetProperties.text()
				.observe(estimateDetailsLabel);
		context.bindValue(estimateSliderValue, estimateValue);
		context.bindValue(estimateSpeedDetailsLabel, estimateValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class, " kts")));
		
		bindMaxMinEstimate(estimateValue, minSpeedValue, maxSpeedValue);
	}
	
	@Override
	protected void initializeWidgets()
	{
		maxSlider.setMaximum((int)SpeedForecastContribution.MAX_SPEED_VALUE_KTS);
		minSlider.setMaximum((int)SpeedForecastContribution.MAX_SPEED_VALUE_KTS);
		estimateSlider.setMaximum((int)SpeedForecastContribution.MAX_SPEED_VALUE_KTS);
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Speed Forecast - ";
	}
}
