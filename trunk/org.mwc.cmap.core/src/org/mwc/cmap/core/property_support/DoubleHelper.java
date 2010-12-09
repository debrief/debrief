/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DoubleHelper extends EditorHelper
{
	Object _previousValue;

	public DoubleHelper()
	{
		super(Double.class);
	}

	@SuppressWarnings({ "rawtypes" })
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

		// just do a quick check that it needs converting
		if (value instanceof Double)
		{
			res = value;
		}
		else
		{
			try
			{
				res = Double.valueOf((String) value);
			}
			catch (NumberFormatException e)
			{
				res = _previousValue;
			}
		}

		return res;
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		TextCellEditor res = new TextCellEditor(parent);
		
		return res;
	}

}