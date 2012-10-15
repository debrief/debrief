package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FrequencyMeasurementContributionView extends Composite
{

	interface FrequencyMeasurementContributionViewUiBinder extends
			UiBinder<Widget, FrequencyMeasurementContributionView>
	{
	}

	private static FrequencyMeasurementContributionViewUiBinder uiBinder = GWT
			.create(FrequencyMeasurementContributionViewUiBinder.class);

	public FrequencyMeasurementContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

}
