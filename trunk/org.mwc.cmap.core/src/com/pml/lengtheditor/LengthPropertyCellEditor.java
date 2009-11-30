package com.pml.lengtheditor;

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

import com.pml.lengtheditor.preferences.LengthsRegistry;

public class LengthPropertyCellEditor extends TextCellEditor {

	private CCombo comboBox;

	private Composite wrapperComposite;

	public LengthPropertyCellEditor(Composite parent) {
		this(parent, SWT.BORDER);
	}

	public LengthPropertyCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void doSetValue(Object value) {
		super.doSetValue(value);
		populateComboBoxItems();
	}

	@Override
	protected Control createControl(Composite parent) {
		wrapperComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(2).equalWidth(true).applyTo(wrapperComposite);
		wrapperComposite.setBackground(parent.getBackground());
		wrapperComposite.setFont(parent.getFont());

		Control textControl = super.createControl(wrapperComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(textControl);
		fixTextControl();

		comboBox = new CCombo(wrapperComposite, getStyle());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(comboBox);
		comboBox.setFont(wrapperComposite.getFont());

		comboBox.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});

		comboBox.addSelectionListener(new SelectionAdapter() {

			public void widgetDefaultSelected(SelectionEvent event) {
			}

			public void widgetSelected(SelectionEvent event) {
				int selection = comboBox.getSelectionIndex();
				text.setText(LengthsRegistry.getRegistry().getLengths().get(selection).toString());
			}
		});

		TraverseListener traverseListener = new TraverseListener() {

			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		};
		comboBox.addTraverseListener(traverseListener);

		new MultiControlFocusHandler(text, comboBox) {

			@Override
			protected void focusReallyLost(FocusEvent e) {
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
	protected void valueChanged(boolean oldValidState, boolean newValidState) {
		super.valueChanged(oldValidState, newValidState);
		initComboValue();
	}

	private void initComboValue() {
		if (comboBox.getItemCount() == 0) {
			return;
		}
		try {
			int i = findValue();
			if (i == -1) {
				setSelectMessageToCombo();
			} else if (comboBox.getSelectionIndex() != i) {
				comboBox.select(i);
			}
		} catch (Exception e) {
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
		Double value = new Double(text.getText());
		List<Double> list = LengthsRegistry.getRegistry().getLengths();
		for (int i = 0; i < list.size(); i++) {
			Double cur = list.get(i);
			if (Math.abs(cur - value) < delta) {
				return i;
			}
		}
		return -1;
	}

	private void populateComboBoxItems() {
		LengthsRegistry registry = LengthsRegistry.getRegistry();
		int itemsCount = registry.getItemsCount();
		comboBox.removeAll();
		if (itemsCount == 0) {
			setNotLoadedMessageToCombo();
			return;
		}
		for (int i = 0; i < itemsCount; i++) {
			Double value = registry.getLengths().get(i);
			String s = registry.getNames().get(i) + "(" + value + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			comboBox.add(s);
		}
	}
}
