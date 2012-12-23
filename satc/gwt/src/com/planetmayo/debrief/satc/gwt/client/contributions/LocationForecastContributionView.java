package com.planetmayo.debrief.satc.gwt.client.contributions;

import java.beans.PropertyChangeEvent;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.planetmayo.debrief.satc.gwt.client.ui.NameWidget;
import com.planetmayo.debrief.satc.gwt.client.ui.Slider2BarWidget;
import com.planetmayo.debrief.satc.gwt.client.ui.StartFinishWidget;
import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.support.SupportServices;

public class LocationForecastContributionView extends BaseContributionView
{

	interface LocationForecastContributionViewUiBinder extends
			UiBinder<Widget, LocationForecastContributionView>
	{
	}

	private static LocationForecastContributionViewUiBinder uiBinder = GWT
			.create(LocationForecastContributionViewUiBinder.class);

	private LocationForecastContribution _myData;

	@UiField
	Slider2BarWidget limit;

	@UiField
	StartFinishWidget startFinish;
	@UiField
	NameWidget name;
	@UiField
	NameWidget estimate;

	public LocationForecastContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));
		initHandlers();
	}

	@Override
	protected BaseContribution getData()
	{
		return _myData;
	}

	@Override
	public void initHandlers()
	{
		super.initHandlers();

		limit.addBarValueChangedHandler(new BarValueChangedHandler()
		{
			@Override
			public void onBarValueChanged(BarValueChangedEvent event)
			{
				_myData.setLimit((double) event.getValue());
				
				// and update the UI
				refreshHardConstraints();
			}
		});
		estimate.addValueChangeHandler(new ValueChangeHandler<String>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				@SuppressWarnings("unused")
				String newLoc = event.getValue();
				
				// DONE: Akash, we need to convert newLoc to a GeoPoint
				GeoPoint geop = SupportServices.INSTANCE.getUtilsService().getGeoPointFromString(newLoc);
				_myData.setEstimate(geop);
				
				// and update the UI
				refreshEstimate();

			}
		});
		name.addValueChangeHandler(new ValueChangeHandler<String>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				_myData.setName(event.getValue());

			}
		});

		startFinish.addValueChangeHandler(new ValueChangeHandler<Date>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<Date> event)
			{
				_myData.setStartDate(event.getValue());

			}
		}, new ValueChangeHandler<Date>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<Date> event)
			{
				_myData.setFinishDate(event.getValue());

			}
		});

		// TODO Ian, do we listen to changes to estimate in Location Forecase,
		// if yes how do we set them into _myData, it takes GeoPoint.java

		/*
		 * estimate.addValueChangeHandler(new ValueChangeHandler<String>() {
		 * 
		 * @Override public void onValueChange(ValueChangeEvent<String> event) {
		 * _myData.setEstimate(event.getValue());
		 * 
		 * } });
		 */

	}
	
	

	@Override
	protected String getHardConstraintsStr()
	{
		
		return (_myData.getLimit()==null)?super.getHardConstraintsStr():""+_myData.getLimit().intValue() + "m";
	}

	@Override
	protected String getEstimateStr()
	{
		return (_myData.getEstimate()==null)?super.getEstimateStr():"location set";
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0)
	{
		super.propertyChange(arg0);
		final String attr = arg0.getPropertyName();
		if (attr.equals(BaseContribution.NAME))
			name.setData((String) arg0.getNewValue());
		else if (attr.equals(LocationForecastContribution.LIMIT))
			limit.setData(( (Double) arg0.getNewValue()).intValue());
		else if (attr.equals(BaseContribution.START_DATE))
			startFinish.setStartData((Date) arg0.getNewValue());
		else if (attr.equals(BaseContribution.FINISH_DATE))
			startFinish.setFinishData((Date) arg0.getNewValue());
		if (attr.equals(BaseContribution.ESTIMATE))
		{
			GeoPoint pt = (GeoPoint) arg0.getNewValue();
			// DONE: Akash, we need to convert pt to a string
			String strPt = SupportServices.INSTANCE.getUtilsService().formatGeoPoint(pt);
			estimate.setData(strPt);
		}

	}

	@Override
	public void setData(BaseContribution contribution)
	{

		// and store the type-casted contribution
		_myData = (LocationForecastContribution) contribution;

		// property changes
		// initialise the UI components
		limit.setData((_myData.getLimit() == null) ? 0 : _myData.getLimit()
				.intValue());
		name.setData(contribution.getName());
		startFinish.setData(contribution.getStartDate(),
				contribution.getFinishDate());
		
		GeoPoint gp = _myData.getEstimate();
		// DONE: Akash, we need to convert gp to a string
		String strVal = SupportServices.INSTANCE.getUtilsService().formatGeoPoint(gp);
		estimate.setData(strVal);

		contribution.addPropertyChangeListener(LocationForecastContribution.LIMIT,
				this);
		
		// and update the parent UI elements/listeners
		super.setData(contribution);
	}

}
