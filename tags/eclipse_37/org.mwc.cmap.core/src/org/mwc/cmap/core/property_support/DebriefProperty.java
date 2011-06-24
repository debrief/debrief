/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Editable;
import MWC.GUI.Editable.CategorisedPropertyDescriptor;

public class DebriefProperty implements IPropertyDescriptor
{
	final PropertyDescriptor _thisProp;

	final Editable _subject;

	EditorHelper _myHelper = null;

	static Vector<EditorHelper> _myHelperList;

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

	@SuppressWarnings({ "rawtypes" })
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
			}
			catch (Exception e)
			{
				CorePlugin.logError(Status.ERROR, "whilst finding helper", e);
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
			Class rawClass = EditableWrapper.getPropertyClass(_thisProp);

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
						+ EditableWrapper.getPropertyClass(prop) + "("
						+ prop.getDisplayName() + ")";
				System.out.println(msg);
			}

		}
		return res;
	}

	public static void addSupplementalHelpers(Vector<EditorHelper> newHelpers)
	{
		// make sure our starter list is created
		initialiseHelpers();

		// now add the new ones
		for (Iterator<EditorHelper> iter = newHelpers.iterator(); iter.hasNext();)
		{
			EditorHelper thisHelper = (EditorHelper) iter.next();
			_myHelperList.add(thisHelper);
		}
	}

	private static void initialiseHelpers()
	{
		if (_myHelperList == null)
		{
			_myHelperList = new Vector<EditorHelper>(0, 1);
			_myHelperList.add(new ColorHelper(_theControl));
			_myHelperList.add(new BoundedIntegerHelper());
			_myHelperList
					.add(new BoundedIntegerHelper.SteppingBoundedIntegerHelper());
			_myHelperList.add(new EditorHelper(String.class)
			{
				public CellEditor getCellEditorFor(Composite parent)
				{
					return new TextCellEditor(parent, SWT.MULTI | SWT.V_SCROLL);
				}
			});
			_myHelperList.add(new EditorHelper(Long.class)
			{

				public CellEditor getCellEditorFor(Composite parent)
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
			_myHelperList.add(new EditorHelper(Integer.class)
			{

				public CellEditor getCellEditorFor(Composite parent)
				{
					return new TextCellEditor(parent);
				}

				public Object translateToSWT(Object value)
				{
					String res = " ";
					Integer val = (Integer) value;
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
					Integer res = null;
					res = new Integer(val);
					return res;
				}

			});
			_myHelperList.add(new BooleanHelper());
			_myHelperList.add(new FontHelper());
			_myHelperList.add(new DTGHelper());
			_myHelperList.add(new LatLongHelper());
			_myHelperList.add(new ArrayLengthHelper());
			_myHelperList.add(new DistanceWithUnitsHelper());
			_myHelperList.add(new WorldSpeedHelper());
			_myHelperList.add(new WorldAccelerationHelper());
			_myHelperList.add(new WorldDistanceHelper());
			_myHelperList.add(new WorldPathHelper());
			_myHelperList.add(new DurationHelper());
			_myHelperList.add(new DoubleHelper());
			_myHelperList.add(new FileNameHelper());

		}
	}

	public CellEditor createPropertyEditor(Composite parent)
	{
		CellEditor res = null;
		if (_myHelper != null)
		{
			res = _myHelper.getCellEditorFor(parent);
		}
		return res;
	}

	public Control createEditor(Composite parent)
	{
		Control res = null;
		if (_myHelper != null)
		{
			res = _myHelper.getEditorControlFor(parent, this);
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

	public EditorHelper getHelper()
	{
		return _myHelper;
	}

	public boolean isCompatibleWith(IPropertyDescriptor anotherProperty)
	{
		// the name properties aren't compatible.
		boolean res = true;
		if (this.getDisplayName().equals("Name"))
			res = false;
		if (this.getDisplayName().equals("DateTimeGroup"))
			res = false;
		if (this.getDisplayName().equals("FixLocation"))
			res = false;
		return res;
	}

	public Object getRawValue()
	{

		Object res = null;
		try
		{
			// find out the type of the editor
			Method m = _thisProp.getReadMethod();

			if (m == null)
			{
				System.out.println("tripped, prop was:" + _thisProp.getDisplayName());
			}
			else
			{
				res = m.invoke(_subject, (Object[]) null);
			}
		}
		catch (Exception e)
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
			write.invoke(_subject, new Object[]
			{ value });
		}
		catch (IllegalArgumentException e)
		{
			CorePlugin.logError(Status.ERROR,
					"Whilst setting property value for:" + value, e);
		}
		catch (IllegalAccessException e)
		{
			CorePlugin.logError(Status.ERROR,
					"Whilst setting property value for:" + value, e);
		}
		catch (InvocationTargetException e)
		{
			CorePlugin.logError(Status.ERROR,
					"Whilst setting property value for:" + value, e);
		}

	}

}