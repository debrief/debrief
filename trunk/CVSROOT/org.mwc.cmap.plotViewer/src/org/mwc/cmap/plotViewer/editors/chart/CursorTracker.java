package org.mwc.cmap.plotViewer.editors.chart;

import java.awt.Point;

import org.eclipse.jface.action.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.mwc.cmap.core.ui_support.LineItem;

import MWC.GUI.*;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.BriefFormatLocation;

public class CursorTracker implements MouseMoveListener
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

	// ///////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////

	public CursorTracker(SWTChart myChart, LineItem theLabel)
	{
		// ok, remember the chart
		_myChart = myChart;

		// and listen to the chart
		_myChart.addCursorMovedListener(new PlainChart.ChartCursorMovedListener()
		{

			public void cursorMoved(WorldLocation thePos, boolean dragging,
					Layers theData)
			{
				String msg = BriefFormatLocation.toString(thePos);
				write(msg);
			}
		});

		_label = theLabel;
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

	/**
	 * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseMoved(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
	 */
	public void mouseMove(final MouseEvent e)
	{
		Point screen = new Point(e.x, e.y);
		WorldLocation loc = _myChart.getCanvas().getProjection().toWorld(screen);

		if (loc != null)
		{
			String msg = BriefFormatLocation.toString(loc);
			write(msg);
		}

		// }
		// Coordinate world = getContext().pixelToWorld(screen.x, screen.y);
		// String x = String.valueOf(world.x);
		// String y = String.valueOf(world.y);
		// if (_label.label.isDisposed())
		// return;
		// _label.label.setText(x.substring(0, Math.min(10, x.length())));
		// if (ylabel.label.isDisposed())
		// return;
		// ylabel.label.setText(y.substring(0, Math.min(10, y.length())));

	}

	/**
	 * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
	 */
	public void mouseDragged(MouseEvent e)
	{
		mouseMove(e);
	}

	/**
	 * @return false if an exception occurs while initializing labels.
	 */
	private boolean initLabels()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{

				IWorkbenchPartReference[] parts = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().getEditorReferences();
				if (parts.length == 0)
					parts = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().getViewReferences();

				for (int i = 0; i < parts.length && _label == null; i++)
				{
					IWorkbenchPart activePart = parts[i].getPart(false);
					if (activePart instanceof ViewPart)
					{
						ViewPart view = (ViewPart) activePart;
						line = (StatusLineManager) view.getViewSite().getActionBars()
								.getStatusLineManager();
					}
					else if (activePart instanceof EditorPart)
					{
						EditorPart view = (EditorPart) activePart;
						line = view.getEditorSite().getActionBars().getStatusLineManager();
					}
					_label = new LineItem("CursorPosition.position"); //$NON-NLS-1$

					line.add(new GroupMarker("CursorPosition.position")); //$NON-NLS-1$
					line.add(_label);
					line.update(true);
				}

			}
		});
		return true;
	}

}
