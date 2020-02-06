
package org.mwc.cmap.core.property_support.ui;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.core.CorePlugin;

public class ValueWithUnitsCellEditor2 extends CellEditor
{

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
	public ValueWithUnitsCellEditor2(final Composite parent, final String textTip,
			final String comboTip, final ValueWithUnitsDataModel model)
	{
		super(parent);

		if (getMyControl() != null)
			getMyControl().init(textTip, comboTip, model);
		else
			CorePlugin.logError(Status.ERROR, "trying to create ValueWithUnitsCellEditor2 with corrupted control", null);
	}
	
	protected ValueWithUnitsControl getMyControl()
	{
		return (ValueWithUnitsControl) super.getControl();
	}

	@Override
	protected Control createControl(final Composite parent)
	{
		ValueWithUnitsControl control = getMyControl();
		if (control == null)
		{
			control = new ValueWithUnitsControl(parent);
		}
		return control;
	}

	@Override
	protected Object doGetValue()
	{
		return getMyControl().getData();
	}

	@Override
	protected void doSetFocus()
	{
		getMyControl().setFocus();
	}

	@Override
	protected void doSetValue(final Object value)
	{
		getMyControl().setData(value);
	}

}