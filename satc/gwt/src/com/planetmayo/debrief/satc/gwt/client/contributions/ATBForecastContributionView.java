package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ATBForecastContributionView extends Composite
{

	interface ATBForecastContributionViewUiBinder extends
			UiBinder<Widget, ATBForecastContributionView>
	{
	}

	private static ATBForecastContributionViewUiBinder uiBinder = GWT
			.create(ATBForecastContributionViewUiBinder.class);

	public ATBForecastContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));

	}

}
