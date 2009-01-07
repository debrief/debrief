/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;

public class DoubleHelper extends EditorHelper
{
	Object _previousValue;

	public DoubleHelper()
	{
		super(Boolean.class);
	}

	@SuppressWarnings("unchecked")
	public boolean editsThis(Class target)
	{
		return ((target == Double.class) || (target == double.class));
	}

	public Object translateToSWT(Object value)
	{
		_previousValue = value;
		return "" + value;
	}

	public Object translateFromSWT(Object value)
	{
		Object res;

		try
		{
			res = Double.valueOf((String) value);
		}
		catch (NumberFormatException e)
		{
			res = _previousValue;
		}
		return res;
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		return new TextCellEditor(parent);
	}

}