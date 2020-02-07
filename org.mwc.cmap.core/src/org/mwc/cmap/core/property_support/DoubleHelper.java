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

package org.mwc.cmap.core.property_support;

import java.text.ParseException;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class DoubleHelper extends EditorHelper {
	Object _previousValue;

	public DoubleHelper() {
		super(Double.class);
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public boolean editsThis(final Class target) {
		return ((target == Double.class) || (target == double.class));
	}

	@Override
	public CellEditor getCellEditorFor(final Composite parent) {
		final TextCellEditor res = new TextCellEditor(parent);

		return res;
	}

	@Override
	public Object translateFromSWT(final Object value) {
		Object res;

		// just do a quick check that it needs converting
		if (value instanceof Double) {
			res = value;
		} else {
			try {
				res = MWCXMLReader.readThisDouble((String) value);
			} catch (final ParseException e) {
				res = _previousValue;
			}
		}

		return res;
	}

	@Override
	public Object translateToSWT(final Object value) {
		_previousValue = value;
		return "" + value;
	}

}