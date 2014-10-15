/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
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

public class CourseContributionView extends BaseContributionView<CourseForecastContribution>
{
	public CourseContributionView(Composite parent, CourseForecastContribution contribution,
			final IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	// don't use inheritance here, because of different nature and although code
	// looks very similar it may be headache in future
	@Override
	protected void bindValues(DataBindingContext context)
	{
		PrefixSuffixLabelConverter labelConverter = new PrefixSuffixLabelConverter(
				Object.class, " \u00B0");
		labelConverter.setNestedUnitConverter(UnitConverter.ANGLE_DEG.getModelToUI());
		
		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.ESTIMATE);
		IObservableValue minCourseValue = BeansObservables.observeValue(
				contribution, CourseForecastContribution.MIN_COURSE);
		IObservableValue maxCourseValue = BeansObservables.observeValue(
				contribution, CourseForecastContribution.MAX_COURSE);		
		MinMaxLimitObservable hardConstraints = new MinMaxLimitObservable(minCourseValue, 
				maxCourseValue, new CompoundConverter(UnitConverter.ANGLE_DEG.getModelToUI(), new IntegerConverter()));
		bindCommonHeaderWidgets(context, hardConstraints, estimateValue, labelConverter);
		bindCommonDates(context);
		
		bindSliderLabel(context, minCourseValue, minSlider, minLabel,	labelConverter, UnitConverter.ANGLE_DEG);
		bindSliderLabel(context, maxCourseValue, maxSlider, maxLabel, labelConverter, UnitConverter.ANGLE_DEG);
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox, 
				labelConverter, new BooleanToNullConverter<Double>(0d), UnitConverter.ANGLE_DEG);		
		
		bindMaxMinEstimate(estimateValue, minCourseValue, maxCourseValue);
	}

	@Override
	protected void initializeWidgets()
	{
		minSlider.setMinimum(0);
		minSlider.setMaximum(360);
		maxSlider.setMinimum(0);
		maxSlider.setMaximum(360);
		estimateSlider.setMinimum(0);
		estimateSlider.setMaximum(360);
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Course Forecast - ";
	}
}
