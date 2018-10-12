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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.core.property_support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.mwc.cmap.core.CorePlugin;

import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.GMTDateFormat;

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

  protected synchronized static void checkDateFormat()
  {
    if (_dateFormat == null)
    {
      _dateFormat = new GMTDateFormat(DATE_FORMAT_DEFN);
      _longTimeFormat = new GMTDateFormat(LONG_TIME_FORMAT_DEFN);
      _shortTimeFormat = new GMTDateFormat(SHORT_TIME_FORMAT_DEFN);
      _fullFormat = new GMTDateFormat(DATE_FORMAT_DEFN + "Z"
          + LONG_TIME_FORMAT_DEFN);
    }
  }

  public static class DTGPropertySource implements IPropertySource2
  {

    protected String _date, _time;
    protected String _originalDate, _originalTime;

    protected HiResDate _originalVal;

    /**
     * name for the date property
     * 
     */
    public static String ID_DATE = "Date";

    /**
     * name for the time property
     * 
     */
    public static String ID_TIME = "Time";

    protected static IPropertyDescriptor[] descriptors;

    static
    {
      descriptors = new IPropertyDescriptor[]
      {new TextPropertyDescriptor(ID_DATE, "date (dd/mmm/yyyy)"),
          new TextPropertyDescriptor(ID_TIME, "time (hh:mm:ss)"),};
    }

    public DTGPropertySource(final HiResDate dtg)
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

      _originalDate = _date;
      _originalTime = _time;
    }

    @Override
    public boolean equals(Object obj)
    {
      final boolean res;

      if (obj instanceof DTGPropertySource)
      {
        DTGPropertySource o = (DTGPropertySource) obj;
        res = this.getValue().compareTo(o.getValue()) == 0;
      }
      else
      {
        res = false;
      }

      return res;
    }

    protected void firePropertyChanged(final String propName)
    {
    }

    public Object getEditableValue()
    {
      return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors()
    {
      return descriptors;
    }

    public Object getPropertyValue(final Object propName)
    {
      final String res;
      final boolean null_date = _originalVal.equals(HiResDate.NULL_DATE);
      if (ID_DATE.equals(propName))
      {
        res = null_date ? DATE_FORMAT_DEFN : _date;
      }
      else if (ID_TIME.equals(propName))
      {
        res = null_date ? LONG_TIME_FORMAT_DEFN : _time;
      }
      else
      {
        throw new IllegalArgumentException(
            "We're not expecting property titled:" + propName);
      }
      
      return res;
    }

    public synchronized HiResDate getValue()
    {
      HiResDate res = _originalVal;
      try
      {
        long millis = 0;

        // see if they have been set yet
        if (!_date.equals(UNSET) && _date.length() > 0)
        {
          final Date date = _dateFormat.parse(_date);
          millis += date.getTime();
        }

        if (!_time.equals(UNSET) && _time.length() > 0)
        {
          // first try with the long format
          Date time = null;

          try
          {
            time = _longTimeFormat.parse(_time);
          }
          catch (final ParseException e)
          {
            time = _shortTimeFormat.parse(_time);
          }

          if (time != null)
            millis += time.getTime();
        }

        if (millis != 0)
        {
          res = new HiResDate(millis, 0);
        }
      }
      catch (final ParseException e)
      {
        // fall back on the original value
        CorePlugin.logError(Status.ERROR,
            "Failed to produce dtg from date/time strings", e);
        res = _originalVal;
      }
      return res;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(Object)
     */
    public boolean isPropertySet(final Object propName)
    {
      // always return true; so we can offer "reset" behaviour
      // return true;
      boolean res = false;
      if (ID_DATE.equals(propName))
      {
        res = !_date.equals(_originalDate);
      }
      if (ID_TIME.equals(propName))
      {
        res = !_time.equals(_originalTime);
      }
      return res;
    }

    public void resetPropertyValue(final Object propName)
    {
      if (ID_DATE.equals(propName))
      {
        _date = _originalDate;
      }
      if (ID_TIME.equals(propName))
      {
        _time = _originalTime;
      }
    }

    public void setPropertyValue(final Object propName, final Object value)
    {
      if (ID_DATE.equals(propName))
      {
        _date = (String) value;
      }
      if (ID_TIME.equals(propName))
      {
        _time = (String) value;
      }
      firePropertyChanged((String) propName);
    }

    public String toString()
    {
      String res;
      if ((_date == UNSET) || (_time == UNSET))
      {
        res = "unset";
      }
      else
      {
        res = "" + _date + "Z" + _time;
      }
      return res;
    }

    public boolean isPropertyResettable(final Object id)
    {
      // both parameters are resettable. cool.
      return true;
    }

  }

  public DTGHelper()
  {
    super(HiResDate.class);
  }

  public CellEditor getCellEditorFor(final Composite parent)
  {
    return null;
  }

  @SuppressWarnings(
  {"rawtypes"})
  public boolean editsThis(final Class target)
  {
    return (target == HiResDate.class);
  }

  public Object translateToSWT(final Object value)
  {
    // ok, we've received a DTG. Return our new property source representing a
    // DTG
    return new DTGPropertySource((HiResDate) value);
  }

  public Object translateFromSWT(final Object value)
  {
    final DTGPropertySource res = (DTGPropertySource) value;
    return res.getValue();
  }

  public ILabelProvider getLabelFor(final Object currentValue)
  {
    final ILabelProvider label1 = new LabelProvider()
    {
      public String getText(final Object element)
      {
        final DTGPropertySource val = (DTGPropertySource) element;
        checkDateFormat();
        final String res;
        if (HiResDate.NULL_DATE.equals(val._originalVal))
        {
          res = "Unset";
        }
        else
        {
          res = val.toString();
        }
        return res;
      }

      public Image getImage(final Object element)
      {
        return null;
      }

    };
    return label1;
  }
}