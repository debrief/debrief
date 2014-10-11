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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.grideditor.GridEditorActionContext;
import org.mwc.cmap.grideditor.GridEditorPlugin;
import org.mwc.cmap.grideditor.GridEditorUndoSupport;


public abstract class AbstractViewerAction extends Action {

	private GridEditorUndoSupport myUndoSupport;

	private IUndoableOperation myOperation;

	public abstract IUndoableOperation createUndoableOperation(GridEditorActionContext actionContext);

	public AbstractViewerAction() {
		setEnabled(false);
	}

	public final void refreshWithActionContext(final GridEditorActionContext actionContext) {
		myUndoSupport = actionContext.getUndoSupport();
		myOperation =  createUndoableOperation(actionContext);
		updateActionAppearance(myOperation);
	}

	@Override
	public final void run() {
		if (myOperation == null || !myOperation.canExecute()) {
			throw new IllegalStateException("I should be disabled");
		}
		try {
			myUndoSupport.getOperationHistory().execute(myOperation, null, null);
		} catch (final ExecutionException e) {
			handleExecutionException(e);
		}
	}
	
	protected void updateActionAppearance(final IUndoableOperation operation) {
		setEnabled(myOperation != null && myOperation.canExecute());
	}

	protected void handleExecutionException(final ExecutionException e) {
		throw new RuntimeException("Operation failed " + myOperation.getLabel(), e);
	}

	protected static final ImageDescriptor loadImageDescriptor(final String key) {
		return GridEditorPlugin.getInstance().getImageRegistry().getDescriptor(key);
	}

}
