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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.CompoundConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.IntegerConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.MinMaxLimitObservable;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.UnitConverter;

public class CourseContributionView extends BaseContributionView<CourseForecastContribution> {
	public CourseContributionView(final Composite parent, final CourseForecastContribution contribution,
			final IContributions contributions) {
		super(parent, contribution, contributions);
		initUI();
	}

	// don't use inheritance here, because of different nature and although code
	// looks very similar it may be headache in future
	@Override
	protected void bindValues(final DataBindingContext context) {
		final PrefixSuffixLabelConverter labelConverter = new PrefixSuffixLabelConverter(Object.class, " \u00B0");
		labelConverter.setNestedUnitConverter(UnitConverter.ANGLE_DEG.getModelToUI());

		final IObservableValue estimateValue = BeanProperties.value(BaseContribution.ESTIMATE).observe(contribution);
		final IObservableValue minCourseValue = BeanProperties.value(CourseForecastContribution.MIN_COURSE)
				.observe(contribution);
		final IObservableValue maxCourseValue = BeanProperties.value(CourseForecastContribution.MAX_COURSE)
				.observe(contribution);
		final MinMaxLimitObservable hardConstraints = new MinMaxLimitObservable(minCourseValue, maxCourseValue,
				new CompoundConverter(UnitConverter.ANGLE_DEG.getModelToUI(), new IntegerConverter()));
		bindCommonHeaderWidgets(context, hardConstraints, estimateValue, labelConverter);
		bindCommonDates(context);

		bindSliderLabel(context, minCourseValue, minSlider, minLabel, labelConverter, UnitConverter.ANGLE_DEG);
		bindSliderLabel(context, maxCourseValue, maxSlider, maxLabel, labelConverter, UnitConverter.ANGLE_DEG);
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox,
				labelConverter, new BooleanToNullConverter<Double>(0d), UnitConverter.ANGLE_DEG);

		bindMaxMinEstimate(estimateValue, minCourseValue, maxCourseValue);
	}

	@Override
	protected String getTitlePrefix() {
		return "Course Forecast - ";
	}

	@Override
	protected void initializeWidgets() {
		minSlider.setMinimum(0);
		minSlider.setMaximum(360);
		maxSlider.setMinimum(0);
		maxSlider.setMaximum(360);
		estimateSlider.setMinimum(0);
		estimateSlider.setMaximum(360);
	}
}
