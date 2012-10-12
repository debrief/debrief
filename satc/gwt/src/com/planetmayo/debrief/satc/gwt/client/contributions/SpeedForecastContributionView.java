package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SpeedForecastContributionView extends Composite {

	private static SpeedForecastContributionViewUiBinder uiBinder = GWT
			.create(SpeedForecastContributionViewUiBinder.class);

	interface SpeedForecastContributionViewUiBinder extends
			UiBinder<Widget, SpeedForecastContributionView> {
	}

	public SpeedForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

}
