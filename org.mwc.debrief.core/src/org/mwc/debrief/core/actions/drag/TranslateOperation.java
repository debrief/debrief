/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
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

import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldVector;

public class TranslateOperation extends CoreDragOperation implements
		DraggableItem, IconProvider
{
	public TranslateOperation(final TrackSegment segment)
	{
		super(segment, "centre point");
	}

	public void shift(final WorldVector vector)
	{
		//
		_segment.shift(vector);

		// tell the segment it's shifted
		_segment.clearBounds();
	}

	public Cursor getHotspotCursor()
	{
		return CursorRegistry.getCursor(CursorRegistry.SELECT_FEATURE_HIT_DRAG);
	}
}