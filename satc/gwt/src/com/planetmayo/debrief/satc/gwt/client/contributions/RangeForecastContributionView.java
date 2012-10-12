package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RangeForecastContributionView extends Composite {

	private static RangeForecastContributionViewUiBinder uiBinder = GWT
			.create(RangeForecastContributionViewUiBinder.class);

	interface RangeForecastContributionViewUiBinder extends
			UiBinder<Widget, RangeForecastContributionView> {
	}

	public RangeForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

}
