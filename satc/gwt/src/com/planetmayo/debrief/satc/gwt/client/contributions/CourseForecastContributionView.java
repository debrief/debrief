package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CourseForecastContributionView extends Composite {

	private static CourseForecastContributionViewUiBinder uiBinder = GWT
			.create(CourseForecastContributionViewUiBinder.class);

	interface CourseForecastContributionViewUiBinder extends
			UiBinder<Widget, CourseForecastContributionView> {
	}

	public CourseForecastContributionView() {
		initWidget(uiBinder.createAndBindUi(this));

	}

}
