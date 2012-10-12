package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AlterationLegForecastContributionView extends Composite {

	private static AlterationLegForecastContributionViewUiBinder uiBinder = GWT
			.create(AlterationLegForecastContributionViewUiBinder.class);

	interface AlterationLegForecastContributionViewUiBinder extends
			UiBinder<Widget, AlterationLegForecastContributionView> {
	}

	public AlterationLegForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

}
