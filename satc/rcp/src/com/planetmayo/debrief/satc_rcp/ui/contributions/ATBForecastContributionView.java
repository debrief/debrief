package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;

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
		bindCommonHeaderWidgets(context, labelConverter);
		bindCommonDates(context);
		
		IObservableValue minValue = BeansObservables.observeValue(contribution, ATBForecastContribution.MIN_ANGLE);
		IObservableValue maxValue = BeansObservables.observeValue(contribution, ATBForecastContribution.MAX_ANGLE);
		IObservableValue estimateValue = BeansObservables.observeValue(contribution, ATBForecastContribution.ESTIMATE);
		
		bindSliderLabelCheckbox(context, minValue, minSlider, minLabel, minActiveCheckbox, 
				labelConverter, new BooleanToNullConverter<Integer>(0));
		bindSliderLabelCheckbox(context, maxValue, maxSlider, maxLabel, maxActiveCheckbox, 
				labelConverter, new BooleanToNullConverter<Integer>(360));
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox, 
				labelConverter, new BooleanToNullConverter<Integer>(0));
		
		//bindMaxMinEstimate(estimateValue, minValue, maxValue);
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
