package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StraightLegForecastContribution extends Composite {

	private static CourseForecastUiBinder uiBinder = GWT
			.create(CourseForecastUiBinder.class);

	interface CourseForecastUiBinder extends
			UiBinder<Widget, StraightLegForecastContribution> {
	}

	public StraightLegForecastContribution() {
		initWidget(uiBinder.createAndBindUi(this));

	}

}
