package org.mwc.cmap.grideditor;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.mwc.cmap.gridharness.data.GriddableSeries;

public class GridEditorUndoSupport {

	private ObjectUndoContext myUndoContext;

	private final IOperationHistory myOperationHistory;

	public GridEditorUndoSupport(IOperationHistory operationHistory) {
		myOperationHistory = operationHistory;
		myUndoContext = createNullContext();
	}

	/**
	 * @return <code>true</code> if undo context has been changed
	 */
	public boolean setTableInput(GriddableSeries mainInput) {
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
