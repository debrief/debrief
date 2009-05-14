package org.mwc.debrief.core.actions.drag;

import java.awt.Point;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.actions.DragSegment.IconProvider;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class ShearDragMode extends FreeDragMode
{

	
	public static class ShearOperation implements DraggableItem, IconProvider
	{
		WorldLocation workingLoc;
		double originalBearing;
		WorldLocation _origin;
		Double lastRotate = null;
		TrackSegment _segment;
	
		public ShearOperation(WorldLocation cursorLoc, WorldLocation origin,
				TrackSegment segment)
		{
			workingLoc = cursorLoc;
			_origin = origin;
			originalBearing = cursorLoc.subtract(_origin).getBearing();
			_segment = segment;
	
		}
	
		public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
				LocationConstruct currentNearest, Layer parentLayer)
		{
		}
	
		public String getName()
		{
			return "end point";
		}
	
		public void paint(CanvasType dest)
		{
			_segment.paint(dest);
		}
	
		public void shift(WorldVector vector)
		{
			// find out where the cursor currently is
			workingLoc.addToMe(vector);
	
			// what's the bearing from the origin
			WorldVector thisVector = workingLoc.subtract(_origin);
	
			// work out the vector (bearing) from the start
			double brg = originalBearing - thisVector.getBearing();
	
			// undo the previous turn
			if (lastRotate != null)
			{
				_segment.rotate(-lastRotate, _origin);
			}
	
			_segment.rotate(brg, _origin);
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
		return new ShearOperation(cursorLoc, subject.getFixLocation(), seg);
	}
	
	
	
}
