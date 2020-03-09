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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.ZoomInTool;
import org.locationtech.jts.geom.Coordinate;
import org.mwc.debrief.lite.DebriefLiteApp;

public class AdvancedZoomInTool extends ZoomInTool {

	private final Point startPosDevice;
	private final Point2D startPosWorld;
	private boolean dragged;

	public AdvancedZoomInTool() {
		super();
		startPosDevice = new Point();
		startPosWorld = new DirectPosition2D();
		dragged = false;
	}

	@Override
	public void onMouseClicked(final MapMouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON3) {
			super.onMouseClicked(e);
		}
	}

	/**
	 * Records that the mouse is being dragged
	 *
	 * @param ev the mouse event
	 */
	@Override
	public void onMouseDragged(final MapMouseEvent ev) {
		dragged = true;
		super.onMouseDragged(ev);
	}

	@Override
	public void onMousePressed(final MapMouseEvent ev) {
		if (ev.getButton() != MouseEvent.BUTTON3) {
			startPosDevice.setLocation(ev.getPoint());
			startPosWorld.setLocation(ev.getWorldPos());
			super.onMousePressed(ev);
		}
	}

	@Override
	public void onMouseReleased(final MapMouseEvent ev) {
		if (dragged && !ev.getPoint().equals(startPosDevice) && ev.getButton() != MouseEvent.BUTTON3) {
			final int overallX = ev.getX() - startPosDevice.x;
			final int overallY = ev.getY() - startPosDevice.y;

			// if the drag was from TL to BR
			if (overallX >= 0 || overallY >= 0) {
				final MapViewport view = ev.getSource().getMapContent().getViewport();
				final ReferencedEnvelope existingArea = view.getBounds();
				// If we are not too zoomed in
				if (existingArea.getArea() > 1e-10) {
					super.onMouseReleased(ev);
				}
			} else {
				performZoomOut(ev);
			}
			DebriefLiteApp.getInstance().updateProjectionArea();
		}
	}

	public void performZoomOut(final MapMouseEvent ev) {
		/**
		 * note - there's quite a bit of code commented out in this method. The
		 * commented out code is a partial implementation of the zoom out behaviour in
		 * Full Debrief.
		 */

		final MapViewport view = ev.getSource().getMapContent().getViewport();
		final ReferencedEnvelope existingArea = view.getBounds();
		final DirectPosition2D startWorld = new DirectPosition2D(startPosWorld);
		final Envelope2D selectedArea = new Envelope2D(startWorld, ev.getWorldPos());
		final DirectPosition2D desiredCenter = new DirectPosition2D(selectedArea.getCenterX(),
				selectedArea.getCenterY());
		final Coordinate centerC = ev.getSource().getMapContent().getViewport().getBounds().centre();
		final DirectPosition2D actualCenter = new DirectPosition2D(centerC.x, centerC.y);
		// final double deltaX = actualCenter.getX() - desiredCenter.getX();
		// final double deltaY = actualCenter.getY() - desiredCenter.getY();

		// double scale = view.getWorldToScreen().getScaleX();
		// scale = Math.min(1000, scale);
		// double newScale = scale;

		// Rectangle paneArea = view.getScreenArea();

		final double scaleVal = Math.sqrt(
				(existingArea.getHeight() * existingArea.getWidth()) / (selectedArea.height * selectedArea.width));

		// only allow zoom out if we're not already too far our
		if (existingArea.getArea() < LiteMapPane.MAX_MAP_AREA) {
			final double deltaX3 = existingArea.getMaxX() - actualCenter.getX();
			final double deltaY3 = existingArea.getMinY() - actualCenter.getY();

			final DirectPosition2D corner = new DirectPosition2D(desiredCenter.x + deltaX3 * scaleVal,
					desiredCenter.y + deltaY3 * scaleVal);

			final Envelope2D newMapArea = new Envelope2D();
			newMapArea.setFrameFromCenter(desiredCenter, corner);

			ev.getSource().setDisplayArea(newMapArea);
		}
	}
}
