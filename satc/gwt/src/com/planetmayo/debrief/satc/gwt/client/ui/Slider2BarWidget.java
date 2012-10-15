package com.planetmayo.debrief.satc.gwt.client.ui;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.kiouri.sliderbar.client.solution.simplehorizontal.SliderBarSimpleHorizontal;

public class Slider2BarWidget extends HorizontalPanel
{

	SliderBarSimpleHorizontal sliderBarSimpleHorizontal;
	HTML value = new HTML();
	private Label text = new Label();
	private CheckBox enabled = new CheckBox();
	private String unitLabelSuffix = "";
	private String unitLabelPrefix = "";
	private final static String SPEED = "SPEED";
	private final static String LOCATION = "LOCATION";
	private final static String COURSE = "COURSE";
	private final static String BEARING = "BEARING";
	private final static String FREQUENCY = "FREQUENCY";

	public Slider2BarWidget()
	{
		addStyleName("slider-widget");
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		sliderBarSimpleHorizontal = new SliderBarSimpleHorizontal(360, "210px",
				true);

		sliderBarSimpleHorizontal
				.addBarValueChangedHandler(new BarValueChangedHandler()
				{
					@Override
					public void onBarValueChanged(BarValueChangedEvent event)
					{
						value.setHTML(unitLabelPrefix + event.getValue() + unitLabelSuffix);
					}
				});

		text.setWordWrap(false);
		text.addStyleName("slider-label");
		enabled.setValue(true);

		add(text);
		add(enabled);
		add(value);
		add(sliderBarSimpleHorizontal);

	}

	public void addBarValueChangedHandler(BarValueChangedHandler handler)
	{
		sliderBarSimpleHorizontal.addBarValueChangedHandler(handler);
	}

	public void setData(int data)
	{
		sliderBarSimpleHorizontal.setValue(data);
	}

	public void setSliderWidth(String width)
	{
		sliderBarSimpleHorizontal.setWidth(width);
	}

	public void setText(String label)
	{
		text.setText(label);
	}

	public void setType(String type)
	{
		if (type.equalsIgnoreCase(SPEED))
		{
			unitLabelSuffix = "kts";
			sliderBarSimpleHorizontal.setMaxValue(30);
			value.setWidth("35px");
		}
		else if (type.equalsIgnoreCase(LOCATION))
		{
			unitLabelSuffix = "m";
			sliderBarSimpleHorizontal.setMaxValue(10000);
			value.setWidth("45px");
		}
		else if (type.equalsIgnoreCase(COURSE))
		{
			unitLabelSuffix = "&deg;";
			sliderBarSimpleHorizontal.setMaxValue(360);
			value.setWidth("35px");
		}
		else if (type.equalsIgnoreCase(BEARING))
		{
			unitLabelPrefix = "+/-";
			unitLabelSuffix = "&deg;";
			sliderBarSimpleHorizontal.setMaxValue(360);
			value.setWidth("45px");
		}
		else if (type.equalsIgnoreCase(FREQUENCY))
		{
			unitLabelPrefix = "+/-";
			unitLabelSuffix = "Hz";
			sliderBarSimpleHorizontal.setMaxValue(360);
			value.setWidth("50px");
		}
	}
}
