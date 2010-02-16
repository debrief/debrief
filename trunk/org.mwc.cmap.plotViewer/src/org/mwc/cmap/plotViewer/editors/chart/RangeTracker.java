package org.mwc.cmap.plotViewer.editors.chart;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.ui_support.LineItem;

public class RangeTracker
{
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
		this("RangeTracker", "[123456.78 yds 1234d]", RANGE_TOOLTIP, null);
	}

	protected RangeTracker(String string, String string2, String rangeTooltip,
			Object object)
	{
		// first the status bar contribution
		_myLine = new LineItem("RangeTracker", "[123456.78 yds 1234d]",
				RANGE_TOOLTIP, null);
	}

	public void close()
	{
		// belt & braces, ditch stuff.
		forgetSettings();
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
	protected void forgetSettings()
	{
		if (_myEditor != null)
		{
			// get the status manager for this editor
			IStatusLineManager oldMgr = _myEditor.getEditorSite().getActionBars()
					.getStatusLineManager();

			// try to remove our line item
			oldMgr.remove(_singleton._myLine);
		}

		_myEditor = null;
	}

	/**
	 * setup for this chart
	 * 
	 * @param editor
	 */
	protected void storeSettings(EditorPart editor)
	{
		_myEditor = editor;

		// get the status manager for this editor
		IStatusLineManager oldMgr = _myEditor.getEditorSite().getActionBars()
				.getStatusLineManager();

		// try to add our line item
		oldMgr.add(_singleton._myLine);

		// and tell everybody about the change
		_myEditor.getEditorSite().getActionBars().updateActionBars();
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
			_singleton.forgetSettings();
		}

		// do we need to create our bits?
		if (_singleton == null)
		{
			_singleton = new RangeTracker();
		}

		// now start listening to the new one
		_singleton.storeSettings(editor);
	}
}
