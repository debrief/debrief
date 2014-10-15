/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.PlainChart;

/**
 * @author ian.mayo
 */
abstract public class CoreEditorAction extends AbstractHandler implements IEditorActionDelegate,
		IWorkbenchWindowActionDelegate
{

	protected IChartBasedEditor _myEditor = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(final IAction action, final IEditorPart targetEditor)
	{
		if (targetEditor == null) {
			_myEditor = null;
			return;
		}

		if (targetEditor instanceof IChartBasedEditor)
		{
			_myEditor = (IChartBasedEditor) targetEditor;
		}
		else
		{
			CorePlugin.logError(Status.ERROR,
					"Debrief action being triggered by wrong type of editor", null);
		}

	}

	public IChartBasedEditor getEditor()
	{
		_myEditor = null;
		// do we know our editor?
		if (_myEditor == null)
		{
			// nope, better generate it
			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			final IWorkbenchPage page = win.getActivePage();
			final IEditorPart editor = page.getActiveEditor();
			setActiveEditor(null, editor);
		}

		// ok, give it a go.
		return _myEditor;
	}

	protected PlainChart getChart()
	{
		PlainChart res = null;
		final IChartBasedEditor editor = getEditor();
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
	public final void run(final IAction action)
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
		executeInternal();
	}

	private void executeInternal()
	{
		// check we're looking at our type of editor
		final PlainChart chrt = getChart();
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
	public void selectionChanged(final IAction action, final ISelection selection)
	{

	}

	public void dispose()
	{

	}

	public void init(final IWorkbenchWindow window)
	{
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		executeInternal();
		return null;
	}

}
