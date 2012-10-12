package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class BearingMeasurementContributionView extends Composite {

	private static BearingMeasurementContributionViewUiBinder uiBinder = GWT
			.create(BearingMeasurementContributionViewUiBinder.class);

	interface BearingMeasurementContributionViewUiBinder extends
			UiBinder<Widget, BearingMeasurementContributionView> {
	}

	public BearingMeasurementContributionView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
