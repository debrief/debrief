package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mwc.cmap.grideditor.command.EnvironmentState.ItemAtKnownPosition;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;


public class DeleteItemOperation extends AbstractGridEditorOperation {

	public DeleteItemOperation(OperationEnvironment environment) {
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
	protected EnvironmentState doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		GriddableSeries series = getOperationEnvironment().getSeries();
		TimeStampedDataItem toBeDeleted = getOperationEnvironment().getSubject();
		series.deleteItem(toBeDeleted);
		return new EnvironmentState.SeriesOfKnownSize(getOperationEnvironment());
	}

	@Override
	protected void doUndo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		GriddableSeries series = getOperationEnvironment().getSeries();
		TimeStampedDataItem wasDeleted = getOperationEnvironment().getSubject();
		int hadPosition = getStateBeforeFirstRun().getPosition();
		series.insertItemAt(wasDeleted, hadPosition);
	}

	@Override
	protected EnvironmentState.ItemAtKnownPosition getStateBeforeFirstRun() {
		return (ItemAtKnownPosition) super.getStateBeforeFirstRun();
	}

}
