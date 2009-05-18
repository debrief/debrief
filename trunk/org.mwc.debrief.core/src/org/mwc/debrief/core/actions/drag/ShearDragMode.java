package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class ShearDragMode extends RotateDragMode
{

	
	public static class ShearOperation  extends RotateOperation
	{

		public ShearOperation(WorldLocation cursorLoc, WorldLocation origin,
				TMASegment segment)
		{
			super(cursorLoc, origin, segment);
		}

		public void shift(WorldVector vector)
		{
			// find out where the cursor currently is
			workingLoc.addToMe(vector);
	
			// what's the bearing from the origin
			WorldVector thisVector = workingLoc.subtract(_origin);
	
			// work out the vector (bearing) from the start
			double brg = originalBearing - thisVector.getBearing();
			
			// work out the distance from the start
			double rng =  thisVector.getRange(); //- _originalDistDegs;
			
			TMASegment seg = (TMASegment) _segment;
		
			// undo the previous turn
			if (lastRotate != null)
			{
				seg.stretch(-rng, _origin);
				seg.rotate(-lastRotate, _origin);
			}
	
			seg.rotate(brg, _origin);
			seg.stretch(rng, _origin);
			
			// and remember it
			lastRotate = new Double(brg);
		}
	
		public Cursor getHotspotCursor()
		{
		  return new Cursor(Display.getDefault(), DebriefPlugin
					.getImageDescriptor("icons/SelectFeatureHitShear.ico").getImageData(), 4,
					2);	
		}
	}

	public ShearDragMode()
	{
		super("Shear", "Apply shear operation to TMA solution");
	}

	@Override
	protected DraggableItem getEndOperation(WorldLocation cursorLoc,
			TrackSegment seg, FixWrapper subject)
	{
		return new ShearOperation(cursorLoc, subject.getFixLocation(), (TMASegment) seg);
	}
	
	@Override
	protected boolean isAcceptable(TrackSegment seg)
	{
		return (seg instanceof TMASegment);
	}		
	
}
