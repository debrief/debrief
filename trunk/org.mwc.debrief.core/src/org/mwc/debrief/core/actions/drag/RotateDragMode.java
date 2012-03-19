package org.mwc.debrief.core.actions.drag;

import java.awt.Point;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.actions.DragSegment.DragMode;
import org.mwc.debrief.core.actions.DragSegment.IconProvider;
import org.mwc.debrief.core.preferences.PrefsPage;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class RotateDragMode extends DragMode
{

	public RotateDragMode()
	{
		this("Rotate", "Vary course, maintain speed of TMA solution");
	}

	public RotateDragMode(String title, String toolTip)
	{
		super(title, toolTip);
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
					if (thisSeg.getVisible())
					{
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

			}
			else if (editable instanceof TrackSegment)
			{
				TrackSegment thisSeg = (TrackSegment) editable;
				if (thisSeg.getVisible())
				{
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
		}
		return nearest;

	}

	@Override
	public void findNearest(Layer thisLayer, final WorldLocation cursorLoc,
			Point cursorPos, LocationConstruct currentNearest, Layer parentLayer,
			Layers theLayers)
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

			// see if it's a candidate (we may only want TMASegments)
			if (isAcceptable(seg))
			{
				final FixWrapper first = (FixWrapper) seg.first();
				final FixWrapper last = (FixWrapper) seg.last();
				WorldLocation firstLoc = first.getFixLocation();
				WorldLocation lastLoc = last.getFixLocation();
				WorldArea lineBounds = new WorldArea(firstLoc, lastLoc);
				WorldLocation centreLoc = lineBounds.getCentre();

				WorldDistance firstDist = calcDist(firstLoc, cursorLoc);
				WorldDistance lastDist = calcDist(lastLoc, cursorLoc);
				WorldDistance centreDist = calcDist(centreLoc, cursorLoc);

				DraggableItem centreEnd = getCentreOperation(seg, track, theLayers);
				DraggableItem firstEnd = getEndOperation(cursorLoc, seg, last, track,
						theLayers);
				DraggableItem lastEnd = getEndOperation(cursorLoc, seg, first, track,
						theLayers);

				currentNearest.checkMe(firstEnd, firstDist, null, thisLayer);
				currentNearest.checkMe(lastEnd, lastDist, null, thisLayer);
				currentNearest.checkMe(centreEnd, centreDist, null, thisLayer);
			}
		}
	}

	/**
	 * whether this type of track is suitable for our operation
	 * 
	 * @param seg
	 * @return
	 */
	protected boolean isAcceptable(TrackSegment seg)
	{
		return true;
	}

	/**
	 * generate an operation for when the end of the line segment is dragged
	 * 
	 * @param cursorLoc
	 *          where the cursor is
	 * @param seg
	 *          the segment that's being dragged
	 * @param subject
	 *          which end we're manipulating
	 * @param parentTrack
	 * @return
	 */
	protected DraggableItem getEndOperation(final WorldLocation cursorLoc,
			final TrackSegment seg, final FixWrapper subject,
			TrackWrapper parentTrack, Layers theLayers)
	{
		return new RotateOperation(cursorLoc, subject.getFixLocation(), seg,
				parentTrack, theLayers);
	}

	/**
	 * generate an operation for when the centre of the line segment is dragged
	 * 
	 * @param seg
	 *          the segment being dragged
	 * @param parent
	 *          the parent track for this segment
	 * @param theLayers
	 *          the set of layers data
	 * @return an operation we can use to do this
	 */
	protected DraggableItem getCentreOperation(final TrackSegment seg,
			TrackWrapper parent, Layers theLayers)
	{
		return new TranslateOperation(seg);
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

	public static class RotateOperation implements DraggableItem, IconProvider
	{
		WorldLocation workingLoc;
		double originalBearing;
		WorldLocation _origin;
		Double lastRotate = null;
		TrackSegment _segment;
		protected TrackWrapper _parent;
		protected Layers _layers;
		protected Cursor _hotspotCursor;

		public RotateOperation(WorldLocation cursorLoc, WorldLocation origin,
				TrackSegment segment, TrackWrapper parentTrack, Layers theLayers)
		{
			workingLoc = cursorLoc;
			_origin = origin;
			originalBearing = cursorLoc.subtract(_origin).getBearing();
			_segment = segment;
			_parent = parentTrack;
			_layers = theLayers;
		}

		public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
				LocationConstruct currentNearest, Layer parentLayer, Layers theLayers)
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

			// get the segment to recalc it's bounds
			// _segment.clearBounds();

			// and remember it
			lastRotate = new Double(brg);

			// and tell the props view to update itself
			updatePropsView(_segment, _parent, _layers);
		}

		public Cursor getHotspotCursor()
		{
			if (_hotspotCursor == null)
				_hotspotCursor = new Cursor(Display.getDefault(), DebriefPlugin
						.getImageDescriptor("icons/SelectFeatureHitRotate.ico")
						.getImageData(), 4, 2);
			return _hotspotCursor;
		}
	}

	/**
	 * whether the user wants the live solution data to be shown in the properties
	 * window
	 * 
	 * @return yes/no
	 */
	private static boolean showInProperties()
	{
		String dontShowDragStr = CorePlugin.getToolParent().getProperty(
				PrefsPage.PreferenceConstants.DONT_SHOW_DRAG_IN_PROPS);
		boolean dontShowDrag = Boolean.parseBoolean(dontShowDragStr);
		return !dontShowDrag;
	}

	/**
	 * utility function to stick the supplied item on the properties view, and
	 * then refresh it.
	 * 
	 * @param subject
	 *          what to show as the current selection
	 */
	public static void updatePropsView(final TrackSegment subject,
			final TrackWrapper parent, final Layers theLayers)
	{
		if (!showInProperties())
			return;

		// get the current properties page
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();
		IViewPart view = page.findView(IPageLayout.ID_PROP_SHEET);

		// do we have a properties view open?
		if (view != null)
		{
			PropertySheet ps = (PropertySheet) view;
			PropertySheetPage thisPage = (PropertySheetPage) ps.getCurrentPage();

			// and have we found a properties page?
			if (thisPage != null && !thisPage.getControl().isDisposed())
			{
				// wrap the plottable
				EditableWrapper parentP = new EditableWrapper(parent, null, theLayers);
				EditableWrapper wrapped = new EditableWrapper(subject, parentP,
						theLayers);
				ISelection selected = new StructuredSelection(wrapped);

				// tell the properties page to show what we're dragging
				thisPage
						.selectionChanged(win.getActivePage().getActivePart(), selected);

				// and trigger the update
				thisPage.refresh();
			}
		}

	}
}
