package org.mwc.cmap.core.property_support.lengtheditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import MWC.GenericData.WorldDistance;


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
					
					final double thisLen = Double.parseDouble(str);
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
