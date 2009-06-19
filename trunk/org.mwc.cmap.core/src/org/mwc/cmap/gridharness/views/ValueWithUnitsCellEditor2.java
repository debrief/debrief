package org.mwc.cmap.gridharness.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.gridharness.data.UnitsSet;
import org.mwc.cmap.gridharness.data.ValueInUnits;
import org.mwc.cmap.gridharness.data.UnitsSet.Unit;


public abstract class ValueWithUnitsCellEditor2 extends CellEditor implements MultiControlCellEditor {

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

	private ValueInUnits _value;

	private UnitsSet _unitsSet;

	private String[] _unitLabels;

	public ValueWithUnitsCellEditor2(Composite parent, String textTip, String comboTip) {
		super(parent);
		_textTip = textTip;
		_comboTip = comboTip;
	}
	
	protected abstract ValueInUnits initializeValue();

	protected Control createControl(Composite parent) {
		_value = initializeValue();
		_unitsSet = _value.getUnitsSet();
		_unitLabels = _unitsSet.getAllUnitLabels();
		return createControl(parent, _textTip, _comboTip);
	}

	protected Control createControl(Composite parent, String tipOne, String tipTwo) {
		Composite holder = new Composite(parent, SWT.NONE);
		RowLayout rows = new RowLayout();
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
		_myCombo.setItems(_unitLabels);
		_myCombo.setToolTipText(tipTwo);

		new MultiControlFocusHandler(_myText, _myCombo) {

			@Override
			protected void focusReallyLost(FocusEvent e) {
				ValueWithUnitsCellEditor2.this.focusLost();
			}
		};

		return holder;
	}

	@Override
	public Control getLastControl() {
		return _myCombo;
	}
	
	protected Object doGetValue() {
		String distTxt = _myText.getText();
		double dist = new Double(distTxt).doubleValue();
		int selectedIndex = _myCombo.getSelectionIndex();
		String selectedLabel = _myCombo.getItem(selectedIndex);
		UnitsSet.Unit units = _unitsSet.findUnit(selectedLabel);

		ValueInUnits result = _value.makeCopy();
		result.setValues(dist, units);
		return result;
	}

	protected void doSetFocus() {
		_myText.setFocus();
	}

	protected void doSetValue(Object value) {
		ValueInUnits valueImpl = (ValueInUnits) value;
		UnitsSet.Unit mainUnit = _unitsSet.getMainUnit();
		double doubleValue = valueImpl.getValueIn(mainUnit);
		_value.setValues(doubleValue, mainUnit);
		UnitsSet.Unit bestFitUnit = _unitsSet.selectUnitsFor(doubleValue);
		_myCombo.select(getUndexFor(bestFitUnit));
		_myText.setText(String.valueOf(_value.getValueIn(bestFitUnit)));
	}

	private int getUndexFor(Unit unit) {
		for (int i = 0; i < _unitLabels.length; i++) {
			if (unit.getLabel().equals(_unitLabels[i])) {
				return i;
			}
		}
		throw new IllegalStateException("Can't find unit: " + unit.getLabel() + " in units set: " + _unitsSet);
	}

}