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
package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;

public class SetTimeStampOperation extends AbstractGridEditorOperation {

	private final boolean myFireRefresh;

	private final HiResDate myNewTimeStamp;

	public SetTimeStampOperation(final OperationEnvironment environment, final HiResDate newTimeStamp) {
		this(environment, newTimeStamp, true);
	}

	public SetTimeStampOperation(final OperationEnvironment environment, final HiResDate newTimeStamp, final boolean fireRefresh) {
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
	protected EnvironmentState doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final TimeStampedDataItem subject = getOperationEnvironment().getSubject();
		subject.setDTG(myNewTimeStamp);
		final EnvironmentState resultState = new ElementHasTimeStampState(subject);
		if (myFireRefresh) {
			getOperationEnvironment().getSeries().fireModified(subject);
		}
		return resultState;
	}

	@Override
	protected void doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final TimeStampedDataItem subject = getOperationEnvironment().getSubject();
		final HiResDate oldTimeStamp = getStateBeforeFirstRun().getTimeStamp();
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

		public ElementHasTimeStampState(final TimeStampedDataItem item) {
			myItem = item;
			myTimeStamp = item.getDTG();
		}

		public boolean isCompatible(final OperationEnvironment environment) {
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
