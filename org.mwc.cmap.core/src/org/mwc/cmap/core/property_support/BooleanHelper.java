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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BooleanHelper extends EditorHelper {

	public BooleanHelper() {
		super(Boolean.class);
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public boolean editsThis(final Class target) {
		return ((target == Boolean.class) || (target == boolean.class));
	}

	@Override
	public CellEditor getCellEditorFor(final Composite parent) {
		// final CellEditor res = new CheckboxCellEditor(parent);
		final ComboBoxCellEditor res = new ComboBoxCellEditor(parent, new String[] { "Yes", "No" }, SWT.READ_ONLY) {

			@Override
			public void activate() {

				super.activate();
				getControl().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (isActivated())
							((CCombo) getControl()).setListVisible(true);
					}

				});
			};

			@Override
			protected Object doGetValue() {
				final Integer selecton = (Integer) super.doGetValue();
				if (selecton == 0) {
					return Boolean.TRUE;
				}

				return Boolean.FALSE;

			};

			@Override
			protected void doSetValue(final Object o) {
				if (Boolean.TRUE.equals(o)) {
					super.doSetValue(0);
				} else {
					super.doSetValue(1);
				}
			}

		};
		return res;
	}

	@Override
	public Control getEditorControlFor(final Composite parent, final IDebriefProperty property) {
		final Button myCheckbox = new Button(parent, SWT.CHECK);
		myCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Boolean val = new Boolean(myCheckbox.getSelection());
				property.setValue(val);
			}
		});
		return myCheckbox;
	}

	@Override
	public ILabelProvider getLabelFor(final Object currentValue) {
		final ILabelProvider label1 = new LabelProvider() {
			@Override
			public Image getImage(final Object element) {
				return null;
				//
				// Image res = null;
				// Boolean val = (Boolean) element;
				// String name = null;
				// if(val.booleanValue())
				// {
				// name = "checked.gif";
				// }
				// else
				// {
				// name = "unchecked.gif";
				// }
				// res = CorePlugin.getImageFromRegistry(name);
				// return res;
			}

			@Override
			public String getText(final Object element) {
				String res = null;
				final Boolean val = (Boolean) element;
				String name = null;
				if (val.booleanValue()) {
					name = "Yes";
				} else {
					name = "No";
				}
				res = name;
				return res;
				// return null;
			}

		};
		return label1;
	}

	@Override
	public Object translateFromSWT(final Object value) {
		return value;
	}

	@Override
	public Object translateToSWT(final Object value) {
		return value;
	}
}