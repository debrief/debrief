/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.editors.PlotEditor;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.PlainChart;

/**
 * @author ian.mayo
 */
abstract public class CoreEditorAction implements IEditorActionDelegate
{

	protected PlotEditor _myEditor = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
		if(targetEditor == null)
			return;
		
		if (targetEditor instanceof PlotEditor)
		{
			_myEditor = (PlotEditor) targetEditor;
		}
		else
		{
			CorePlugin.logError(Status.ERROR,
					"Debrief action being triggered by wrong type of editor", null);
		}

	}
	
	protected PlotEditor getPlot()
	{
		return _myEditor;
	}
	
	protected PlainChart getChart()
	{
		return _myEditor.getChart();
	}
	
	protected void redrawChart()
	{
		getChart().getCanvas().updateMe();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		run();
	}

	/** perform our operation
	 */
	abstract protected void run();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		// TODO Auto-generated method stub

	}

}
