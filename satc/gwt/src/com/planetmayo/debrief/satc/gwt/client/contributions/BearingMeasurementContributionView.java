package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public class BearingMeasurementContributionView extends Composite implements
		ContributionView
{

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

	@Override
	public void setData(BaseContribution contribution)
	{
		// TODO Auto-generated method stub

	}

}
