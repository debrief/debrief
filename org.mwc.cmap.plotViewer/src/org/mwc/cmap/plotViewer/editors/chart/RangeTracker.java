/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
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
import org.mwc.cmap.core.preferences.CMAPPrefsPage;

public class RangeTracker extends CoreTracker
{

	private static final String RANGE_TOOLTIP = "Current measured range/bearing";
	private static final String DUFF_RANGE_STRING = "[------.-- yds ----d]";

	/**
	 * create a range tracker object
	 * 
	 */
	public RangeTracker()
	{
		super("RangeTracker", DUFF_RANGE_STRING, RANGE_TOOLTIP,
				CMAPPrefsPage.PREFS_PAGE_ID);
	}

	/**
	 * start tracking the indicated chart
	 * 
	 * @param chart
	 *          the chart who's mouse movements we now track
	 */
	public static void displayResultsIn(final EditorPart editor)
	{
		if ((_singleton == null) || (_singleton._myEditor != editor))
		{
			// do we need to create our bits?
			if (_singleton == null)
			{
				_singleton = new RangeTracker();
			}
			else
			{
				forgetSettings(_singleton);
			}

			// now start listening to the new one
			storeSettings(_singleton, editor);
		}
		else
		{
			if(_singleton._lastText != null)
				CoreTracker.write(_singleton._lastText);
		}
	}

}
