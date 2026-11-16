/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.GUI.Tools.Chart;



import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;

public class ZoomOut extends PlainTool {

	///////////////////////////////////////////////////////
	// store action information
	///////////////////////////////////////////////////////
	public static class ZoomOutAction implements Action {
		private final PlainChart _theChart;
		private final WorldArea _oldArea;
		private final double _zoomFactor;

		public ZoomOutAction(final PlainChart theChart, final WorldArea oldArea, final double zoomFactor) {
			_theChart = theChart;
			_oldArea = oldArea;
			_zoomFactor = zoomFactor;
		}

		@Override
		public void execute() {
			// set the new zoom factor (which will trigger a resize operation
			_theChart.getCanvas().getProjection().zoom(_zoomFactor);

			// and trigger a redraw
			// no, don't bother - the chart catches the data-area change
			// _theChart.update();
		}

		@Override
		public boolean isRedoable() {
			return true;
		}

		@Override
		public boolean isUndoable() {
			return true;
		}

		@Override
		public String toString() {
			return "Zoom out";
		}

		@Override
		public void undo() {
			// set the data area for the chart to the old area
			_theChart.getCanvas().getProjection().setDataArea(_oldArea);

			// get the projection to refit-itself
			// _theChart.getCanvas().getProjection().zoom(0.0);

			// and redraw the whole plot
			_theChart.update();
		}
	}

	public static class ZoomOutAreaAction implements Action {

		private final PlainChart _theChart;
		private final WorldArea _oldArea;
		private final WorldArea _selectedArea;

		private final double _zoomFactor;

		public ZoomOutAreaAction(final PlainChart theChart, final WorldArea oldArea, final WorldArea selectedArea,
				final double zoomFactor) {
			_theChart = theChart;
			_oldArea = oldArea;
			_selectedArea = selectedArea;
			_zoomFactor = zoomFactor;
		}

		@Override
		public void execute() {
			_theChart.getCanvas().getProjection().zoom(_zoomFactor, _selectedArea);

			// get the projection to refit-itself
			// _theChart.getCanvas().getProjection().zoom(0.0);
		}

		@Override
		public boolean isRedoable() {
			return true;
		}

		@Override
		public boolean isUndoable() {
			return true;
		}

		@Override
		public String toString() {
			return "Zoom out area operation";
		}

		@Override
		public void undo() {
			// set the data area for the chart to the old area
			_theChart.getCanvas().getProjection().setDataArea(_oldArea);

			// get the projection to refit-itself
			// _theChart.getCanvas().getProjection().zoom(0.0);
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/**
	 * my copy of the chart, so that I can tell it to update
	 */
	protected PlainChart _theChart;

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	public ZoomOut(final ToolParent theParent, final PlainChart theChart) {
		super(theParent, "Zoom Out", "images/zoomout.png");
		// remember the chart we are acting upon
		_theChart = theChart;
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////
	@Override
	public Action getData() {
		// get the current data area
		final WorldArea oldArea = _theChart.getCanvas().getProjection().getDataArea();
		return new ZoomOutAction(_theChart, oldArea, 2.0);
	}

}
