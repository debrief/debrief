package com.planetmayo.debrief.satc_rcp.ui.contributions;

import junit.framework.TestCase;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.MinMaxLimitObservable;
import com.planetmayo.debrief.satc_rcp.ui.converters.NullToBooleanConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.ScaleConverterFrom;
import com.planetmayo.debrief.satc_rcp.ui.converters.ScaleConverterTo;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.MeterToYds;
import com.planetmayo.debrief.satc_rcp.ui.converters.units.YdsToMeter;

public class RangeForecastContributionView extends BaseContributionView<RangeForecastContribution>
{
	//private PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(
	//		Object.class, " m");
	final String suffix = " Yds";
	private PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(Object.class, "", suffix) {

		@Override
		public Object convert(Object from)
		{
			if (from instanceof Double) {
				int value = new MeterToYds().safeConvert((Double)from).intValue();
				return new Integer(value).toString() + suffix;
			}
			return super.convert(from);
		}
		
	};
	public RangeForecastContributionView(Composite parent,
			RangeForecastContribution contribution, IContributions contributions)
	{
		super(parent, contribution, contributions);
		initUI();
	}

	protected void bindSliderForRange(DataBindingContext context, IObservableValue modelValue,
			Scale slider, Label label, Button checkBox, boolean maxValue) {
		IObservableValue sliderValue = WidgetProperties.selection().observe(slider);
		IObservableValue sliderEnabled = WidgetProperties.enabled().observe(slider);
		IObservableValue labelValue = WidgetProperties.text().observe(label);
		
		double MAX_SELECTABLE_RANGE_YDS = new MeterToYds().safeConvert(new Double(RangeForecastContribution.MAX_SELECTABLE_RANGE_M));
		
		int[] borders = {0, 1000, 3000, 7000, 17000, (int)MAX_SELECTABLE_RANGE_YDS};
		int[] increments = {50, 100, 200, 500, 1000};
		context.bindValue(sliderValue, modelValue,
				UIUtils.converterStrategy(new ScaleConverterFrom(increments, borders)),
				UIUtils.converterStrategy(new ScaleConverterTo(increments, borders))
		);
		double defaultValue = maxValue ? MAX_SELECTABLE_RANGE_YDS : 0;
		context.bindValue(sliderEnabled, modelValue, null, 
				UIUtils.converterStrategy(new NullToBooleanConverter()));
		if (checkBox != null) 
		{
			IObservableValue checkBoxValue = WidgetProperties.selection().observe(checkBox);
			context.bindValue(checkBoxValue, modelValue,
					UIUtils.converterStrategy(new BooleanToNullConverter<Double>(defaultValue)),
					UIUtils.converterStrategy(new NullToBooleanConverter()));			
		}
		context.bindValue(labelValue, modelValue, null,
				UIUtils.converterStrategy(labelsConverter));		
	}
	
	@Override
	protected void bindValues(DataBindingContext context)
	{
		IObservableValue estimateValue = BeansObservables.observeValue(
				contribution, BaseContribution.ESTIMATE);
		IObservableValue minRangeValue = BeansObservables.observeValue(
				contribution, RangeForecastContribution.MIN_RANGE);
		IObservableValue maxRangeValue = BeansObservables.observeValue(
				contribution, RangeForecastContribution.MAX_RANGE);
		MinMaxLimitObservable hardConstraints = new MinMaxLimitObservable(
				minRangeValue, maxRangeValue);
		bindCommonHeaderWidgets(context, hardConstraints, estimateValue, labelsConverter);
		bindCommonDates(context);
		
		bindSliderForRange(context, minRangeValue, minSlider, minLabel, null, false);
		bindSliderForRange(context, maxRangeValue, maxSlider, maxLabel, null, true);
		bindSliderForRange(context, estimateValue, estimateSlider, estimateDetailsLabel, estimateActiveCheckbox, false);
		bindMaxMinEstimate(estimateValue, minRangeValue, maxRangeValue);
	}


	@Override
	protected void initializeWidgets()
	{
		// give a monster max range
		maxSlider.setMaximum(103);
		minSlider.setMaximum(103);
		estimateSlider.setMaximum(103);
		minSlider.setPageIncrement(1);
		maxSlider.setPageIncrement(1);
		estimateSlider.setPageIncrement(1);

		startDate.setEnabled(false);
		startTime.setEnabled(false);
		endDate.setEnabled(false);
		endTime.setEnabled(false);
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Range Forecast - ";
	}
	
	/** quick test for units conversions
	 * 
	 */
	public static class TestConvert extends TestCase
	{
		public void testBoth(){
			YdsToMeter y2m = new YdsToMeter();
			MeterToYds m2y = new MeterToYds();
			assertEquals("yards to meters worked", 1852, y2m.safeConvert(2025.37), 0.01);
			assertEquals("metesr to yds worked", 2025.37, m2y.safeConvert(1852), 0.01);
		}
	}
}
