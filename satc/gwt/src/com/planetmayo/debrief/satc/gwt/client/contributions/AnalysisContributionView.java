package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AnalysisContributionView extends Composite
{

	interface AnalysisContributionViewUiBinder extends
			UiBinder<Widget, AnalysisContributionView>
	{
	}

	private static AnalysisContributionViewUiBinder uiBinder = GWT
			.create(AnalysisContributionViewUiBinder.class);

	public AnalysisContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

}
