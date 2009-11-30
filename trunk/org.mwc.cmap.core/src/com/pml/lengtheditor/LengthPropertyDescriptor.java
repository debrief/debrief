package com.pml.lengtheditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import MWC.GenericData.WorldDistance;


public class LengthPropertyDescriptor extends TextPropertyDescriptor {

	public LengthPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		setValidator(new ICellEditorValidator() {

			public String isValid(Object value) {
				if (value instanceof String == false) {
					return Messages.LengthPropertyDescriptor_InvalidValueType;
				}
				String str = (String) value;
				try {
					// right, do we end in metres? if so, ditch it
					if(str.endsWith("m"))
						str = str.substring(0, str.length() - 2);
					
					double thisLen = Double.parseDouble(str);
					new WorldDistance.ArrayLength(thisLen);
					return null;
				} catch (Exception e) {
					return Messages.LengthPropertyDescriptor_NotValid;
				}
			}
		});
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new LengthPropertyCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
