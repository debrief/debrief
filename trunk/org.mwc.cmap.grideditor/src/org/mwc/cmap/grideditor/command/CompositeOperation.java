package org.mwc.cmap.grideditor.command;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.mwc.cmap.grideditor.GridEditorPlugin;


public class CompositeOperation extends AbstractOperation implements ICompositeOperation {

	private final ArrayList<IUndoableOperation> myOperations = new ArrayList<IUndoableOperation>(5);

	private boolean myDidExecuted;

	public CompositeOperation(final String label, final IUndoContext enclosingUndoContext) {
		super(label);
		addContext(enclosingUndoContext);
	}

	public void add(final IUndoableOperation operation) {
		checkNotExecuted();
		myOperations.add(operation);
	}

	public void remove(final IUndoableOperation operation) {
		checkNotExecuted();
		myOperations.remove(operation);
	}

	@Override
	public boolean canExecute() {
		for (final IUndoableOperation next : myOperations) {
			if (!next.canExecute()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canRedo() {
		for (final IUndoableOperation next : myOperations) {
			if (!next.canRedo()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canUndo() {
		//reversed order, as for undo()
		for (int i = myOperations.size() - 1; i >= 0; i--) {
			final IUndoableOperation next = myOperations.get(i);
			if (!next.canUndo()) {
				return false;
			}
		}
		return true;
	}

	public int size() {
		return myOperations.size();
	}

	@Override
	public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		return doExecute(monitor, info, true);
	}

	@Override
	public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		return doExecute(monitor, info, false);
	}

	@Override
	public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final List<IStatus> result = new java.util.ArrayList<IStatus>(size());
		IProgressMonitor theMonitor = monitor;
		
		if (theMonitor == null) {
			theMonitor = new NullProgressMonitor();
		}

		ExecutionException caughtException = null;
		theMonitor.beginTask(getLabel(), size());
		try {

			for (final ListIterator<IUndoableOperation> iter = myOperations.listIterator(size()); iter.hasPrevious();) {
				final IUndoableOperation prev = iter.previous();
				IStatus status = null;

				try {
					status = prev.undo(new SubProgressMonitor(theMonitor, 1), info);
				} catch (final ExecutionException e) {
					caughtException = e;
				}

				if (status != null) {
					result.add(status);
				}

				final boolean childFailed = (caughtException != null) || status.matches(IStatus.ERROR | IStatus.CANCEL);
				// monitor cancellation doesn't matter if this was the first child
				if (childFailed || (theMonitor.isCanceled() && iter.hasPrevious())) {
					if (childFailed) {
						// back-track over the operation that failed, assuming that it
						//    already rolled itself back by whatever means available
						iter.next();
					} else {
						// monitor was canceled
						result.add(new Status(IStatus.CANCEL, GridEditorPlugin.getInstance().getPluginId(), 0, "Undo interrupted", null));
					}

					while (iter.hasNext()) {
						// unwind the child operations
						final IUndoableOperation next = iter.next();
						if (!next.canRedo()) {
							// oops!  Can't continue unwinding.  Oh, well
							GridEditorPlugin.getInstance().getLog().log(new Status( //
									IStatus.ERROR, //
									GridEditorPlugin.getInstance().getPluginId(), //
									0, "Undo recovery failed: can't redo", null));
							break;
						}

						try {
							next.redo(new NullProgressMonitor(), info);
						} catch (final ExecutionException inner) {
							GridEditorPlugin.getInstance().getLog().log(new Status( //
									IStatus.ERROR, //
									GridEditorPlugin.getInstance().getPluginId(), //
									0, "Undo recovery failed: can't redo", //
									inner));
							break;
						}
					}

					if (caughtException != null) {
						throw caughtException;
					}

					break; // don't go through the list again
				}
			}
		} finally {
			theMonitor.done();
		}
		return aggregateStatuses(result);
	}

	@Override
	public void dispose() {
		for (final IUndoableOperation next : myOperations) {
			next.dispose();
		}
	}

	private void checkNotExecuted() {
		if (myDidExecuted) {
			throw new IllegalStateException("I am already executed");
		}
	}

	private IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info, final boolean firstRunNotRedo) throws ExecutionException {
		final List<IStatus> result = new java.util.ArrayList<IStatus>(size());
		IProgressMonitor theMonitor = monitor;
		
		if (theMonitor == null) {
			theMonitor = new NullProgressMonitor();
		}

		ExecutionException caughtException = null;
		theMonitor.beginTask(getLabel(), size());
		try {
			for (final ListIterator<IUndoableOperation> iter = myOperations.listIterator(); iter.hasNext();) {
				final IUndoableOperation next = iter.next();
				IStatus status = null;

				try {
					if (firstRunNotRedo) {
						status = next.execute(new SubProgressMonitor(theMonitor, 1), info);
					} else {
						status = next.redo(new SubProgressMonitor(theMonitor, 1), info);
					}
				} catch (final ExecutionException e) {
					caughtException = e;
				}

				boolean statusMatchVal =  false;
				if (status != null) {
					result.add(status);
					statusMatchVal = status.matches(IStatus.ERROR | IStatus.CANCEL);
				}
				
				final boolean childFailed = (caughtException != null) || statusMatchVal;
				// monitor cancellation doesn't matter if this was the last child
				if (childFailed || (theMonitor.isCanceled() && iter.hasNext())) {
					if (childFailed) {
						// back-track over the operation that failed, assuming that it
						//    already rolled itself back by whatever means available
						iter.previous();
					} else {
						// monitor was canceled
						result.add(new Status(IStatus.CANCEL, GridEditorPlugin.getInstance().getPluginId(), 0, "Undo interrupted", null));
					}

					while (iter.hasPrevious()) {
						// unwind the child operations
						final IUndoableOperation prev = iter.previous();
						if (!next.canUndo()) {
							// oops!  Can't continue unwinding.  Oh, well
							GridEditorPlugin.getInstance().getLog().log(new Status( //
									IStatus.ERROR, //
									GridEditorPlugin.getInstance().getPluginId(), //
									0, //
									(firstRunNotRedo ? "Execution" : "Redo") + " recovery failed: can't undo", // 
									null));
							break;
						}

						try {
							prev.redo(new NullProgressMonitor(), info);
						} catch (final ExecutionException inner) {
							GridEditorPlugin.getInstance().getLog().log(new Status( //
									IStatus.ERROR, //
									GridEditorPlugin.getInstance().getPluginId(), //
									0, //
									(firstRunNotRedo ? "Execution" : "Redo") + " recovery failed: can't undo", //
									inner));
							break;
						}
					}

					if (caughtException != null) {
						throw caughtException;
					}

					break; // don't go through the list again
				}
			}
		} finally {
			theMonitor.done();
		}
		return aggregateStatuses(result);
	}

	private IStatus aggregateStatuses(final List<? extends IStatus> statuses) {
		final IStatus result;

		if (statuses.isEmpty()) {
			result = Status.OK_STATUS;
		} else if (statuses.size() == 1) {
			result = statuses.get(0);
		} else {
			// find the most severe status, to use its plug-in, code, and message
			final IStatus[] children = statuses.toArray(new IStatus[statuses.size()]);

			IStatus worst = children[0];
			for (int i = 1; i < children.length; i++) {
				if (children[i].getSeverity() > worst.getSeverity()) {
					worst = children[i];
				}
			}

			result = new MultiStatus(worst.getPlugin(), worst.getCode(), children, worst.getMessage(), null);
		}
		return result;
	}

}
