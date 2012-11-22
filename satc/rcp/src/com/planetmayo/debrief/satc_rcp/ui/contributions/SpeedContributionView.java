package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;

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

		IObservableValue estimateValue = BeansObservables.observeValue(contribution, BaseContribution.ESTIMATE);
		IObservableValue minSpeedValue = BeansObservables.observeValue(contribution, SpeedForecastContribution.MIN_SPEED);
		IObservableValue maxSpeedValue = BeansObservables.observeValue(contribution, SpeedForecastContribution.MAX_SPEED);
		
		bindSliderLabelCheckbox(context, minSpeedValue, minSlider, minLabel, 
				minActiveCheckbox, labelsConverter, new BooleanToNullConverter<Double>(0d));
		bindSliderLabelCheckbox(context, maxSpeedValue, maxSlider, maxLabel, 
				maxActiveCheckbox, labelsConverter, new BooleanToNullConverter<Double>(SpeedForecastContribution.MAX_SPEED_VALUE_KTS));
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, 
				estimateActiveCheckbox, labelsConverter, new BooleanToNullConverter<Double>(0d));		
		
		//bindMaxMinEstimate(estimateValue, minSpeedValue, maxSpeedValue);
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
