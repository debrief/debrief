package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public abstract class AbstractGridEditorOperation extends AbstractOperation {

	private final OperationEnvironment myEnvironment;

	private EnvironmentState myStateBeforeFirstRun;

	private EnvironmentState myStateAfterFirstRun;

	protected abstract EnvironmentState doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

	protected abstract void doUndo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;

	protected abstract EnvironmentState computeBeforeExecutionState() throws ExecutionException;

	public AbstractGridEditorOperation(String label, OperationEnvironment environment) {
		super(label);
		addContext(environment.getUndoContext());
		myEnvironment = environment;
	}

	@Override
	public final IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (myStateBeforeFirstRun == null) {
			myStateBeforeFirstRun = computeBeforeExecutionState();
		}
		EnvironmentState afterExecute = doExecute(monitor, info);
		if (myStateAfterFirstRun == null) {
			myStateAfterFirstRun = afterExecute;
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (myStateBeforeFirstRun == null) {
			throw new IllegalStateException("I haven't been executed yet or already failed");
		}
		doUndo(monitor, info);
		if (!myStateBeforeFirstRun.isCompatible(getOperationEnvironment())) {
			//can't roll-back state, subsequent undo's/redo's don't make sense 
			myStateAfterFirstRun = null;
			myStateBeforeFirstRun = null;
			throw new ExecutionException("Attempt to undo have been made, but state haven't been restored");
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (myStateAfterFirstRun == null || myStateAfterFirstRun.isCompatible(getOperationEnvironment())) {
			throw new IllegalStateException("I told you I can't redo");
		}
		execute(monitor, info);
		return Status.OK_STATUS;
	}

	@Override
	public final boolean canRedo() {
		if (myStateBeforeFirstRun == null) {
			return false;
		}
		return myStateBeforeFirstRun.isCompatible(getOperationEnvironment());
	}

	@Override
	public final boolean canUndo() {
		if (myStateAfterFirstRun == null) {
			return false;
		}
		return myStateAfterFirstRun.isCompatible(getOperationEnvironment());
	}

	protected OperationEnvironment getOperationEnvironment() {
		return myEnvironment;
	}

	protected EnvironmentState getStateAfterFirstRun() {
		return myStateAfterFirstRun;
	}

	protected EnvironmentState getStateBeforeFirstRun() {
		return myStateBeforeFirstRun;
	}

}
