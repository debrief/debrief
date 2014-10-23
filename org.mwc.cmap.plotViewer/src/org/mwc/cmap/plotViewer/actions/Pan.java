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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.CursorRegistry;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Chart.Pan.PanAction;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * @author ian.mayo
 */
public class Pan extends CoreDragAction
{

	public static class PanMode extends SWTChart.PlotMouseDragger
	{

		/**
		 * the original area
		 * 
		 */
		WorldArea _originalArea;

		/**
		 * the last area viewed
		 * 
		 */
		WorldArea _lastArea;

		/**
		 * remember the last location
		 * 
		 */
		WorldLocation _lastLocation;

		/**
		 * keep track of the layers, since we need to update them in the action
		 * 
		 */
		private Layers _theLayers;

		/**
		 * the chart that we need to refresh during drag
		 * 
		 */
		private PlainChart _myChart;

		/**
		 * the viewport that we're controlling
		 * 
		 */
		private PlainProjection _theProjection;

		public void doMouseDrag(final org.eclipse.swt.graphics.Point pt,
				final int JITTER, final Layers theLayers, final SWTCanvas theCanvas)
		{
			final WorldLocation theLocation = theCanvas.getProjection().toWorld(
					new java.awt.Point(pt.x, pt.y));

			// just do a check that we have our start point (it may have been cleared
			// at the end of the move operation)
			if (_lastLocation != null)
			{

				// sort out the vector to apply to the corners
				final WorldVector wv = _lastLocation.subtract(theLocation);

				// apply this vector to the corners
				final WorldArea newArea = new WorldArea(_lastArea.getTopLeft().add(wv),
						_lastArea.getBottomRight().add(wv));

				// ok, set the new area
				MWC.GUI.Tools.Chart.Pan.PanAction.setNewArea(theCanvas.getProjection(),
						newArea);

				_myChart.update();

				// remember the last area
				_lastArea = theCanvas.getProjection().getDataArea();
			}
			else
				_lastLocation = new WorldLocation(theLocation);
		}

		public void doMouseUp(final org.eclipse.swt.graphics.Point point, final int keyState)
		{

			// and ditch our old one
			// cool, sorted. create an action, so we can put it into the undo buffer.
			final Action theAction = new PanAction(_theProjection, _originalArea, _lastArea);

			// and wrap it
			final DebriefActionWrapper daw = new DebriefActionWrapper(theAction,
					_theLayers, null);

			// and add it to the clipboard
			CorePlugin.run(daw);
		}

		public void mouseDown(final org.eclipse.swt.graphics.Point point,
				final SWTCanvas canvas, final PlainChart theChart)
		{
			_theProjection = theChart.getCanvas().getProjection();
			_theLayers = theChart.getLayers();
			_myChart = theChart;

			_originalArea = new WorldArea(_theProjection.getVisibleDataArea());

			_lastArea = new WorldArea(_originalArea);
			_lastLocation = null;

			
			// and assign it to the control
			canvas.getCanvas().setCursor(getDownCursor());
		}

		/**
		 * ok, assign the cursor for when we're just hovering
		 * 
		 * @return the new cursor to use, silly.
		 */
		public Cursor getDownCursor()
		{
			// ok, return the pan cursor
			return CursorRegistry.getCursor(CursorRegistry.HAND_FIST);
		}

		/**
		 * ok, assign the cursor for when we're just hovering
		 * 
		 * @return the new cursor to use, silly.
		 */
		public Cursor getNormalCursor()
		{
			// ok, return the normal cursor
			return CursorRegistry.getCursor(CursorRegistry.HAND);
		}
	}

	public PlotMouseDragger getDragMode()
	{
		return new PanMode();
	}

}