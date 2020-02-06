/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc_rcp.ui.contributions;

import java.text.DecimalFormat;

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

public class SpeedContributionView extends BaseContributionView<SpeedForecastContribution> {

	public SpeedContributionView(final Composite parent, final SpeedForecastContribution contribution,
			final IContributions contributions) {
		super(parent, contribution, contributions);
		initUI();
	}

	@Override
	protected void bindValues(final DataBindingContext context) {
		final DecimalFormat speedFormat = new DecimalFormat("0.0");
		final PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(Object.class, "", " kts",
				speedFormat);
		final PrefixSuffixLabelConverter minMaxConverter = new PrefixSuffixLabelConverter(Object.class, "", "",
				speedFormat);
		minMaxConverter.setNestedUnitConverter(UnitConverter.SPEED_KTS.getModelToUI());
		labelsConverter.setNestedUnitConverter(UnitConverter.SPEED_KTS.getModelToUI());

		final IObservableValue estimateValue = BeansObservables.observeValue(contribution, BaseContribution.ESTIMATE);
		final IObservableValue minSpeedValue = BeansObservables.observeValue(contribution,
				SpeedForecastContribution.MIN_SPEED);
		final IObservableValue maxSpeedValue = BeansObservables.observeValue(contribution,
				SpeedForecastContribution.MAX_SPEED);

		bindCommonHeaderWidgets(context, new MinMaxLimitObservable(minSpeedValue, maxSpeedValue, minMaxConverter),
				estimateValue, labelsConverter);
		bindCommonDates(context);

		bindSliderLabel(context, minSpeedValue, minSlider, minLabel, labelsConverter,
				UnitConverter.scale(UnitConverter.SPEED_KTS, 10));
		bindSliderLabel(context, maxSpeedValue, maxSlider, maxLabel, labelsConverter,
				UnitConverter.scale(UnitConverter.SPEED_KTS, 10));
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox,
				labelsConverter, new BooleanToNullConverter<Double>(0d),
				UnitConverter.scale(UnitConverter.SPEED_KTS, 10));

		bindMaxMinEstimate(estimateValue, minSpeedValue, maxSpeedValue);
	}

	@Override
	protected String getTitlePrefix() {
		return "Speed Forecast - ";
	}

	@Override
	protected void initializeWidgets() {
		maxSlider.setMaximum((int) SpeedForecastContribution.MAX_SPEED_VALUE_KTS * 10);
		minSlider.setMaximum((int) SpeedForecastContribution.MAX_SPEED_VALUE_KTS * 10);
		estimateSlider.setMaximum((int) SpeedForecastContribution.MAX_SPEED_VALUE_KTS * 10);
		maxSlider.setPageIncrement(10);
		minSlider.setPageIncrement(10);
		estimateSlider.setPageIncrement(10);
	}
}
