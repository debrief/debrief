package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;

public class NumberSpinner extends Composite {

	private int RATE = 1;
	private IntegerBox integerBox;

	public NumberSpinner() {
		this(1);
	}

	HorizontalPanel horizontalPanel;
	FlowPanel buttonsPanel;

	public NumberSpinner(int defaultValue) {
		horizontalPanel = new HorizontalPanel();
		initWidget(horizontalPanel);

		buttonsPanel = new FlowPanel();
		buttonsPanel.addStyleName("spinner-buttons");

		integerBox = new IntegerBox();
		horizontalPanel.add(integerBox);
		horizontalPanel.add(buttonsPanel);

		integerBox.setWidth("20px");
		integerBox.setValue(defaultValue);

		Button upButton = new Button();
		upButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setValue(getValue() + RATE);
				event.stopPropagation();
				event.preventDefault();
			}
		});
		upButton.setStyleName("dp-spinner-upbutton");

		buttonsPanel.add(upButton);
		upButton.setSize("12px", "10px");

		Button downButton = new Button();
		downButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (getValue() == 0) {
					event.stopPropagation();
					event.preventDefault();
					return;

				}
				setValue(getValue() - RATE);
				event.stopPropagation();
				event.preventDefault();
			}
		});
		downButton.setStyleName("dp-spinner-downbutton");
		buttonsPanel.add(downButton);
		downButton.setSize("12px", "10px");

	}

	/**
	 * Returns the value being held.
	 * 
	 * @return
	 */
	public int getValue() {
		return integerBox.getValue() == null ? 0 : integerBox.getValue();
	}

	/**
	 * Sets the value to the control
	 * 
	 * @param value
	 *            Value to be set
	 */
	public void setValue(int value) {
		integerBox.setValue(value);
	}

	/**
	 * Sets the rate at which increment or decrement is done.
	 * 
	 * @param rate
	 */
	public void setRate(int rate) {
		this.RATE = rate;
	}
}