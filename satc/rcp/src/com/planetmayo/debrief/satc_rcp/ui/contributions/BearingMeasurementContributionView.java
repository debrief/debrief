package com.planetmayo.debrief.satc_rcp.ui.contributions;

import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class BearingMeasurementContributionView extends AnalystContributionView
{

	private BaseContribution contribution;
	private DataBindingContext context;
	private PropertyChangeListener titleChangeListener;
	
	private Scale errorSlider;
	private Label errorLabel;

	public BearingMeasurementContributionView(Composite parent,
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
				new PrefixSuffixLabelConverter(Object.class, " Measurements"),
				new PrefixSuffixLabelConverter(Object.class, "+/- ", " degs"));
		bindCommonDates(context, contribution);

		IObservableValue errorValue = BeansObservables.observeValue(
				contribution, BearingMeasurementContribution.BEARING_ERROR);
		IObservableValue errorLabelValue = WidgetProperties.text().observe(
				errorLabel);		
		IObservableValue errorSliderValue = WidgetProperties.selection().observe(
				errorSlider);
		context.bindValue(errorSliderValue, errorValue);
		context.bindValue(errorLabelValue, errorValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(double.class,
						"Error: +/- ", " degs")));
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
	public void dispose()
	{
		super.dispose();
		contribution.removePropertyChangeListener(BaseContribution.NAME,
				titleChangeListener);
		context.dispose();
	}

	@Override
	protected void initializeWidgets()
	{
		titleChangeListener = attachTitleChangeListener(contribution,
				"Bearing Forecast - ");
	}
}
