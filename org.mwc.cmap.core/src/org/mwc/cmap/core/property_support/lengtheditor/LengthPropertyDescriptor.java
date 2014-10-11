/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.property_support.lengtheditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


public class LengthPropertyDescriptor extends TextPropertyDescriptor {

	public LengthPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);
		setValidator(new ICellEditorValidator() {

			public String isValid(final Object value) {
				if (value instanceof String == false) {
					return Messages.LengthPropertyDescriptor_InvalidValueType;
				}
				String str = (String) value;
				try {
					// right, do we end in metres? if so, ditch it
					if(str.endsWith("m"))
						str = str.substring(0, str.length() - 2);
					
					final double thisLen =  MWCXMLReader.readThisDouble(str);
					new WorldDistance.ArrayLength(thisLen);
					return null;
				} catch (final Exception e) {
					return Messages.LengthPropertyDescriptor_NotValid;
				}
			}
		});
	}

	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		final CellEditor editor = new LengthPropertyCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
