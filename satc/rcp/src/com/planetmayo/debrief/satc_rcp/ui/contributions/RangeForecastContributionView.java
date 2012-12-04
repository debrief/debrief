package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.MinMaxLimitObservable;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;

public class RangeForecastContributionView extends BaseContributionView<RangeForecastContribution>
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

		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.ESTIMATE);
		IObservableValue minSpeedValue = BeansObservables.observeValue(
				contribution, RangeForecastContribution.MIN_RANGE);
		IObservableValue maxSpeedValue = BeansObservables.observeValue(
				contribution, RangeForecastContribution.MAX_RANGE);
		MinMaxLimitObservable hardConstraints = new MinMaxLimitObservable(
				minSpeedValue, maxSpeedValue);
		bindCommonHeaderWidgets(context, hardConstraints, estimateValue, labelsConverter);
		bindCommonDates(context);
		
		bindSliderLabelCheckbox(context, minSpeedValue, minSlider, minLabel, minActiveCheckbox, 
				labelsConverter, new BooleanToNullConverter<Double>(0d), null);
		bindSliderLabelCheckbox(context, maxSpeedValue, maxSlider, maxLabel, maxActiveCheckbox, 
				labelsConverter, new BooleanToNullConverter<Double>(RangeForecastContribution.MAX_SELECTABLE_RANGE_M), null);
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox, 
				labelsConverter, new BooleanToNullConverter<Double>(0d), null);		
		bindMaxMinEstimate(estimateValue, minSpeedValue, maxSpeedValue);
	}


	@Override
	protected void initializeWidgets()
	{
		// give a monster max range
		maxSlider.setMaximum((int) RangeForecastContribution.MAX_SELECTABLE_RANGE_M);
		minSlider.setMaximum((int) RangeForecastContribution.MAX_SELECTABLE_RANGE_M);
		estimateSlider.setMaximum((int) RangeForecastContribution.MAX_SELECTABLE_RANGE_M);
		minSlider.setPageIncrement(100);
		maxSlider.setPageIncrement(100);
		estimateSlider.setPageIncrement(100);

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
