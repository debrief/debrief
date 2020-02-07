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

package org.mwc.cmap.grideditor.table;

import org.eclipse.jface.viewers.ColumnViewer;

/**
 * Represents the cell of the editor (may be not exist at the time of
 * construction) on which in-place editor should be activated
 */
public interface EditableTarget {

	public int getColumnIndex();

	public ColumnViewer getColumnViewer();

	public Object getElementToEdit(Object actuallyEdited);
}
