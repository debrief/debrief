package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.operations.IUndoContext;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;


public class OperationEnvironment {

	private final GriddableSeries mySeries;

	private final TimeStampedDataItem mySubject;

	private final GriddableItemDescriptor myOptionalDescriptor;

	private final IUndoContext myUndoContext;

	public OperationEnvironment(IUndoContext undoContext, GriddableSeries series) {
		this(undoContext, series, null, null);
	}

	public OperationEnvironment(IUndoContext undoContext, GriddableSeries series, TimeStampedDataItem optionalSubject) {
		this(undoContext, series, optionalSubject, null);
	}

	public OperationEnvironment(IUndoContext undoContext, GriddableSeries series, TimeStampedDataItem optionalSubject, GriddableItemDescriptor optionalDescriptor) {
		myUndoContext = undoContext;
		mySeries = series;
		mySubject = optionalSubject;
		myOptionalDescriptor = optionalDescriptor;
	}

	public GriddableSeries getSeries() {
		return mySeries;
	}

	/**
	 * @return optional subject item for this context or <code>null</code> if
	 * 	not applicable
	 */
	public TimeStampedDataItem getSubject() {
		return mySubject;
	}

	/**
	 * @return optional descriptor for this context or <code>null</code> if not
	 * 	applicable
	 */
	public GriddableItemDescriptor getDescriptor() {
		return myOptionalDescriptor;
	}
	
	public IUndoContext getUndoContext() {
		return myUndoContext;
	}
}
