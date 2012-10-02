package com.planetmayo.debrief.satc.ui.contributions;

import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc.ui.UIUtils;

public class CourseContributionPanel extends AnalystContributionPanel {
	
	private CourseForecastContribution contribution;
	private DataBindingContext context;
	private PropertyChangeListener titleChangeListener;
	
	public CourseContributionPanel(Composite parent, CourseForecastContribution contribution) {
		super(parent);
		this.contribution = contribution;
		initUI();
	}
	
	@Override
	protected void initializeWidgets() {
		titleChangeListener = attachTitleChangeListener(contribution, "Course Forecast - ");
		
		minSlider.setMinimum(0);
		minSlider.setMaximum(360);
		maxSlider.setMinimum(0);
		maxSlider.setMaximum(360);
		estimateSlider.setMinimum(0);
		estimateSlider.setMaximum(360);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		contribution.removePropertyChangeListener("name", titleChangeListener);
		context.dispose();
	}

	// don't use inheritance here, because of different nature and although code looks very similar it may be headache in future
	@Override
	protected void bindValues() {
		context = new DataBindingContext();
		
		bindCommonHeaderWidgets(context, contribution, new PrefixSuffixLabelConverter(Object.class, " degs"));
		bindCommonDates(context, contribution);
		
		IObservableValue minCourseValue = BeansObservables.observeValue(contribution, "minCourse");
		IObservableValue minCourseSlider = WidgetProperties.selection().observe(minSlider);
		IObservableValue minCourseLabel = WidgetProperties.text().observe(minLabel);
		IObservableValue esimateSliderMin = WidgetProperties.minimum().observe(estimateSlider);
		IObservableValue maxSliderMin = WidgetProperties.minimum().observe(maxSlider);
		context.bindValue(minCourseSlider, minCourseValue);
		context.bindValue(esimateSliderMin, minCourseValue);
		context.bindValue(maxSliderMin, minCourseValue);
		context.bindValue(minCourseLabel, minCourseValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(int.class, "min: ", " degs")));
		
		IObservableValue maxCourseValue = BeansObservables.observeValue(contribution, "maxCourse");
		IObservableValue maxCourseSlider = WidgetProperties.selection().observe(maxSlider);
		IObservableValue maxCourseLabel = WidgetProperties.text().observe(maxLabel);
		IObservableValue esimateSliderMax = WidgetProperties.maximum().observe(estimateSlider);
		IObservableValue minSliderMax = WidgetProperties.maximum().observe(minSlider);
		context.bindValue(maxCourseSlider, maxCourseValue);
		context.bindValue(esimateSliderMax, maxCourseValue);
		context.bindValue(minSliderMax, maxCourseValue);
		context.bindValue(maxCourseLabel, maxCourseValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(int.class, "max: ", " degs")));
		
		IObservableValue estimateSliderValue = WidgetProperties.selection().observe(estimateSlider);
		IObservableValue estimateCourseDetailsLabel = WidgetProperties.text().observe(estimateDetailsLabel);
		IObservableValue estimateValue = BeansObservables.observeValue(contribution, "estimate");
		context.bindValue(estimateSliderValue, estimateValue);
		context.bindValue(estimateCourseDetailsLabel, estimateValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(int.class, "Estimate: ", " degs")));		
		
	}
}
