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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.property_support.lengtheditor;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.mwc.cmap.core.property_support.lengtheditor.preferences.LengthsRegistry;


public class LengthPropertyCellEditor extends TextCellEditor {

	private CCombo comboBox;

	private Composite wrapperComposite;

	public LengthPropertyCellEditor(final Composite parent) {
		this(parent, SWT.BORDER);
	}

	public LengthPropertyCellEditor(final Composite parent, final int style) {
		super(parent, style);
	}

	@Override
	protected void doSetValue(final Object value) {
		super.doSetValue(value);
		populateComboBoxItems();
	}

	@Override
	protected Control createControl(final Composite parent) {
		wrapperComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(2).equalWidth(true).applyTo(wrapperComposite);
		wrapperComposite.setBackground(parent.getBackground());
		wrapperComposite.setFont(parent.getFont());

		final Control textControl = super.createControl(wrapperComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(textControl);
		fixTextControl();

		comboBox = new CCombo(wrapperComposite, getStyle());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(comboBox);
		comboBox.setFont(wrapperComposite.getFont());

		comboBox.addKeyListener(new KeyAdapter() {

			public void keyPressed(final KeyEvent e) {
				keyReleaseOccured(e);
			}
		});

		comboBox.addSelectionListener(new SelectionAdapter() {

			public void widgetDefaultSelected(final SelectionEvent event) {
			}

			public void widgetSelected(final SelectionEvent event) {
				final int selection = comboBox.getSelectionIndex();
				text.setText(LengthsRegistry.getRegistry().getLengths().get(selection).toString());
			}
		});

		final TraverseListener traverseListener = new TraverseListener() {

			public void keyTraversed(final TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		};
		comboBox.addTraverseListener(traverseListener);

		new MultiControlFocusHandler(text, comboBox) {

			@Override
			protected void focusReallyLost(final FocusEvent e) {
				LengthPropertyCellEditor.this.focusLost();
			}
		};

		return wrapperComposite;
	}

	private void fixTextControl() {
		Listener[] ls;
		ls = text.getListeners(SWT.FocusOut);
		if (ls.length > 0) {
			text.removeListener(SWT.FocusOut, ls[ls.length - 1]);
		}

		ls = text.getListeners(SWT.Traverse);
		if (ls.length > 0) {
			text.removeListener(SWT.Traverse, ls[ls.length - 1]);
		}

	}

	@Override
	public void activate() {
		super.activate();
		initComboValue();
	}

	@Override
	protected void valueChanged(final boolean oldValidState, final boolean newValidState) {
		super.valueChanged(oldValidState, newValidState);
		initComboValue();
	}

	private void initComboValue() {
		if (comboBox.getItemCount() == 0) {
			return;
		}
		try {
			final int i = findValue();
			if (i == -1) {
				setSelectMessageToCombo();
			} else if (comboBox.getSelectionIndex() != i) {
				comboBox.select(i);
			}
		} catch (final Exception e) {
			setSelectMessageToCombo();
		}
	}

	private void setSelectMessageToCombo() {
		comboBox.setText(Messages.LengthPropertyCellEditor_Select);
	}

	private void setNotLoadedMessageToCombo() {
		comboBox.setText(Messages.LengthPropertyCellEditor_NotLoaded);
	}

	private static final double delta = 0.000001;

	/**
	 * find length from text field in registry
	 * 
	 * @return position in list
	 */
	private int findValue() {
		String val = text.getText();
		if(val.contains("m"))
		{
			val = val.replace("m"," ");
		}
		final Double value = new Double(val);
		final List<Double> list = LengthsRegistry.getRegistry().getLengths();
		for (int i = 0; i < list.size(); i++) {
			final Double cur = list.get(i);
			if (Math.abs(cur - value) < delta) {
				return i;
			}
		}
		return -1;
	}

	private void populateComboBoxItems() {
		final LengthsRegistry registry = LengthsRegistry.getRegistry();
		final int itemsCount = registry.getItemsCount();
		comboBox.removeAll();
		if (itemsCount == 0) {
			setNotLoadedMessageToCombo();
			return;
		}
		for (int i = 0; i < itemsCount; i++) {
			final Double value = registry.getLengths().get(i);
			final String s = registry.getNames().get(i) + "(" + value + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			comboBox.add(s);
		}
	}
}
