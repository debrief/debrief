package org.mwc.cmap.plotViewer.editors.chart;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.ui_support.LineItem;

import MWC.GUI.*;
import MWC.GUI.PlainChart.ChartCursorMovedListener;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.BriefFormatLocation;

public class CursorTracker //implements MouseMoveListener
{
	/**
	 * the projection we're looking at
	 */
	final SWTChart _myChart;

	IStatusLineManager line = null;

	/**
	 * the label we're updating
	 */
	LineItem _label = null;

	/** something to listen out for chart movement
	 * 
	 */
	final private ChartCursorMovedListener _moveListener;
	

	/** keep track of how many people have a pointer to this tracker
	 * 
	 */
	private static int _lineUsers = 0;	
	

	/**
	 * the shared line of status text used across CMAP apps
	 */
	private static LineItem _myLineItem = null;	

	// ///////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////

	public CursorTracker(SWTChart myChart, EditorPart editor)
	{
		// ok, get a new status line
		_label = getStatusLine(editor);;
		
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

	protected void write(final String msg)
	{
		Display d = Display.getDefault();
		d.asyncExec(new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run()
			{

				if (_label != null)
				{
					if (!_label.isDisposed())
					{
						_label.setText(msg);
					}
					else
					{
						// don't worry. the label isn't available when no editor is selected
					}
				}
			}
		});
	}

	public void close()
	{
		_myChart.removeCursorMovedListener(_moveListener);
		_lineUsers --;		
	}

	private static LineItem getStatusLine(EditorPart editor)
	{
		// right, is anybody holding onto the last line item?  If nobody is,
		// Eclipse will ditch it, and we have to create a new one.
		if(_lineUsers == 0)
		{
			IStatusLineManager mgr = editor.getEditorSite().getActionBars()
					.getStatusLineManager();
			_myLineItem = new LineItem("vv aa");
			mgr.add(_myLineItem);
		}
		
		if(_lineUsers < 0)
		{
			System.err.println("CorePlugin: CursorTracker in unstable state");
		}
		
		_lineUsers++;

		return _myLineItem;
	}

		
}
