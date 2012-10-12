package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FrequencyMeasurementContributionView extends Composite {

	private static FrequencyMeasurementContributionViewUiBinder uiBinder = GWT
			.create(FrequencyMeasurementContributionViewUiBinder.class);

	interface FrequencyMeasurementContributionViewUiBinder extends
			UiBinder<Widget, FrequencyMeasurementContributionView> {
	}

	public FrequencyMeasurementContributionView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
