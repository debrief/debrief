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
package org.mwc.cmap.grideditor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.grideditor.data.GriddableWrapper;
import org.mwc.cmap.grideditor.table.actons.GridEditorActionGroup;

import MWC.GUI.Editable;
import MWC.GUI.GriddableSeriesMarker;

public class GridEditorView extends ViewPart
{

	private ISelectionListener mySelectionListener;

	private GridEditorActionGroup myActions;

	private GridEditorUI myUI;

	private GridEditorUndoSupport myUndoSupport;

	private UndoActionHandler myUndoAction;

	private RedoActionHandler myRedoAction;

	@Override
	public void createPartControl(final Composite parent)
	{
		final GridEditorActionContext actionContext = new GridEditorActionContext(
				myUndoSupport);
		myActions = new GridEditorActionGroup(this, actionContext);
		myUI = new GridEditorUI(parent, myActions);
		final ISelectionService selectionService = getSite().getWorkbenchWindow()
				.getSelectionService();
		handleWorkspaceSelectionChanged(selectionService.getSelection());

		final IActionBars actionBars = getViewSite().getActionBars();
		myActions.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), myUndoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), myRedoAction);
	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(getSelectionListener());
		super.dispose();
	}

	private GriddableWrapper extractGriddableSeries(final ISelection selection)
	{
		GriddableWrapper res = null;

		if (false == selection instanceof IStructuredSelection)
		{
			return null;
		}
		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if (structuredSelection.isEmpty())
		{
			return null;
		}
		final Object firstElement = structuredSelection.getFirstElement();

		// right, see if this is a series object already. if it is, we've got
		// our
		// data. if it isn't, see
		// if it's a candidate for editing and collate a series of elements
		if (firstElement instanceof EditableWrapper)
		{
			final EditableWrapper wrapped = (EditableWrapper) firstElement;
			final Object value = wrapped.getEditableValue();
			if (wrapped != null)
			{
				if ((value instanceof GriddableSeriesMarker)
						&& !(value instanceof Editable.DoNoInspectChildren))
				{
					res = new GriddableWrapper(wrapped);
				}
			}
		}
		else
		{
			// see if it can be adapted
			if (firstElement instanceof IAdaptable)
			{
				final IAdaptable is = (IAdaptable) firstElement;
				final EditableWrapper wrapped = (EditableWrapper) is
						.getAdapter(EditableWrapper.class);
				if (wrapped != null)
				{
					final Object value = wrapped.getEditableValue();

					if ((value instanceof GriddableSeriesMarker)
							&& !(value instanceof Editable.DoNoInspectChildren))
					{
						res = new GriddableWrapper(wrapped);
					}
				}
			}
		}

		return res;
	}

	private ISelectionListener getSelectionListener()
	{
		if (mySelectionListener == null)
		{
			mySelectionListener = new ISelectionListener()
			{

				public void selectionChanged(final IWorkbenchPart part, final ISelection selection)
				{
					if (part == GridEditorView.this)
					{
						// ignore, we are going to handle our own selection
						// ourselves
						return;
					}
					handleWorkspaceSelectionChanged(selection);
				}
			};
		}
		return mySelectionListener;
	}

	public GridEditorUI getUI()
	{
		return myUI;
	}

	private void handleWorkspaceSelectionChanged(final ISelection actualSelection)
	{
		if (myUI.isDisposed())
		{
			return;
		}

		// am I even tracking the selection?
		if (!myUI.getTable().isTrackingSelection())
			return;

		final GriddableWrapper input = extractGriddableSeries(actualSelection);

		if (input == null)
		{
			// not valid data - set input to null (which clears the UI
			myUI.inputSeriesChanged(null);
		}
		else
		{

			// yes, but what are we currently looking at?
			final GriddableWrapper existingInput = (GriddableWrapper) myUI.getTable()
					.getTableViewer().getInput();

			// see if we're currently looking at something
			EditableWrapper editable = null;
			if (existingInput != null)
			{
				editable = existingInput.getWrapper();
			}

			// are they the same?
			if (input.getWrapper() == editable)
			{
				// ignore, we're already looking at it
			}
			else
			{
				myUI.inputSeriesChanged(input);
			}
		}
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
		final ISelectionService selectionService = site.getWorkbenchWindow()
				.getSelectionService();
		selectionService.addSelectionListener(getSelectionListener());

		initUndoSupport();
	}

	private void initUndoSupport()
	{
		myUndoSupport = new GridEditorUndoSupport(PlatformUI.getWorkbench()
				.getOperationSupport().getOperationHistory());
		// set up action handlers that operate on the current context
		myUndoAction = new UndoActionHandler(this.getSite(),
				myUndoSupport.getUndoContext());
		myRedoAction = new RedoActionHandler(this.getSite(),
				myUndoSupport.getUndoContext());
	}

	public void refreshUndoContext()
	{
		if (myUndoAction != null)
		{
			myUndoAction.dispose();
			myUndoAction = null;
		}
		if (myRedoAction != null)
		{
			myRedoAction.dispose();
			myRedoAction = null;
		}

		myUndoAction = new UndoActionHandler(this.getSite(),
				myUndoSupport.getUndoContext());
		myRedoAction = new RedoActionHandler(this.getSite(),
				myUndoSupport.getUndoContext());
		final IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), myUndoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), myRedoAction);
		actionBars.updateActionBars();
	}

	@Override
	public void setFocus()
	{
		myUI.forceTableFocus();
	}

}
