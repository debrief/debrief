/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;

abstract class EditorHelper
{
	final protected Class _myTargetClass;

	public EditorHelper(Class targetClass)
	{
		_myTargetClass = targetClass;
	}

	public boolean editsThis(Class target)
	{
		return (target == _myTargetClass);
	}

	abstract public CellEditor getEditorFor(Composite parent);

	public Object translateToSWT(Object value)
	{
		return value;
	}

	public Object translateFromSWT(Object value)
	{
		return value;
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		return null;
	}
}