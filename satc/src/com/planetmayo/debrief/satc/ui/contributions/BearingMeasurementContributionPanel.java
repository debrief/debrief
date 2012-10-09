package com.planetmayo.debrief.satc.ui.contributions;

import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc.ui.UIUtils;

public class BearingMeasurementContributionPanel extends AnalystContributionPanel
{

	private BaseContribution contribution;
	private DataBindingContext context;
	private PropertyChangeListener titleChangeListener;

	public BearingMeasurementContributionPanel(Composite parent,
			BaseContribution contribution)
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
				new PrefixSuffixLabelConverter(Object.class, " degs"));
		bindCommonDates(context, contribution);

		IObservableValue minSpeedValue = BeansObservables.observeValue(
				contribution, BearingMeasurementContribution.BEARING_ERROR);
		IObservableValue minSpeedSlider = WidgetProperties.selection().observe(
				minSlider);
		IObservableValue minSpeedLabel = WidgetProperties.text().observe(minLabel);
		context.bindValue(minSpeedSlider, minSpeedValue);
		context.bindValue(minSpeedLabel, minSpeedValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"error: ", " degs")));
	}

	@Override
	public void dispose()
	{
		super.dispose();
		contribution.removePropertyChangeListener(BaseContribution.NAME, titleChangeListener);
		context.dispose();
	}

	@Override
	protected void initializeWidgets()
	{
		titleChangeListener = attachTitleChangeListener(contribution,
				"Bearing Forecast - ");
	}
}
