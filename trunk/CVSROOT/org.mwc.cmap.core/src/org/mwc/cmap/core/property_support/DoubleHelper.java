/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.mwc.cmap.core.CorePlugin;

public class DoubleHelper extends EditorHelper
{
	Object _previousValue;

	public DoubleHelper()
	{
		super(Boolean.class);
	}

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