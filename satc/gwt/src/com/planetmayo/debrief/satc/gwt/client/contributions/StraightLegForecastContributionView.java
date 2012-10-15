package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StraightLegForecastContributionView extends Composite
{

	interface StraightLegForecastContributionViewUiBinder extends
			UiBinder<Widget, StraightLegForecastContributionView>
	{
	}

	private static StraightLegForecastContributionViewUiBinder uiBinder = GWT
			.create(StraightLegForecastContributionViewUiBinder.class);

	public StraightLegForecastContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));

	}

}
