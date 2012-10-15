package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SpatialView extends Composite
{

	interface SpatialViewUiBinder extends UiBinder<Widget, SpatialView>
	{
	}

	private static SpatialViewUiBinder uiBinder = GWT
			.create(SpatialViewUiBinder.class);

	public SpatialView()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

}
