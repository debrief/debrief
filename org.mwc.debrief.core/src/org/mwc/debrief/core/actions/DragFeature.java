/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.actions;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Enumeration;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.CursorRegistry;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter;
import org.mwc.cmap.plotViewer.actions.CoreDragAction;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * @author ian.mayo
 */
public class DragFeature extends CoreDragAction
{

	/**
	 * action representing a track being dragged. It's undo-able and redo-able,
	 * since it's quite simple really.
	 */
	public static final class DragFeatureAction implements MWC.GUI.Tools.Action
	{
		/**
		 * the track we're going to apply it to
		 */
		private final DraggableItem _itemToDrag;

		private final DragOperation _operation;

		/**
		 * the offset we're going to apply
		 */
		private final WorldVector _theOffset;

		/**
		 * constructor - providing the parameters to store to execute/reproduce the
		 * operation
		 * 
		 * @param theOffset
		 * @param theTrack
		 * @param theLayers
		 */
		public DragFeatureAction(final WorldVector theOffset,
				final DraggableItem theTrack, final Layers theLayers,
				final Layer parentLayer, final DragOperation operation)
		{
			_theOffset = theOffset;
			_itemToDrag = theTrack;
			_operation = operation;
		}

		/**
		 * this method calls the 'do' event in the parent tool, passing the
		 * necessary data to it
		 */
		@Override
		public void execute()
		{
			// apply the shift
			_operation.apply(_itemToDrag, _theOffset);

			// update the layers
			// No, don't bother - let the DebriefActionWrapper fire the event
			// _theLayers.fireModified(_parentLayer);
		}

		/**
		 * @return boolean flag to indicate whether this action may be redone
		 */
		@Override
		public boolean isRedoable()
		{
			return true;
		}

		/**
		 * @return boolean flag to describe whether this operation may be undone
		 */
		@Override
		public boolean isUndoable()
		{
			return true;
		}

		/**
		 * @return a string representation of the object.
		 */
		@Override
		public String toString()
		{
			final String res = "Drag " + _itemToDrag.getName()
					+ _theOffset.toString();
			return res;
		}

		/**
		 * this method calls the 'undo' event in the parent tool, passing the
		 * necessary data to it
		 */
		@Override
		public void undo()
		{
			// reverse the drag direction
			final WorldVector reverseVector = _theOffset.generateInverse();

			// and apply it
			_operation.apply(_itemToDrag, reverseVector);

			// No, don't bother - let the DebriefActionWrapper fire the event
			// _theLayers.fireModified(_parentLayer);
		}
	}

	/**
	 * embedded class that handles the range/bearing measurement
	 * 
	 * @author Ian
	 */
	public class DragFeatureMode extends SWTChart.PlotMouseDragger
	{

		/**
		 * the thing we're currently hovering over
		 */
		protected DraggableItem _hoverTarget;

		private WorldLocation _lastLocation;

		/**
		 * the last place we dragged over
		 */
		java.awt.Point _lastPoint;

		/**
		 * the canvas we're updating..
		 */
		SWTCanvas _myCanvas;

		private PlainChart _myChart;

		/**
		 * the layer to update when dragging is complete
		 */
		protected Layer _parentLayer;

		/**
		 * the start point, in world coordinates (so we don't have to calculate it
		 * as often)
		 */
		WorldLocation _startLocation;

		/**
		 * the start point, in screen coordinates - where we started our drag
		 */
		Point _startPoint;

		@Override
		@SuppressWarnings("deprecation")
		public void doMouseDrag(final org.eclipse.swt.graphics.Point pt, final int JITTER,
				final Layers theLayers, final SWTCanvas theCanvas)
		{
			// if the chart's editor is not active
			IWorkbenchPart activePart = CorePlugin.getActivePart();
			if (activePart instanceof IChartBasedEditor && !activePart.equals(_myEditor)) {
				setActiveEditor(null, (IEditorPart) activePart);
			}
			if (!CorePlugin.isActivePart((IWorkbenchPart)_myEditor))
				return;
						
			// do we have something selected?
			if (_hoverTarget == null)
				return;

			if (_startPoint != null)
			{
				final GC gc = new GC(_myCanvas.getCanvas());

				// This is the same as a !XOR
				gc.setXORMode(true);
				gc.setForeground(gc.getBackground());

				// Erase existing track, if we have one
				if (_lastPoint != null)
				{
					//drawHere(gc, null);
					// slowing down redrawing on Linux makes dragging more smootly
					if (Platform.getOS().equals(Platform.OS_LINUX))
					{
						try
						{
							Thread.sleep(150);
						} catch (InterruptedException e)
						{
							// ignore
						}
					}
					_myCanvas.getCanvas().redraw();
					Display.getCurrent().update();
				}
				else
				{
					// we're drawing for the first time. make the last location equal the
					// start location
					_lastLocation = _startLocation;

					// also override the cursor, if we have to.
					theCanvas.getCanvas().setCursor(getDragCursor());
					
				}

				// remember where we are
				_lastPoint = new java.awt.Point(pt.x, pt.y);
				final WorldLocation newLocation = new WorldLocation(_myCanvas.getProjection()
						.toWorld(_lastPoint));

				// now work out the vector from the last place plotted to the current
				// place
				final WorldVector offset = newLocation.subtract(_lastLocation);

				// draw new track
				drawHere(gc, offset);

				// remember the last location
				_lastLocation = newLocation;
				// ok, let's ditch the GC
				gc.dispose();


			}
			else
			{
				// System.out.println("no point.");
			}

		}

