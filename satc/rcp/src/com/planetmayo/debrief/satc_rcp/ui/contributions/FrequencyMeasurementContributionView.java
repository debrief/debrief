package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class FrequencyMeasurementContributionView extends AnalystContributionView<FrequencyMeasurementContribution>
{
	private Scale errorSlider;
	private Label errorLabel;

	public FrequencyMeasurementContributionView(Composite parent,
			FrequencyMeasurementContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		bindCommonHeaderWidgets(context, 
				new PrefixSuffixLabelConverter(Object.class, " Measurements"),
				new PrefixSuffixLabelConverter(Object.class, "+/- ", " Hz"));
		bindCommonDates(context);

		IObservableValue errorValue = BeansObservables.observeValue(
				contribution, FrequencyMeasurementContribution.FREQUENCY_ERROR);
		IObservableValue errorLabelValue = WidgetProperties.text().observe(
				errorLabel);		
		IObservableValue errorSliderValue = WidgetProperties.selection().observe(
				errorSlider);
		context.bindValue(errorSliderValue, errorValue);
		context.bindValue(errorLabelValue, errorValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"Error: +/- ", " Hz")));
	}
	
	@Override
	protected void createLimitAndEstimateSliders()
	{
		errorLabel = new Label(bodyGroup, SWT.NONE);
		errorLabel.setText("Error");
		
		errorSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		errorSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	@Override
	protected void initializeWidgets()
	{
		startDate.setEnabled(false);
		startTime.setEnabled(false);
		endDate.setEnabled(false);
		endTime.setEnabled(false);
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Frequency Measurement - ";
	}	
}
