/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package org.mwc.cmap.NarrativeViewer;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;

public class TableViewerColumnFactory {
	private final GridTableViewer _viewer;

	public TableViewerColumnFactory(final GridTableViewer viewer) {
		super();
		this._viewer = viewer;
	}

	public GridViewerColumn createColumn(final String header, final int width, final CellLabelProvider provider,
			final boolean wrap) {
		return createColumn(header, width, provider, SWT.LEFT, wrap);
	}

	public GridViewerColumn createColumn(final String header, final int width, final CellLabelProvider provider,
			final int alignment, final boolean wrap) {
		final GridViewerColumn viewerColumn = new GridViewerColumn(_viewer, SWT.NONE);
		final GridColumn column = viewerColumn.getColumn();
		column.setText(header == null ? "" : header);

		if (width > 0) {
			column.setWidth(width);
		}
		column.setResizeable(true);
		column.setMoveable(true);
		column.setAlignment(alignment);
		column.setWordWrap(wrap);
		viewerColumn.setLabelProvider(provider);

		return viewerColumn;
	}
}
