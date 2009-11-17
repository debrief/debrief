package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class StretchDragMode extends RotateDragMode
{

	
	public static class StretchOperation extends RotateOperation
	{
		private Double lastRange;

		public StretchOperation(WorldLocation cursorLoc, WorldLocation origin,
				CoreTMASegment segment)
		{
			super(cursorLoc, origin, segment);
		}

		public void shift(WorldVector vector)
		{
			CoreTMASegment seg = (CoreTMASegment) _segment;
			
			// find out where the cursor currently is
			workingLoc.addToMe(vector);
	
			// what's the bearing from the origin
			WorldVector thisVector = workingLoc.subtract(_origin);
	
			// work out the distance from the start
			double rng =  thisVector.getRange(); //- _originalDistDegs;
	
			// undo the previous turn
			if (lastRange != null)
			{
				seg.stretch(-lastRange, _origin);
			}
	
			// now do the current one
			seg.stretch(rng, _origin);
			
			// and remember it
			lastRange = new Double(rng);
			
		}
	
		public Cursor getHotspotCursor()
		{
		  return new Cursor(Display.getDefault(), DebriefPlugin
					.getImageDescriptor("icons/SelectFeatureHitStretch.ico").getImageData(), 4,
					2);	
		}
	}
	
	/**
	 * generate an operation for when the centre of the line segment is dragged
	 * 
	 * @param seg
	 *          the segment being dragged
	 * @return an operation we can use to do this
	 */
	protected DraggableItem getCentreOperation(final TrackSegment seg)
	{
		return new StretchFanOperation(seg);
	}


	public StretchDragMode()
	{
		super("Stretch", "Apply stretch operation to TMA solution");
	}

	@Override
	protected DraggableItem getEndOperation(WorldLocation cursorLoc,
			TrackSegment seg, FixWrapper subject)
	{
		return new StretchOperation(cursorLoc, subject.getFixLocation(), (CoreTMASegment) seg);
	}

	@Override
	protected boolean isAcceptable(TrackSegment seg)
	{
		return (seg instanceof CoreTMASegment);
	}		
	
	
}
