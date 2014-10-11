/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.plotViewer.editors.chart;

import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.gridharness.LocationFormatPreferencePage;

import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.PlainChart.ChartCursorMovedListener;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.BriefFormatLocation;
import MWC.Utilities.TextFormatting.PlainFormatLocation;

public final class CursorTracker extends CoreTracker
{
	private static final String POSITION_TOOLTIP = "Mouse position";

	private static final String POSITION_TEMPLATE = " 00"
			+ BriefFormatLocation.DEGREE_SYMBOL + "00\'00.000\"N 000"
			+ BriefFormatLocation.DEGREE_SYMBOL + "00\'00.000\"W ";

	/**
	 * single instance of cursor tracker.
	 * 
	 */
	private static CursorTracker _singleton;

	/**
	 * the projection we're looking at
	 */
	private SWTChart _myChart;
	/**
	 * something to listen out for chart movement
	 * 
	 */
	private final ChartCursorMovedListener _myMoveListener;

	// ///////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////

	private CursorTracker()
	{

		// declare the item
		super("CursorTracker", POSITION_TEMPLATE, POSITION_TOOLTIP, LocationFormatPreferencePage.PREFS_PAGE_ID);

		// sort out a chart listener
		_myMoveListener = new PlainChart.ChartCursorMovedListener()
		{
			public void cursorMoved(final WorldLocation thePos, final boolean dragging,
					final Layers theData)
			{
				String msg;
				
				final PlainFormatLocation locationFormatter = CorePlugin.getDefault().getLocationFormat();

				if (locationFormatter != null)
					msg = locationFormatter.convertToString(thePos);
				else
					msg = BriefFormatLocation.toString(thePos);

				_myLine.setText(msg);
			}
		};
	}

	public void close()
	{
		// belt & braces, ditch stuff.
		forgetSettings();
	}

	/**
	 * teardown for this chart
	 * 
	 */
	private void forgetSettings()
	{
		// forget the parent bits
		super.forgetSettings(this);

		if (_myChart != null)
		{
			// well, stop listening to that one
			_myChart.removeCursorMovedListener(_singleton._myMoveListener);
		}

		_myChart = null;
	}

	/**
	 * setup for this chart
	 * 
	 * @param editor
	 * @param chart
	 */
	private void storeSettings(final EditorPart editor, final SWTChart chart)
	{
		// do the parent's store bit
		CoreTracker.storeSettings(this, editor);

		// now do our store bit
		_myChart = chart;

		// well, and start listening to that one
		_myChart.addCursorMovedListener(_singleton._myMoveListener);

		// and reset the data string
		_singleton._myLine.setText(POSITION_TEMPLATE);
	}

	/**
	 * start tracking the indicated chart
	 * 
	 * @param chart
	 *          the chart who's mouse movements we now track
	 */
	public static void trackThisChart(final SWTChart chart, final EditorPart editor)
	{
		if ((_singleton == null) || (_singleton._myEditor != editor))
		{
			// do we need to create our bits?
			if (_singleton == null)
			{
				_singleton = new CursorTracker();
			}
			else
			{
				forgetSettings(_singleton);
			}

			// now start listening to the new one
			_singleton.storeSettings(editor, chart);
		}
		else
		{
			if (_singleton._lastText != null)
				CoreTracker.write(_singleton._lastText);
		}
	}
}
