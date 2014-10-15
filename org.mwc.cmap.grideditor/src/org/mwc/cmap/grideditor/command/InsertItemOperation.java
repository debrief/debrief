/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
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
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;

public class InsertItemOperation extends AbstractGridEditorOperation {

	private final int myInsertPosition;

	public InsertItemOperation(final OperationEnvironment environment) {
		this(environment, environment.getSeries().getItems().size());
	}

	public InsertItemOperation(final OperationEnvironment environment, final int insertPosition) {
		super("Adding item to series", environment);
		if (environment.getSubject() == null) {
			throw new IllegalArgumentException("I need a subject item to insert. Is the makeCopy method implemented?");
		}
		myInsertPosition = insertPosition;
	}

	@Override
	protected EnvironmentState computeBeforeExecutionState() {
		return new EnvironmentState.SeriesOfKnownSize(getOperationEnvironment());
	}

	@Override
	protected EnvironmentState doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final GriddableSeries series = getOperationEnvironment().getSeries();
		final TimeStampedDataItem toBeInserted = getOperationEnvironment().getSubject();
		series.insertItemAt(toBeInserted, myInsertPosition);
		return new EnvironmentState.ItemAtKnownPosition(getOperationEnvironment(), myInsertPosition);
	}

	@Override
	protected void doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final GriddableSeries series = getOperationEnvironment().getSeries();
		final TimeStampedDataItem wasInserted = getOperationEnvironment().getSubject();
		series.deleteItem(wasInserted);
	}

}
