package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CourseForecastContribution extends Composite {

	private static CourseForecastUiBinder uiBinder = GWT
			.create(CourseForecastUiBinder.class);

	interface CourseForecastUiBinder extends
			UiBinder<Widget, CourseForecastContribution> {
	}

	public CourseForecastContribution() {
		initWidget(uiBinder.createAndBindUi(this));

	}

}
