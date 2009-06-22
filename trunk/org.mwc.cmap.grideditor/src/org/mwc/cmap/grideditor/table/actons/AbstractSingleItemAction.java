package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.mwc.cmap.grideditor.GridEditorActionContext;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;


public abstract class AbstractSingleItemAction extends AbstractViewerAction {

	private final boolean myAllowEmptySelection;

	public AbstractSingleItemAction(boolean allowEmptySelection) {
		myAllowEmptySelection = allowEmptySelection;
	}

	@Override
	public IUndoableOperation createUndoableOperation(GridEditorActionContext actionContext) {
		final IUndoContext undoContext = actionContext.getUndoSupport().getUndoContext();
		final GriddableSeries mySeries = actionContext.getTableInput();
		if (mySeries == null) {
			return null;
		}
		IStructuredSelection selection = actionContext.getStructuredSelection();
		if (selection.isEmpty() && !myAllowEmptySelection) {
			return null;
		}
		if (selection.size() > 1) {
			return null;
		}
		Object firstSelected = selection.getFirstElement();
		if(firstSelected == null)
			return null;
		
		if (firstSelected != null && false == firstSelected instanceof TimeStampedDataItem) {
			return null;
		}
		return createUndoableOperation(undoContext, mySeries, (TimeStampedDataItem) firstSelected);
	}

	protected abstract IUndoableOperation createUndoableOperation(IUndoContext undoContext, GriddableSeries series, TimeStampedDataItem subject);
}
