package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.Range1959ForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;

public class Ranging1959ContributionView extends BaseContributionView<Range1959ForecastContribution>
{

	public Ranging1959ContributionView(Composite parent, Range1959ForecastContribution contribution,
			IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
//		DecimalFormat speedFormat = new DecimalFormat("0.0");
//		PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(Object.class, "", " kts", speedFormat);
//		PrefixSuffixLabelConverter minMaxConverter = new PrefixSuffixLabelConverter(Object.class, "", "", speedFormat);
//		minMaxConverter.setNestedUnitConverter(UnitConverter.SPEED_KTS.getModelToUI());		
//		labelsConverter.setNestedUnitConverter(UnitConverter.SPEED_KTS.getModelToUI());
//		
//		IObservableValue estimateValue = BeansObservables.observeValue(contribution, BaseContribution.ESTIMATE);
//		IObservableValue minSpeedValue = BeansObservables.observeValue(contribution, SpeedForecastContribution.MIN_SPEED);
//		IObservableValue maxSpeedValue = BeansObservables.observeValue(contribution, SpeedForecastContribution.MAX_SPEED);
//		
		bindCommonHeaderWidgets(context, null, null, null, null);
		bindCommonDates(context);
//
//		bindSliderLabel(context, minSpeedValue, minSlider, minLabel, labelsConverter, UnitConverter.scale(UnitConverter.SPEED_KTS, 10));
//		bindSliderLabel(context, maxSpeedValue, maxSlider, maxLabel, labelsConverter, UnitConverter.scale(UnitConverter.SPEED_KTS, 10));
//		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, 
//				estimateActiveCheckbox, labelsConverter, new BooleanToNullConverter<Double>(0d), 
//				UnitConverter.scale(UnitConverter.SPEED_KTS, 10));		
//		
//		bindMaxMinEstimate(estimateValue, minSpeedValue, maxSpeedValue);
	}
	
	@Override
	protected void initializeWidgets()
	{
//		maxSlider.setMaximum((int)SpeedForecastContribution.MAX_SPEED_VALUE_KTS * 10);
//		minSlider.setMaximum((int)SpeedForecastContribution.MAX_SPEED_VALUE_KTS * 10);
//		estimateSlider.setMaximum((int)SpeedForecastContribution.MAX_SPEED_VALUE_KTS * 10);
//		maxSlider.setPageIncrement(10);
//		minSlider.setPageIncrement(10);
//		estimateSlider.setPageIncrement(10);
	}

	@Override
	protected String getTitlePrefix()
	{
		return "1959 Ranging Forecast - ";
	}
}
