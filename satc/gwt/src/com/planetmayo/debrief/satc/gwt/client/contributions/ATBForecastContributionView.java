package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ATBForecastContributionView extends Composite {

	private static ATBForecastContributionViewUiBinder uiBinder = GWT
			.create(ATBForecastContributionViewUiBinder.class);

	interface ATBForecastContributionViewUiBinder extends
			UiBinder<Widget, ATBForecastContributionView> {
	}

	public ATBForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

}
