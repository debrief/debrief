/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public abstract class EditorHelper
{
	@SuppressWarnings(
	{ "rawtypes" })
	final protected Class _myTargetClass;

	@SuppressWarnings("rawtypes")
	public EditorHelper(Class targetClass)
	{
		_myTargetClass = targetClass;
	}

	@SuppressWarnings("rawtypes")
	public boolean editsThis(Class target)
	{
		return (target == _myTargetClass);
	}

	/**
	 * get a cell editor - suited for insertion into the properties window
	 * 
	 * @param parent
	 * @return
	 */
	abstract public CellEditor getCellEditorFor(Composite parent);

	/**
	 * create an editor suitable for insertion into a dialog
	 * 
	 * @param parent
	 *          the parent object to insert the control into
	 * @return the new control
	 */
	public Control getEditorControlFor(final Composite parent,
			final DebriefProperty property)
	{
		// just provide a text editor
		final Text res = new Text(parent, SWT.SINGLE | SWT.BORDER);
		res.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				// inform our parent property that we've changed
				property.setValue(res.getText());

				// also tell any listeners
				Listener[] listeners = res.getListeners(SWT.Selection);
				for (int i = 0; i < listeners.length; i++)
				{
					Listener listener = listeners[i];
					listener.handleEvent(new Event());
				}

			}
		});
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