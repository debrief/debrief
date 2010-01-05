/**
 * 
 */
package org.mwc.debrief.core.actions.drag;

import java.awt.Point;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.actions.DragSegment.IconProvider;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class StretchFanOperation implements DraggableItem, IconProvider
{

	final private TrackSegment _mySegment;
	final private Layers _layers;
	private TrackWrapper _parent;

	public StretchFanOperation(TrackSegment segment, TrackWrapper parent, Layers theLayers)
	{
		_mySegment = segment;
		_layers = theLayers;
		_parent = parent;
		
	}
	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			LocationConstruct currentNearest, Layer parentLayer, Layers theLayers)
	{
	}

	public String getName()
	{
		return "centre point";
	}

	public void paint(CanvasType dest)
	{
		_mySegment.paint(dest);
	}

	public void shift(WorldVector vector)
	{
		// right, check that this is a segment that we can do business with.
		if(_mySegment instanceof RelativeTMASegment)
		{
			RelativeTMASegment seg = (RelativeTMASegment) _mySegment;			
			
			// tell it to do a fan stretch
			seg.fanStretch(vector);
			
			
			// and tell the props view to update itself
			RotateDragMode.updatePropsView(seg, _parent, _layers);

		}
		else
		{
			System.err.println("can't do it!");
		}
		
	}

	public Cursor getHotspotCursor()
	{
	  return new Cursor(Display.getDefault(), DebriefPlugin
				.getImageDescriptor("icons/SelectFeatureHitFanStretch.ico").getImageData(), 4,
				2);	
	}
}