package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManageSolutions extends Composite {

	private static ManageSolutionsUiBinder uiBinder = GWT
			.create(ManageSolutionsUiBinder.class);

	interface ManageSolutionsUiBinder extends UiBinder<Widget, ManageSolutions> {
	}

	public ManageSolutions() {

		initWidget(uiBinder.createAndBindUi(this));
		header.setCellWidth(active, "20%");
		header.setCellWidth(estimate, "30%");
		header.setCellWidth(hardConstraints, "30%");
		header.setCellWidth(weighting, "20%");
	}

	@UiField
	HorizontalPanel header;

	@UiField
	Anchor active;

	@UiField
	Anchor estimate;

	@UiField
	Anchor hardConstraints;

	@UiField
	Anchor weighting;

	@UiField
	Button add;

	@UiField
	PopupPanel contextMenu;

	@UiField
	Label courseForecast;

	@UiField
	Label speedForecast;

	@UiField
	Label locationForecast;

	@UiField
	HTMLPanel analystContributions;

	@UiHandler("add")
	void onClick(ClickEvent e) {
		contextMenu.showRelativeTo(add);
	}

	@UiHandler(value = { "courseForecast", "speedForecast", "locationForecast" })
	void handleClick(ClickEvent e) {
		contextMenu.hide();
		if ((Label) e.getSource() == courseForecast) {
			analystContributions.add(new CourseForecast());
		} else if ((Label) e.getSource() == speedForecast) {
			Window.alert(((Label) e.getSource()).getText());

		} else if ((Label) e.getSource() == locationForecast) {
			Window.alert(((Label) e.getSource()).getText());

		}
	}

}
