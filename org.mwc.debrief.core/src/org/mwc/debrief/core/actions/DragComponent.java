/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.CursorRegistry;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.property_support.ColorHelper;
import org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter;
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
import MWC.GUI.Shapes.HasDraggableComponents;
import MWC.GUI.Shapes.HasDraggableComponents.ComponentConstruct;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * @author ian.mayo
 */
public class DragComponent extends DragFeature
{

	/**
	 * action representing a track being dragged. It's undo-able and redo-able,
	 * since it's quite simple really.
	 */
	public static final class DragComponentAction implements MWC.GUI.Tools.Action
	{
		/**
		 * the layer to update after drag is complete
		 */
		private final Layer _parentLayer;

		/**
		 * the component we're going to shift
		 */
		private final WorldLocation _theComponent;

		/**
		 * the track we're going to apply it to
		 */
		private final HasDraggableComponents _theFeature;

		/**
		 * the set of layers we're need to update on completion
		 */
		private final Layers _theLayers;

		/**
		 * the offset we're going to apply
		 */
		private final WorldVector _theOffset;

		/**
		 * constructor - providing the parameters to store to execute/reproduce the
		 * operation
		 * 
		 * @param theOffset
		 * @param theFeature
		 * @param theLayers
		 */
		public DragComponentAction(final WorldVector theOffset,
				final HasDraggableComponents theFeature, final WorldLocation theComponent,
				final Layers theLayers, final Layer parentLayer)
		{
			_theOffset = theOffset;
			_theFeature = theFeature;
			_theLayers = theLayers;
			_parentLayer = parentLayer;
			_theComponent = theComponent;
		}

		/**
		 * this method calls the 'do' event in the parent tool, passing the
		 * necessary data to it
		 */
		@Override
		public void execute()
		{
			// apply the shift
			_theFeature.shift(_theComponent, _theOffset);

			// update the layers
			// no, don't bother - the DebriefActionWrapper handles this
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
			final String res = "Drag " + _theFeature.getName()
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
			_theFeature.shift(_theComponent, reverseVector);

			_theLayers.fireModified(_parentLayer);
		}
	}

	/**
	 * embedded class that handles the range/bearing measurement
	 * 
	 * @author Ian
	 */
	final public class DragComponentMode extends SWTChart.PlotMouseDragger
	{

		/**
		 * the component we're going to drag
		 */
		protected WorldLocation _hoverComponent;

