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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GUI.Shapes.FindNearest;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class GenericDragTool extends CursorTool {

	/** Tool name */
	public static final String TOOL_NAME = "Drag Element";

	/** Tool tip text */
	public static final String TOOL_TIP = "Drag Element";

	/** Cursor */
	public static final String CURSOR_IMAGE = "/icons/16/whitehand.png";

	/** Icon for the control */
	public static final String ICON_IMAGE = "/icons/16/whitehand.png";

	/** Icon for the control */
	public static final String ICON_IMAGE_GREEN = "/icons/16/SelectFeatureHit.png";

	/** Icon for the control */
	public static final String ICON_IMAGE_DRAGGING = "/icons/16/SelectFeatureHitDown.png";

	/** Cursor hotspot coordinates */
	public static final Point CURSOR_HOTSPOT = new Point(15, 15);

	/**
	 * how close we have to be (in screen pixels) to display hotspot cursor
	 */
	private static double SCREEN_JITTER = 11;

	protected final Cursor normalCursor;

	protected final Cursor greenCursor;

	protected final Cursor draggingCursor;

	/**
	 * We are going to use this to avoid re-assigning the same cursor.
	 */
	protected Cursor lastCursor;

	protected boolean panning;

	protected Point panePos;

	protected final Layers layers;

	protected final GeoToolMapProjection _projection;

	protected final JMapPane _mapPane;

	/**
	 * the component we're going to drag
	 */
	protected WorldLocation _hoverComponent;

	/**
	 * the layer to update when dragging is complete
	 */
	protected Layer _parentLayer;

	/**
	 * how far the mouse has to be dragged before it's registered as a drag
	 * operation
	 */
	protected final double JITTER = SCREEN_JITTER;

	public GenericDragTool(final Layers _layers, final GeoToolMapProjection projection, final JMapPane mapPane) {
		final Toolkit tk = Toolkit.getDefaultToolkit();
		final ImageIcon imgIcon = new ImageIcon(getClass().getResource(CURSOR_IMAGE));
		normalCursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT, TOOL_NAME);

		final ImageIcon imgGreenIcon = new ImageIcon(getClass().getResource(ICON_IMAGE_GREEN));
		greenCursor = tk.createCustomCursor(imgGreenIcon.getImage(), CURSOR_HOTSPOT, TOOL_NAME);

		final ImageIcon imgDragIcon = new ImageIcon(getClass().getResource(ICON_IMAGE_DRAGGING));
		draggingCursor = tk.createCustomCursor(imgDragIcon.getImage(), CURSOR_HOTSPOT, TOOL_NAME);

		lastCursor = normalCursor;

		this.layers = _layers;
		this._projection = projection;
		this._mapPane = mapPane;
	}

	/**
	 * Returns false to indicate that this tool does not draw a box on the map
	 * display when the mouse is being dragged
	 */
	@Override
	public boolean drawDragBox() {
		return false;
	}

	/** Get the mouse cursor for this tool */
	@Override
	public Cursor getCursor() {
		return normalCursor;
	}

	/**
	 * Move the point a bit to the upper left corner to adjust the animation with
	 * the mouse icon.
	 *
	 * @param originalPoint
	 * @return
	 */
	protected Point mouseDelta(final Point originalPoint) {
		return new Point(originalPoint.x - 10, originalPoint.y - 10);
	}

	@Override
	public void onMouseMoved(final MapMouseEvent ev) {
		super.onMouseMoved(ev);

		// try to determine if we're going over an item, to
		// change the cursor

		if (LiteMapPane.isViewportAcceptable(_mapPane)) {

			// don't bother if we're already in a pan operation
			if (!panning && !lastCursor.equals(draggingCursor)) {
				panePos = mouseDelta(ev.getPoint());

				final WorldLocation cursorLoc = _projection.toWorld(panePos);
				// find the nearest editable item
				final LocationConstruct currentNearest = new LocationConstruct();
				final int num = layers.size();
				for (int i = 0; i < num; i++) {
					final Layer thisL = layers.elementAt(i);
					if (thisL.getVisible()) {
						// find the nearest items, this method call will recursively pass down
						// through
						// the layers
						// final Layer thisLayer,
						FindNearest.findNearest(thisL, cursorLoc, panePos, currentNearest, null, layers);
					}
				}

				// Note - the following test does a distance check using world distance,
				// which is quite unreliable,

				// did we find anything?
				if (currentNearest.populated()) {
					// generate a screen point from the cursor pos plus our distnace
					// NOTE: we're not basing this on the target location - we may not have
					// a
					// target location as such for a strangely shaped object
					final WorldLocation tgtPt = cursorLoc
							.add(new WorldVector(Math.PI / 2, currentNearest._distance, null));

					// is it close enough
					final Point tPoint = _projection.toScreen(tgtPt);

					// get click point
					final Point cursorPos = ev.getPoint();

					// get distance of click point from nearest object, in screen coords
					final double scrDist = tPoint.distance(cursorPos);

					if (scrDist <= SCREEN_JITTER && !lastCursor.equals(greenCursor)) {
						lastCursor = greenCursor;
						_mapPane.setCursor(greenCursor);
					} else if (scrDist > SCREEN_JITTER && !lastCursor.equals(normalCursor)) {
						lastCursor = normalCursor;
						_mapPane.setCursor(normalCursor);
					}
				}
			}
		}
	}

	@Override
	public void onMousePressed(final MapMouseEvent ev) {
		if (lastCursor.equals(greenCursor)) {
			lastCursor = draggingCursor;
			_mapPane.setCursor(draggingCursor);
		}
	}

	/**
	 * If this button release is the end of a mouse dragged event, requests the map
	 * mapPane to repaint the display
	 *
	 * @param ev the mouse event
	 */
	@Override
	public void onMouseReleased(final MapMouseEvent ev) {
		panning = false;

		lastCursor = greenCursor;
		_mapPane.setCursor(greenCursor);
	}
}
