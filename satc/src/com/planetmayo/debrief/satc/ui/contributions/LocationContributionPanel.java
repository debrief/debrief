package com.planetmayo.debrief.satc.ui.contributions;

import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.nebula.widgets.formattedtext.DoubleFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.ui.PrefixSuffixLabelConverter;
import com.planetmayo.debrief.satc.ui.UIUtils;

public class LocationContributionPanel extends AnalystContributionPanel
{

	private BaseContribution contribution;
	private DataBindingContext context;
	private PropertyChangeListener titleChangeListener;

	private Scale limitSlider;
	private FormattedText latitude;
	private FormattedText longitude;

	public LocationContributionPanel(Composite parent, BaseContribution contribution)
	{
		super(parent);
		this.contribution = contribution;
		initUI();
	}

	@Override
	protected void bindValues()
	{
		context = new DataBindingContext();
		
		bindCommonHeaderWidgets(context, contribution, null, new PrefixSuffixLabelConverter(String.class, " m"));
		bindCommonDates(context, contribution);
		
		IObservableValue limitSliderValue = WidgetProperties.selection().observe(limitSlider);
		IObservableValue limitLabelValue = WidgetProperties.text().observe(limitLabel);
		IObservableValue limitValue = BeansObservables.observeValue(contribution, LocationForecastContribution.LIMIT);
		context.bindValue(limitSliderValue, limitValue);
		context.bindValue(limitLabelValue, limitValue, null,
				UIUtils.converterStrategy(new PrefixSuffixLabelConverter(int.class, "Limit: ", " m")));
		
		IObservableValue latValue = BeansObservables.observeDetailValue(
				BeansObservables.observeValue(contribution, LocationForecastContribution.ESTIMATE), GeoPoint.LAT, double.class);
		context.bindValue(PojoObservables.observeValue(latitude, "value"), latValue);
		latitude.getControl().addListener(SWT.Modify, new Listener()
		{			
			@Override
			public void handleEvent(Event event)
			{
				LocationForecastContribution temp = ((LocationForecastContribution) contribution);
				GeoPoint geoPoint = new GeoPoint((Double) latitude.getValue(), temp.getEstimate().getLon());
				temp.setEstimate(geoPoint);
			}
		});
		
		IObservableValue lonValue = BeansObservables.observeDetailValue(
				BeansObservables.observeValue(contribution, LocationForecastContribution.ESTIMATE), GeoPoint.LON, double.class);
		context.bindValue(PojoObservables.observeValue(longitude, "value"), lonValue);
		longitude.getControl().addListener(SWT.Modify, new Listener()
		{			
			@Override
			public void handleEvent(Event event)
			{
				LocationForecastContribution temp = ((LocationForecastContribution) contribution);
				GeoPoint geoPoint = new GeoPoint(temp.getEstimate().getLat(), (Double) longitude.getValue());
				temp.setEstimate(geoPoint);
			}
		});		
	}

	@Override
	protected void createLimitAndEstimateSliders()
	{
		limitLabel = UIUtils.createLabel(bodyGroup, "Limit:", new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		limitSlider = new Scale(bodyGroup, SWT.HORIZONTAL);
		limitSlider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout estimateLayout = new GridLayout(2, false);
		estimateLayout.horizontalSpacing = 15;
		Composite estimateComposite = UIUtils.createEmptyComposite(bodyGroup,
				estimateLayout, new GridData(GridData.VERTICAL_ALIGN_FILL
						| GridData.HORIZONTAL_ALIGN_FILL));
		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		UIUtils.createLabel(estimateComposite, "Estimate", gridData);
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
	public void dispose()
	{
		super.dispose();
		contribution.removePropertyChangeListener("name", titleChangeListener);
		context.dispose();
	}

	@Override
	protected void initializeWidgets()
	{
		titleChangeListener = attachTitleChangeListener(contribution,
				"Location Forecast - ");
		}
	
	private static class DecimalConverter implements IConverter {

		@Override
		public Object convert(Object arg0)
		{
			DecimalFormat format = new DecimalFormat("000.00");
			return "-" + format.format(Math.abs((Double) arg0));
		}

		@Override
		public Object getFromType()
		{
			return double.class;
		}

		@Override
		public Object getToType()
		{
			return String.class;
		}
		
		
	}
	
	
}
