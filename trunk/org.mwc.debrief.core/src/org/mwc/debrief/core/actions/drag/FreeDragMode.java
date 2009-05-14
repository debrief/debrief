package org.mwc.debrief.core.actions.drag;

import java.awt.Point;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.actions.DragSegment.DragMode;
import org.mwc.debrief.core.actions.DragSegment.IconProvider;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class FreeDragMode extends DragMode
{

	public FreeDragMode()
	{
		super("Speed=", "Vary course, maintain speed of TMA solution");
	}
	
	public Cursor getHotspotCursor()
	{
		return new Cursor(Display.getDefault(), DebriefPlugin
				.getImageDescriptor("icons/SelectFeatureHitRotate.ico").getImageData(), 4,
				2);		
	}


	protected TrackSegment findNearest(TrackWrapper track, WorldLocation loc)
	{
		double res = -1;
		TrackSegment nearest = null;
		Enumeration<Editable> items = track.elements();
		while (items.hasMoreElements())
		{
			Editable editable = (Editable) items.nextElement();
			if (editable instanceof SegmentList)
			{
				SegmentList segList = (SegmentList) editable;
				Collection<Editable> segments = segList.getData();
				for (Iterator<Editable> iterator = segments.iterator(); iterator
						.hasNext();)
				{
					TrackSegment thisSeg = (TrackSegment) iterator.next();
					double thisRes = thisSeg.rangeFrom(loc);
					if (nearest == null)
					{
						nearest = thisSeg;
						res = thisRes;
					}
					else
					{
						if (thisRes < res)
						{
							nearest = thisSeg;
							res = thisRes;
						}
					}
				}

			}
			else if (editable instanceof TrackSegment)
			{
				TrackSegment thisSeg = (TrackSegment) editable;
				double thisRes = thisSeg.rangeFrom(loc);
				if (nearest == null)
				{
					nearest = thisSeg;
					res = thisRes;
				}
				else
				{
					if (thisRes < res)
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
	public void findNearest(Layer thisLayer, final WorldLocation cursorLoc,
			Point cursorPos, LocationConstruct currentNearest, Layer parentLayer)
	{
		/**
		 * we need to get the following hit points, both ends (to support rotate),
		 * and the middle (to support drag)
		 */
		if (thisLayer instanceof TrackWrapper)
		{
			TrackWrapper track = (TrackWrapper) thisLayer;

			// find the nearest segment
			final TrackSegment seg = findNearest(track, cursorLoc);
			final FixWrapper first = (FixWrapper) seg.first();
			final FixWrapper last = (FixWrapper) seg.last();
			WorldLocation firstLoc = first.getFixLocation();
			WorldLocation lastLoc = last.getFixLocation();
			WorldArea lineBounds = new WorldArea(firstLoc, lastLoc);
			WorldLocation centreLoc = lineBounds.getCentre();

			WorldDistance firstDist = calcDist(firstLoc, cursorLoc);
			WorldDistance lastDist = calcDist(lastLoc, cursorLoc);
			WorldDistance centreDist = calcDist(centreLoc, cursorLoc);

			DraggableItem centreEnd = new DragOperation(seg);
			DraggableItem firstEnd = new RotateOperation(cursorLoc, last
					.getFixLocation(), seg);

			DraggableItem lastEnd = new RotateOperation(cursorLoc, first
					.getFixLocation(), seg);

			currentNearest.checkMe(firstEnd, firstDist, null, thisLayer);
			currentNearest.checkMe(lastEnd, lastDist, null, thisLayer);
			currentNearest.checkMe(centreEnd, centreDist, null, thisLayer);

		}
	}

	private WorldDistance calcDist(WorldLocation myLoc, WorldLocation cursorLoc)
	{
		return new WorldDistance(myLoc.subtract(cursorLoc).getRange(),
				WorldDistance.DEGS);

	}

	/*
	 * Public Function RotatePoint(ByRef pPoint As POINT, ByRef pOrigin As POINT,
	 * _ ByVal Degrees As Single) As POINT RotatePoint.X = pOrigin.X + (
	 * Cos(D2R(Degrees)) * (pPoint.X - pOrigin.X) - _ Sin(D2R(Degrees)) *
	 * (pPoint.Y - pOrigin.Y) ) RotatePoint.Y = pOrigin.Y + ( Sin(D2R(Degrees)) *
	 * (pPoint.X - pOrigin.X) + _ Cos(D2R(Degrees)) * (pPoint.Y - pOrigin.Y) ) End
	 * Function
	 */



	public static class DragOperation implements DraggableItem, IconProvider
	{

		final private TrackSegment _mySegment;

		public DragOperation(TrackSegment segment)
		{
			_mySegment = segment;
			
		}
		public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
				LocationConstruct currentNearest, Layer parentLayer)
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

	public static class RotateOperation implements DraggableItem, IconProvider
	{
		WorldLocation workingLoc;
		double originalBearing;
		WorldLocation _origin;
		Double lastRotate = null;
		TrackSegment _segment;

		public RotateOperation(WorldLocation cursorLoc, WorldLocation origin,
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
					.getImageDescriptor("icons/SelectFeatureHitRotate.ico").getImageData(), 4,
					2);	
		}
	};

}
