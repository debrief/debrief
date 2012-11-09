package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class CourseContributionView extends AnalystContributionView<CourseForecastContribution>
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
		PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(
				Object.class, " \u00B0");
		bindCommonHeaderWidgets(context, labelsConverter);
		bindCommonDates(context);

		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.ESTIMATE);
		IObservableValue estimateLabel = WidgetProperties.text().observe(
				this.estimateLabel);
		context.bindValue(estimateLabel, estimateValue, null,
				UIUtils.converterStrategy(labelsConverter));

		IObservableValue minCourseValue = BeansObservables.observeValue(
				contribution, CourseForecastContribution.MIN_COURSE);
		IObservableValue minCourseSlider = WidgetProperties.selection().observe(
				minSlider);
		IObservableValue minCourseLabel = WidgetProperties.text().observe(minLabel);
		context.bindValue(minCourseSlider, minCourseValue);
		context.bindValue(minCourseLabel, minCourseValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(int.class, "min: ",
						" \u00B0")));

		IObservableValue maxCourseValue = BeansObservables.observeValue(
				contribution, CourseForecastContribution.MAX_COURSE);
		IObservableValue maxCourseSlider = WidgetProperties.selection().observe(
				maxSlider);
		IObservableValue maxCourseLabel = WidgetProperties.text().observe(maxLabel);
		context.bindValue(maxCourseSlider, maxCourseValue);
		context.bindValue(maxCourseLabel, maxCourseValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(int.class, "max: ",
						" \u00B0")));

		IObservableValue estimateSliderValue = WidgetProperties.selection()
				.observe(estimateSlider);
		IObservableValue estimateCourseDetailsLabel = WidgetProperties.text()
				.observe(estimateDetailsLabel);
		context.bindValue(estimateSliderValue, estimateValue);
		context.bindValue(estimateCourseDetailsLabel, estimateValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(int.class,
						"Estimate: ", " \u00B0")));
		
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
