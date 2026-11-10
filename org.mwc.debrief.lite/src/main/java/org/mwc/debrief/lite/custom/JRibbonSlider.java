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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicSliderUI;

import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;

/**
 * @author Ayesha
 *
 */
public class JRibbonSlider extends JSlider {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The skin draws a small grey thumb on a thin pale track, both hard to see,
	 * above all at the start value where no part of the track is highlighted. So
	 * a darker, thicker track is drawn over the skin's, filled orange up to the
	 * thumb, and then a larger orange thumb with a white rim.
	 */
	static final Color THUMB_COLOR = new Color(230, 120, 0);

	static final Color THUMB_DISABLED_COLOR = Color.GRAY;

	static final int THUMB_DIAMETER = 14;

	static final Color TRACK_COLOR = new Color(90, 90, 90);

	static final Color TRACK_DISABLED_COLOR = new Color(170, 170, 170);

	static final int TRACK_THICKNESS = 4;

	/**
	 * the first x at which the UI's value is at least {@code value}
	 */
	private static int firstX(final BasicSliderUI ui, final int width, final int value) {
		int lo = 0;
		int hi = width;
		while (lo < hi) {
			final int mid = (lo + hi) / 2;
			if (ui.valueForXPosition(mid) < value) {
				lo = mid + 1;
			} else {
				hi = mid;
			}
		}
		return lo;
	}

	/**
	 * Where the thumb's centre is, for a horizontal slider. The track's ends are
	 * found through the UI's public {@code valueForXPosition}, so this works with
	 * whichever look and feel is installed.
	 *
	 * @return the x of the thumb's centre, or -1 if it cannot be worked out
	 */
	public static int thumbCentreX(final JSlider slider) {
		final int[] ends = trackEnds(slider);
		if (ends == null) {
			return -1;
		}
		final double fraction = (slider.getValue() - slider.getMinimum())
				/ (double) (slider.getMaximum() - slider.getMinimum());
		return ends[0] + (int) Math.round((ends[1] - ends[0]) * fraction);
	}

	/**
	 * The x of the thumb's centre at the minimum and at the maximum, for a
	 * horizontal slider, found through the UI's public {@code valueForXPosition}.
	 *
	 * @return {left, right}, or null if they cannot be worked out
	 */
	public static int[] trackEnds(final JSlider slider) {
		if (!(slider.getUI() instanceof BasicSliderUI) || slider.getOrientation() != HORIZONTAL
				|| slider.getMaximum() <= slider.getMinimum() || slider.getWidth() <= 0) {
			return null;
		}
		final BasicSliderUI ui = (BasicSliderUI) slider.getUI();
		final int left = firstX(ui, slider.getWidth(), slider.getMinimum() + 1) - 1;
		final int right = firstX(ui, slider.getWidth(), slider.getMaximum());
		return new int[] { left, right };
	}

	public JRibbonSlider(
			final Projection<JRibbonSlider, SliderCompContMdl, ComponentPresentationModel> projection) {
		final SliderCompContMdl contentModel = projection.getContentModel();
		setValue(contentModel.getValue());
		setEnabled(contentModel.isEnabled());
		setMaximum(contentModel.getMaximum());
		setMinimum(contentModel.getMinimum());
		setPreferredSize(new Dimension(400, 20));
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
			}
			if ("minimum".equals(event.getPropertyName())) {
				setMinimum(contentModel.getMinimum());
			}

		});
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final int[] ends = trackEnds(this);
		if (ends == null) {
			return;
		}
		final int x = thumbCentreX(this);
		final int y = getHeight() / 2;
		final Graphics2D g2 = (Graphics2D) g.create();
		try {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// the track, and the part of it up to the thumb
			final int trackTop = y - TRACK_THICKNESS / 2;
			g2.setColor(isEnabled() ? TRACK_COLOR : TRACK_DISABLED_COLOR);
			g2.fillRoundRect(ends[0], trackTop, ends[1] - ends[0], TRACK_THICKNESS, TRACK_THICKNESS,
					TRACK_THICKNESS);
			if (isEnabled() && x > ends[0]) {
				g2.setColor(THUMB_COLOR);
				g2.fillRoundRect(ends[0], trackTop, x - ends[0], TRACK_THICKNESS, TRACK_THICKNESS, TRACK_THICKNESS);
			}

			// the thumb
			final int left = x - THUMB_DIAMETER / 2;
			final int top = y - THUMB_DIAMETER / 2;
			g2.setColor(isEnabled() ? THUMB_COLOR : THUMB_DISABLED_COLOR);
			g2.fillOval(left, top, THUMB_DIAMETER, THUMB_DIAMETER);
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(2f));
			g2.drawOval(left, top, THUMB_DIAMETER, THUMB_DIAMETER);
		} finally {
			g2.dispose();
		}
	}
}
