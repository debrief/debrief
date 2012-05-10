/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import java.awt.Point;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.editors.chart.*;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.*;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Chart.Pan.PanAction;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class Pan extends CoreDragAction
{

	public static class PanMode extends SWTChart.PlotMouseDragger
	{
		Point _startPoint;

		SWTCanvas _myCanvas;

		PlainChart _myChart;

		/**
		 * the hand cursor we show when dragging
		 * 
		 */
		Cursor _newCursor;

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
		 * where we started dragging from
		 * 
		 */
		protected WorldLocation _theStart;

		protected WorldLocation _theEnd;

		public void doMouseDrag(final org.eclipse.swt.graphics.Point pt,
				final int JITTER, final Layers theLayers, SWTCanvas theCanvas)
		{
			WorldLocation theLocation = _myChart.getCanvas().getProjection()
					.toWorld(new java.awt.Point(pt.x, pt.y));

			// just do a check that we have our start point (it may have been cleared
			// at the end of the move operation)
			if (_lastLocation != null)
			{

				// sort out the vector to apply to the corners
				WorldVector wv = _lastLocation.subtract(theLocation);

				// apply this vector to the corners
				WorldArea newArea = new WorldArea(_lastArea.getTopLeft().add(wv),
						_lastArea.getBottomRight().add(wv));

				// ok, store the new area
				setNewArea(_myChart.getCanvas().getProjection(), newArea);

				// remember the last area
				_lastArea = _myChart.getCanvas().getProjection().getDataArea();

				// and get the chart to redraw itself
				_myChart.update();
			}
			else
				_lastLocation = new WorldLocation(theLocation);
		}

		public void doMouseUp(org.eclipse.swt.graphics.Point point, int keyState)
		{

			// and ditch our old one
			_newCursor.dispose();
			_newCursor = null;

			// we've got to restore the old area in order to calculate
			// the destination position in terms of old coordinates
			// instead of the current screen coordinates
			setNewArea(_myChart.getCanvas().getProjection(), _originalArea);

			// now we can do our data/world transform correctly
			_theEnd = _myChart.getCanvas().toWorld(
					new java.awt.Point(point.x, point.y));

			// sort out the vector to apply to the corners
			WorldVector wv = _theStart.subtract(_theEnd);

			// apply this vector to the corners
			WorldLocation currentCentre = _originalArea.getCentre();
			WorldLocation newCentre = currentCentre.add(wv);

			// and store the new area
			WorldArea _newArea = new WorldArea(_originalArea);
			_newArea.setCentre(newCentre);

			// cool, sorted. remember the action
			Action theAction = new PanAction(_myChart, _originalArea, _newArea);

			System.out.println("start point cleared.");
			_startPoint = null;

			// and wrap it
			DebriefActionWrapper daw = new DebriefActionWrapper(theAction,
					_myChart.getLayers(), null);

			// and add it to the clipboard
			CorePlugin.run(daw);
		}

		public void mouseDown(org.eclipse.swt.graphics.Point point,
				SWTCanvas canvas, PlainChart theChart)
		{
			_startPoint = new java.awt.Point(point.x, point.y);
			_myCanvas = canvas;
			_myChart = theChart;

			_originalArea = new WorldArea(_myChart.getCanvas().getProjection()
					.getVisibleDataArea());

			_lastArea = new WorldArea(_originalArea);
			_lastLocation = null;

			_theStart = new WorldLocation(_myChart.getCanvas().getProjection()
					.toWorld(new java.awt.Point(point.x, point.y)));

			// create the new cursor
			_newCursor = getDownCursor();

			// and assign it to the control
			canvas.getCanvas().setCursor(_newCursor);
		}

		protected void setNewArea(PlainProjection proj, WorldArea theArea)
		{
			double oldBorder = proj.getDataBorder();
			proj.setDataBorderNoZoom(1.0);
			proj.setDataArea(theArea);
			// in the shiny new GeoTools projection we don't need to fit-to-win after
			// changing the data area
			// proj.zoom(0.0);
			proj.setDataBorderNoZoom(oldBorder);
		}

		/**
		 * ok, assign the cursor for when we're just hovering
		 * 
		 * @return the new cursor to use, silly.
		 */
		public Cursor getDownCursor()
		{
			// ok, return the pan cursor
			if ((_downCursor == null) || (_downCursor.isDisposed()))
			{
				_downCursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/hand_fist.ico").getImageData(), 4, 2);
			}

			return _downCursor;
		}

		/**
		 * ok, assign the cursor for when we're just hovering
		 * 
		 * @return the new cursor to use, silly.
		 */
		public Cursor getNormalCursor()
		{
			// ok, return the pan cursor
			if ((_normalCursor == null) || (_normalCursor.isDisposed()))
				_normalCursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/hand.ico").getImageData(), 4, 2);

			return _normalCursor;
		}
	}

	public PlotMouseDragger getDragMode()
	{
		return new PanMode();
	}

}