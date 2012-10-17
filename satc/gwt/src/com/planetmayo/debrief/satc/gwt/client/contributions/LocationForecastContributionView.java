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
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;

public class LocationForecastContributionView extends BaseContributionView {

	interface LocationForecastContributionViewUiBinder extends
			UiBinder<Widget, LocationForecastContributionView> {
	}

	private static LocationForecastContributionViewUiBinder uiBinder = GWT
			.create(LocationForecastContributionViewUiBinder.class);

	public LocationForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));
		initHandlers();
	}

	private LocationForecastContribution _myData;

	@UiField
	Slider2BarWidget limit;
	@UiField
	StartFinishWidget startFinish;
	@UiField
	NameWidget name;
	@UiField
	NameWidget estimate;

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		super.propertyChange(arg0);
		final String attr = arg0.getPropertyName();
		if (attr.equals(BaseContribution.NAME))
			name.setData((String) arg0.getNewValue());
		else if (attr.equals(LocationForecastContribution.LIMIT))
			limit.setData((Integer) arg0.getNewValue());
		else if (attr.equals(BaseContribution.START_DATE))
			startFinish.setStartData((Date) arg0.getNewValue());
		else if (attr.equals(BaseContribution.FINISH_DATE))
			startFinish.setFinishData((Date) arg0.getNewValue());
		if (attr.equals(BaseContribution.ESTIMATE))
			estimate.setData((String) arg0.getNewValue());

	}

	@Override
	public void setData(BaseContribution contribution) {

		super.setData(contribution);

		// and store the type-casted contribution
		_myData = (LocationForecastContribution) contribution;

		// property changes
		// initialise the UI components
		limit.setData((int) _myData.getLimit());
		name.setData(contribution.getName());
		startFinish.setData(contribution.getStartDate(),
				contribution.getFinishDate());
		estimate.setData(_myData.getEstimate().toString());

		contribution.addPropertyChangeListener(
				LocationForecastContribution.LIMIT, this);

	}

	@Override
	protected BaseContribution getData() {
		return _myData;
	}

	@Override
	public void initHandlers() {
		super.initHandlers();

		limit.addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				_myData.setLimit(event.getValue());
			}
		});
		name.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				_myData.setName(event.getValue());

			}
		});

		startFinish.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				_myData.setStartDate(event.getValue());

			}
		}, new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
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

}
