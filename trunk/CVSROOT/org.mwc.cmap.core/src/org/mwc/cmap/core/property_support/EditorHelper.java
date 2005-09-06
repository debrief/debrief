/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

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

	/** get a cell editor - suited for insertion into the properties window
	 * 
	 * @param parent
	 * @return
	 */
	abstract public CellEditor getCellEditorFor(Composite parent);
	
	/** create an editor suitable for insertion into a dialog
	 * 
	 * @param parent the parent object to insert the control into
	 * @return the new control
	 */
	public Control getEditorControlFor(Composite parent)
	{
		// just provide a text editor
		Text res = new Text(parent, SWT.SINGLE);
		return res;
	}

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