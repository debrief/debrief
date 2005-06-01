/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;

public class TagListHelper extends EditorHelper
{
	String [] _theTags;
	final java.beans.PropertyEditor _propEditor;
	
	public TagListHelper(String[] theTags, final java.beans.PropertyEditor propEditor)
	{
		super(null);
		_theTags = theTags;
		_propEditor = propEditor;
	}
	
	public CellEditor getEditorFor(Composite parent)
	{
		return new ComboBoxCellEditor(parent, _theTags);
	}

	public Object translateFromSWT(Object value)
	{
		Object res = value;
		if (value instanceof String)
		{
			_propEditor.setAsText((String) value);
			res = _propEditor.getValue();
		}
		else
		{
			Integer index = (Integer) value;
			// ok, set the index of the text field first, then get the
			// object vlaue
			String selectedItem = _theTags[index.intValue()];
			res = translateFromSWT(selectedItem);
		}
		return res;
	}

	public Object translateToSWT(Object value)
	{
		Object res = value;
		if (value instanceof String)
		{
			// we have to translate the string to the string index
			for (int i = 0; i < _theTags.length; i++)
			{
				String thisItem = _theTags[i];
				if (thisItem.equals(value))
				{
					res = new Integer(i);
					break;
				}
			}
		}
		else
		{
			// get the string representation of the object, then get the
			// index of
			// that string
			_propEditor.setValue(value);
			String txtVersion = _propEditor.getAsText();
			res = translateToSWT(txtVersion);
		}
		return res;
	}

	public ILabelProvider getLabelFor(Object value)
	{
		LabelProvider theProvider = new LabelProvider()
		{
			public String getText(Object element)
			{
				String res = null;
				_propEditor.setValue(element);
				res = _propEditor.getAsText();
				return res;
			}
		};
		return theProvider;
	}
}