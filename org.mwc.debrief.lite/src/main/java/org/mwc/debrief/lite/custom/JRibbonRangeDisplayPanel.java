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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;

/**
 * @author Ayesha
 *
 */
public class JRibbonRangeDisplayPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final JLabel lblMinimumValue;
	private final JLabel lblMaximumValue;

	public JRibbonRangeDisplayPanel(
			final Projection<JRibbonRangeDisplayPanel, RangeDisplayComponentContentModel, ComponentPresentationModel> projection) {
		final RangeDisplayComponentContentModel contentModel = projection.getContentModel();
		setEnabled(contentModel.isEnabled());
		lblMinimumValue = new JLabel();
		lblMaximumValue = new JLabel();
		final JPanel valuePanel = new JPanel();
		valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.X_AXIS));
		valuePanel.add(lblMinimumValue);
		valuePanel.add(Box.createGlue());
		valuePanel.add(lblMaximumValue);
		setLayout(new BorderLayout());
		this.add(valuePanel);
		contentModel.addPropertyChangeListener((final PropertyChangeEvent event) -> {
			if ("minValueText".equals(event.getPropertyName())) {
				lblMinimumValue.setText(contentModel.getMinValueText());
			} else if ("maxValueText".equals(event.getPropertyName())) {
				lblMaximumValue.setText(contentModel.getMaxValueText());
			} else if ("background".equals(event.getPropertyName())) {
				lblMinimumValue.setBackground(contentModel.getBackgroundColor());
				lblMaximumValue.setBackground(contentModel.getBackgroundColor());
				setBackground(contentModel.getBackgroundColor());

			} else if ("foreground".equals(event.getPropagationId())) {
				lblMinimumValue.setForeground(contentModel.getForegroundColor());
				lblMaximumValue.setForeground(contentModel.getForegroundColor());
				setForeground(contentModel.getForegroundColor());

			}
		});
	}

	public JLabel getMaximumValue() {
		return lblMaximumValue;
	}

	public JLabel getMinimumValue() {
		return lblMinimumValue;
	}

	public void setMaxValueText(final String text) {
		lblMaximumValue.setText(text);
	}

	public void setMinValueText(final String text) {
		lblMinimumValue.setText(text);
	}
}
