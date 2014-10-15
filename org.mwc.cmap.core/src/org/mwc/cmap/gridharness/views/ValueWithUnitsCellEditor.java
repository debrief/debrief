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
package org.mwc.cmap.gridharness.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

abstract public class ValueWithUnitsCellEditor extends CellEditor implements MultiControlCellEditor {

	/**
	 * hmm, the text bit.
	 * 
	 */
	Text _myText;

	/**
	 * and the drop-down units bit
	 * 
	 */
	Combo _myCombo;

	final private String _textTip;

	final private String _comboTip;

	public ValueWithUnitsCellEditor(final Composite parent, final String textTip, final String comboTip) {
		super(parent);
		_textTip = textTip;
		_comboTip = comboTip;
	}

	protected Control createControl(final Composite parent) {
		return createControl(parent, _textTip, _comboTip);
	}
	
	public Control getLastControl() {
		return _myCombo;
	}

	protected Control createControl(final Composite parent, final String tipOne, final String tipTwo) {
		final Composite holder = new Composite(parent, SWT.NONE);
		final RowLayout rows = new RowLayout();
		rows.marginLeft = rows.marginRight = 0;
		rows.marginTop = rows.marginBottom = 0;
		rows.fill = false;
		rows.spacing = 0;
		rows.pack = false;
		holder.setLayout(rows);

		_myText = new Text(holder, SWT.BORDER);
		_myText.setTextLimit(7);
		_myText.setToolTipText(tipOne);
		_myCombo = new Combo(holder, SWT.DROP_DOWN);
		_myCombo.setItems(getTagsList());
		_myCombo.setToolTipText(tipTwo);

		new MultiControlFocusHandler(_myText, _myCombo) {

			@Override
			protected void focusReallyLost(final FocusEvent e) {
				ValueWithUnitsCellEditor.this.focusLost();
			}
		};

		return holder;
	}

	/**
	 * 
	 */
	final private void doUpdate() {
		// get the best units
		final int units = getUnitsValue();
		final String txt = "" + getDoubleValue();
		_myCombo.select(units);
		_myText.setText(txt);
	}

	/**
	 * @return
	 */
	abstract protected int getUnitsValue();

	/**
	 * @return
	 */
	abstract protected double getDoubleValue();

	/**
	 * @return
	 */
	abstract protected String[] getTagsList();

	protected Object doGetValue() {
		final String distTxt = _myText.getText();
		final double dist = new Double(distTxt).doubleValue();
		final int units = _myCombo.getSelectionIndex();
		final Object res = createResultsObject(dist, units);
		return res;
	}

	/**
	 * @param dist
	 * 		the value typed in
	 * @param units
	 * 		the units for the value
	 * @return an object representing the new data value
	 */
	abstract protected Object createResultsObject(double dist, int units);

	protected void doSetFocus() {
		_myText.setFocus();
	}

	protected void doSetValue(final Object value) {
		storeMe(value);
		doUpdate();
	}

	abstract protected void storeMe(Object value);

}