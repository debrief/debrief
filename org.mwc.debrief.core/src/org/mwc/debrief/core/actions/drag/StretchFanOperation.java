/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.mwc.cmap.core.CursorRegistry;
import org.mwc.debrief.core.actions.DragSegment.IconProvider;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldVector;

public class StretchFanOperation extends CoreDragOperation implements DraggableItem, IconProvider
{

	final private Layers _layers;
	private final TrackWrapper _parent;

	public StretchFanOperation(final TrackSegment segment, final TrackWrapper parent,
			final Layers theLayers)
	{
		super(segment, "centre point");
		_layers = theLayers;
		_parent = parent;

	}


	public void shift(final WorldVector vector)
	{
		// right, check that this is a segment that we can do business with.
		if (_segment instanceof RelativeTMASegment)
		{
			final RelativeTMASegment seg = (RelativeTMASegment) _segment;

			// tell it to do a fan stretch
			seg.fanStretch(vector);

			// tell the segment it's shifted
			seg.clearBounds();

			// and tell the props view to update itself
			updatePropsView(seg, _parent, _layers);

		}
		else
		{
			System.err.println("can't do it!");
		}

	}

	public Cursor getHotspotCursor()
	{
		return CursorRegistry.getCursor(CursorRegistry.SELECT_FEATURE_HIT_FAN_STRETCH);
	}
}