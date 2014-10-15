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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.mwc.cmap.grideditor.GridEditorActionContext;
import org.mwc.cmap.grideditor.data.GriddableWrapper;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.Editable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.TimeStampedDataItem;


public abstract class AbstractSingleItemAction extends AbstractViewerAction {

	private final boolean myAllowEmptySelection;
	private final boolean needsAddRemoveSupport;

	/**
	 * 
	 * @param allowEmptySelection whether we can run without a selection
	 * @param operationRequiresAddRemoveSupport whether our data source must support add/remove
	 */
	public AbstractSingleItemAction(final boolean allowEmptySelection, final boolean operationRequiresAddRemoveSupport) {
		myAllowEmptySelection = allowEmptySelection;
		needsAddRemoveSupport = operationRequiresAddRemoveSupport;
	}

	@Override
	public IUndoableOperation createUndoableOperation(final GridEditorActionContext actionContext) {
		final IUndoContext undoContext = actionContext.getUndoSupport().getUndoContext();
		final GriddableSeries mySeries = actionContext.getTableInput();
		if (mySeries == null) {
			return null;
		}
		final IStructuredSelection selection = actionContext.getStructuredSelection();
		if (selection.isEmpty() && !myAllowEmptySelection) {
			return null;
		}
		if (selection.size() > 1) {
			return null;
		}
		final Object firstSelected = selection.getFirstElement();
		if(firstSelected == null)
			return null;
		
		if (firstSelected != null && false == firstSelected instanceof TimeStampedDataItem) {
			return null;
		}
		
		// and the add/remove
		if(needsAddRemoveSupport)
		{

			final GriddableWrapper gw = (GriddableWrapper) actionContext.getInput();
			final Editable ed = gw.getWrapper().getEditable();
			if(ed instanceof GriddableSeriesMarker)
			{
				final GriddableSeriesMarker gs = (GriddableSeriesMarker) ed;
				if(!gs.supportsAddRemove())
					return null;
			}
			
		}
		
		return createUndoableOperation(undoContext, mySeries, (TimeStampedDataItem) firstSelected);
	}

	protected abstract IUndoableOperation createUndoableOperation(IUndoContext undoContext, GriddableSeries series, TimeStampedDataItem subject);
}
