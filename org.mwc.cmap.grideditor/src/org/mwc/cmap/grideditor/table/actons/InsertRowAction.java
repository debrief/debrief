/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
	protected IUndoableOperation createUndoableOperation(final IUndoContext undoContext, 
			final GriddableSeries series, final TimeStampedDataItem selected) {
		TimeStampedDataItem theSelected = selected;
		
		if (theSelected == null) {
			final List<TimeStampedDataItem> allItems = series.getItems();
			theSelected = allItems.get(allItems.size() - 1);
		}

		int selectedIndex = series.getItems().indexOf(theSelected);
		if (selectedIndex < 0) {
			//wow
			selectedIndex = series.getItems().size() - 1;
		}
		final TimeStampedDataItem copy = series.makeCopy(theSelected);
		final OperationEnvironment environment = new OperationEnvironment(undoContext, series, copy);
		final InsertItemOperation insert = new InsertItemOperation(environment, selectedIndex + 1);
		return insert;
	}

}
