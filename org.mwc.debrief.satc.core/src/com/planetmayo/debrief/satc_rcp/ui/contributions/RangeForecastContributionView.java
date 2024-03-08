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
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
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
import com.planetmayo.debrief.satc_rcp.ui.converters.units.MeterToYds;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.UnitConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.YdsToMeter;

import junit.framework.TestCase;

public class RangeForecastContributionView extends BaseContributionView<RangeForecastContribution> {

	/**
	 * quick test for units conversions
	 * 
	 */
	public static class TestConvert extends TestCase {
		public void testBoth() {
			final YdsToMeter y2m = new YdsToMeter();
			final MeterToYds m2y = new MeterToYds();
			assertEquals("yards to meters worked", 1852, y2m.safeConvert(2025.37), 0.01);
			assertEquals("metesr to yds worked", 2025.37, m2y.safeConvert(1852), 0.01);
		}
	}

	public RangeForecastContributionView(final Composite parent, final RangeForecastContribution contribution,
			final IContributions contributions) {
		super(parent, contribution, contributions);
		initUI();
	}

	protected void bindSliderForRange(final DataBindingContext context, final IObservableValue modelValue,
			final Scale slider, final Label label, final Button checkBox, final boolean maxValue) {
		final IObservableValue sliderValue = WidgetProperties.scaleSelection().observe(slider);
		final IObservableValue sliderEnabled = WidgetProperties.enabled().observe(slider);
		final IObservableValue labelValue = WidgetProperties.text().observe(label);

		final double MAX_SELECTABLE_RANGE_YDS = new MeterToYds()
				.safeConvert(new Double(RangeForecastContribution.MAX_SELECTABLE_RANGE_M));

		// here are the original values. We had to change them to allow range out to
		// 100kyds
		// int[] borders = {0, 1000, 3000, 7000, 17000, (int)MAX_SELECTABLE_RANGE_YDS};
		// int[] increments = {50, 100, 200, 500, 1000};
		final int[] borders = { 0, 1000, 3000, 7000, 10000, (int) MAX_SELECTABLE_RANGE_YDS };
		final int[] increments = { 50, 100, 500, 1000, 2000 };
		context.bindValue(sliderValue, modelValue,
				UIUtils.converterStrategy(new ScaleConverterFrom(increments, borders)),
				UIUtils.converterStrategy(new ScaleConverterTo(increments, borders)));
		final double defaultValue = maxValue ? MAX_SELECTABLE_RANGE_YDS : 0;
		context.bindValue(sliderEnabled, modelValue, null, UIUtils.converterStrategy(new NullToBooleanConverter()));
		if (checkBox != null) {
			final IObservableValue checkBoxValue = WidgetProperties.buttonSelection().observe(checkBox);
			context.bindValue(checkBoxValue, modelValue,
					UIUtils.converterStrategy(new BooleanToNullConverter<Double>(defaultValue)),
					UIUtils.converterStrategy(new NullToBooleanConverter()));
		}
		final PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(Object.class, "", " Yds",
				new DecimalFormat("0"));
		labelsConverter.setNestedUnitConverter(UnitConverter.RANGE_YDS.getModelToUI());
		context.bindValue(labelValue, modelValue, null, UIUtils.converterStrategy(labelsConverter));
	}

	@Override
	protected void bindValues(final DataBindingContext context) {
		final DecimalFormat rangeFormat = new DecimalFormat("0");
		final PrefixSuffixLabelConverter minMaxConverter = new PrefixSuffixLabelConverter(Object.class, "", "",
				rangeFormat);
		minMaxConverter.setNestedUnitConverter(UnitConverter.RANGE_YDS.getModelToUI());

		final IObservableValue estimateValue = BeanProperties.value(BaseContribution.ESTIMATE).observe(contribution);
		final IObservableValue minRangeValue = BeanProperties.value(RangeForecastContribution.MIN_RANGE)
				.observe(contribution);
		final IObservableValue maxRangeValue = BeanProperties.value(RangeForecastContribution.MAX_RANGE)
				.observe(contribution);
		final MinMaxLimitObservable hardConstraints = new MinMaxLimitObservable(minRangeValue, maxRangeValue,
				minMaxConverter, " Yds");
		final PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(Object.class, "", " Yds",
				rangeFormat);
		labelsConverter.setNestedUnitConverter(UnitConverter.RANGE_YDS.getModelToUI());
		bindCommonHeaderWidgets(context, hardConstraints, estimateValue, labelsConverter, minMaxConverter);
		bindCommonDates(context);

		bindSliderForRange(context, minRangeValue, minSlider, minLabel, null, false);
		bindSliderForRange(context, maxRangeValue, maxSlider, maxLabel, null, true);
		bindSliderForRange(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox, false);
		bindMaxMinEstimate(estimateValue, minRangeValue, maxRangeValue);
	}

	@Override
	protected String getTitlePrefix() {
		return "Range Forecast - ";
	}

	@Override
	protected void initializeWidgets() {
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
}
