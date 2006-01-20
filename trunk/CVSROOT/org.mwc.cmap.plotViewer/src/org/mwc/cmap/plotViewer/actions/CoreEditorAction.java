/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.*;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;

import MWC.GUI.PlainChart;

/**
 * @author ian.mayo
 */
abstract public class CoreEditorAction implements IEditorActionDelegate
{

	protected CorePlotEditor _myEditor = null;

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
		
		if (targetEditor instanceof CorePlotEditor)
		{
			_myEditor = (CorePlotEditor) targetEditor;
		}
		else
		{
			CorePlugin.logError(Status.ERROR,
					"Debrief action being triggered by wrong type of editor", null);
		}

	}
	
	private CorePlotEditor getEditor()
	{
		// do we know our editor?
		if(_myEditor == null)
		{
			// nope, better generate it
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			IEditorPart editor = page.getActiveEditor();
			setActiveEditor(null, editor);
		}
		
		// ok, give it a go.
		return _myEditor;
	}
	
	protected CorePlotEditor getPlot()
	{
		return getEditor();
	}
	
	protected PlainChart getChart()
	{
		return getEditor().getChart();
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
