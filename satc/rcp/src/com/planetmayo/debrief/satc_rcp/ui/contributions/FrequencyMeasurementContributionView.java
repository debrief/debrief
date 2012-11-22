package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc_rcp.ui.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class FrequencyMeasurementContributionView extends BaseContributionView<FrequencyMeasurementContribution>
{
	private Scale errorSlider;
	private Label errorLabel;
	private Button errorActiveCheckbox;	

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
		bindSliderLabelCheckbox(context, errorValue, errorSlider, errorLabel, errorActiveCheckbox, 
				new PrefixSuffixLabelConverter(Object.class, "+/- ", " Hz"), 
				new BooleanToNullConverter<Double>(0d));
	}
	
	@Override
	protected void createLimitAndEstimateSliders()
	{
		UIUtils.createLabel(bodyGroup, "Error: ", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		Composite group = new Composite(bodyGroup, SWT.NONE);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		group.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));
		errorActiveCheckbox = new Button(group, SWT.CHECK);
		errorLabel = UIUtils.createSpacer(group, new GridData(GridData.FILL_HORIZONTAL));
		
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
