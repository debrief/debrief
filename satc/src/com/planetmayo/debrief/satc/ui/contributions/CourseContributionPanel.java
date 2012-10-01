package com.planetmayo.debrief.satc.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.DateAndTimeObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc.ui.UIUtils;

public class CourseContributionPanel extends AnalystContributionPanel {
	
	private CourseForecastContribution contribution;
	
	public CourseContributionPanel(Composite parent, CourseForecastContribution contribution) {
		super(parent);
		this.contribution = contribution;
		initUI();
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

	// don't use inheritance here, because of different nature and although code looks very similar it may be headache in future
	@Override
	protected void bindValues() {
		DataBindingContext context = new DataBindingContext();
		
		IObservableValue activeValue = BeansObservables.observeValue(contribution, "active");
		IObservableValue activeButton = WidgetProperties.selection().observe(activeCheckBox);
		context.bindValue(activeButton, activeValue);
		
		IObservableValue hardContraintValue = BeansObservables.observeValue(contribution, "hardConstraints");
		IObservableValue hardContraintLabel = WidgetProperties.text().observe(hardConstraintLabel);
		context.bindValue(hardContraintLabel, hardContraintValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(String.class, " degs")));
		
		IObservableValue estimateValue = BeansObservables.observeValue(contribution, "estimate");
		IObservableValue estimateLabel = WidgetProperties.text().observe(this.estimateLabel);
		context.bindValue(estimateLabel, estimateValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(int.class, " degs")));
		
		IObservableValue startDateValue = BeansObservables.observeValue(contribution, "startDate");
		IObservableValue startDateWidget = WidgetProperties.selection().observe(startDate);
		IObservableValue startTimeWidget = WidgetProperties.selection().observe(startTime);
		context.bindValue(new DateAndTimeObservableValue(startDateWidget, startTimeWidget), startDateValue);		

		IObservableValue endDateValue = BeansObservables.observeValue(contribution, "finishDate");
		IObservableValue endDateWidget = WidgetProperties.selection().observe(endDate);
		IObservableValue endTimeWidget = WidgetProperties.selection().observe(endTime);
		context.bindValue(new DateAndTimeObservableValue(endDateWidget, endTimeWidget), endDateValue);	
		
		IObservableValue weightValue = BeansObservables.observeValue(contribution, "weight");
		IObservableValue weightWidget = WidgetProperties.selection().observe(weightSpinner);
		context.bindValue(weightWidget, weightValue);
		
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

		context.bindValue(estimateSliderValue, estimateValue);
		context.bindValue(estimateCourseDetailsLabel, estimateValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(int.class, "Estimate: ", " degs")));		
		
	}
}
