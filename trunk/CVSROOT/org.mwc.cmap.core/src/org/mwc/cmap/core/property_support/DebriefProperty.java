/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import MWC.GUI.Editable;
import MWC.GUI.Editable.CategorisedPropertyDescriptor;

public class DebriefProperty implements IPropertyDescriptor
{
	final PropertyDescriptor _thisProp;

	final Editable _subject;

	EditorHelper _myHelper = null;

	static Vector _myHelperList;

	static Control _theControl;

	public DebriefProperty(PropertyDescriptor prop, Editable subject,
			Control theControl)
	{
		_thisProp = prop;
		_subject = subject;
		_theControl = theControl;

		initialiseHelpers();

		_myHelper = findHelperFor(prop, subject);
	}

	public static class TagListHelper extends EditorHelper
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
	};
	
	private EditorHelper findHelperFor(PropertyDescriptor prop, Editable subject)
	{
		EditorHelper res = null;

		// is there an explicit editor specified?
		Class specificEditor = prop.getPropertyEditorClass();

		// did we find one?
		if (specificEditor != null)
		{
			Object theEditor = null;
			try
			{
				theEditor = specificEditor.newInstance();
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			if (theEditor instanceof java.beans.PropertyEditor)
			{
				final java.beans.PropertyEditor propEditor = (java.beans.PropertyEditor) theEditor;
				// ok. wrap it.
				if (propEditor.getTags() != null)
				{
					// ok - do one of the combo-box editor types
					final String[] theTags = propEditor.getTags();
					res = new TagListHelper(theTags, propEditor);

				}
			}
		}

		if (res == null)
		{

			// ok, find the type of object we're working with
			Class rawClass = PlottableWrapper.getPropertyClass(_thisProp);

			for (Iterator iter = _myHelperList.iterator(); iter.hasNext();)
			{
				EditorHelper thisHelper = (EditorHelper) iter.next();
				if (thisHelper.editsThis(rawClass))
				{
					res = thisHelper;
					break;
				}
			}

			if (res == null)
			{
				// ok, log the error
				String msg = "editor not found for:"
						+ PlottableWrapper.getPropertyClass(prop) + "("
						+ prop.getDisplayName() + ")";
				System.out.println(msg);
			//	CorePlugin.logError(Status.INFO, msg, null);
			}

		}
		return res;
	}

	private void initialiseHelpers()
	{
		if (_myHelperList == null)
		{
			_myHelperList = new Vector(0, 1);
			_myHelperList.add(new ColorHelper(_theControl));
			_myHelperList.add(new EditorHelper(String.class)
			{

				public CellEditor getEditorFor(Composite parent)
				{
					return new TextCellEditor(parent);
				}

			});
			_myHelperList.add(new EditorHelper(Long.class)
			{

				public CellEditor getEditorFor(Composite parent)
				{
					return new TextCellEditor(parent);
				}

				public Object translateToSWT(Object value)
				{
					String res = " ";
					Long val = (Long) value;
					if (val != null)
					{
						int thisInt = val.intValue();
						res = "" + thisInt;
					}
					return res;
				}

				public Object translateFromSWT(Object value)
				{
					String val = (String) value;
					Long res = null;
					res = new Long(val);
					return res;
				}

			});
			_myHelperList.add(new BooleanHelper());
			_myHelperList.add(new FontHelper());

		}
	}

	public CellEditor createPropertyEditor(Composite parent)
	{
		CellEditor res = null;
		if (_myHelper != null)
		{
			res = _myHelper.getEditorFor(parent);
		}
		return res;
	}

	public String getCategory()
	{
		String res = null;
		if (_thisProp instanceof Editable.CategorisedPropertyDescriptor)
		{
			Editable.CategorisedPropertyDescriptor desc = (CategorisedPropertyDescriptor) _thisProp;
			res = desc.getCategory();
		}
		return res;
	}

	public String getDescription()
	{
		return _thisProp.getShortDescription();
	}

	public String getDisplayName()
	{
		return _thisProp.getDisplayName();
	}

	public String[] getFilterFlags()
	{
		return null;
	}

	public Object getHelpContextIds()
	{
		return null;
	}

	public Object getId()
	{
		return _thisProp.getDisplayName();
	}

	public ILabelProvider getLabelProvider()
	{
		ILabelProvider res = null;
		if (_myHelper != null)
		{
			res = _myHelper.getLabelFor(_thisProp);
		}
		return res;
	}

	public boolean isCompatibleWith(IPropertyDescriptor anotherProperty)
	{
		// the name properties aren't compatible.
		boolean res = true;
		if (this.getDisplayName().equals("Name"))
			res = false;
		return res;
	}

	private Object getRawValue()
	{

		Object res = null;
		try
		{
			// find out the type of the editor
			Method m = _thisProp.getReadMethod();

			res = m.invoke(_subject, null);
		} catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		return res;
	}

	public Object getValue()
	{
		Object res = null;

		// get the raw value for this object
		res = getRawValue();

		if (_myHelper != null)
		{
			res = _myHelper.translateToSWT(res);
		}

		return res;
	}

	public void setValue(Object value)
	{
		if (_myHelper != null)
		{
			value = _myHelper.translateFromSWT(value);
		}

		// find out the type of the editor
		Method write = _thisProp.getWriteMethod();
		try
		{
			write.invoke(_subject, new Object[] { value });
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

	}

}