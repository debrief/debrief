package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public class BearingMeasurementContributionView extends BaseContributionView {

	interface BearingMeasurementContributionViewUiBinder extends
			UiBinder<Widget, BearingMeasurementContributionView>
	{
	}

	private static BearingMeasurementContributionViewUiBinder uiBinder = GWT
			.create(BearingMeasurementContributionViewUiBinder.class);

	public BearingMeasurementContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private BaseContribution _myData;

	@Override
	public void setData(BaseContribution contribution)
	{
		_myData = contribution;
	}
	
	@Override
	protected BaseContribution getData()
	{
		return _myData;
	}
	

}
