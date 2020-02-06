/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.cmap.core.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Tools.Action;

public class DebriefChartModifiedActionWrapper extends AbstractOperation {

	final private Action _myAction;

	/**
	 * constructor, so that we can wrap our action
	 *
	 * @param theAction
	 */
	public DebriefChartModifiedActionWrapper(final Action theAction) {
		super(theAction.toString());

		if (CorePlugin.getUndoContext() != null) {
			super.addContext(CorePlugin.getUndoContext());
		}

		_myAction = theAction;
	}

	//////////////////////////////////////////////////////////////
	// eclipse action bits
	//////////////////////////////////////////////////////////////

	/**
	 * @return
	 */
	@Override
	public boolean canExecute() {
		return true;
	}

	/**
	 * @return
	 */
	@Override
	public boolean canRedo() {
		return _myAction.isRedoable();
	}

	/**
	 * @return
	 */
	@Override
	public boolean canUndo() {
		return _myAction.isUndoable();
	}

	@Override
	public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		_myAction.execute();

		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		if (_myAction.isRedoable())
			_myAction.execute();

		return Status.OK_STATUS;
	}

	/**
	 * @return
	 */
	@Override
	public String toString() {
		return _myAction.toString();
	}

	@Override
	public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		if (_myAction.isUndoable())
			_myAction.undo();

		return Status.OK_STATUS;
	}

}
