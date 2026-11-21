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

package org.mwc.debrief.lite.gui.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingConstants;

import org.mwc.debrief.lite.DebriefLiteApp;

public class JXCollapsiblePaneWithTitle extends JXCollapsiblePane {
	/**
	 *
	 */
	private static final long serialVersionUID = -2723902372006197600L;

	private static final Integer MIN_ANIMATION_SIZE = 20;

	private final RotatedLabel titleLabel;

	// Variables used to implement the resize behavior.
	private boolean dragging = false;
	private Point dragLocation = new Point();

	public JXCollapsiblePaneWithTitle(final Direction direction, final String title, final int defaultSize) {
		super(direction, MIN_ANIMATION_SIZE, false);

		final JXCollapsiblePane collapsiblePaneInstance = this;
		collapsiblePaneInstance.setPreferredSize(new Dimension(defaultSize, defaultSize));
		collapsiblePaneInstance.setName(title);

		titleLabel = new RotatedLabel(title, SwingConstants.CENTER);
		titleLabel.setName("titleLabel");
		final Dimension titleDimension;

		if (direction.isVertical()) {
			titleDimension = new Dimension(defaultSize, 20);
		} else {
			titleDimension = new Dimension(20, defaultSize);
		}
		titleLabel.setPreferredSize(titleDimension);
		setLayout(new BorderLayout());

		if (direction == Direction.LEFT) {
			add(titleLabel, BorderLayout.EAST);
			setMinimumSize(new Dimension(30, titleLabel.getHeight()));

		} else if (direction == Direction.RIGHT) {
			add(titleLabel, BorderLayout.WEST);

		} else if (direction == Direction.DOWN) {
			add(titleLabel, BorderLayout.NORTH);
			setMinimumSize(new Dimension(titleLabel.getWidth(), 30));
		} else if (direction == Direction.UP) {
			add(titleLabel, BorderLayout.SOUTH);

		}
		if (!direction.isVertical()) {
			titleLabel.setTextRotation(3 * Math.PI / 2);
		}

		titleLabel.setBackground(Color.black);

		titleLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					collapsiblePaneInstance.setCollapsed(!collapsiblePaneInstance.isCollapsed());
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				dragging = true;
				dragLocation = e.getLocationOnScreen();
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				dragging = false;
			}
		});

		titleLabel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent event) {
				if (!dragging) {
					return;
				}
				final int delta = dragDelta(direction, dragLocation, event.getLocationOnScreen());
				wrapper.getView().setVisible(true);

				final int newDimension = Math.max(extent(wrapper.getSize()) + delta, getMinimunAnimationSize());
				final int oldExtent = resizeWrapper(newDimension);

				final Rectangle bounds = getBounds();
				final int maxExtent = maxExtent(direction);
				final int extent = extent(bounds.getSize());
				currentDimension = setExtent(bounds,
						extent < maxExtent ? extent - oldExtent + newDimension : maxExtent - 2);
				if (direction.isVertical()) {
					bounds.width = 0;
				} else {
					bounds.height = 0;
				}

				collapsiblePaneInstance.collapsed = newDimension == MIN_ANIMATION_SIZE;
				collapsiblePaneInstance.setPreferredSize(new Dimension(bounds.width, bounds.height));
				setBounds(bounds);

				validate();

				dragLocation = event.getLocationOnScreen();
			}

			@Override
			public void mouseMoved(final MouseEvent event) {
				// Nothing to do here. Added a line to avoid the travis warning
				System.out.print("");
			}
		});

		if (collapsiblePaneInstance.getDirection().isVertical()) {
			titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		} else {
			titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
		}
	}

	@Override
	public String getName() {
		return titleLabel.getText();
	}

	/**
	 * how far the title has been dragged along the pane's collapse direction,
	 * signed so that a positive value enlarges the pane
	 */
	static int dragDelta(final Direction direction, final Point from, final Point to) {
		final int raw = direction.isVertical() ? (int) (to.getY() - from.getY()) : (int) (to.getX() - from.getX());
		return direction == Direction.DOWN || direction == Direction.RIGHT ? -raw : raw;
	}

	/**
	 * the largest the pane may grow to along its collapse direction: the frame's
	 * height less the ribbon and two title bars (plus 5 for minor adjustments), or
	 * its width less the title bar
	 */
	private int maxExtent(final Direction direction) {
		final var frame = DebriefLiteApp.getInstance().getApplicationFrame();
		if (direction.isVertical()) {
			return frame.getHeight() - (frame.getRibbon().getHeight() + titleLabel.getHeight() * 2 + 5);
		}
		return frame.getWidth() - titleLabel.getWidth();
	}
}
