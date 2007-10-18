package org.mwc.cmap.plotViewer.editors.chart;

import org.eclipse.ui.part.EditorPart;

import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.PlainChart.ChartCursorMovedListener;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.BriefFormatLocation;

public class CursorTracker extends StatusPanel
{
	/**
	 * the projection we're looking at
	 */
	final SWTChart _myChart;


	/** something to listen out for chart movement
	 * 
	 */
	final private ChartCursorMovedListener _moveListener;
	
	// ///////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////

	public CursorTracker(SWTChart myChart, EditorPart editor)
	{
		super(editor, "CursorTracker", " 00" + BriefFormatLocation.DEGREE_SYMBOL
			+ "00\'00.00\"N 000" + BriefFormatLocation.DEGREE_SYMBOL
			+ "00\'00.00\"W ", "Mouse position", null);
		
		// ok, remember the chart
		_myChart = myChart;
		
		// sort out a chart listener
		_moveListener = new PlainChart.ChartCursorMovedListener()
		{
			public void cursorMoved(WorldLocation thePos, boolean dragging,
					Layers theData)
			{
				String msg = BriefFormatLocation.toString(thePos);
				write(msg);
			}
		};
		
		// and listen to the chart
		_myChart.addCursorMovedListener(_moveListener);
	}

	public void close()
	{
		_myChart.removeCursorMovedListener(_moveListener);
		super.close();
	}
		
}
