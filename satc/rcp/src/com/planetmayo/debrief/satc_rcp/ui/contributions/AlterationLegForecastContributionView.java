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

import com.planetmayo.debrief.satc.model.contributions.AlterationLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class AlterationLegForecastContributionView extends AnalystContributionView<AlterationLegForecastContribution>
{
	
	private Label maxCourseLabel;
	private Label maxSpeedLabel;
	
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
		maxCourseLabel = UIUtils.createLabel(bodyGroup, "Max Course Change: ", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		maxCourseSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		maxCourseSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		maxSpeedLabel = UIUtils.createLabel(bodyGroup, "Max Speed Change: ", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
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
		IObservableValue maxCourseLabelValue = WidgetProperties.text().observe(maxCourseLabel);
		IObservableValue maxCourseSliderValue = WidgetProperties.selection().observe(maxCourseSlider);
		context.bindValue(maxCourseSliderValue, maxCourseValue);
		context.bindValue(maxCourseLabelValue, maxCourseValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(Integer.class, "Max Course Change: ", " \u00b0")));
		
		IObservableValue maxSpeedValue = BeansObservables.observeValue(contribution, 
				AlterationLegForecastContribution.MAX_SPEED_CHANGE);
		IObservableValue maxSpeedLabelValue = WidgetProperties.text().observe(maxSpeedLabel);
		IObservableValue maxSpeedSliderValue = WidgetProperties.selection().observe(maxSpeedSlider);
		context.bindValue(maxSpeedSliderValue, maxSpeedValue);
		context.bindValue(maxSpeedLabelValue, maxSpeedValue, null, 
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(Double.class, "Max Speed Change: ", " kts")));
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Alteration Leg Forecast - ";
	}	
}
