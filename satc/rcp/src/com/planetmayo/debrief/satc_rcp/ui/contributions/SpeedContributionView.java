package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.MinMaxLimitObservable;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.UnitConverter;

public class SpeedContributionView extends BaseContributionView<SpeedForecastContribution>
{

	public SpeedContributionView(Composite parent, SpeedForecastContribution contribution,
			IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(
				Object.class, " kts");
		labelsConverter.setNestedUnitConverter(UnitConverter.SPEED_KTS.getModelToUI());
		IObservableValue estimateValue = BeansObservables.observeValue(contribution, BaseContribution.ESTIMATE);
		IObservableValue minSpeedValue = BeansObservables.observeValue(contribution, SpeedForecastContribution.MIN_SPEED);
		IObservableValue maxSpeedValue = BeansObservables.observeValue(contribution, SpeedForecastContribution.MAX_SPEED);
		
		bindCommonHeaderWidgets(context, new MinMaxLimitObservable(minSpeedValue, maxSpeedValue, 
				UnitConverter.SPEED_KTS), estimateValue, labelsConverter);
		bindCommonDates(context);

		bindSliderLabel(context, minSpeedValue, minSlider, minLabel, labelsConverter, UnitConverter.SPEED_KTS);
		bindSliderLabel(context, maxSpeedValue, maxSlider, maxLabel, labelsConverter, UnitConverter.SPEED_KTS);
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, 
				estimateActiveCheckbox, labelsConverter, new BooleanToNullConverter<Double>(0d), UnitConverter.SPEED_KTS);		
		
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
