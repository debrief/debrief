package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;

public class CourseContributionView extends BaseContributionView<CourseForecastContribution>
{
	public CourseContributionView(Composite parent, CourseForecastContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}

	// don't use inheritance here, because of different nature and although code
	// looks very similar it may be headache in future
	@Override
	protected void bindValues(DataBindingContext context)
	{
		PrefixSuffixLabelConverter labelConverter = new PrefixSuffixLabelConverter(
				Object.class, " \u00B0");
		bindCommonHeaderWidgets(context, labelConverter);
		bindCommonDates(context);

		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.ESTIMATE);
		IObservableValue minCourseValue = BeansObservables.observeValue(
				contribution, CourseForecastContribution.MIN_COURSE);
		IObservableValue maxCourseValue = BeansObservables.observeValue(
				contribution, CourseForecastContribution.MAX_COURSE);
		
		bindSliderLabelCheckbox(context, minCourseValue, minSlider, minLabel, minActiveCheckbox, 
				labelConverter, new BooleanToNullConverter<Integer>(0));
		bindSliderLabelCheckbox(context, maxCourseValue, maxSlider, maxLabel, maxActiveCheckbox, 
				labelConverter, new BooleanToNullConverter<Integer>(360));
		bindSliderLabelCheckbox(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox, 
				labelConverter, new BooleanToNullConverter<Integer>(0));		
		
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
