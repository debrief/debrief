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
package MWC.GUI.JFreeChart;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.TickUnits;

import MWC.GUI.Properties.AbstractPropertyEditor;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class DateAxisEditor extends AbstractPropertyEditor
{

  public static class DatedRNFormatter extends GMTDateFormat
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * cache the last date formatted. when this moves backwards, we know we're restarting - which
     * helps us to detect the first item.
     */
    private long _lastDate = Long.MIN_VALUE;

    /**
     * pattern for the on-the-day values. Note: we currently ignore this, since we just prepend the
     * date value as a 2-digit value
     */
    @SuppressWarnings("unused")
    private final String _datePattern;

    /**
     * Construct a SimpleDateFormat using the given pattern in the default locale. <b>Note:</b> Not
     * all locales support SimpleDateFormat; for full generality, use the factory methods in the
     * DateFormat class.
     */
    public DatedRNFormatter(final String pattern, final String datePattern)
    {
      super(pattern);

      // store the date (even if we then choose to ignore it)
      _datePattern = datePattern;
    }

    @SuppressWarnings("deprecation")
    @Override
    public StringBuffer format(final Date arg0, final StringBuffer arg1,
        final FieldPosition arg2)
    {
      StringBuffer timeBit = super.format(arg0, arg1, arg2);

      // see of we've moved back in time
      long thisT = arg0.getTime();

      // when in this special mode we show the date for the first item
      final boolean firstOne = thisT < _lastDate;

      // see if we're on the exact day (or if this is the first one)
      if (((arg0.getHours() == 0) && (arg0.getMinutes() == 0) && (arg0
          .getSeconds() == 0)) || firstOne)
      {
        // ok, use the suffix
        final DecimalFormat df = new DecimalFormat("00");
        final String prefix = df.format(arg0.getDate());
        final StringBuffer res = new StringBuffer();
        res.append(prefix);
        res.append(timeBit);
        timeBit = res;
      }

      // remember the date
      _lastDate = thisT;

      return timeBit;
    }

  }

  /*****************************************************************************
   * class to store components of tick unit in accessible form
   ****************************************************************************/
  public static class MWCDateTickUnitWrapper
  {
    public static MWCDateTickUnitWrapper getAutoScale()
    {
      return new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 0, null);
    }

    /**
     * components of DateTickUnit
     */
    protected DateTickUnitType _unit;

    protected int _count;

    protected String _formatter;

    public MWCDateTickUnitWrapper(final DateTickUnitType unit, final int count,
        final String formatter)
    {
      _unit = unit;
      _count = count;
      _formatter = formatter;
    }

    public DateTickUnit getUnit()
    {
      DateTickUnit res = null;

      if (_formatter != DateAxisEditor.RELATIVE_DTG_FORMAT)
      {
        final DateFormat sdf = new GMTDateFormat(_formatter);

        res = new OptimisedDateTickUnit(_unit, _count, sdf);

      }
      else
      {
        final DateFormat sdf = new GMTDateFormat(_formatter);

        res = new OptimisedDateTickUnit(_unit, _count, sdf)
        {
          /**
           *
           */
          private static final long serialVersionUID = 1L;

          /**
           * Formats a date.
           *
           * @param date
           *          the date.
           * @return the formatted date.
           */
          @Override
          public String dateToString(final Date date)
          {
            String res1 = null;
            // how many secs?
            final long secs = date.getTime() / 1000;
            res1 = secs + "s";
            return res1;
          }
        };

      }

      return res;

    }

    private String getUnitLabel()
    {
      switch (_unit.getCalendarField())
      {
        case (Calendar.YEAR):
          return "Year";
        case (Calendar.MONTH):
          return "Month";
        case (Calendar.DAY_OF_MONTH):
          return "Day";
        case (Calendar.HOUR):
        case (Calendar.HOUR_OF_DAY):
          return "Hour";
        case (Calendar.MINUTE):
          return "Min";
        case (Calendar.SECOND):
          return "Sec";
        default:
          return "Milli";
      }
    }

    public boolean isAutoScale()
    {
      return (_formatter == null);
    }

    @Override
    public String toString()
    {
      String res = null;

      if (_formatter == null)
      {
        res = "Auto-scale";
      }
      else
      {
        res = _count + " " + getUnitLabel() + " " + _formatter;
      }

      return res;
    }
  }

  public static class OptimisedDateTickUnit extends DateTickUnit
  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * static calendar instance, to reduce object allocation
     *
     */
    private static Calendar _myCal = null;

    public OptimisedDateTickUnit(final DateTickUnitType unitType,
        final int multiple)
    {
      super(unitType, multiple);
    }

    public OptimisedDateTickUnit(final DateTickUnitType unitType,
        final int multiple, final DateFormat formatter)
    {
      super(unitType, multiple, formatter);
    }

    public OptimisedDateTickUnit(final DateTickUnitType unitType,
        final int multiple, final DateTickUnitType rollUnitType,
        final int rollMultiple, final DateFormat formatter)
    {
      super(unitType, multiple, rollUnitType, rollMultiple, formatter);
    }

    /**
     * Overrides parent implementation, in order that we can use static Calendar instance rather
     * than creating it lots of times.
     */
    @Override
    @SuppressWarnings("deprecation")
    public Date addToDate(final Date base, final TimeZone zone)
    {

      // do we have a calenar already?
      if (_myCal == null)
        _myCal = Calendar.getInstance(zone);

      _myCal.setTime(base);
      _myCal.add(this.getUnitType().getCalendarField(), this.getCount());
      return _myCal.getTime();

    }
  }

  /**
   * the string format used to denote a relative time description
   */
  public static final String RELATIVE_DTG_FORMAT = "T+SSS";

  // ////////////////////////////////////////////////
  // member methods
  // ////////////////////////////////////////////////

  /**
   * a list of strings representing the tick units
   */
  private static String[] _theTags = null;

  /**
   * the actual tick units in use
   */
  private static MWCDateTickUnitWrapper[] _theData = null;

  /**
   * Returns a collection of standard date tick units. This collection will be used by default, but
   * you are free to create your own collection if you want to (see the setStandardTickUnits(...)
   * method inherited from the ValueAxis class).
   *
   * @return a collection of standard date tick units.
   */
  public static ArrayList<MWCDateTickUnitWrapper>
      createStandardDateTickUnitsAsArrayList()
  {

    final ArrayList<MWCDateTickUnitWrapper> units =
        new ArrayList<MWCDateTickUnitWrapper>();

    units.add(MWCDateTickUnitWrapper.getAutoScale());

    // //////////////////////////////////////////////////////

    // milliseconds
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.MILLISECOND, 500,
        "HH:mm:ss.SSS"));

    // seconds
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 1,
        "HH:mm:ss"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 5,
        "HH:mm:ss"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 10,
        "HH:mm:ss"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 30,
        "HH:mm:ss"));

    // minutes
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.MINUTE, 1, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.MINUTE, 2, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.MINUTE, 5, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.MINUTE, 10, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.MINUTE, 15, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.MINUTE, 20, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.MINUTE, 30, "HH:mm"));

    // hours
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.HOUR, 1, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.HOUR, 2, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.HOUR, 4, "HH:mm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.HOUR, 6, "ddHHmm"));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.HOUR, 12, "ddHHmm"));

    // days
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.DAY, 1, "d-MMM"));

    // absolute seconds
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 1,
        RELATIVE_DTG_FORMAT));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 5,
        RELATIVE_DTG_FORMAT));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 10,
        RELATIVE_DTG_FORMAT));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 30,
        RELATIVE_DTG_FORMAT));
    units.add(new MWCDateTickUnitWrapper(DateTickUnitType.SECOND, 60,
        RELATIVE_DTG_FORMAT));

    return units;

  }

  public static TickUnits createStandardDateTickUnitsAsTickUnits()
  {
    final TickUnits units = new TickUnits();

    // milliseconds
    units.add(new OptimisedDateTickUnit(DateTickUnitType.MILLISECOND, 500,
        new GMTDateFormat("HH:mm:ss.SSS")));

    // seconds
    units.add(new OptimisedDateTickUnit(DateTickUnitType.SECOND, 1,
        new GMTDateFormat("HH:mm:ss")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.SECOND, 5,
        new GMTDateFormat("HH:mm:ss")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.SECOND, 10,
        new GMTDateFormat("HH:mm:ss")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.SECOND, 30,
        new GMTDateFormat("HH:mm:ss")));

    // minutes
    units.add(new OptimisedDateTickUnit(DateTickUnitType.MINUTE, 1,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.MINUTE, 2,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.MINUTE, 5,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.MINUTE, 10,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.MINUTE, 15,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.MINUTE, 20,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.MINUTE, 30,
        new GMTDateFormat("HH:mm")));

    // hours
    units.add(new OptimisedDateTickUnit(DateTickUnitType.HOUR, 1,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.HOUR, 2,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.HOUR, 4,
        new GMTDateFormat("HH:mm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.HOUR, 6,
        new GMTDateFormat("ddHHmm")));
    units.add(new OptimisedDateTickUnit(DateTickUnitType.HOUR, 12,
        new GMTDateFormat("ddHHmm")));

    // days
    units.add(new OptimisedDateTickUnit(DateTickUnitType.DAY, 1,
        new GMTDateFormat("d-MMM")));

    return units;
  }

  protected synchronized void checkCreated()
  {
    // have they been created?
    if (_theData == null)
    {
      // create them
      final ArrayList<MWCDateTickUnitWrapper> theList =
          createStandardDateTickUnitsAsArrayList();

      // _theDates = new TickUnits();

      _theTags = new String[theList.size()];

      _theData = new MWCDateTickUnitWrapper[theList.size()];

      // work through the list
      for (int i = 0; i < theList.size(); i++)
      {
        final MWCDateTickUnitWrapper unit = theList.get(i);

        _theData[i] = unit;

        // and create the strings
        _theTags[i] = unit.toString();
      }
    }
  }

  public MWCDateTickUnitWrapper getDateTickUnit()
  {
    final Integer index = (Integer) this.getValue();
    final MWCDateTickUnitWrapper theUnit = _theData[index.intValue()];
    return theUnit;
  }

  /**
   * retrieve the list of tags we display
   *
   * @return the list of options
   */
  @Override
  public String[] getTags()
  {

    // check we're ready
    checkCreated();

    return _theTags;
  }

  /**
   * return the currently selected string
   *
   * @return
   */
  @Override
  public Object getValue()
  {
    // check we have the data
    checkCreated();

    final Integer theIndex = (Integer) super.getValue();
    return _theData[theIndex.intValue()];
  }

  /**
   * select this vlaue
   *
   * @param p1
   */
  @Override
  public void setValue(final Object p1)
  {
    // check we have the data
    checkCreated();

    if (p1 instanceof MWCDateTickUnitWrapper)
    {
      // pass through to match
      for (int i = 0; i < _theData.length; i++)
      {
        final MWCDateTickUnitWrapper unit = _theData[i];
        if (unit.equals(p1))
        {
          this.setValue(new Integer(i));
        }
      }
    }
    else
      super.setValue(p1);
  }

}
