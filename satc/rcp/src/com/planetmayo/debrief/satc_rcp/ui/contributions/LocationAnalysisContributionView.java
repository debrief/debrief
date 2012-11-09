package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;

public class LocationAnalysisContributionView extends AnalystContributionView<LocationAnalysisContribution>
{

	public LocationAnalysisContributionView(Composite parent,
			LocationAnalysisContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		bindCommonHeaderWidgets(context, 
				new PrefixSuffixLabelConverter(Object.class, " "));
		bindCommonDates(context);

		// IObservableValue minSpeedValue = BeansObservables.observeValue(
		// contribution, BearingMeasurementContribution.BEARING_ERROR);
		// IObservableValue minSpeedSlider = WidgetProperties.selection().observe(
		// minSlider);
		// IObservableValue minSpeedLabel =
		// WidgetProperties.text().observe(minLabel);
		// context.bindValue(minSpeedSlider, minSpeedValue);
		// context.bindValue(minSpeedLabel, minSpeedValue, null, UIUtils
		// .converterStrategy(new PrefixSuffixLabelConverter(double.class,
		// "error: ", " degs")));
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Location Analysis - ";
	}
}
