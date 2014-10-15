/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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

	public CellEditor getCellEditorFor(final Composite parent)
	{
		final CellEditor res = new CheckboxCellEditor(parent);
		return res; 
	}

	@SuppressWarnings({ "rawtypes" })
	public boolean editsThis(final Class target)
	{
		return ((target == Boolean.class) || (target == boolean.class));
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
		final ILabelProvider label1 = new LabelProvider()
		{
			public String getText(final Object element)
			{
				String res = null;
				final Boolean val = (Boolean) element;
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

			public Image getImage(final Object element)
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
	

	@Override
	public Control getEditorControlFor(final Composite parent, final IDebriefProperty property)
	{
		final Button myCheckbox = new Button(parent, SWT.CHECK);
		myCheckbox.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(final SelectionEvent e)
			{
				final Boolean val = new Boolean(myCheckbox.getSelection());
				property.setValue(val);
			}});
		return myCheckbox;
	}	
}