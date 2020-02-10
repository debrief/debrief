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

import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GUI.Shapes.FindNearest;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class DragWholeFeatureElementTool extends GenericDragTool {

	/**
	 * the thing we're currently hovering over
	 */
	protected DraggableItem _hoverTarget;

	public DragWholeFeatureElementTool(final Layers layers, final GeoToolMapProjection projection,
			final JMapPane mapPane) {
		super(layers, projection, mapPane);
	}

	/**
	 * Respond to a mouse dragged event. Calls
	 * {@link org.geotools.swing.MapPane#moveImage()}
	 *
	 * @param ev the mouse event
	 */
	@Override
	public void onMouseDragged(final MapMouseEvent ev) {
		if (panning) {
			final Point pos = mouseDelta(ev.getPoint());

			if (!pos.equals(panePos)) {
				final WorldLocation cursorLoc = _projection.toWorld(panePos);

				if (_hoverTarget != null) {
					final WorldLocation newLocation = new WorldLocation(_projection.toWorld(pos));

					// now work out the vector from the last place plotted to the current
					// place
					final WorldVector offset = newLocation.subtract(cursorLoc);

					_hoverTarget.shift(offset);
					_mapPane.repaint();
				}
				panePos = pos;
			}
		}
	}

	/**
	 * Respond to a mouse button press event from the map mapPane. This may signal
	 * the start of a mouse drag. Records the event's window position.
	 *
	 * @param ev the mouse event
	 */
	@Override
	public void onMousePressed(final MapMouseEvent ev) {
		super.onMousePressed(ev);

		final double currentArea = _mapPane.getMapContent().getViewport().getBounds().getArea();

		if (currentArea < LiteMapPane.MAX_MAP_AREA && !panning) {
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

			// did we find anything?
			if (currentNearest.populated()) {
				// generate a screen point from the cursor pos plus our distnace
				// NOTE: we're not basing this on the target location - we may not have
				// a
				// target location as such for a strangely shaped object
				final WorldLocation tgtPt = cursorLoc.add(new WorldVector(Math.PI / 2, currentNearest._distance, null));

				// is it close enough
				final Point tPoint = _projection.toScreen(tgtPt);

				// get click point
				final Point cursorPos = ev.getPoint();

				// get distance of click point from nearest object, in screen coords
				final double distance = tPoint.distance(cursorPos);
				if (distance < JITTER) {
					panning = true;

					_hoverTarget = currentNearest._object;
					_parentLayer = currentNearest._topLayer;
				}
			}
		}
	}
}