		/**
		 * follow the mouse being moved over the plot. switch cursor when we're over
		 * a target
		 * 
		 * @param pt
		 * @param JITTER
		 * @param theLayers
		 * @param theCanvas
		 */
		@Override
		public void doMouseMove(final org.eclipse.swt.graphics.Point pt,
				final int JITTER, final Layers theData, final SWTCanvas theCanvas)
		{

			// check we're not currently dragging something
			if (_lastPoint != null)
				return;

			// clear our bits
			_hoverTarget = null;
			_parentLayer = null;

			final java.awt.Point cursorPt = new java.awt.Point(pt.x, pt.y);
			final WorldLocation cursorLoc = theCanvas.toWorld(cursorPt);

			// find the nearest editable item
			final LocationConstruct currentNearest = new LocationConstruct();
			final int num = theData.size();
			for (int i = 0; i < num; i++)
			{
				final Layer thisL = theData.elementAt(i);
				if (thisL.getVisible())
				{
					// find the nearest items, this method call will recursively pass down
					// through
					// the layers
					findNearest(thisL, cursorLoc, cursorPt, currentNearest, null, theData);
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
				final WorldLocation tgtPt = cursorLoc.add(new WorldVector(Math.PI / 2,
						currentNearest._distance, null));

				// is it close enough
				final java.awt.Point tPoint = theCanvas.toScreen(tgtPt);

				final double scrDist = tPoint.distance(new java.awt.Point(pt.x, pt.y));

				if (scrDist <= JITTER)
				{
					// ok - change what the cursor looks liks
					// create the new cursor
					// and assign it to the control
					theCanvas.getCanvas().setCursor(getHotspotCursor(currentNearest._object));

					highlightShown = true;

					_hoverTarget = currentNearest._object;
					_parentLayer = currentNearest._topLayer;

				}
			}

			// have we shown the 'hit' cursor?
			if (!highlightShown)
			{
				// show our special cursor
				theCanvas.getCanvas().setCursor(getNormalCursor());

			}
		}

		@Override
		public void doMouseUp(final org.eclipse.swt.graphics.Point point, final int keyState)
		{
			if (_hoverTarget == null)
				return;

			if (_myCanvas == null)
			{
				System.out.println("canvas is null!");
				return;
			}

			// Erase existing rectangle
			if (_lastPoint != null)
			{
				// hmm, we've finished plotting. see if the ctrl button is
				// down
				if ((keyState & SWT.CTRL) == 0) {
					//drawHere(gc, null);
					_myCanvas.getCanvas().redraw();
					Display.getCurrent().update();
				}
			}

			// generate the reverse vector
			final WorldVector reverse = _startLocation.subtract(_lastLocation);

			// apply the reverse vector
			getOperation().apply(_hoverTarget, reverse);

			// and get the chart to redraw itself
			// _myChart.update(_parentLayer);

			// ok, now calculate the real offset to apply
			final WorldVector forward = _lastLocation.subtract(_startLocation);

			// put it into our action
			final DragFeatureAction dta = new DragFeatureAction(forward, _hoverTarget,
					_myChart.getLayers(), _parentLayer, getOperation());

			// and wrap it
			final DebriefActionWrapper daw = new DebriefActionWrapper(dta,
					_myChart.getLayers(), _parentLayer);

			// and add it to the clipboard
			CorePlugin.run(daw);

			_startPoint = null;
			_lastPoint = null;
			_lastLocation = null;
			_myCanvas = null;
			_startLocation = null;
			

			// cool, is it a track that we've just dragged?
			if (_parentLayer instanceof TrackWrapper)
			{
				// if the current editor is a track data provider,
				// tell it that we've shifted
				final IEditorPart editor = CorePlugin.getActivePage().getActiveEditor();
				final TrackDataProvider dataMgr = (TrackDataProvider) editor
						.getAdapter(TrackDataProvider.class);
				// is it one of ours?
				if (dataMgr != null)
				{
					{
						dataMgr.fireTrackShift((TrackWrapper) _parentLayer);
					}
				}
			}
			
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
			org.eclipse.swt.graphics.Color fc = new org.eclipse.swt.graphics.Color(Display.getDefault(), 255, 255,255);
			//graphics.setForeground(ColorHelper.getColor(java.awt.Color.WHITE));
			graphics.setForeground(fc);

			// ok, move the target to the new location...
			if (newVector != null)
			{
				getOperation().apply(_hoverTarget, newVector);
			}

			if (_hoverTarget != null)
			{
				// TrackWrapper tw = (TrackWrapper) _hoverTarget;
				final SWTCanvasAdapter ca = new SWTCanvasAdapter(_myCanvas.getProjection())
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void drawImage(final Image image, final int x, final int y, final int width, final int height)
					{
					}

					@Override
					public void drawText(final Font theFont, final String theStr, final int x, final int y)
					{
					}

					@Override
					public void drawText(final String theStr, final int x, final int y)
					{
						super.drawText(theStr, x, y);
					}

					@Override
					public void setColor(final Color theCol)
					{
						// ignore the color change, we just want to keep it white...
					}

					@Override
					protected void switchAntiAliasOn(final boolean val)
					{
						// ignore this, we won't be anti-aliasing
					}

				};
				// change the color by hand
				ca.startDraw(graphics);
				_hoverTarget.paint(ca);
				ca.endDraw(null);
			}

			fc.dispose();
		}

