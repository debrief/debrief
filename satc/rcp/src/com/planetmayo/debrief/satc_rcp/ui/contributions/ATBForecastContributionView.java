package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.MinMaxLimitObservable;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.UnitConverter;

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
		PrefixSuffixLabelConverter labelConverter = new PrefixSuffixLabelConverter(Object.class, " \u00B0");
		labelConverter.setNestedUnitConverter(UnitConverter.ANGLE_DEG.getModelToUI());
		
		IObservableValue minValue = BeansObservables.observeValue(contribution, ATBForecastContribution.MIN_ANGLE);
		IObservableValue maxValue = BeansObservables.observeValue(contribution, ATBForecastContribution.MAX_ANGLE);
		IObservableValue estimateValue = BeansObservables.observeValue(contribution, ATBForecastContribution.ESTIMATE);
		MinMaxLimitObservable hardContraints = new MinMaxLimitObservable(minValue, maxValue, UnitConverter.ANGLE_DEG);
		bindCommonHeaderWidgets(context, hardContraints,  estimateValue, labelConverter);
		bindCommonDates(context);

		bindSliderLabel(context, minValue, minSlider, minLabel,	labelConverter, UnitConverter.ANGLE_DEG);
		bindSliderLabel(context, maxValue, maxSlider, maxLabel,	labelConverter, UnitConverter.ANGLE_DEG);
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox, 
				labelConverter, new BooleanToNullConverter<Double>(0d), UnitConverter.ANGLE_DEG);
		
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
