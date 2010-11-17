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

	public CompositeOperation(String label, IUndoContext enclosingUndoContext) {
		super(label);
		addContext(enclosingUndoContext);
	}

	@Override
	public void add(IUndoableOperation operation) {
		checkNotExecuted();
		myOperations.add(operation);
	}

	@Override
	public void remove(IUndoableOperation operation) {
		checkNotExecuted();
		myOperations.remove(operation);
	}

	@Override
	public boolean canExecute() {
		for (IUndoableOperation next : myOperations) {
			if (!next.canExecute()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canRedo() {
		for (IUndoableOperation next : myOperations) {
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
			IUndoableOperation next = myOperations.get(i);
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
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return doExecute(monitor, info, true);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return doExecute(monitor, info, false);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		final List<IStatus> result = new java.util.ArrayList<IStatus>(size());

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		ExecutionException caughtException = null;
		monitor.beginTask(getLabel(), size());
		try {

			for (ListIterator<IUndoableOperation> iter = myOperations.listIterator(size()); iter.hasPrevious();) {
				IUndoableOperation prev = iter.previous();
				IStatus status = null;

				try {
					status = prev.undo(new SubProgressMonitor(monitor, 1), info);
				} catch (ExecutionException e) {
					caughtException = e;
				}

				if (status != null) {
					result.add(status);
				}

				boolean childFailed = (caughtException != null) || status.matches(IStatus.ERROR | IStatus.CANCEL);
				// monitor cancellation doesn't matter if this was the first child
				if (childFailed || (monitor.isCanceled() && iter.hasPrevious())) {
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
						IUndoableOperation next = iter.next();
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
						} catch (ExecutionException inner) {
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
			monitor.done();
		}
		return aggregateStatuses(result);
	}

	@Override
	public void dispose() {
		for (IUndoableOperation next : myOperations) {
			next.dispose();
		}
	}

	private void checkNotExecuted() {
		if (myDidExecuted) {
			throw new IllegalStateException("I am already executed");
		}
	}

	private IStatus doExecute(IProgressMonitor monitor, IAdaptable info, boolean firstRunNotRedo) throws ExecutionException {
		final List<IStatus> result = new java.util.ArrayList<IStatus>(size());

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		ExecutionException caughtException = null;
		monitor.beginTask(getLabel(), size());
		try {
			for (ListIterator<IUndoableOperation> iter = myOperations.listIterator(); iter.hasNext();) {
				IUndoableOperation next = iter.next();
				IStatus status = null;

				try {
					if (firstRunNotRedo) {
						status = next.execute(new SubProgressMonitor(monitor, 1), info);
					} else {
						status = next.redo(new SubProgressMonitor(monitor, 1), info);
					}
				} catch (ExecutionException e) {
					caughtException = e;
				}

				boolean statusMatchVal =  false;
				if (status != null) {
					result.add(status);
					statusMatchVal = status.matches(IStatus.ERROR | IStatus.CANCEL);
				}
				
				boolean childFailed = (caughtException != null) || statusMatchVal;
				// monitor cancellation doesn't matter if this was the last child
				if (childFailed || (monitor.isCanceled() && iter.hasNext())) {
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
						IUndoableOperation prev = iter.previous();
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
						} catch (ExecutionException inner) {
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
			monitor.done();
		}
		return aggregateStatuses(result);
	}

	private IStatus aggregateStatuses(List<? extends IStatus> statuses) {
		final IStatus result;

		if (statuses.isEmpty()) {
			result = Status.OK_STATUS;
		} else if (statuses.size() == 1) {
			result = statuses.get(0);
		} else {
			// find the most severe status, to use its plug-in, code, and message
			IStatus[] children = statuses.toArray(new IStatus[statuses.size()]);

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
