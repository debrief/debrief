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
	public EditorHelper(final Class targetClass)
	{
		_myTargetClass = targetClass;
	}

	@SuppressWarnings("rawtypes")
	public boolean editsThis(final Class target)
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
			final IDebriefProperty property)
	{
		// just provide a text editor
		final Text res = new Text(parent, SWT.SINGLE | SWT.BORDER);
		res.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				// inform our parent property that we've changed
				if (property != null)
					property.setValue(res.getText());

				// also tell any listeners
				final Listener[] listeners = res.getListeners(SWT.Selection);
				for (int i = 0; i < listeners.length; i++)
				{
					final Listener listener = listeners[i];
					listener.handleEvent(new Event());
				}

			}
		});
		return res;
	}

	public Object translateToSWT(final Object value)
	{
		return value;
	}

	public Object translateFromSWT(final Object value)
	{
		return value;
	}

	public ILabelProvider getLabelFor(final Object currentValue)
	{
		return null;
	}
}