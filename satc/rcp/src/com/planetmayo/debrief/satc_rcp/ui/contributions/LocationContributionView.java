package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.nebula.widgets.formattedtext.DoubleFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc_rcp.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

public class LocationContributionView extends BaseContributionView<LocationForecastContribution>
{

	private Label limitLabel;
	private Button limitActiveButton;
	private Scale limitSlider;
	private FormattedText latitude;
	private FormattedText longitude;

	public LocationContributionView(Composite parent,
			LocationForecastContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		bindCommonHeaderWidgets(context, null,
				new PrefixSuffixLabelConverter(String.class, " m"));
		bindCommonDates(context);

		IObservableValue limitSliderValue = WidgetProperties.selection().observe(
				limitSlider);
		IObservableValue limitLabelValue = WidgetProperties.text().observe(
				limitLabel);
		IObservableValue limitValue = BeansObservables.observeValue(contribution,
				LocationForecastContribution.LIMIT);
		context.bindValue(limitSliderValue, limitValue);
		context.bindValue(limitLabelValue, limitValue, null, UIUtils
				.converterStrategy(new PrefixSuffixLabelConverter(int.class, " m")));

		IObservableValue latValue = BeansObservables.observeDetailValue(
				BeansObservables.observeValue(contribution, BaseContribution.ESTIMATE),
				GeoPoint.LAT, double.class);
		context
				.bindValue(PojoObservables.observeValue(latitude, "value"), latValue);
		latitude.getControl().addListener(SWT.Modify, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				LocationForecastContribution temp = ((LocationForecastContribution) contribution);
				GeoPoint geoPoint = new GeoPoint((Double) latitude.getValue(), temp
						.getEstimate().getLon());
				temp.setEstimate(geoPoint);
			}
		});

		IObservableValue lonValue = BeansObservables.observeDetailValue(
				BeansObservables.observeValue(contribution, BaseContribution.ESTIMATE),
				GeoPoint.LON, double.class);
		context.bindValue(PojoObservables.observeValue(longitude, "value"),
				lonValue);
		longitude.getControl().addListener(SWT.Modify, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				LocationForecastContribution temp = ((LocationForecastContribution) contribution);
				GeoPoint geoPoint = new GeoPoint(temp.getEstimate().getLat(),
						(Double) longitude.getValue());
				temp.setEstimate(geoPoint);
			}
		});
	}

	@Override
	protected void createLimitAndEstimateSliders()
	{
		UIUtils.createLabel(bodyGroup, "Constraint:", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		Composite composite = new Composite(bodyGroup, SWT.NONE);
		composite.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		limitActiveButton = new Button(composite, SWT.CHECK);		
		limitLabel = UIUtils.createSpacer(composite, new GridData(GridData.FILL_HORIZONTAL));
		limitSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		limitSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		UIUtils.createLabel(bodyGroup, "Estimate", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		GridLayout estimateLayout = new GridLayout(1, false);
		estimateLayout.horizontalSpacing = 15;
		Composite estimateComposite = UIUtils.createEmptyComposite(bodyGroup,
				estimateLayout, new GridData(GridData.VERTICAL_ALIGN_FILL
						| GridData.HORIZONTAL_ALIGN_FILL));
		UIUtils.createLabel(estimateComposite, "lat:", new GridData(
				GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL));
		UIUtils.createLabel(estimateComposite, "lon:", new GridData(
				GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL));

		Composite estimateGroup = UIUtils.createEmptyComposite(bodyGroup,
				new RowLayout(SWT.VERTICAL), new GridData(
						GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		latitude = new FormattedText(estimateGroup);
		latitude.setFormatter(new DoubleFormatter("-##0.00"));
		latitude.getControl().setLayoutData(new RowData(100, SWT.DEFAULT));
		longitude = new FormattedText(estimateGroup);
		longitude.setFormatter(new DoubleFormatter("-##0.00"));
		longitude.getControl().setLayoutData(new RowData(100, SWT.DEFAULT));
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Location Forecast - ";
	}
}
