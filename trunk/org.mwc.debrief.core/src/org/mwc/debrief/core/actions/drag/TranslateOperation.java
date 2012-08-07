/**
 * 
 */
package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.actions.DragSegment.IconProvider;

import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldVector;

public class TranslateOperation extends CoreDragOperation implements
		DraggableItem, IconProvider
{
	private Cursor _hotspotCursor;

	public TranslateOperation(TrackSegment segment)
	{
		super(segment, "centre point");
	}

	public void shift(WorldVector vector)
	{
		//
		_segment.shift(vector);

		// tell the segment it's shifted
		_segment.clearBounds();
	}

	public Cursor getHotspotCursor()
	{
		if (_hotspotCursor == null)
			_hotspotCursor = new Cursor(Display.getDefault(), DebriefPlugin
					.getImageDescriptor("icons/SelectFeatureHitDrag.ico").getImageData(),
					4, 2);
		return _hotspotCursor;

	}
}