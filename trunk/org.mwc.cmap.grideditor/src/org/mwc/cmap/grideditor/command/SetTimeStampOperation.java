package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;

public class SetTimeStampOperation extends AbstractGridEditorOperation {

	private final boolean myFireRefresh;

	private final HiResDate myNewTimeStamp;

	public SetTimeStampOperation(OperationEnvironment environment, HiResDate newTimeStamp) {
		this(environment, newTimeStamp, true);
	}

	public SetTimeStampOperation(OperationEnvironment environment, HiResDate newTimeStamp, boolean fireRefresh) {
		super("Setting TimeStamp", environment);
		myNewTimeStamp = newTimeStamp;
		myFireRefresh = fireRefresh;
		if (environment.getSubject() == null) {
			throw new IllegalArgumentException("I need a subject item");
		}
	}

	@Override
	protected EnvironmentState computeBeforeExecutionState() {
		return new ElementHasTimeStampState(getOperationEnvironment().getSubject());
	}

	@Override
	protected EnvironmentState doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		TimeStampedDataItem subject = getOperationEnvironment().getSubject();
		subject.setDTG(myNewTimeStamp);
		EnvironmentState resultState = new ElementHasTimeStampState(subject);
		if (myFireRefresh) {
			getOperationEnvironment().getSeries().fireModified(subject);
		}
		return resultState;
	}

	@Override
	protected void doUndo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		TimeStampedDataItem subject = getOperationEnvironment().getSubject();
		HiResDate oldTimeStamp = getStateBeforeFirstRun().getTimeStamp();
		subject.setDTG(oldTimeStamp);
		if (myFireRefresh) {
			getOperationEnvironment().getSeries().fireModified(subject);
		}
	}

	@Override
	protected ElementHasTimeStampState getStateBeforeFirstRun() {
		return (ElementHasTimeStampState) super.getStateBeforeFirstRun();
	}

	protected static class ElementHasTimeStampState implements EnvironmentState {

		private final HiResDate myTimeStamp;

		private final TimeStampedDataItem myItem;

		public ElementHasTimeStampState(TimeStampedDataItem item) {
			myItem = item;
			myTimeStamp = item.getDTG();
		}

		@Override
		public boolean isCompatible(OperationEnvironment environment) {
			return myTimeStamp.equals(environment.getSubject().getDTG());
		}

		public HiResDate getTimeStamp() {
			return myTimeStamp;
		}

		public TimeStampedDataItem getItem() {
			return myItem;
		}

	}

}
