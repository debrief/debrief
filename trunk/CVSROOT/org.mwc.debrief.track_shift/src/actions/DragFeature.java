/**
 * 
 */
package actions;

import java.awt.Point;
import java.util.Enumeration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.actions.CoreDragAction;
import org.mwc.cmap.plotViewer.editors.chart.*;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.*;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class DragFeature extends CoreDragAction
{

	public static void checkClosest(Plottable thisSubject,
			MWC.GenericData.WorldLocation cursorPos, LocationConstruct currentNearest,
			DragTargetChecker helper)
	{
	}

	public static void findNearest(Layer thisLayer,
			MWC.GenericData.WorldLocation cursorLoc, java.awt.Point cursorPos,
			LocationConstruct currentNearest, Layer parentLayer)
	{
		// 
		Layer thisParentLayer;
		if(parentLayer == null)
			thisParentLayer = thisLayer;
		else
			thisParentLayer = parentLayer;

		// so, step through this layer
		if (thisLayer.getVisible())
		{
			boolean sorted = false;

			// is this layer a track?
			if (thisLayer instanceof DraggableItem)
			{
				DraggableItem dw = (DraggableItem) thisLayer;

				// yup, find the distance to it's nearest point
				dw.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest, thisParentLayer);

				// right, this one's processed. carry on
				sorted = true;
			}

			// have we processed this item
			if (!sorted)
			{
				// nope, let's just run through it
				Enumeration pts = thisLayer.elements();
				while (pts.hasMoreElements())
				{
					Plottable pt = (Plottable) pts.nextElement();

					// is this item a layer itself?
					if (pt instanceof Layer)
					{
						findNearest((Layer) pt, cursorLoc, cursorPos, currentNearest, thisParentLayer);
					}
					else
					{
						DraggableItem draggable = null;
						
						// is it a shape?
						if (pt instanceof DraggableItem)
						{
							draggable = (DraggableItem) pt;

							// yup, find the distance to it's nearest point
							draggable.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest, thisParentLayer);

							// right, this one's processed. carry on
							sorted = true;
						}

						if (!sorted)
						{
							double rngDegs = pt.rangeFrom(cursorLoc);
							if (rngDegs != -1)
							{
								WorldDistance thisSep = new WorldDistance(pt.rangeFrom(cursorLoc),
										WorldDistance.DEGS);
								currentNearest.checkMe(draggable, thisSep, null, thisLayer);
							}
						}

					}
				}
			}
		}
	}

