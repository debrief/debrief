/**
 * 
 */
package org.mwc.debrief.core.actions.drag;

import java.awt.Point;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.actions.DragSegment.IconProvider;

import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class TranslateOperation implements DraggableItem, IconProvider
{

	final private TrackSegment _mySegment;

	public TranslateOperation(TrackSegment segment)
	{
		_mySegment = segment;
		
	}
	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			LocationConstruct currentNearest, Layer parentLayer, Layers theData)
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
		//
		_mySegment.shift(vector);
	}

	public Cursor getHotspotCursor()
	{
	  return new Cursor(Display.getDefault(), DebriefPlugin
				.getImageDescriptor("icons/SelectFeatureHitDrag.ico").getImageData(), 4,
				2);	
	}
}