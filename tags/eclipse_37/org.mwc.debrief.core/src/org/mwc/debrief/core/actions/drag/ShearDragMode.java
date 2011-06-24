package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.core.DebriefPlugin;

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

		public ShearOperation(WorldLocation cursorLoc, WorldLocation origin,
				CoreTMASegment segment, TrackWrapper parentTrack, Layers theLayers)
		{
			super(cursorLoc, origin, segment, parentTrack, theLayers);
		}

		public void shift(WorldVector vector)
		{
			// find out where the cursor currently is
			workingLoc.addToMe(vector);

			CoreTMASegment seg = (CoreTMASegment) _segment;

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
			return new Cursor(Display.getDefault(), DebriefPlugin.getImageDescriptor(
					"icons/SelectFeatureHitShear.ico").getImageData(), 4, 2);
		}
	}

	public ShearDragMode()
	{
		super("Shear", "Apply shear operation to TMA solution");
	}

	@Override
	protected DraggableItem getEndOperation(WorldLocation cursorLoc,
			TrackSegment seg, FixWrapper subject, TrackWrapper parent, Layers theLayers)
	{
		return new ShearOperation(cursorLoc, subject.getFixLocation(),
				(CoreTMASegment) seg, parent, theLayers);
	}

	@Override
	protected boolean isAcceptable(TrackSegment seg)
	{
		return (seg instanceof CoreTMASegment);
	}

}
