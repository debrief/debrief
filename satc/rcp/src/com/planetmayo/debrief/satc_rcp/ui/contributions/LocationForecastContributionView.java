package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.GeoPointFormatter;
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
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;
import com.planetmayo.debrief.satc_rcp.ui.converters.BooleanToNullConverter;
import com.planetmayo.debrief.satc_rcp.ui.converters.PrefixSuffixLabelConverter;

public class LocationForecastContributionView extends
		BaseContributionView<LocationForecastContribution>
{
	private static final int MAX_RANGE = 20000;
	private Label limitLabel;
	private Button limitActiveButton;
	private Scale limitSlider;
	private FormattedText latitude;
	private FormattedText longitude;
	private IConverter geoConverter = new IConverter()
	{

		@Override
		public Object getToType()
		{
			return String.class;
		}

		@Override
		public Object getFromType()
		{
			return GeoPoint.class;
		}

		@Override
		public Object convert(Object arg0)
		{
			if (arg0 instanceof GeoPoint)
				return GeoSupport.formatGeoPoint((GeoPoint) arg0);
			return arg0.toString();
		}
	};

	public LocationForecastContributionView(Composite parent,
			LocationForecastContribution contribution)
	{
		super(parent, contribution);
		initUI();
	}

	@Override
	protected void bindValues(DataBindingContext context)
	{
		PrefixSuffixLabelConverter labelsConverter = new PrefixSuffixLabelConverter(
				Object.class, " m");	
		IObservableValue limitValue = BeansObservables.observeValue(contribution,
				LocationForecastContribution.LIMIT);
		IObservableValue locationValue = BeansObservables.observeValue(contribution, 
				LocationForecastContribution.LOCATION);
		bindCommonHeaderWidgets(context, limitValue, locationValue, geoConverter , labelsConverter);
		bindCommonDates(context);

		bindSliderLabelCheckbox(context, limitValue, limitSlider, limitLabel, limitActiveButton, 
				labelsConverter, new BooleanToNullConverter<Double>(0d), null);

		IObservableValue latValue = BeansObservables.observeDetailValue(
				BeansObservables.observeValue(contribution, LocationForecastContribution.LOCATION),
				GeoPoint.LAT, double.class);
		context.bindValue(PojoObservables.observeValue(latitude, "value"), latValue);
		latitude.getControl().addListener(SWT.FocusOut, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				LocationForecastContribution temp = ((LocationForecastContribution) contribution);
				GeoPoint geoPoint = new GeoPoint((Double) latitude.getValue(), temp
						.getLocation().getLon());
				temp.setLocation(geoPoint);
			}
		});

		IObservableValue lonValue = BeansObservables.observeDetailValue(
				BeansObservables.observeValue(contribution, LocationForecastContribution.LOCATION),
				GeoPoint.LON, double.class);
		context.bindValue(PojoObservables.observeValue(longitude, "value"),
				lonValue);
		longitude.getControl().addListener(SWT.FocusOut, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				LocationForecastContribution temp = ((LocationForecastContribution) contribution);
				GeoPoint geoPoint = new GeoPoint(temp.getLocation().getLat(),
						(Double) longitude.getValue());
				temp.setLocation(geoPoint);
			}
		});
	}

	@Override
	protected void createLimitAndEstimateSliders()
	{
		UIUtils.createLabel(bodyGroup, "Range:", new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));
		Composite composite = new Composite(bodyGroup, SWT.NONE);
		composite.setLayout(UIUtils.createGridLayoutWithoutMargins(2, false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		limitActiveButton = new Button(composite, SWT.CHECK);
		limitLabel = UIUtils.createSpacer(composite, new GridData(
				GridData.FILL_HORIZONTAL));
		limitSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		limitSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		limitSlider.setMaximum(MAX_RANGE);

		UIUtils.createLabel(bodyGroup, "Location", new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));
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
		latitude.setFormatter(new GeoPointFormatter(GeoPointFormatter.LAT));
		latitude.getControl().setLayoutData(new RowData(100, SWT.DEFAULT));
		longitude = new FormattedText(estimateGroup);
		longitude.setFormatter(new GeoPointFormatter(GeoPointFormatter.LON));
		longitude.getControl().setLayoutData(new RowData(100, SWT.DEFAULT));
	}

	@Override
	protected String getTitlePrefix()
	{
		return "Location Forecast - ";
	}
}