//	private static void findNearestHotSpotIn(ShapeWrapper sw, Point cursorPos,
//			WorldLocation cursorLoc, LocationConstruct currentNearest)
//	{
//
//		// initialise thisDist, since we're going to be over-writing it
//		WorldDistance thisDist = new WorldDistance(sw.rangeFrom(cursorLoc),
//				WorldDistance.DEGS);
//
//		// is this our first item?
//		currentNearest.checkMe(sw, thisDist, null);
//
//	}
//
//	protected static void findNearestHotSpotIn(TrackWrapper host, java.awt.Point cursorPt,
//			WorldLocation cursorLoc, LocationConstruct currentNearest)
//	{
//
//		// initialise thisDist, since we're going to be over-writing it
//		WorldDistance thisDist = new WorldDistance(0, WorldDistance.DEGS);
//
//		Enumeration fixes = host.getTrack().getFixes();
//		while (fixes.hasMoreElements())
//		{
//			Fix thisF = (Fix) fixes.nextElement();
//
//			// how far away is it?
//			thisDist = thisF.getLocation().subtract(cursorLoc, thisDist);
//
//			// is it closer?
//			currentNearest.checkMe(host, thisDist, null);
//		}
//	}

	public PlotMouseDragger getDragMode()
	{
		return new PanMode();
	}

	public static class PanMode extends SWTChart.PlotMouseDragger
	{
		Point _startPoint;

		/**
		 * the thing we're dragging on
		 */
		SWTCanvas _myCanvas;

		/**
		 * the data we're modifying
		 */
		PlainChart _myChart;

		/**
		 * the hand cursor we show when dragging
		 */
		Cursor _newCursor;

		/**
		 * the original area
		 */
		WorldArea _originalArea;

		/**
		 * the last area viewed
		 */
		WorldArea _lastArea;

		/**
		 * remember the last location
		 */
		WorldLocation _lastLocation;

		/**
		 * where we started dragging from
		 */
		protected WorldLocation _theStart;

		/**
		 * where we finished the drag
		 */
		protected WorldLocation _theEnd;

		/**
		 * the thing we're currently hovering over
		 */
		protected DraggableItem _hoverTarget;

		/** the place where the drag started...
		 * 
		 */
		private WorldLocation _startLocation;

		/** the layer to update when dragging is complete
		 * 
		 */
		private Layer _parentLayer;

		/**
		 * follow the mouse being moved over the plot. switch cursor when we're over
		 * a target
		 * 
		 * @param pt
		 * @param JITTER
		 * @param theLayers
		 * @param theCanvas
		 */
		public void doMouseMove(final org.eclipse.swt.graphics.Point pt, final int JITTER,
				final Layers theData, SWTCanvas theCanvas)
		{

			// check we're not currently dragging something
			if(_lastLocation != null)
				return;
			
			java.awt.Point cursorPt = new java.awt.Point(pt.x, pt.y);
			WorldLocation cursorLoc = theCanvas.toWorld(cursorPt);

			// find the nearest editable item
			LocationConstruct currentNearest = new LocationConstruct();
			int num = theData.size();
			for (int i = 0; i < num; i++)
			{
				Layer thisL = theData.elementAt(i);
				if (thisL.getVisible())
				{
					// find the nearest items, this method call will recursively pass down
					// through
					// the layers
					findNearest(thisL, cursorLoc, cursorPt, currentNearest, null);
				}
			}

			// right, how did we get on?
			boolean highlightShown = false;

			// did we find anything?
			if (currentNearest.populated())
			{
				// generate a screen point from the cursor pos plus our distnace
				// NOTE: we're not basing this on the target location - we may not have
				// a
				// target location as such for a strangely shaped object
				WorldLocation tgtPt = cursorLoc.add(new WorldVector(Math.PI / 2,
						currentNearest._distance, null));

				// is it close enough
				java.awt.Point tPoint = theCanvas.toScreen(tgtPt);

				double scrDist = tPoint.distance(new java.awt.Point(pt.x, pt.y));

				if (scrDist <= JITTER)
				{
					// ok - change what the cursor looks liks
					// create the new cursor
					_newCursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);

					// and assign it to the control
					theCanvas.getCanvas().setCursor(_newCursor);

					highlightShown = true;

					_hoverTarget = currentNearest._object;
					_parentLayer = currentNearest._topLayer;

				}
			}

			if (!highlightShown)
			{
				// reset the cursor on the canvas
				theCanvas.getCanvas().setCursor(null);
			}
		}

		public void doMouseDrag(final org.eclipse.swt.graphics.Point pt, final int JITTER,
				final Layers theLayers, SWTCanvas theCanvas)
		{
			WorldLocation theLocation = _myChart.getCanvas().getProjection().toWorld(
					new java.awt.Point(pt.x, pt.y));

			
			// do we need to remember the drag start?
			if(_startLocation == null)
				_startLocation = new WorldLocation(theLocation);
			
			// just do a check that we have our start point (it may have been cleared
			// at the end of the move operation)
			if (_lastLocation == null)
			{
				_lastLocation = new WorldLocation(theLocation);				
			}
			else
			{
				// and to we have something to drag?
				if (_hoverTarget != null)
				{

					// sort out the vector to apply
					WorldVector wv = theLocation.subtract(_lastLocation);

					// move the target accordingly 
					_hoverTarget.shift(wv);
					// ok, remember the last location
					_lastLocation = new WorldLocation(theLocation);

					// and get the chart to redraw itself
					_myChart.getLayers().fireModified(_parentLayer);
//					_myChart.update();					
				}
			}

		}

		public void doMouseUp(org.eclipse.swt.graphics.Point point, int keyState)
		{
			
			// right, put the track back where it came from
			
			// generate the reverse vector
			WorldVector reverse = _startLocation.subtract(_lastLocation);
			
			// apply the reverse vector
			_hoverTarget.shift(reverse);
			
			//	and get the chart to redraw itself
			_myChart.update();			
			
			// ok, now calculate the real offset to apply
			WorldVector forward = _lastLocation.subtract(_startLocation);
			
			// put it into our action
			DragFeatureAction dta = new DragFeatureAction(forward, _hoverTarget, _myChart.getLayers(), _parentLayer);
			
			// and wrap it
			DebriefActionWrapper daw = new DebriefActionWrapper(dta, _myChart.getLayers());

			// and add it to the clipboard
			CorePlugin.run(daw);
			
			// ok - forget the drag-start location
			_startLocation = null;
			_lastLocation = null;
			
			
			// and forget what we selected last
			_hoverTarget = null;
			
		}

		public void mouseDown(org.eclipse.swt.graphics.Point point, SWTCanvas canvas,
				PlainChart theChart)
		{
			_startPoint = new java.awt.Point(point.x, point.y);
			_myCanvas = canvas;
			_myChart = theChart;

			_originalArea = new WorldArea(_myChart.getCanvas().getProjection()
					.getVisibleDataArea());

			_lastArea = new WorldArea(_originalArea);
			_lastLocation = null;

			_theStart = new WorldLocation(_myChart.getCanvas().getProjection().toWorld(
					new java.awt.Point(point.x, point.y)));

			// create the new cursor
			_newCursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);

			// and assign it to the control
			canvas.getCanvas().setCursor(_newCursor);
		}

		protected void setNewArea(PlainProjection proj, WorldArea theArea)
		{
			double oldBorder = proj.getDataBorder();
			proj.setDataBorder(1.0);
			proj.setDataArea(theArea);
			proj.zoom(0.0);
			proj.setDataBorder(oldBorder);
		}

	}

	public static abstract class DragTargetChecker
	{
		/**
		 * decide whether to inspect child components of this layer
		 * 
		 * @param target
		 * @return
		 */
		abstract public boolean breakDown(Layer target);

		/**
		 * decide if this is a candidate for dragging
		 */
		abstract public boolean isDraggingCandidate(Plottable target);

		/**
		 * return the current location of the nearest draggable component
		 * 
		 * @param target
		 * @param spot
		 *          TODO
		 * @param nearestLocation
		 *          TODO
		 * @return
		 */
		public void updateNearest(Plottable target, WorldLocation spot,
				LocationConstruct nearestLocation)
		{

		}

		abstract public WorldLocation findNearestHotSpot(Plottable target, WorldLocation spot);
	}

  /**
   * action representing a track being dragged.  It's undo-able and redo-able, since it's quite simple really.
   */
  public static final class DragFeatureAction implements MWC.GUI.Tools.Action
  {
    /**
     * the offset we're going to apply
     */
    private final WorldVector _theOffset;

    /**
     * the track we're going to apply it to
     */
    private final DraggableItem _itemToDrag;

    /**
     * the set of layers we're need to update on completion
     */
    private final Layers _theLayers;

    /** the layer to update after drag is complete
     * 
     */
		private Layer _parentLayer;

    /**
     * constructor - providing the parameters to store to execute/reproduce the operation
     *
     * @param theOffset
     * @param theTrack
     * @param theLayers
     */
    public DragFeatureAction(final WorldVector theOffset,
                           final DraggableItem theTrack,
                           final Layers theLayers,
                           final Layer parentLayer)
    {
      _theOffset = theOffset;
      _itemToDrag = theTrack;
      _theLayers = theLayers;
      _parentLayer = parentLayer;
    }

    /**
     * @return a string representation of the object.
     */
    public String toString()
    {
      final String res = "Drag " + _itemToDrag.getName() + _theOffset.toString();
      return res;
    }

    /**
     * this method calls the 'do' event in the parent tool, passing the necessary data to it
     */
    public void execute()
    {
      // apply the shift
      _itemToDrag.shift(_theOffset);

      // update the layers
      _theLayers.fireModified(_parentLayer);
    }

    /**
     * this method calls the 'undo' event in the parent tool, passing the necessary data to it
     */
    public void undo()
    {
      // reverse the drag direction
      final WorldVector reverseVector = new WorldVector(0.0, 0.0, 0.0);
      reverseVector.setValues(_theOffset.getBearing() + Math.PI, _theOffset.getRange(), _theOffset.getBearing());

      // and apply it
      _itemToDrag.shift(reverseVector);

      _theLayers.fireModified(_parentLayer);
    }

    /**
     * @return boolean flag to indicate whether this action may be redone
     */
    public boolean isRedoable()
    {
      return true;
    }

    /**
     * @return boolean flag to describe whether this operation may be undone
     */
    public boolean isUndoable()
    {
      return true;
    }
  }
	
}