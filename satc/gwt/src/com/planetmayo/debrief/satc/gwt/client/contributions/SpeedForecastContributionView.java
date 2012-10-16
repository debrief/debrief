package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

public class SpeedForecastContributionView extends BaseContributionView {

	interface SpeedForecastContributionViewUiBinder extends
			UiBinder<Widget, SpeedForecastContributionView> {
	}

	private static SpeedForecastContributionViewUiBinder uiBinder = GWT
			.create(SpeedForecastContributionViewUiBinder.class);

	public SpeedForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@Override
	public void setData(BaseContribution contribution) {
		// TODO Auto-generated method stub

	}

}