		@Override
		public Cursor getNormalCursor()
		{
			return CursorRegistry.getCursor(CursorRegistry.SELECT_FEATURE);
		}

		public DragOperation getOperation()
		{
			return new DragOperation()
			{

				@Override
				public void apply(final DraggableItem item, final WorldVector offset)
				{
					item.shift(offset);
				}

			};
		}

		@Override
		final public void mouseDown(final org.eclipse.swt.graphics.Point point,
				final SWTCanvas canvas, final PlainChart theChart)
		{
			_startPoint = new Point(point.x, point.y);
			_myCanvas = canvas;
			_lastPoint = null;
			_startLocation = new WorldLocation(_myCanvas.getProjection().toWorld(
					new java.awt.Point(point.x, point.y)));
			_myChart = theChart;
		}

	}

	/**
	 * wrapper for an operation we apply to an object - such as drag
	 * 
	 * @author Administrator
	 * 
	 */
	public static interface DragOperation
	{
		/**
		 * do the operation
		 * 
		 * @param item
		 *          what we're doing it to
		 * @param offset
		 *          how far to do it
		 */
		public void apply(DraggableItem item, WorldVector offset);
	}

	public void findNearest(final Layer thisLayer,
			final MWC.GenericData.WorldLocation cursorLoc, final java.awt.Point cursorPos,
			final LocationConstruct currentNearest, final Layer parentLayer, final Layers theData)
	{
		//
		Layer thisParentLayer;
		if (parentLayer == null)
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
				final DraggableItem dw = (DraggableItem) thisLayer;

				// yup, find the distance to it's nearest point
				dw.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest,
						thisParentLayer, theData);

				// right, this one's processed. carry on
				sorted = true;
			}

			// have we processed this item
			if (!sorted)
			{
				// nope, let's just run through it
				final Enumeration<Editable> pts = thisLayer.elements();
				while (pts.hasMoreElements())
				{
					final Plottable pt = (Plottable) pts.nextElement();

					if (pt.getVisible())
					{

						// is this item a layer itself?
						if (pt instanceof Layer)
						{
							findNearest((Layer) pt, cursorLoc, cursorPos, currentNearest,
									thisParentLayer, theData);
						}
						else
						{
							DraggableItem draggable = null;

							// is it a shape?
							if (pt instanceof DraggableItem)
							{
								draggable = (DraggableItem) pt;

								// yup, find the distance to it's nearest point
								draggable.findNearestHotSpotIn(cursorPos, cursorLoc,
										currentNearest, thisParentLayer, theData);

								// right, this one's processed. carry on
								sorted = true;
							}

							if (!sorted)
							{
								final double rngDegs = pt.rangeFrom(cursorLoc);
								if (rngDegs != -1)
								{
									final WorldDistance thisSep = new WorldDistance(
											pt.rangeFrom(cursorLoc), WorldDistance.DEGS);
									currentNearest.checkMe(draggable, thisSep, null, thisLayer);
								}
							}

						}
					}
				}
			}
		}
	}

	public Cursor getDragCursor()
	{
		return CursorRegistry.getCursor(CursorRegistry.SELECT_FEATURE_HIT_DOWN);
	}

	@Override
	public PlotMouseDragger getDragMode()
	{
		return new DragFeatureMode();
	}

	public Cursor getHotspotCursor(final DraggableItem hoverTarget)
	{
		return CursorRegistry.getCursor(CursorRegistry.SELECT_FEATURE_HIT);
	}

}