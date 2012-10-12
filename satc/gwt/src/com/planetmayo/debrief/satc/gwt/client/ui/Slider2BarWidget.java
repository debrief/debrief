package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

public class Slider2BarWidget extends HorizontalPanel {

	SliderBarSimpleHorizontal sliderBarSimpleHorizontal;
	HTML value = new HTML();
	private Label text = new Label();
	private CheckBox enabled = new CheckBox();
	private String unitLabel = "";
	private final static String SPEED = "SPEED";
	private final static String LOCATION = "LOCATION";
	private final static String COURSE = "COURSE";

	public Slider2BarWidget() {
		addStyleName("slider-widget");
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		sliderBarSimpleHorizontal = new SliderBarSimpleHorizontal(360, "220px",
				true);

		sliderBarSimpleHorizontal
				.addBarValueChangedHandler(new BarValueChangedHandler() {
					public void onBarValueChanged(BarValueChangedEvent event) {
						value.setHTML(event.getValue() + unitLabel);
					}
				});

		value.setWidth("35px");
		text.setWordWrap(false);
		text.addStyleName("slider-label");
		enabled.setValue(true);

		add(text);
		add(enabled);
		add(value);
		add(sliderBarSimpleHorizontal);

	}

	public void setText(String label) {
		text.setText(label);
	}

	public void setType(String type) {
		if (type.equalsIgnoreCase(SPEED)) {
			unitLabel = "kts";
			sliderBarSimpleHorizontal.setMaxValue(30);
		} else if (type.equalsIgnoreCase(LOCATION)) {
			unitLabel = "m";
			sliderBarSimpleHorizontal.setMaxValue(10000);
		} else if (type.equalsIgnoreCase(COURSE)) {
			unitLabel = "&deg;";
			sliderBarSimpleHorizontal.setMaxValue(360);
		}
	}
}
