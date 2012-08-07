/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
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
				final int JITTER, final Layers theLayers, SWTCanvas theCanvas)
		{
			WorldLocation theLocation = theCanvas.getProjection().toWorld(
					new java.awt.Point(pt.x, pt.y));

			// just do a check that we have our start point (it may have been cleared
			// at the end of the move operation)
			if (_lastLocation != null)
			{

				// sort out the vector to apply to the corners
				WorldVector wv = _lastLocation.subtract(theLocation);

				// apply this vector to the corners
				WorldArea newArea = new WorldArea(_lastArea.getTopLeft().add(wv),
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

		public void doMouseUp(org.eclipse.swt.graphics.Point point, int keyState)
		{

			// and ditch our old one
			_newCursor.dispose();
			_newCursor = null;

			// cool, sorted. create an action, so we can put it into the undo buffer.
			Action theAction = new PanAction(_theProjection, _originalArea, _lastArea);

			// and wrap it
			DebriefActionWrapper daw = new DebriefActionWrapper(theAction,
					_theLayers, null);

			// and add it to the clipboard
			CorePlugin.run(daw);
		}

		public void mouseDown(org.eclipse.swt.graphics.Point point,
				SWTCanvas canvas, PlainChart theChart)
		{
			_theProjection = theChart.getCanvas().getProjection();
			_theLayers = theChart.getLayers();
			_myChart = theChart;

			_originalArea = new WorldArea(_theProjection.getVisibleDataArea());

			_lastArea = new WorldArea(_originalArea);
			_lastLocation = null;

			// create the new cursor
			_newCursor = getDownCursor();

			// and assign it to the control
			canvas.getCanvas().setCursor(_newCursor);
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
			// ok, return the normal cursor
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