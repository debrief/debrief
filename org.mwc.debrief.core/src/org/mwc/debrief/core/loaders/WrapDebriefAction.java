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
package org.mwc.debrief.core.loaders;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import MWC.GUI.Tools.Action;

/**
 * wrapper class, so we can put legacy Debrief actions onto the Eclipse undo
 * buffer
 *
 * @author ian
 *
 */
class WrapDebriefAction implements IUndoableOperation {
	private final Action _action;

	public WrapDebriefAction(final Action action) {
		_action = action;
	}

	@Override
	public void addContext(final IUndoContext context) {
		// skip;
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public boolean canRedo() {
		return _action.isRedoable();
	}

	@Override
	public boolean canUndo() {
		return _action.isUndoable();
	}

	@Override
	public void dispose() {
		// skip.
	}

	@Override
	public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		_action.execute();
		return Status.OK_STATUS;
	}

	@Override
	public IUndoContext[] getContexts() {
		return new UndoContext[] {};
	}

	@Override
	public String getLabel() {
		return _action.toString();
	}

	@Override
	public boolean hasContext(final IUndoContext context) {

		return false;
	}

	@Override
	public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		_action.execute();
		return Status.OK_STATUS;
	}

	@Override
	public void removeContext(final IUndoContext context) {
		// skip.
	}

	@Override
	public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		_action.undo();
		return Status.OK_STATUS;
	}

}