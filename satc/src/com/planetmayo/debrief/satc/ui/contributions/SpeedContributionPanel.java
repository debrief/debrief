package com.planetmayo.debrief.satc.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.DateAndTimeObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc.ui.UIUtils;

public class SpeedContributionPanel extends AnalystContributionPanel {
	
	private SpeedForecastContribution contribution;
	
	public SpeedContributionPanel(Composite parent, SpeedForecastContribution contribution) {
		super(parent, "Speed Forecast");
		this.contribution = contribution;
		initUI();
	}	

	@Override
	protected void initializeWidgets() {
		// min and max speed goes here		
	}

	@Override
	protected void bindValues() {
		DataBindingContext context = new DataBindingContext();
		
		IObservableValue activeValue = BeansObservables.observeValue(contribution, "active");
		IObservableValue activeButton = WidgetProperties.selection().observe(activeCheckBox);
		context.bindValue(activeButton, activeValue);
		
		IObservableValue hardContraintValue = BeansObservables.observeValue(contribution, "hardConstraints");
		IObservableValue hardContraintLabel = WidgetProperties.text().observe(hardConstraintLabel);
		context.bindValue(hardContraintLabel, hardContraintValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(String.class, " kts")));
		
		IObservableValue estimateValue = BeansObservables.observeValue(contribution, "estimate");
		IObservableValue estimateLabel = WidgetProperties.text().observe(this.estimateLabel);
		context.bindValue(estimateLabel, estimateValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(double.class, " kts")));
		
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
		
		IObservableValue minSpeedValue = BeansObservables.observeValue(contribution, "minSpeed");
		IObservableValue minSpeedSlider = WidgetProperties.selection().observe(minSlider);
		IObservableValue minSpeedLabel = WidgetProperties.text().observe(minLabel);
		IObservableValue esimateSliderMin = WidgetProperties.minimum().observe(estimateSlider);
		IObservableValue maxSliderMin = WidgetProperties.minimum().observe(maxSlider);
		context.bindValue(minSpeedSlider, minSpeedValue);
		context.bindValue(esimateSliderMin, minSpeedValue);
		context.bindValue(maxSliderMin, minSpeedValue);
		context.bindValue(minSpeedLabel, minSpeedValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(double.class, "min: ", " kts")));
		
		IObservableValue maxSpeedValue = BeansObservables.observeValue(contribution, "maxSpeed");
		IObservableValue maxSpeedSlider = WidgetProperties.selection().observe(maxSlider);
		IObservableValue maxSpeedLabel = WidgetProperties.text().observe(maxLabel);
		IObservableValue esimateSliderMax = WidgetProperties.maximum().observe(estimateSlider);
		IObservableValue minSliderMax = WidgetProperties.maximum().observe(minSlider);
		context.bindValue(maxSpeedSlider, maxSpeedValue);
		context.bindValue(esimateSliderMax, maxSpeedValue);
		context.bindValue(minSliderMax, maxSpeedValue);
		context.bindValue(maxSpeedLabel, maxSpeedValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(double.class, "max: ", " kts")));
		
		IObservableValue estimateSliderValue = WidgetProperties.selection().observe(estimateSlider);
		IObservableValue estimateSpeedDetailsLabel = WidgetProperties.text().observe(estimateDetailsLabel);

		context.bindValue(estimateSliderValue, estimateValue);
		context.bindValue(estimateSpeedDetailsLabel, estimateValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(double.class, "Estimate: ", " kts")));
	}
}
