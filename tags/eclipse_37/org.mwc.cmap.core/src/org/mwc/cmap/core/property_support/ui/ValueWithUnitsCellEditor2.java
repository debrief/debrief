/**
 * 
 */
package org.mwc.cmap.core.property_support.ui;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.core.CorePlugin;

public class ValueWithUnitsCellEditor2 extends CellEditor
{
	/**
	 * the control we're using
	 * 
	 */
	private ValueWithUnitsControl _myControl;

	/**
	 * constructor
	 * 
	 * @param parent
	 *          where to stick ourselves
	 * @param textTip
	 *          the tooltip on the text field
	 * @param comboText
	 *          the tooltip on the combo box
	 * @param dataModel
	 *          the data model we're manipulating
	 */
	public ValueWithUnitsCellEditor2(Composite parent, String textTip,
			String comboTip, ValueWithUnitsDataModel model)
	{
		super(parent);

		if (_myControl != null)
			_myControl.init(textTip, comboTip, model);
		else
			CorePlugin.logError(Status.ERROR, "trying to create ValueWithUnitsCellEditor2 with corrupted control", null);
	}

	@Override
	protected Control createControl(Composite parent)
	{
		if (_myControl == null)
			_myControl = new ValueWithUnitsControl(parent);
		return _myControl;
	}

	@Override
	protected Object doGetValue()
	{
		return _myControl.getData();
	}

	@Override
	protected void doSetFocus()
	{
		_myControl.setFocus();
	}

	@Override
	protected void doSetValue(Object value)
	{
		_myControl.setData(value);
	}

}