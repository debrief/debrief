package org.mwc.debrief.core.actions.drag;

import java.awt.Point;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.mwc.debrief.core.actions.DragSegment.DragMode;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.TrackWrapper_Support.SegmentList;
import Debrief.Wrappers.TrackWrapper_Support.TrackSegment;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class FreeDragMode extends DragMode {

	public FreeDragMode() {
		super("free");
	}
	
	protected TrackSegment findNearest(TrackWrapper track, WorldLocation loc)
	{
		double res = -1;
		TrackSegment nearest = null;
		Enumeration<Editable> items = track.elements();
		while (items.hasMoreElements()) {
			Editable editable = (Editable) items.nextElement();
			if(editable instanceof SegmentList)
			{
				SegmentList segList = (SegmentList) editable;
				Collection<Editable> segments = segList.getData();
				for (Iterator<Editable> iterator = segments.iterator(); iterator
						.hasNext();) {
					TrackSegment thisSeg = (TrackSegment) iterator.next();
					double thisRes  = thisSeg.rangeFrom(loc);
					if(nearest == null)
					{
						nearest = thisSeg;
						res = thisRes;
					}
					else
					{
						if(thisRes < res)
						{
							nearest = thisSeg;
							res = thisRes;
						}
					}
				}
				
			}else if(editable instanceof TrackSegment)
			{
				TrackSegment thisSeg = (TrackSegment) editable;
				double thisRes  = thisSeg.rangeFrom(loc);
				if(nearest == null)	
				{
					nearest = thisSeg;
					res = thisRes;
				}
				else
				{
					if(thisRes < res)
					{
						nearest = thisSeg;
						res = thisRes;
					}
				}
				
			}
		}
		return nearest;
	
	}

	@Override
	public void findNearest(Layer thisLayer, WorldLocation cursorLoc,
			Point cursorPos, LocationConstruct currentNearest, Layer parentLayer) {
		/** we need to get the following hit points, both ends (to support rotate), and the middle
		 * (to support drag)
		 */
		if(thisLayer instanceof TrackWrapper)
		{
			TrackWrapper track = (TrackWrapper) thisLayer;
			
			// find the nearest segment
			final TrackSegment seg = findNearest(track, cursorLoc);
			FixWrapper first = (FixWrapper) seg.first();
			FixWrapper last = (FixWrapper) seg.last();
			WorldLocation firstLoc = first.getFixLocation();
			WorldLocation lastLoc = last.getFixLocation();
			WorldArea lineBounds = new WorldArea(firstLoc, lastLoc);
			WorldLocation centreLoc = lineBounds.getCentre();

			WorldDistance firstDist = calcDist(firstLoc, cursorLoc);
			WorldDistance lastDist = calcDist(lastLoc, cursorLoc);
			WorldDistance centreDist = calcDist(centreLoc, cursorLoc);
			
			DraggableItem centreEnd = new DraggableItem(){

				@Override
				public void findNearestHotSpotIn(Point cursorPos,
						WorldLocation cursorLoc,
						LocationConstruct currentNearest, Layer parentLayer) {
				}

				@Override
				public String getName() {
					return "centre point";
				}

				@Override
				public void paint(CanvasType dest) {
					seg.paint(dest);
				}

				@Override
				public void shift(WorldVector vector) {
					//
					seg.shift(vector);
				}};
				DraggableItem firstEnd = new DraggableItem(){

					@Override
					public void findNearestHotSpotIn(Point cursorPos,
							WorldLocation cursorLoc,
							LocationConstruct currentNearest, Layer parentLayer) {
					}

					@Override
					public String getName() {
						return "Start point";
					}

					@Override
					public void paint(CanvasType dest) {
						seg.paint(dest);
					}

					@Override
					public void shift(WorldVector vector) {
						//
						System.err.println("rotate the track about the first point");
					}};;
					DraggableItem lastEnd = new DraggableItem(){

						@Override
						public void findNearestHotSpotIn(Point cursorPos,
								WorldLocation cursorLoc,
								LocationConstruct currentNearest, Layer parentLayer) {
						}

						@Override
						public String getName() {
							return "end	 point";
						}

						@Override
						public void paint(CanvasType dest) {
							seg.paint(dest);
						}

						@Override
						public void shift(WorldVector vector) {
							//
							System.err.println("rotate the track about the last point");
						}};;
						
			currentNearest.checkMe(firstEnd, firstDist,
					 null, parentLayer);
			currentNearest.checkMe(lastEnd, lastDist,
					 null, parentLayer);
			currentNearest.checkMe(centreEnd, centreDist,
					 null, parentLayer);
			
		}
		else
		{
			System.err.println("WE SHOULD HAVE A TRACK" + thisLayer);
		}
	}
	

	private WorldDistance calcDist(WorldLocation myLoc, WorldLocation cursorLoc)
	{
		return new WorldDistance(myLoc.subtract(cursorLoc).getRange(), WorldDistance.DEGS);
		
	}
	

}
