package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StraightLegForecastContributionView extends Composite {

	private static StraightLegForecastContributionViewUiBinder uiBinder = GWT
			.create(StraightLegForecastContributionViewUiBinder.class);

	interface StraightLegForecastContributionViewUiBinder extends
			UiBinder<Widget, StraightLegForecastContributionView> {
	}

	public StraightLegForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

}
