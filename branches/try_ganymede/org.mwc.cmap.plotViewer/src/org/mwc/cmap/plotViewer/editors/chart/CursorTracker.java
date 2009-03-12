package org.mwc.cmap.plotViewer.editors.chart;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.ui_support.LineItem;

import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.PlainChart.ChartCursorMovedListener;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.BriefFormatLocation;

public class CursorTracker /* extends StatusPanel */
{
	private static final String POSITION_TOOLTIP = "Mouse position";

	private static final String POSITION_TEMPLATE = " 00"
			+ BriefFormatLocation.DEGREE_SYMBOL + "00\'00.00\"N 000"
			+ BriefFormatLocation.DEGREE_SYMBOL + "00\'00.00\"W ";

	/** single instance of cursor tracker.
	 * 
	 */
	private static CursorTracker _singleton;

	/**
	 * the projection we're looking at
	 */
	private SWTChart _myChart;


	/** the currently assigned editor
	 * 
	 */
	private EditorPart _myEditor;
	
	/** the line instance we write to
	 * 
	 */
	final LineItem _myLine;

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
		// first the status bar contribution
		_myLine = new LineItem("CursorTracker", POSITION_TEMPLATE,
				POSITION_TOOLTIP, null);

		// sort out a chart listener
		_myMoveListener = new PlainChart.ChartCursorMovedListener()
		{
			public void cursorMoved(WorldLocation thePos, boolean dragging,
					Layers theData)
			{
				String msg = BriefFormatLocation.toString(thePos);
				_myLine.setText(msg);
			}
		};		
	}

	public void close()
	{
		// belt & braces, ditch stuff.
		forgetSettings();
	}

	/** teardown for this chart
	 * 
	 */
	private void forgetSettings()
	{
		if (_myEditor != null)
		{
			// get the status manager for this editor
			IStatusLineManager oldMgr = _myEditor.getEditorSite().getActionBars()
					.getStatusLineManager();

			// try to remove our line item
			oldMgr.remove(_singleton._myLine);
		}
		
		if(_myChart != null)
		{
			// well, stop listening to that one
			_myChart.removeCursorMovedListener(_singleton._myMoveListener);
		}

		_myEditor = null;
		_myChart = null;
	}

	/** setup for this chart
	 * 
	 * @param editor
	 * @param chart
	 */
	private void storeSettings(EditorPart editor, SWTChart chart)
	{
		_myEditor = editor;
		_myChart = chart;

		// get the status manager for this editor
		IStatusLineManager oldMgr = _myEditor.getEditorSite().getActionBars()
				.getStatusLineManager();

		// try to add our line item
		oldMgr.add(_singleton._myLine);

		// well, and start listening to that one
		_myChart.addCursorMovedListener(_singleton._myMoveListener);

	}

	/**
	 * start tracking the indicated chart
	 * 
	 * @param chart
	 *          the chart who's mouse movements we now track
	 */
	public static void trackThisChart(SWTChart chart, EditorPart editor)
	{
		// are we already listening to a chart?
		if (_singleton != null)
		{
			_singleton.forgetSettings();
		}

		// do we need to create our bits?
		if (_singleton == null)
		{
			_singleton = new CursorTracker();
		}

		// now start listening to the new one
		_singleton.storeSettings(editor, chart);
	}
}
