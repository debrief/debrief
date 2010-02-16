package org.mwc.cmap.plotViewer.editors.chart;

import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.preferences.CMAPPrefsPage;

public class RangeTracker extends CoreTracker
{

	private static final String RANGE_TOOLTIP = "Current measured range/bearing";
	private static final String DUFF_RANGE_STRING = "[------.-- yds ----d]";

	
	/** create a range tracker object
	 * 
	 */
	public RangeTracker()
	{
		super("RangeTracker", DUFF_RANGE_STRING, RANGE_TOOLTIP, CMAPPrefsPage.PREFS_PAGE_ID);
	}
	
	
	/**
	 * start tracking the indicated chart
	 * 
	 * @param chart
	 *          the chart who's mouse movements we now track
	 */
	public static void displayResultsIn(EditorPart editor)
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


}
