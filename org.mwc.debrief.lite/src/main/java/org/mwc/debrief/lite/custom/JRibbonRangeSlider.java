/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package org.mwc.debrief.lite.custom;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import org.mwc.debrief.lite.gui.custom.RangeSlider;
import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;

public class JRibbonRangeSlider extends RangeSlider {

	private static HashMap<Projection<JRibbonRangeSlider, SliderComponentContentModel, ComponentPresentationModel>, JRibbonRangeSlider> instances = new HashMap<>();

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	Projection<JRibbonRangeSlider, SliderComponentContentModel, ComponentPresentationModel> projection;

	public JRibbonRangeSlider(
			final Projection<JRibbonRangeSlider, SliderComponentContentModel, ComponentPresentationModel> projection) {
		this.projection = projection;
		setSize(new Dimension(250, 40));
		setBackground(new Color(180, 180, 230));
		initialize(projection);
	}

	public static JRibbonRangeSlider getInstance(
			final Projection<JRibbonRangeSlider, SliderComponentContentModel, ComponentPresentationModel> projection) {
		if (!instances.containsKey(projection)) {
			instances.put(projection, new JRibbonRangeSlider(projection));
		}
		return instances.get(projection);
	}

	public void initialize(
			final Projection<JRibbonRangeSlider, SliderComponentContentModel, ComponentPresentationModel> projection) {
		final SliderComponentContentModel contentModel = projection.getContentModel();
		setValue(contentModel.getValue());
		setEnabled(contentModel.isEnabled());
		setMaximum(contentModel.getMaximum());
		setMinimum(contentModel.getMinimum());

		addChangeListener((final ChangeEvent ae) -> {
			contentModel.setValue(((JSlider) ae.getSource()).getValue());
			contentModel.setMinimum(((JSlider) ae.getSource()).getMinimum());
			contentModel.setMaximum(((JSlider) ae.getSource()).getMaximum());
			if (contentModel.getChangeListener() != null) {
				contentModel.getChangeListener().stateChanged(ae);
			}
		});

		contentModel.addPropertyChangeListener((final PropertyChangeEvent event) -> {
			if ("value".equals(event.getPropertyName())) {
				setValue(contentModel.getValue());
			}
			if ("maximum".equals(event.getPropertyName())) {
				setMaximum(contentModel.getMaximum());
				setUpperValue(contentModel.getMaximum());

			}
			if ("minimum".equals(event.getPropertyName())) {
				setMinimum(contentModel.getMinimum());
			}
		});
	}

	@Override
	public void setMaximum(final int maximum) {
		projection.getContentModel().setMaximum(maximum);
		getModel().setMaximum(maximum);
	}

	@Override
	public void setMinimum(final int min) {
		projection.getContentModel().setMinimum(min);
		getModel().setMinimum(min);
	}

}
