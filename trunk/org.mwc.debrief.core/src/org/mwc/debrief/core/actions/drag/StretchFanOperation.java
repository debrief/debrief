/**
 * 
 */
package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CursorRegistry;
import org.mwc.debrief.core.DebriefPlugin;
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