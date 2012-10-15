package com.planetmayo.debrief.satc.gwt.client.contributions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReferenceContributionView extends Composite
{

	interface ReferenceContributionViewUiBinder extends
			UiBinder<Widget, ReferenceContributionView>
	{
	}

	private static ReferenceContributionViewUiBinder uiBinder = GWT
			.create(ReferenceContributionViewUiBinder.class);

	public ReferenceContributionView()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

}
