package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.mwc.cmap.grideditor.GridEditorActionContext;
import org.mwc.cmap.grideditor.data.GriddableWrapper;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.Editable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.TimeStampedDataItem;


public abstract class AbstractSingleItemAction extends AbstractViewerAction {

	private final boolean myAllowEmptySelection;
	private final boolean needsAddRemoveSupport;

	/**
	 * 
	 * @param allowEmptySelection whether we can run without a selection
	 * @param operationRequiresAddRemoveSupport whether our data source must support add/remove
	 */
	public AbstractSingleItemAction(boolean allowEmptySelection, boolean operationRequiresAddRemoveSupport) {
		myAllowEmptySelection = allowEmptySelection;
		needsAddRemoveSupport = operationRequiresAddRemoveSupport;
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
		
		// and the add/remove
		if(needsAddRemoveSupport)
		{

			GriddableWrapper gw = (GriddableWrapper) actionContext.getInput();
			Editable ed = gw.getWrapper().getEditable();
			if(ed instanceof GriddableSeriesMarker)
			{
				GriddableSeriesMarker gs = (GriddableSeriesMarker) ed;
				if(!gs.supportsAddRemove())
					return null;
			}
			
		}
		
		return createUndoableOperation(undoContext, mySeries, (TimeStampedDataItem) firstSelected);
	}

	protected abstract IUndoableOperation createUndoableOperation(IUndoContext undoContext, GriddableSeries series, TimeStampedDataItem subject);
}
