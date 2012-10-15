package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SpatialView extends Composite {

	private static SpatialViewUiBinder uiBinder = GWT
			.create(SpatialViewUiBinder.class);

	interface SpatialViewUiBinder extends UiBinder<Widget, SpatialView> {
	}

	public SpatialView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
