package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AlterationLegForecastContributionView extends Composite
{

	interface AlterationLegForecastContributionViewUiBinder extends
			UiBinder<Widget, AlterationLegForecastContributionView>
	{
	}

	private static AlterationLegForecastContributionViewUiBinder uiBinder = GWT
			.create(AlterationLegForecastContributionViewUiBinder.class);

	public AlterationLegForecastContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));

	}

}
