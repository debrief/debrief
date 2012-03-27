package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.mwc.cmap.grideditor.GridEditorPlugin;
import org.mwc.cmap.grideditor.command.DeleteItemOperation;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;


/**
 * Action that deletes currently selected {@link TimeStampedDataItem} from its
 * series.
 */
public class DeleteRowAction extends AbstractSingleItemAction {

	public DeleteRowAction() {
		super(false, true);
		setImageDescriptor(loadImageDescriptor(GridEditorPlugin.IMG_REMOVE));
		setText("Delete row");
	}

	@Override
	protected IUndoableOperation createUndoableOperation(IUndoContext undoContext, GriddableSeries series, TimeStampedDataItem subject) {
		OperationEnvironment environment = new OperationEnvironment(undoContext, series, subject);
		DeleteItemOperation delete = new DeleteItemOperation(environment);
		return delete;
	}
}
