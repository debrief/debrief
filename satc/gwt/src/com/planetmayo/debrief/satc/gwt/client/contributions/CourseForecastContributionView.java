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
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;

public class CourseForecastContributionView extends BaseContributionView {

	interface CourseForecastContributionViewUiBinder extends
			UiBinder<Widget, CourseForecastContributionView> {
	}

	private static CourseForecastContributionViewUiBinder uiBinder = GWT
			.create(CourseForecastContributionViewUiBinder.class);

	@UiField
	Slider2BarWidget min;

	@UiField
	Slider2BarWidget max;

	@UiField
	Slider2BarWidget estimate;

	@UiField
	NameWidget name;

	@UiField
	StartFinishWidget startFinish;

	private CourseForecastContribution _myData;

	public CourseForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

		initHandlers();

	}

	@Override
	protected BaseContribution getData()
	{
		return _myData;
	}
	
	@Override
	public void initHandlers() {
		// DONE: Akash - we need to respond to other UI changes aswell.
		// respond to the UI

		// ADDED BY AKASH - Component specific handlers are here.
		super.initHandlers();

		max.addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				_myData.setMaxCourse(event.getValue());
			}
		});

		min.addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				_myData.setMinCourse(event.getValue());
			}
		});

		estimate.addBarValueChangedHandler(new BarValueChangedHandler() {
			@Override
			public void onBarValueChanged(BarValueChangedEvent event) {
				_myData.setEstimate(event.getValue());
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

		// DONE: Akash - we also need to listen for changes in the headerfor
		// Active plus weighting

		// ADDED BY AKASH - Handlers specific to base contribution are in
		// BaseContributionView.java
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		super.propertyChange(arg0);
		final String attr = arg0.getPropertyName();
		if (attr.equals(CourseForecastContribution.MIN_COURSE))
			min.setData((Integer) arg0.getNewValue());
		else if (attr.equals(CourseForecastContribution.MAX_COURSE))
			max.setData((Integer) arg0.getNewValue());
		else if (attr.equals(BaseContribution.ESTIMATE))
			estimate.setData((Integer) arg0.getNewValue());
		else if (attr.equals(BaseContribution.NAME))
			name.setData((String) arg0.getNewValue());
		else if (attr.equals(BaseContribution.START_DATE))
			startFinish.setStartData((Date) arg0.getNewValue());
		else if (attr.equals(BaseContribution.FINISH_DATE))
			startFinish.setFinishData((Date) arg0.getNewValue());

	}

	@Override
	public void setData(BaseContribution contribution) {

		// let the parent register with the contribution
		super.setData(contribution);

		// and store the type-casted contribution
		_myData = (CourseForecastContribution) contribution;

		// property changes
		// initialise the UI components
		min.setData(_myData.getMinCourse());
		max.setData(_myData.getMaxCourse());
		estimate.setData((Integer) _myData.getEstimate());
		name.setData(contribution.getName());
		startFinish.setData(contribution.getStartDate(),
				contribution.getFinishDate());
	}

}
