/**
 * 
 */
package org.mwc.debrief.core.actions;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Enumeration;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.property_support.ColorHelper;
import org.mwc.cmap.plotViewer.actions.CoreDragAction;
import org.mwc.cmap.plotViewer.editors.chart.*;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.*;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class DragSegment extends CoreDragAction
{

	public static void checkClosest(Plottable thisSubject,
			MWC.GenericData.WorldLocation cursorPos, LocationConstruct currentNearest,
			DragTargetChecker helper)
	{
	}

	protected void execute()
	{
		// ok, fire our parent
		super.execute();

		// now, try to open the stacked dots view
		try
		{
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			page.showView(CorePlugin.STACKED_DOTS);
		}
		catch (PartInitException e)
		{
			CorePlugin.logError(Status.ERROR, "Failed to open stacked dots", e);
		}

	}

	public PlotMouseDragger getDragMode()
	{
		return new DragSegmentMode();
	}

	/**
	 * embedded class that handles the range/bearing measurement
	 * 
	 * @author Ian
	 */
	final static public class DragSegmentMode extends SWTChart.PlotMouseDragger
	{

		/**
		 * the start point, in world coordinates (so we don't have to calculate it
		 * as often)
		 */
		WorldLocation _startLocation;

		/**
		 * the start point, in screen coordinates - where we started our drag
		 */
		Point _startPoint;

		/**
		 * the canvas we're updating..
		 */
		SWTCanvas _myCanvas;

		/**
		 * the last place we dragged over
		 */
		java.awt.Point _lastPoint;

		/**
		 * the thing we're currently hovering over
		 */
		protected DraggableItem _hoverTarget;

		/**
		 * the hand cursor we show when dragging
		 */
		Cursor _newCursor;

		/**
		 * the layer to update when dragging is complete
		 */
		private Layer _parentLayer;

		private WorldLocation _lastLocation;

		private PlainChart _myChart;

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
			if (_lastPoint != null)
				return;

			// clear our bits
			_hoverTarget = null;
			_parentLayer = null;

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
					// we only act on track wrappers, check if this is one
					if(thisL instanceof TrackWrapper)
					{
						TrackWrapper thisTrack = (TrackWrapper) thisL;
					// find the nearest items, this method call will recursively pass down
					// through
					// the layers
						thisTrack.findNearestSegmentHotspotFor(cursorLoc, cursorPt, currentNearest);
					}
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
					// _newCursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);

					if (_newCursor != null)
						_newCursor.dispose();

					_newCursor = new Cursor(Display.getDefault(), DebriefPlugin.getImageDescriptor(
							"icons/SelectFeatureHit.ico").getImageData(), 4, 2);

					// and assign it to the control
					theCanvas.getCanvas().setCursor(_newCursor);

					highlightShown = true;

					_hoverTarget = currentNearest._object;
					_parentLayer = currentNearest._topLayer;

				}
			}

			// have we shown the 'hit' cursor?
			if (!highlightShown)
			{
				// nope. do we already have a local cursor?
				if (_newCursor != null)
				{
					// yup, better ditch it
					_newCursor.dispose();
				}

				// and show our special cursor
				_newCursor = getNormalCursor();
				theCanvas.getCanvas().setCursor(_newCursor);

			}
		}

		@SuppressWarnings("deprecation")
		final public void doMouseDrag(org.eclipse.swt.graphics.Point pt, int JITTER,
				Layers theLayers, SWTCanvas theCanvas)
		{
			
			// do we have something selected?
			if(_hoverTarget == null)
				return;
			
			if (_startPoint != null)
			{
				GC gc = new GC(_myCanvas.getCanvas());

				// This is the same as a !XOR
				gc.setXORMode(true);
				gc.setForeground(gc.getBackground());

				// Erase existing track, if we have one
				if (_lastPoint != null)
				{
					drawHere(gc, null);
				}
				else
				{
					// we're drawing for the first time. make the last location equal the
					// start location
					_lastLocation = _startLocation;

					// also override the cursor, if we have to.
					if (_newCursor != null)
					{
						_newCursor.dispose();
						_newCursor = new Cursor(Display.getDefault(), DebriefPlugin
								.getImageDescriptor("icons/SelectFeatureHitDown.ico").getImageData(), 4,
								2);
						theCanvas.getCanvas().setCursor(_newCursor);
					}
				}

				// remember where we are
				_lastPoint = new java.awt.Point(pt.x, pt.y);
				WorldLocation newLocation = new WorldLocation(_myCanvas.getProjection().toWorld(
						_lastPoint));

				// now work out the vector from the last place plotted to the current
				// place
				WorldVector offset = newLocation.subtract(_lastLocation);

				// draw new track
				drawHere(gc, offset);

				// remember the last location
				_lastLocation = newLocation;

				// cool, is it a track that we've just dragged?
				if (_hoverTarget instanceof TrackWrapper)
				{
					// if the current editor is a track data provider,
					// tell it that we've shifted
					IWorkbench wb = PlatformUI.getWorkbench();
					IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
					IWorkbenchPage page = win.getActivePage();
					IEditorPart editor = page.getActiveEditor();
					TrackDataProvider dataMgr = (TrackDataProvider) editor
							.getAdapter(TrackDataProvider.class);
					// is it one of ours?
					if (dataMgr != null)
					{
						{
							dataMgr.fireTrackShift((TrackWrapper) _hoverTarget);
						}
					}
				}
				
				// ok, let's ditch the GC
				gc.dispose();

			}
			else
			{
				// System.out.println("no point.");
			}

		}

		@SuppressWarnings("deprecation")
		final public void doMouseUp(org.eclipse.swt.graphics.Point point, int keyState)
		{
			if(_hoverTarget == null)
				return;
			
			if (_myCanvas == null)
			{
				System.out.println("canvas is null!");
				return;
			}

			GC gc = new GC(_myCanvas.getCanvas());

			// This is the same as a !XOR
			gc.setXORMode(true);
			gc.setForeground(gc.getBackground());

			// Erase existing rectangle
			if (_lastPoint != null)
			{
				// hmm, we've finished plotting. see if the ctrl button is
				// down
				if ((keyState & SWT.CTRL) == 0)
					drawHere(gc, null);
			}

			// generate the reverse vector
			WorldVector reverse = _startLocation.subtract(_lastLocation);

			// apply the reverse vector
			_hoverTarget.shift(reverse);

			// and get the chart to redraw itself
			_myChart.update();

			// ok, now calculate the real offset to apply
			WorldVector forward = _lastLocation.subtract(_startLocation);

			// put it into our action
			DragSegmentAction dta = new DragSegmentAction(forward, _hoverTarget, _myChart
					.getLayers(), _parentLayer);

			// and wrap it
			DebriefActionWrapper daw = new DebriefActionWrapper(dta, _myChart.getLayers());

			// and add it to the clipboard
			CorePlugin.run(daw);

			_startPoint = null;
			_lastPoint = null;
			_lastLocation = null;
			_myCanvas = null;
			_startLocation = null;
		}

		final public void mouseDown(org.eclipse.swt.graphics.Point point, SWTCanvas canvas,
				PlainChart theChart)
		{
			_startPoint = new Point(point.x, point.y);
			_myCanvas = canvas;
			_lastPoint = null;
			_startLocation = new WorldLocation(_myCanvas.getProjection().toWorld(
					new java.awt.Point(point.x, point.y)));
			_myChart = theChart;
		}

		public Cursor getNormalCursor()
		{
			Cursor res = new Cursor(Display.getDefault(), DebriefPlugin.getImageDescriptor(
					"icons/SelectFeature.ico").getImageData(), 4, 2);
			return res;
		}

		/**
		 * dragging happening. Either draw (or erase) the previous point
		 * 
		 * @param graphics
		 *          where we're plotting to
		 * @param pt
		 *          where the cursor is
		 */
		private void drawHere(final GC graphics, final WorldVector newVector)
		{
			graphics.setForeground(ColorHelper.getColor(java.awt.Color.WHITE));

			// ok, move the target to the new location...
			if (newVector != null)
			{
				_hoverTarget.shift(newVector);
			}

			if (_hoverTarget != null)
			{
				// TrackWrapper tw = (TrackWrapper) _hoverTarget;
				SWTCanvasAdapter ca = new SWTCanvasAdapter(_myCanvas.getProjection())
				{
					private static final long serialVersionUID = 1L;

					public void setColor(Color theCol)
					{
						// ignore the color change, we just want to keep it white...
					}

					protected void switchAntiAliasOn(boolean val)
					{
						// ignore this, we won't be anti-aliasing
					}

					public void drawImage(Image image, int x, int y, int width, int height)
					{
					}

					public void drawText(Font theFont, String theStr, int x, int y)
					{
					}

					public void drawText(String theStr, int x, int y)
					{
					}

				};
				// change the color by hand
				ca.startDraw(graphics);
				_hoverTarget.paint(ca);
				ca.endDraw(null);
			}

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
	 * action representing a track being dragged. It's undo-able and redo-able,
	 * since it's quite simple really.
	 */
	public static final class DragSegmentAction implements MWC.GUI.Tools.Action
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

		/**
		 * the layer to update after drag is complete
		 */
		private Layer _parentLayer;

		/**
		 * constructor - providing the parameters to store to execute/reproduce the
		 * operation
		 * 
		 * @param theOffset
		 * @param theTrack
		 * @param theLayers
		 */
		public DragSegmentAction(final WorldVector theOffset, final DraggableItem theTrack,
				final Layers theLayers, final Layer parentLayer)
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
		 * this method calls the 'do' event in the parent tool, passing the
		 * necessary data to it
		 */
		public void execute()
		{
			// apply the shift
			_itemToDrag.shift(_theOffset);

			// update the layers
			_theLayers.fireModified(_parentLayer);
		}

		/**
		 * this method calls the 'undo' event in the parent tool, passing the
		 * necessary data to it
		 */
		public void undo()
		{
			// reverse the drag direction
			final WorldVector reverseVector = _theOffset.generateInverse();

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