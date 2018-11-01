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
package MWC.GenericData;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: ian.mayo Date: 23-Nov-2004 Time: 10:52:54 To
 * change this template use File | Settings | File Templates.
 */
public class HiResDate implements Serializable, Comparable<HiResDate>
{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	/**
	 * number of microseconds
	 * 
	 */
	long _micros;

	public static final String HI_RES_PROPERTY_NAME = "MWC_HI_RES";

	/**
	 * if the current application has alternate processing for hi-res & lo-res
	 * timings, we check when asked, and remember the value here
	 */
	private static Boolean _hiResProcessing = null;
	
	public static final HiResDate NULL_DATE = new HiResDate(-1);

	// the marker for incomplete hi-res changes:
	// HI-RES NOT DONE

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////
	/**
	 * if the current application has alternate processing for hi-res & lo-res
	 * timings,
	 * 
	 * @param millis
	 * @param micros
	 */
	public HiResDate(final long millis, final long micros)
	{
		this._micros = millis * 1000 + micros;
	}

	public HiResDate(final long millis)
	{
		this(millis, 0);
	}

	public HiResDate(final Date val)
	{
		this(val.getTime());
	}

	public HiResDate(final HiResDate other)
	{
		_micros = other._micros;
	}

	public HiResDate()
	{
		this(new Date().getTime());
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	public String toString()
	{
		return MWC.Utilities.TextFormatting.FormatRNDateTime.toString(this
				.getDate().getTime())
				+ ":" + super.toString();
	}

	/**
	 * when an application is capable of alternate time resolution processing
	 * modes, this method indicates the user preference
	 * 
	 * @return yes/no
	 */
	public static boolean inHiResProcessingMode()
	{
		if (_hiResProcessing == null)
		{
			final String hiRes = System.getProperty(HiResDate.HI_RES_PROPERTY_NAME);
			if (hiRes == null)
			{
				_hiResProcessing = Boolean.FALSE;
			}
			else
				_hiResProcessing = new Boolean(hiRes);
		}

		return _hiResProcessing.booleanValue();

	}

	public long getMicros()
	{
		return _micros;
	}

	public Date getDate()
	{
		// throw new RuntimeException("not ready to handle long times");
		return new Date(_micros / 1000);
	}

	public boolean greaterThan(final HiResDate other)
	{
		return getMicros() > other.getMicros();
	}

	public boolean greaterThanOrEqualTo(final HiResDate other)
	{
		return getMicros() >= other.getMicros();
	}

	public boolean lessThan(final HiResDate other)
	{
		return getMicros() < other.getMicros();
	}

	public boolean lessThanOrEqualTo(final HiResDate other)
	{
		return getMicros() <= other.getMicros();
	}

	

	/**
	 * compare the supplied date to us
	 * 
	 * @param o
	 *          other date
	 * @return whether we're later than it
	 */
	public int compareTo(final HiResDate other)
	{
		int res = 0;
		if (this.greaterThan(other))
		{
			res = 1;
		}
		else if (this.lessThan(other))
		{
			res = -1;
		}
		else
			res = 0;
		return res;
	}

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (_micros ^ (_micros >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HiResDate other = (HiResDate) obj;
    if (_micros != other._micros)
      return false;
    return true;
  }

  /** convert null date into the NULL_DATE object, which we 
   * use in property editors
   * @param date
   * @return converted value
   */
  public static HiResDate wrapped(HiResDate date)
  {
    return date == null ? NULL_DATE : date;
  }

  /** convert NULL_DATE object back to null value, which we 
   * use in property editors
   * @param date
   * @return converted value
   */
  public static HiResDate unwrapped(HiResDate date)
  {
    return NULL_DATE.equals(date) ? null : date;
  }
}
