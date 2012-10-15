package com.planetmayo.debrief.satc.gwt.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TrackStates extends Composite {

	private static TrackStatesUiBinder uiBinder = GWT
			.create(TrackStatesUiBinder.class);

	interface TrackStatesUiBinder extends UiBinder<Widget, TrackStates> {
	}

	public TrackStates() {
		initWidget(uiBinder.createAndBindUi(this));
		addStates(new Date().toLocaleString(), "8pts 0.00000", "0-18", "n/a");
	}

	@UiField
	Grid grid;

	public void addStates(String time, String location, String speed,
			String course) {
		int index = grid.insertRow(grid.getRowCount());
		grid.setWidget(index, 0, new Label(time));
		grid.setWidget(index, 1, new Label(location));
		grid.setWidget(index, 2, new Label(speed));
		grid.setWidget(index, 3, new Label(course));

	}

}
