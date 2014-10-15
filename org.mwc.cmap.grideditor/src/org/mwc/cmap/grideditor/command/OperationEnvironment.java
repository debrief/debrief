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

//	public OperationEnvironment(IUndoContext undoContext, GriddableSeries series) {
//		this(undoContext, series, null, null);
//	}

	public OperationEnvironment(final IUndoContext undoContext, final GriddableSeries series, final TimeStampedDataItem optionalSubject) {
		this(undoContext, series, optionalSubject, null);
	}

	public OperationEnvironment(final IUndoContext undoContext, final GriddableSeries series, final TimeStampedDataItem optionalSubject, final GriddableItemDescriptor optionalDescriptor) {
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
