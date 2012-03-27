package org.mwc.cmap.grideditor.table.actons;

import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.mwc.cmap.grideditor.GridEditorPlugin;
import org.mwc.cmap.grideditor.command.InsertItemOperation;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;


/**
 * Action that copies selected {@link TimeStampedDataItem} (or the last item if
 * nothing is selected) and inserts the copy just below the template item.
 */
public class InsertRowAction extends AbstractSingleItemAction {

	public InsertRowAction() {
		super(true, true);
		setImageDescriptor(loadImageDescriptor(GridEditorPlugin.IMG_ADD));
		setText("Insert row");
	}

	@Override
	protected IUndoableOperation createUndoableOperation(IUndoContext undoContext, GriddableSeries series, TimeStampedDataItem selected) {
		
		if (selected == null) {
			List<TimeStampedDataItem> allItems = series.getItems();
			selected = allItems.get(allItems.size() - 1);
		}

		int selectedIndex = series.getItems().indexOf(selected);
		if (selectedIndex < 0) {
			//wow
			selectedIndex = series.getItems().size() - 1;
		}
		TimeStampedDataItem copy = series.makeCopy(selected);
		OperationEnvironment environment = new OperationEnvironment(undoContext, series, copy);
		InsertItemOperation insert = new InsertItemOperation(environment, selectedIndex + 1);
		return insert;
	}

}
