package com.planetmayo.debrief.satc.gwt.client.contributions;

import java.beans.PropertyChangeEvent;

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
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;

public class BearingMeasurementContributionView extends BaseContributionView
{

	interface BearingMeasurementContributionViewUiBinder extends
			UiBinder<Widget, BearingMeasurementContributionView>
	{
	}

	private static BearingMeasurementContributionViewUiBinder uiBinder = GWT
			.create(BearingMeasurementContributionViewUiBinder.class);

	@UiField
	Slider2BarWidget bearing;

	@UiField
	StartFinishWidget startFinish;
	@UiField
	NameWidget name;
	private BearingMeasurementContribution _myData;

	public BearingMeasurementContributionView()
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

		bearing.addBarValueChangedHandler(new BarValueChangedHandler()
		{
			@Override
			public void onBarValueChanged(BarValueChangedEvent event)
			{
				_myData.setBearingError(Math.toRadians(event.getValue()));

				// update the displayed constraints
				refreshHardConstraints();
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

	}

	@Override
	protected String getHardConstraintsStr()
	{
		return (_myData.getBearingError() == null) ? "unset" : ""
				+ _myData.getBearingError().intValue() + " degs";
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0)
	{
		super.propertyChange(arg0);
		final String attr = arg0.getPropertyName();
		if (attr.equals(BaseContribution.NAME))
			name.setData((String) arg0.getNewValue());
		else if (attr.equals(BearingMeasurementContribution.BEARING_ERROR))
		{
			bearing.setData((Integer) arg0.getNewValue());
			super.refreshHardConstraints();
		}
	}

	@Override
	public void setData(BaseContribution contribution)
	{

		// and store the type-casted contribution
		_myData = (BearingMeasurementContribution) contribution;

		// property changes
		// initialise the UI components
		bearing.setData((_myData.getBearingError() == null) ? 0 : (int) Math
				.toDegrees(_myData.getBearingError()));
		name.setData(contribution.getName());
		startFinish.setData(contribution.getStartDate(),
				contribution.getFinishDate());

		contribution.addPropertyChangeListener(
				BearingMeasurementContribution.BEARING_ERROR, this);

		super.setData(contribution);

	}

}
