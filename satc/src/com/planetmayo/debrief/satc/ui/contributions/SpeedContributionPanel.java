package com.planetmayo.debrief.satc.ui.contributions;

import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc.ui.UIUtils;

public class SpeedContributionPanel extends AnalystContributionPanel
{

	private SpeedForecastContribution contribution;
	private DataBindingContext context;
	private PropertyChangeListener titleChangeListener;

	public SpeedContributionPanel(Composite parent,
			SpeedForecastContribution contribution)
	{
		super(parent);
		this.contribution = contribution;
		initUI();
	}

	@Override
	protected void bindValues()
	{
		context = new DataBindingContext();

		bindCommonHeaderWidgets(context, contribution,
				new PrefixSuffixLabelConverter(Object.class, " kts"));
		bindCommonDates(context, contribution);

		IObservableValue minSpeedValue = BeansObservables.observeValue(
				contribution, "minSpeed");
		IObservableValue minSpeedSlider = WidgetProperties.selection().observe(
				minSlider);
		IObservableValue minSpeedLabel = WidgetProperties.text().observe(minLabel);
		IObservableValue esimateSliderMin = WidgetProperties.minimum().observe(
				estimateSlider);
		IObservableValue maxSliderMin = WidgetProperties.minimum().observe(
				maxSlider);
		context.bindValue(minSpeedSlider, minSpeedValue);
		context.bindValue(esimateSliderMin, minSpeedValue);
		context.bindValue(maxSliderMin, minSpeedValue);
		context.bindValue(minSpeedLabel, minSpeedValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"min: ", " kts")));

		IObservableValue maxSpeedValue = BeansObservables.observeValue(
				contribution, "maxSpeed");
		IObservableValue maxSpeedSlider = WidgetProperties.selection().observe(
				maxSlider);
		IObservableValue maxSpeedLabel = WidgetProperties.text().observe(maxLabel);
		IObservableValue esimateSliderMax = WidgetProperties.maximum().observe(
				estimateSlider);
		IObservableValue minSliderMax = WidgetProperties.maximum().observe(
				minSlider);
		context.bindValue(maxSpeedSlider, maxSpeedValue);
		context.bindValue(esimateSliderMax, maxSpeedValue);
		context.bindValue(minSliderMax, maxSpeedValue);
		context.bindValue(maxSpeedLabel, maxSpeedValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"max: ", " kts")));

		IObservableValue estimateSliderValue = WidgetProperties.selection()
				.observe(estimateSlider);
		IObservableValue estimateSpeedDetailsLabel = WidgetProperties.text()
				.observe(estimateDetailsLabel);
		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, "estimate");
		context.bindValue(estimateSliderValue, estimateValue);
		context.bindValue(estimateSpeedDetailsLabel, estimateValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"Estimate: ", " kts")));
	}

	@Override
	public void dispose()
	{
		super.dispose();
		contribution.removePropertyChangeListener("name", titleChangeListener);
		context.dispose();
	}

	@Override
	protected void initializeWidgets()
	{
		titleChangeListener = attachTitleChangeListener(contribution,
				"Speed Forecast - ");
	}
}
