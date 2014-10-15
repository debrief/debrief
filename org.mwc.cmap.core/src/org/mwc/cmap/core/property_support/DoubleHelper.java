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
package org.mwc.cmap.core.property_support;

import java.text.ParseException;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class DoubleHelper extends EditorHelper
{
	Object _previousValue;

	public DoubleHelper()
	{
		super(Double.class);
	}

	@SuppressWarnings({ "rawtypes" })
	public boolean editsThis(final Class target)
	{
		return ((target == Double.class) || (target == double.class));
	}

	public Object translateToSWT(final Object value)
	{
		_previousValue = value;
		return "" + value;
	}

	public Object translateFromSWT(final Object value)
	{
		Object res;

		// just do a quick check that it needs converting
		if (value instanceof Double)
		{
			res = value;
		}
		else
		{
			try
			{
				res = MWCXMLReader.readThisDouble((String) value);
			}
			catch (final ParseException e)
			{
				res = _previousValue;
			}
		}

		return res;
	}

	public CellEditor getCellEditorFor(final Composite parent)
	{
		final TextCellEditor res = new TextCellEditor(parent);
		
		return res;
	}

}