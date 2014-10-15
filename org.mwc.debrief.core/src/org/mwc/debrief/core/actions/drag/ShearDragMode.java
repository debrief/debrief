/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.mwc.cmap.core.CursorRegistry;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class ShearDragMode extends RotateDragMode
{

	public static class ShearOperation extends RotateOperation
	{

		private WorldLocation _lastLoc;
		
		public ShearOperation(final WorldLocation cursorLoc, final WorldLocation origin,
				final CoreTMASegment segment, final TrackWrapper parentTrack, final Layers theLayers)
		{
			super(cursorLoc, origin, segment, parentTrack, theLayers);
		}

		public void shift(final WorldVector vector)
		{
			// find out where the cursor currently is
			workingLoc.addToMe(vector);

			final CoreTMASegment seg = (CoreTMASegment) _segment;
			
			// undo the previous turn
			if (_lastLoc != null)
			{
				seg.shear(_lastLoc, _origin);
			}

			seg.shear(workingLoc, _origin);
			
			// tell the segment it's shifted
			seg.clearBounds();

			// and remember it
			_lastLoc = new WorldLocation(workingLoc);

			// and tell the props view to update itself
			updatePropsView(seg, _parent, _layers);
		}

		public Cursor getHotspotCursor()
		{
			return CursorRegistry.getCursor(CursorRegistry.SELECT_FEATURE_HIT_SHEAR);
		}
	}

	public ShearDragMode()
	{
		super("Shear", "Apply shear operation to TMA solution");
	}

	@Override
	protected DraggableItem getEndOperation(final WorldLocation cursorLoc,
			final TrackSegment seg, final FixWrapper origin, final TrackWrapper parent, final Layers theLayers)
	{
		return new ShearOperation(cursorLoc, origin.getFixLocation(),
				(CoreTMASegment) seg, parent, theLayers);
	}

	@Override
	protected boolean isAcceptable(final TrackSegment seg)
	{
		return (seg instanceof CoreTMASegment);
	}

}
