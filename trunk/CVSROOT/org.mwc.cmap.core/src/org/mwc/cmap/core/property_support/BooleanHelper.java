/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.mwc.cmap.core.CorePlugin;

public class BooleanHelper extends EditorHelper
{

	public BooleanHelper()
	{
		super(Boolean.class);
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		return new CheckboxCellEditor(parent);
	}

	public boolean editsThis(Class target)
	{
		return ((target == Boolean.class) || (target == boolean.class));
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
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				return null;
			}

			public Image getImage(Object element)
			{
				Image res = null;
				Boolean val = (Boolean) element;
				String name = null;
				if(val.booleanValue())
				{
					name = "checked.gif";
				}
				else
				{
					name = "unchecked.gif";
				}
				res = CorePlugin.getImageFromRegistry(name);
				return res;
			}

		};
		return label1;
	}
	

	public Control getEditorControlFor(Composite parent, final DebriefProperty property)
	{
		final Button myCheckbox = new Button(parent, SWT.CHECK);
		myCheckbox.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e)
			{
				Boolean val = new Boolean(myCheckbox.getSelection());
				property.setValue(val);
			}});
		return myCheckbox;
	}	
}