package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mwc.cmap.grideditor.command.EnvironmentState.ItemAtKnownPosition;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;


public class DeleteItemOperation extends AbstractGridEditorOperation {

	public DeleteItemOperation(final OperationEnvironment environment) {
		super("Deleting item", environment);
		if (environment.getSubject() == null) {
			throw new IllegalArgumentException("I need a subject item to delete");
		}
	}

	@Override
	protected EnvironmentState computeBeforeExecutionState() {
		return new EnvironmentState.ItemAtKnownPosition(getOperationEnvironment());
	}

	@Override
	protected EnvironmentState doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final GriddableSeries series = getOperationEnvironment().getSeries();
		final TimeStampedDataItem toBeDeleted = getOperationEnvironment().getSubject();
		series.deleteItem(toBeDeleted);
		return new EnvironmentState.SeriesOfKnownSize(getOperationEnvironment());
	}

	@Override
	protected void doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final GriddableSeries series = getOperationEnvironment().getSeries();
		final TimeStampedDataItem wasDeleted = getOperationEnvironment().getSubject();
		final int hadPosition = getStateBeforeFirstRun().getPosition();
		series.insertItemAt(wasDeleted, hadPosition);
	}

	@Override
	protected EnvironmentState.ItemAtKnownPosition getStateBeforeFirstRun() {
		return (ItemAtKnownPosition) super.getStateBeforeFirstRun();
	}

}