		/**
		 * the thing we're currently hovering over
		 */
		protected HasDraggableComponents _hoverTarget;

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
		private Layer _parentLayer;

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
		final public void doMouseDrag(final org.eclipse.swt.graphics.Point pt,
				final int JITTER, final Layers theLayers, final SWTCanvas theCanvas)
		{
			if ((_startPoint != null) && (_hoverTarget != null))
			{
				final GC gc = new GC(_myCanvas.getCanvas());

				// This is the same as a !XOR
				gc.setXORMode(true);
				gc.setForeground(gc.getBackground());

				// Erase existing track, if we have one
				if (_lastPoint != null)
				{
					//drawHere(gc, null);
					_myCanvas.getCanvas().redraw();
					Display.getCurrent().update();
				}
				else
				{
					// we're drawing for the first time. make the last location equal the
					// start location
					_lastLocation = _startLocation;

					// override the icon we're using
					
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

				// cool, is it a track that we've just dragged?
				if (_hoverTarget instanceof TrackWrapper)
				{
					// if the current editor is a track data provider,
					// tell it that we've shifted
					final IWorkbenchPage page = CorePlugin.getActivePage();
					final IEditorPart editor = page.getActiveEditor();
					final TrackDataProvider dataMgr = (TrackDataProvider) editor
							.getAdapter(TrackDataProvider.class);
					// is it one of ours?
					if (dataMgr != null)
					{
						{
							dataMgr.fireTrackShift((TrackWrapper) _hoverTarget);
						}
					}
				}

				// and ditch the GC
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
			// if the chart's editor is not active
			IWorkbenchPart activePart = CorePlugin.getActivePart();
			if (activePart instanceof IChartBasedEditor && !activePart.equals(_myEditor)) {
				setActiveEditor(null, (IEditorPart) activePart);
			}
			if (!CorePlugin.isActivePart((IWorkbenchPart)_myEditor))
				return;
			
			// check we're not currently dragging something
			if (_lastPoint != null)
				return;

			// clear our bits
			_hoverTarget = null;
			_hoverComponent = null;
			_parentLayer = null;

			final java.awt.Point cursorPt = new java.awt.Point(pt.x, pt.y);
			final WorldLocation cursorLoc = theCanvas.toWorld(cursorPt);

			// find the nearest editable item
			final ComponentConstruct currentNearest = new ComponentConstruct();
			final int num = theData.size();
			for (int i = 0; i < num; i++)
			{
				final Layer thisL = theData.elementAt(i);
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
				final WorldLocation tgtPt = cursorLoc.add(new WorldVector(Math.PI / 2,
						currentNearest._distance, null));

				// is it close enough
				final java.awt.Point tPoint = theCanvas.toScreen(tgtPt);

				final double scrDist = tPoint.distance(new java.awt.Point(pt.x, pt.y));

				if (scrDist <= JITTER)
				{
					// ok - change what the cursor looks liks

					// and assign it to the control
					theCanvas.getCanvas().setCursor(CursorRegistry.getCursor(CursorRegistry.SELECT_POINT_HIT));

					highlightShown = true;

					_hoverTarget = currentNearest._object;
					_hoverComponent = currentNearest._draggableComponent;
					_parentLayer = currentNearest._topLayer;

				}
			}

			if (!highlightShown)
			{
				// nope, we haven't found anything. clear our settings
				_hoverTarget = null;
				_hoverComponent = null;
				_parentLayer = null;

				// reset the cursor on the canvas
				// and assign it to the control
				theCanvas.getCanvas().setCursor(getNormalCursor());
			}
		}

		@Override
		final public void doMouseUp(final org.eclipse.swt.graphics.Point point,
				final int keyState)
		{
			// just check we actually dragged something
			if (_hoverTarget != null)
			{

				// this gc was leaked in old code; not used anymore
				//final GC gc = new GC(_myCanvas.getCanvas());

				// This is the same as a !XOR
				//gc.setXORMode(true);
				//gc.setForeground(gc.getBackground());

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
				_hoverTarget.shift(_hoverComponent, reverse);

				// and get the chart to redraw itself
				// No, don't bother - the DebriefActionWrapper handles that
				// _myChart.update(_parentLayer);

				// ok, now calculate the real offset to apply
				final WorldVector forward = _lastLocation.subtract(_startLocation);

				// put it into our action
				final DragComponentAction dta = new DragComponentAction(forward,
						_hoverTarget, _hoverComponent, _myChart.getLayers(), _parentLayer);

				// and wrap it
				final DebriefActionWrapper daw = new DebriefActionWrapper(dta,
						_myChart.getLayers(), _parentLayer);

				// and add it to the clipboard
				CorePlugin.run(daw);

			}

			_startPoint = null;
			_lastPoint = null;
			_lastLocation = null;
			_myCanvas = null;
			_startLocation = null;
			_hoverTarget = null;
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

			// ok, move the target ot the new location...
			if (newVector != null)
				_hoverTarget.shift(_hoverComponent, newVector);

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

		@Override
		public Cursor getNormalCursor()
		{
			return CursorRegistry.getCursor(CursorRegistry.SELECT_POINT);
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

	public static void findNearest(final Layer thisLayer,
			final MWC.GenericData.WorldLocation cursorLoc, final java.awt.Point cursorPos,
			final MWC.GUI.Shapes.HasDraggableComponents.ComponentConstruct currentNearest,
			final Layer parentLayer)
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
			if (thisLayer instanceof HasDraggableComponents)
			{
				final HasDraggableComponents dw = (HasDraggableComponents) thisLayer;

				// yup, find the distance to it's nearest point
				dw.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest,
						thisParentLayer);

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

					// is this item a layer itself?
					if (pt instanceof Layer)
					{
						findNearest((Layer) pt, cursorLoc, cursorPos, currentNearest,
								thisParentLayer);
					}
					else
					{
						HasDraggableComponents draggable = null;

						// is it a shape?
						if (pt instanceof HasDraggableComponents)
						{
							draggable = (HasDraggableComponents) pt;

							// yup, find the distance to it's nearest point
							draggable.findNearestHotSpotIn(cursorPos, cursorLoc,
									currentNearest, thisParentLayer);

							// right, this one's processed. carry on
							sorted = true;
						}
					}
				}
			}
		}
	}

	@Override
	public PlotMouseDragger getDragMode()
	{
		return new DragComponentMode();
	}
	public Cursor getDragCursor()
	{
		return CursorRegistry.getCursor(CursorRegistry.SELECT_POINT_HIT_DOWN);
	}
}