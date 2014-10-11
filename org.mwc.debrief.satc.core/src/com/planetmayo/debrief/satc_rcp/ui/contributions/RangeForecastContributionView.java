/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.MinMaxLimitObservable;
import com.planetmayo.debrief.satc_rcp.ui.converters.NullToBooleanConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.ScaleConverterFrom;
import com.planetmayo.debrief.satc_rcp.ui.converters.ScaleConverterTo;

public class RangeForecastContributionView extends BaseContributionView<RangeForecastContribution>
{
	private PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(
			Object.class, " m");

	public RangeForecastContributionView(Composite parent,
			RangeForecastContribution contribution, IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	protected void bindSliderForRange(DataBindingContext context, IObservableValue modelValue,
			Scale slider, Label label, Button checkBox, boolean maxValue) {
		IObservableValue sliderValue = WidgetProperties.selection().observe(slider);
		IObservableValue sliderEnabled = WidgetProperties.enabled().observe(slider);
		IObservableValue labelValue = WidgetProperties.text().observe(label);
		
		int[] borders = {0, 1000, 3000, 7000, 17000, 40000};
		int[] increments = {50, 100, 200, 500, 1000};
		context.bindValue(sliderValue, modelValue,
				UIUtils.converterStrategy(new ScaleConverterFrom(increments, borders)),
				UIUtils.converterStrategy(new ScaleConverterTo(increments, borders))
		);
		double defaultValue = maxValue ? RangeForecastContribution.MAX_SELECTABLE_RANGE_M : 0;
		context.bindValue(sliderEnabled, modelValue, null, 
				UIUtils.converterStrategy(new NullToBooleanConverter()));
		if (checkBox != null) 
		{
			IObservableValue checkBoxValue = WidgetProperties.selection().observe(checkBox);
			context.bindValue(checkBoxValue, modelValue,
					UIUtils.converterStrategy(new BooleanToNullConverter<Double>(defaultValue)),
					UIUtils.converterStrategy(new NullToBooleanConverter()));			
		}
		context.bindValue(labelValue, modelValue, null,
				UIUtils.converterStrategy(labelsConverter));		
	}
	
	@Override
	protected void bindValues(DataBindingContext context)
	{
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
		
		bindSliderForRange(context, minSpeedValue, minSlider, minLabel, null, false);
		bindSliderForRange(context, maxSpeedValue, maxSlider, maxLabel, null, true);
		bindSliderForRange(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox, false);
		bindMaxMinEstimate(estimateValue, minSpeedValue, maxSpeedValue);
	}


	@Override
	protected void initializeWidgets()
	{
		// give a monster max range
		maxSlider.setMaximum(103);
		minSlider.setMaximum(103);
		estimateSlider.setMaximum(103);
		minSlider.setPageIncrement(1);
		maxSlider.setPageIncrement(1);
		estimateSlider.setPageIncrement(1);

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
