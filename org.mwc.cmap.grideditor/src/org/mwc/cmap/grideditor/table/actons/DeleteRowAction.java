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
	protected IUndoableOperation createUndoableOperation(final IUndoContext undoContext, final GriddableSeries series, final TimeStampedDataItem subject) {
		final OperationEnvironment environment = new OperationEnvironment(undoContext, series, subject);
		final DeleteItemOperation delete = new DeleteItemOperation(environment);
		return delete;
	}
}
