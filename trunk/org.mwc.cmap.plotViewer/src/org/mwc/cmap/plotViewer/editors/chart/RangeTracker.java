package org.mwc.cmap.plotViewer.editors.chart;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.ui_support.LineItem;

public class RangeTracker
{
	private static final String DUFF_RANGE_STRING = "[------.-- yds ----d]";

	private static final String RANGE_TOOLTIP = "Current measured range/bearing";

	/**
	 * single instance of cursor tracker.
	 * 
	 */
	private static RangeTracker _singleton;

	/**
	 * the currently assigned editor
	 * 
	 */
	protected EditorPart _myEditor;

	/**
	 * the line instance we write to
	 * 
	 */
	final LineItem _myLine;

	// ///////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////

	public RangeTracker()
	{
		this("RangeTracker", DUFF_RANGE_STRING, RANGE_TOOLTIP, null);
	}

	protected RangeTracker(String string, String duffString, String tooltip, String prefsId)
	{
		// first the status bar contribution
		_myLine = new LineItem(string, duffString, tooltip, prefsId);
	}

	public void close()
	{
		// belt & braces, ditch stuff.
		forgetSettings(this);
	}

	public static void write(String txt)
	{
		if (_singleton != null)
			_singleton._myLine.setText(txt);
	}

	/**
	 * teardown for this chart
	 * 
	 */
	protected static void forgetSettings(RangeTracker tracker)
	{
		if (tracker._myEditor != null)
		{
			// get the status manager for this editor
			IStatusLineManager oldMgr = tracker._myEditor.getEditorSite().getActionBars()
					.getStatusLineManager();

			// try to remove our line item
			oldMgr.remove(tracker._myLine);
		}

		tracker._myEditor = null;
	}

	
	/**
	 * setup for this chart
	 * 
	 * @param editor
	 */
	protected static void storeSettings(RangeTracker tracker, EditorPart editor)
	{
		tracker._myEditor = editor;

		// get the status manager for this editor
		IStatusLineManager oldMgr = tracker._myEditor.getEditorSite().getActionBars()
				.getStatusLineManager();

		// try to add our line item
		oldMgr.add(tracker._myLine);

		// and tell everybody about the change
		tracker._myEditor.getEditorSite().getActionBars().updateActionBars();

		_singleton._myLine.setText(DUFF_RANGE_STRING);
	}
	
	/**
	 * start tracking the indicated chart
	 * 
	 * @param chart
	 *          the chart who's mouse movements we now track
	 */
	public static void displayResultsIn(EditorPart editor)
	{
		// are we already listening to a chart?
		if (_singleton != null)
		{
			forgetSettings(_singleton);
		}

		// do we need to create our bits?
		if (_singleton == null)
		{
			_singleton = new RangeTracker();
		}

		// now start listening to the new one
		storeSettings(_singleton, editor);
	}
}
