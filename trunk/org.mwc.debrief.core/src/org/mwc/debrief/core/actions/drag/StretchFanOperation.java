/**
 * 
 */
package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
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
	private TrackWrapper _parent;
	private Cursor _hotspotCursor;

	public StretchFanOperation(TrackSegment segment, TrackWrapper parent,
			Layers theLayers)
	{
		super(segment, "centre point");
		_layers = theLayers;
		_parent = parent;

	}


	public void shift(WorldVector vector)
	{
		// right, check that this is a segment that we can do business with.
		if (_segment instanceof RelativeTMASegment)
		{
			RelativeTMASegment seg = (RelativeTMASegment) _segment;

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
		if (_hotspotCursor == null)
			_hotspotCursor = new Cursor(Display.getDefault(), DebriefPlugin
					.getImageDescriptor("icons/SelectFeatureHitFanStretch.ico")
					.getImageData(), 4, 2);
		return _hotspotCursor;
	}
}