package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class RangeForecastContributionView extends AnalystContributionView<RangeForecastContribution>
{

	public RangeForecastContributionView(Composite parent,
			RangeForecastContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(
				Object.class, " m");
		bindCommonHeaderWidgets(context, labelsConverter);
		bindCommonDates(context);

		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.ESTIMATE);
		IObservableValue estimateLabel = WidgetProperties.text().observe(
				this.estimateLabel);
		context.bindValue(estimateLabel, estimateValue, null,
				UIUtils.converterStrategy(labelsConverter));

		IObservableValue minSpeedValue = BeansObservables.observeValue(
				contribution, RangeForecastContribution.MIN_RANGE);
		IObservableValue minSpeedSlider = WidgetProperties.selection().observe(
				minSlider);
		IObservableValue minSpeedLabel = WidgetProperties.text().observe(minLabel);
		context.bindValue(minSpeedSlider, minSpeedValue);
		context.bindValue(minSpeedLabel, minSpeedValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"min: ", " m")));

		IObservableValue maxSpeedValue = BeansObservables.observeValue(
				contribution, RangeForecastContribution.MAX_RANGE);
		IObservableValue maxSpeedSlider = WidgetProperties.selection().observe(
				maxSlider);
		IObservableValue maxSpeedLabel = WidgetProperties.text().observe(maxLabel);
		context.bindValue(maxSpeedSlider, maxSpeedValue);
		context.bindValue(maxSpeedLabel, maxSpeedValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"max: ", "m")));

		IObservableValue estimateSliderValue = WidgetProperties.selection()
				.observe(estimateSlider);
		IObservableValue estimateSpeedDetailsLabel = WidgetProperties.text()
				.observe(estimateDetailsLabel);
		context.bindValue(estimateSliderValue, estimateValue);
		context.bindValue(estimateSpeedDetailsLabel, estimateValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"Estimate: ", " m")));
		
		bindMaxMinEstimate(estimateValue, minSpeedValue, maxSpeedValue);
	}


	@Override
	protected void initializeWidgets()
	{
		// give a monster max range
		maxSlider.setMaximum(RangeForecastContribution.MAX_SELECTABLE_RANGE_M);
		minSlider.setMaximum(RangeForecastContribution.MAX_SELECTABLE_RANGE_M);
		estimateSlider.setMaximum(RangeForecastContribution.MAX_SELECTABLE_RANGE_M);

		startDate.setEnabled(false);
		startTime.setEnabled(false);
		endDate.setEnabled(false);
		endTime.setEnabled(false);
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Range Forecast - ";
	}
}
