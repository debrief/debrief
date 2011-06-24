package org.mwc.cmap.grideditor;


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
	public void createPartControl(Composite parent)
	{
		GridEditorActionContext actionContext = new GridEditorActionContext(
				myUndoSupport);
		myActions = new GridEditorActionGroup(this, actionContext);
		myUI = new GridEditorUI(parent, myActions);
		ISelectionService selectionService = getSite().getWorkbenchWindow()
				.getSelectionService();
		handleWorkspaceSelectionChanged(selectionService.getSelection());

		IActionBars actionBars = getViewSite().getActionBars();
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

	private GriddableWrapper extractGriddableSeries(ISelection selection)
	{
		GriddableWrapper res = null;

		if (false == selection instanceof IStructuredSelection)
		{
			return null;
		}
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if (structuredSelection.isEmpty())
		{
			return null;
		}
		Object firstElement = structuredSelection.getFirstElement();

		// right, see if this is a series object already. if it is, we've got
		// our
		// data. if it isn't, see
		// if it's a candidate for editing and collate a series of elements
		if (firstElement instanceof EditableWrapper)
		{
			EditableWrapper wrapped = (EditableWrapper) firstElement;
			if (wrapped.getEditableValue() instanceof GriddableSeriesMarker)
			{
				res = new GriddableWrapper(wrapped);
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

				public void selectionChanged(IWorkbenchPart part, ISelection selection)
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

	private void handleWorkspaceSelectionChanged(ISelection actualSelection)
	{
		if (myUI.isDisposed())
		{
			return;
		}
		GriddableWrapper input = extractGriddableSeries(actualSelection);

		if (input == null)
		{
			// not valid data - set input to null (which clears the UI
			myUI.inputSeriesChanged(null);
		}
		else
		{

			// yes, but what are we currently looking at?
			GriddableWrapper existingInput = (GriddableWrapper) myUI.getTable()
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
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		ISelectionService selectionService = site.getWorkbenchWindow()
				.getSelectionService();
		selectionService.addSelectionListener(getSelectionListener());

		initUndoSupport();
	}

	private void initUndoSupport()
	{
		myUndoSupport = new GridEditorUndoSupport(PlatformUI.getWorkbench()
				.getOperationSupport().getOperationHistory());
		// set up action handlers that operate on the current context
		myUndoAction = new UndoActionHandler(this.getSite(), myUndoSupport
				.getUndoContext());
		myRedoAction = new RedoActionHandler(this.getSite(), myUndoSupport
				.getUndoContext());
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

		myUndoAction = new UndoActionHandler(this.getSite(), myUndoSupport
				.getUndoContext());
		myRedoAction = new RedoActionHandler(this.getSite(), myUndoSupport
				.getUndoContext());
		IActionBars actionBars = getViewSite().getActionBars();
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
