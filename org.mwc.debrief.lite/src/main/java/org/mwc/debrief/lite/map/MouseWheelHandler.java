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
package org.mwc.debrief.lite.map;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.MapPane;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;

import Debrief.GUI.Tote.StepControl;

/**
 * Mouse wheel support for the map, following Full Debrief: with Ctrl held the
 * wheel zooms the plot in and out (about the cursor), otherwise it steps the
 * time forwards and backwards.
 */
public class MouseWheelHandler extends MapMouseAdapter {

	/** how much one notch of the wheel zooms (as Full Debrief) */
	public static final double ZOOM_FACTOR = 1.1;

	/** don't zoom in beyond this map area (as the zoom-in tool) */
	private static final double MIN_MAP_AREA = 1e-10;

	/**
	 * the area to show after zooming, keeping the anchor at the same place on
	 * screen
	 *
	 * @param current the area currently shown
	 * @param anchor  the point to zoom about
	 * @param factor  how much to scale the area by: &gt; 1 zooms out, &lt; 1 zooms
	 *                in
	 */
	public static Rectangle2D.Double zoomedArea(final Rectangle2D current, final Point2D anchor,
			final double factor) {
		return new Rectangle2D.Double(anchor.getX() - (anchor.getX() - current.getX()) * factor,
				anchor.getY() - (anchor.getY() - current.getY()) * factor, current.getWidth() * factor,
				current.getHeight() * factor);
	}

	/**
	 * the factor to scale the visible area by: rolling the wheel away from the user
	 * (negative rotation) zooms in
	 */
	public static double zoomFactor(final int wheelRotation) {
		return Math.pow(ZOOM_FACTOR, wheelRotation);
	}

	private final StepControl _stepper;

	private final Runnable _afterZoom;

	/**
	 * @param stepper   the time stepper to move
	 * @param afterZoom run once the visible area has changed
	 */
	public MouseWheelHandler(final StepControl stepper, final Runnable afterZoom) {
		_stepper = stepper;
		_afterZoom = afterZoom;
	}

	@Override
	public void onMouseWheelMoved(final MapMouseEvent ev) {
		final int rotation = ev.getWheelAmount();
		if (rotation == 0) {
			// a high-resolution wheel that hasn't completed a notch yet
			return;
		}

		if (ev.isControlDown()) {
			zoom(ev.getSource(), ev.getWorldPos(), rotation);
		} else {
			stepTime(rotation);
		}
	}

	/**
	 * move the time by one small step per notch: rolling the wheel towards the
	 * user (positive rotation) moves forwards
	 */
	public void stepTime(final int wheelRotation) {
		final boolean forwards = wheelRotation > 0;
		for (int i = 0; i < Math.abs(wheelRotation); i++) {
			_stepper.doStep(forwards, false);
		}
	}

	private void zoom(final MapPane map, final Point2D anchor, final int wheelRotation) {
		final ReferencedEnvelope bounds = map.getDisplayArea();
		if (bounds == null || bounds.isEmpty()) {
			return;
		}

		// don't go beyond the limits the zoom tools use
		final boolean zoomingOut = wheelRotation > 0;
		if (zoomingOut ? bounds.getArea() >= LiteMapPane.MAX_MAP_AREA : bounds.getArea() <= MIN_MAP_AREA) {
			return;
		}

		final Rectangle2D current = new Rectangle2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(),
				bounds.getHeight());
		map.setDisplayArea(
				new Envelope2D(bounds.getCoordinateReferenceSystem(), zoomedArea(current, anchor, zoomFactor(wheelRotation))));

		if (_afterZoom != null) {
			_afterZoom.run();
		}
	}
}
