/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

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
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.CompoundConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.IntegerConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.MinMaxLimitObservable;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.UnitConverter;

public class ATBForecastContributionView extends BaseContributionView<ATBForecastContribution>
{

	public ATBForecastContributionView(Composite parent, ATBForecastContribution contribution, 
			final IContributions contributions)
	{
		super(parent, contribution, contributions);
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
		MinMaxLimitObservable hardContraints = new MinMaxLimitObservable(minValue, maxValue, 
				new CompoundConverter(UnitConverter.ANGLE_DEG.getModelToUI(), new IntegerConverter()));
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
