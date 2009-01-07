/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.text.*;
import java.util.*;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.*;
import org.mwc.cmap.core.CorePlugin;

import MWC.GenericData.HiResDate;

public class DTGHelper extends EditorHelper
{

	protected static SimpleDateFormat _dateFormat;

	protected static SimpleDateFormat _longTimeFormat;

	protected static SimpleDateFormat _shortTimeFormat;

	protected static SimpleDateFormat _fullFormat;

	protected final static String DATE_FORMAT_DEFN = "dd/MMM/yyyy";

	protected final static String LONG_TIME_FORMAT_DEFN = "HH:mm:ss";

	protected final static String SHORT_TIME_FORMAT_DEFN = "HH:mm";

	protected final static String UNSET = "unset";

	protected static void checkDateFormat()
	{
		if (_dateFormat == null)
		{
			_dateFormat = new SimpleDateFormat(DATE_FORMAT_DEFN);
			_dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			_longTimeFormat = new SimpleDateFormat(LONG_TIME_FORMAT_DEFN);
			_longTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			_shortTimeFormat = new SimpleDateFormat(SHORT_TIME_FORMAT_DEFN);
			_shortTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			_fullFormat = new SimpleDateFormat(DATE_FORMAT_DEFN + "Z"
					+ LONG_TIME_FORMAT_DEFN);
			_fullFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
	}

	public static class DTGPropertySource implements IPropertySource2
	{

		protected String _date, _time;
		protected String _originalDate, _originalTime;

		protected HiResDate _originalVal;

		/** name for the date property
		 * 
		 */
		public static String ID_DATE = "Date";

		/** name for the time property
		 * 
		 */
		public static String ID_TIME = "Time";

		protected static IPropertyDescriptor[] descriptors;

		static
		{
			descriptors = new IPropertyDescriptor[] {
					new TextPropertyDescriptor(ID_DATE, "date (dd/mmm/yyyy)"),
					new TextPropertyDescriptor(ID_TIME, "time (hh:mm:ss)"), };
		}

		public DTGPropertySource(HiResDate dtg)
		{

			checkDateFormat();

			if (dtg == null)
			{
				_originalVal = null;
				_date = UNSET;
				_time = UNSET;
			}
			else
			{
				_originalVal = new HiResDate(dtg);
				_date = _dateFormat.format(dtg.getDate());
				_time = _longTimeFormat.format(dtg.getDate());
			}
			
			_originalDate = new String( _date);
			_originalTime = new String(_time);
		}

		protected void firePropertyChanged(String propName)
		{
			// Control ctl = (Control)element.getControl();
			//			
			// if (ctl == null) {
			// // the GUIView is probably hidden in this case
			// return;
			// }
			// ctl.setSize(_dtg);
		}

		public Object getEditableValue()
		{
			return this;
		}

		public IPropertyDescriptor[] getPropertyDescriptors()
		{
			return descriptors;
		}

		public Object getPropertyValue(Object propName)
		{
			String res = "";
			if (ID_DATE.equals(propName))
			{
				// ok, extract the year component
				res = new String(_date);
			}
			if (ID_TIME.equals(propName))
			{
				res = new String(_time);
			}
			return res;
		}

		public HiResDate getValue()
		{
			HiResDate res = _originalVal;
			try
			{
				long millis = 0;
				
				// see if they have been set yet
				if(!_date.equals(UNSET))
				{
					Date date = _dateFormat.parse(_date);
					millis += date.getTime();
				}
				
				if(!_time.equals(UNSET))
				{
					// first try with the long format
					Date time = null;
					
					try
					{
						time = _longTimeFormat.parse(_time);
					}
					catch (ParseException e)
					{
						time = _shortTimeFormat.parse(_time);
					}
					
					if(time != null)
						millis += time.getTime();
				}
				
				if(millis != 0)
				{
					res = new HiResDate(millis, 0);
				}
				
			} catch (ParseException e)
			{
				// fall back on the original value
				CorePlugin.logError(Status.ERROR, "Failed to produce dtg", e);
				res = _originalVal;
			}
			return res;
		}

		/**
		 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(Object)
		 */
		public boolean isPropertySet(Object propName)
		{
			boolean res = false;
			if (ID_DATE.equals(propName))
			{
				res =  !_date.equals(_originalDate);
			}
			if (ID_TIME.equals(propName))
			{
				res =  !_time.equals(_originalTime);
			}
			return res;
		}

		public void resetPropertyValue(Object propName)
		{
			if (ID_DATE.equals(propName))
			{
				_date = new String(_originalDate);
			}
			if (ID_TIME.equals(propName))
			{
				_time = new String(_originalTime);
			}
		}

		public void setPropertyValue(Object propName, Object value)
		{
			if (ID_DATE.equals(propName))
			{
				_date = new String((String) value);
			}
			if (ID_TIME.equals(propName))
			{
				_time = new String((String) value);
			}
			firePropertyChanged((String) propName);
		}

		public String toString()
		{
			String res;
			if((_date == UNSET) || (_time == UNSET))
			{
				res = "unset";
			}
			else
			{
				res = "" + _date + "Z" + _time;
			}
			return res;
		}

		public boolean isPropertyResettable(Object id)
		{
			// both parameters are resettable. cool.
			return true;
		}

	}

	public DTGHelper()
	{
		super(HiResDate.class);
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean editsThis(Class target)
	{
		return (target == HiResDate.class);
	}

	public Object translateToSWT(Object value)
	{
		// ok, we've received a DTG. Return our new property source representing a
		// DTG
		return new DTGPropertySource((HiResDate) value);
	}

	public Object translateFromSWT(Object value)
	{
		DTGPropertySource res = (DTGPropertySource) value;
		return res.getValue();
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				DTGPropertySource val = (DTGPropertySource) element;
				checkDateFormat();
				return val.toString();
			}

			public Image getImage(Object element)
			{
				return null;
			}

		};
		return label1;
	}
}