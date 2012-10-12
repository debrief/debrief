package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LocationForecastContributionView extends Composite {

	private static LocationForecastContributionViewUiBinder uiBinder = GWT
			.create(LocationForecastContributionViewUiBinder.class);

	interface LocationForecastContributionViewUiBinder extends
			UiBinder<Widget, LocationForecastContributionView> {
	}

	public LocationForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
