/**
 * 
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

	public ValueWithUnitsCellEditor(Composite parent, String textTip, String comboTip) {
		super(parent);
		_textTip = textTip;
		_comboTip = comboTip;
	}

	protected Control createControl(Composite parent) {
		return createControl(parent, _textTip, _comboTip);
	}
	
	@Override
	public Control getLastControl() {
		return _myCombo;
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
		_myCombo.setItems(getTagsList());
		_myCombo.setToolTipText(tipTwo);

		new MultiControlFocusHandler(_myText, _myCombo) {

			@Override
			protected void focusReallyLost(FocusEvent e) {
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
		String distTxt = _myText.getText();
		double dist = new Double(distTxt).doubleValue();
		int units = _myCombo.getSelectionIndex();
		Object res = createResultsObject(dist, units);
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

	protected void doSetValue(Object value) {
		storeMe(value);
		doUpdate();
	}

	abstract protected void storeMe(Object value);

}