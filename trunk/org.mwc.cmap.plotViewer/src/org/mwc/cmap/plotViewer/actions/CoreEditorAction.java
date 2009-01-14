/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.IResourceProvider;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;

import MWC.GUI.PlainChart;

/**
 * @author ian.mayo
 */
abstract public class CoreEditorAction implements IEditorActionDelegate,
		IWorkbenchWindowActionDelegate
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
		if (targetEditor == null)
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

	public CorePlotEditor getEditor()
	{
		_myEditor = null;
		// do we know our editor?
		if (_myEditor == null)
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

	protected IResourceProvider getPlot()
	{
		return getEditor();
	}

	protected PlainChart getChart()
	{
		PlainChart res = null;
		CorePlotEditor editor = getEditor();
		if (editor != null)
			res = editor.getChart();
		return res;
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
	public final void run(IAction action)
	{
		// right - we get called when radio buttons get de-selected. We only want to
		// fire
		// the event for the 'new action' - that's the action that's checked.
		if (action == null)
		{

		}
		else if ((action.getStyle() == IAction.AS_RADIO_BUTTON) && (!action.isChecked()))
		{
			// no, drop out
			return;
		}

		// ok - if we were going to drop out we'd have done it already. Let's just
		// go for it.

		// check we're looking at our type of editor
		PlainChart chrt = getChart();
		if (chrt == null)
		{
			// report to user that they do need to have a plot editor open...
			// System.err.println("wrong type of editor");
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Debrief action",
					"Please select a Debrief plot before performing this action");
		}
		else
		{
			execute();
		}
	}

	/**
	 * perform our operation
	 */
	abstract protected void execute();

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

	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window)
	{
		// TODO Auto-generated method stub
	}

}
