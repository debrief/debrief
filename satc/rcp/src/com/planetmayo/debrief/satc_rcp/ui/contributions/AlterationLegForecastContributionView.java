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

import com.planetmayo.debrief.satc.model.contributions.AlterationLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class AlterationLegForecastContributionView extends BaseContributionView<AlterationLegForecastContribution>
{
	
	private Label maxCourseLabel;
	private Label maxSpeedLabel;
	
	private Button maxCourseActiveCheckbox;
	private Button maxSpeedActiveCheckbox;
	
	private Scale maxCourseSlider;
	private Scale maxSpeedSlider;
	
	public AlterationLegForecastContributionView(Composite parent, AlterationLegForecastContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}
	
	@Override
	protected void createLimitAndEstimateSliders()
	{
		GridData data = new GridData();		
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		Composite composite = new Composite(bodyGroup, SWT.NONE);
		composite.setLayoutData(data);
		composite.setLayout(UIUtils.createGridLayoutWithoutMargins(3, false));
		UIUtils.createLabel(composite, "Max Course Change: ", new GridData(115, SWT.DEFAULT));
		maxCourseActiveCheckbox = new Button(composite, SWT.CHECK);
		maxCourseLabel = UIUtils.createLabel(composite, "", new GridData(GridData.FILL_HORIZONTAL));

		maxCourseSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		maxCourseSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		composite = new Composite(bodyGroup, SWT.NONE);
		composite.setLayoutData(data);
		composite.setLayout(UIUtils.createGridLayoutWithoutMargins(3, false));
		UIUtils.createLabel(composite, "Max Speed Change: ", new GridData(115, SWT.DEFAULT));
		maxSpeedActiveCheckbox = new Button(composite, SWT.CHECK);
		maxSpeedLabel = UIUtils.createLabel(composite, "", new GridData(GridData.FILL_HORIZONTAL));

		maxSpeedSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		maxSpeedSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
	}
	
	@Override
	protected void initializeWidgets()
	{
		maxCourseSlider.setMinimum(0);
		maxCourseSlider.setMaximum(360);
		
		maxSpeedSlider.setMinimum(0);
		maxSpeedSlider.setMaximum((int)SpeedForecastContribution.MAX_SPEED_VALUE_KTS);
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		bindCommonHeaderWidgets(context, null);
		bindCommonDates(context);
		
		IObservableValue maxCourseValue = BeansObservables.observeValue(contribution, 
				AlterationLegForecastContribution.MAX_COURSE_CHANGE);
		IObservableValue maxSpeedValue = BeansObservables.observeValue(contribution, 
				AlterationLegForecastContribution.MAX_SPEED_CHANGE);
		
		bindSliderLabelCheckbox(context, maxCourseValue, maxCourseSlider, maxCourseLabel, maxCourseActiveCheckbox, 
				new PrefixSuffixLabelConverter(Integer.class, " \u00b0"), 
				new BooleanToNullConverter<Integer>(360));
		bindSliderLabelCheckbox(context, maxSpeedValue, maxSpeedSlider, maxSpeedLabel, maxSpeedActiveCheckbox, 
				new PrefixSuffixLabelConverter(Double.class, " kts"), 
				new BooleanToNullConverter<Double>(SpeedForecastContribution.MAX_SPEED_VALUE_KTS));		
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Alteration Leg Forecast - ";
	}	
}
