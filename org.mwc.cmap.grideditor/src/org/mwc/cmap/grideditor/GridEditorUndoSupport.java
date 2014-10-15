/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.mwc.cmap.gridharness.data.GriddableSeries;

public class GridEditorUndoSupport {

	private ObjectUndoContext myUndoContext;

	private final IOperationHistory myOperationHistory;

	public GridEditorUndoSupport(final IOperationHistory operationHistory) {
		myOperationHistory = operationHistory;
		myUndoContext = createNullContext();
	}

	/**
	 * @return <code>true</code> if undo context has been changed
	 */
	public boolean setTableInput(final GriddableSeries mainInput) {
		if (myUndoContext != null && myUndoContext.getObject() == mainInput) {
			return false;
		}

		myOperationHistory.dispose(myUndoContext, true, true, true);
		myUndoContext = mainInput == null ? createNullContext() : new ObjectUndoContext(mainInput);
		return true;
	}

	public IUndoContext getUndoContext() {
		return myUndoContext;
	}

	public IOperationHistory getOperationHistory() {
		return myOperationHistory;
	}

	private ObjectUndoContext createNullContext() {
		return new ObjectUndoContext(new Object());
	}

}
