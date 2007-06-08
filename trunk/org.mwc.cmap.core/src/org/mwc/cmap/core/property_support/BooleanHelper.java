/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

public class BooleanHelper extends EditorHelper
{

	public BooleanHelper()
	{
		super(Boolean.class);
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		CellEditor res = new CheckboxCellEditor(parent);
		return res; 
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
				String res = null;
				Boolean val = (Boolean) element;
				String name = null;
				if(val.booleanValue())
				{
					name = "Yes";
				}
				else
				{
					name = "No";
				}
				res = name;
				return res;
//				return null;
			}

			public Image getImage(Object element)
			{
				return null;
//				
//				Image res = null;
//				Boolean val = (Boolean) element;
//				String name = null;
//				if(val.booleanValue())
//				{
//					name = "checked.gif";
//				}
//				else
//				{
//					name = "unchecked.gif";
//				}
//				res = CorePlugin.getImageFromRegistry(name);
//				return res;
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