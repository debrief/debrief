/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
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
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: ian.mayo Date: 23-Nov-2004 Time: 10:52:54 To
 * change this template use File | Settings | File Templates.
 */
public class HiResDate implements Serializable, Comparable<HiResDate> {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	public static final String HI_RES_PROPERTY_NAME = "MWC_HI_RES";

	/**
	 * if the current application has alternate processing for hi-res & lo-res
	 * timings, we check when asked, and remember the value here
	 */
	private static Boolean _hiResProcessing = null;

	public static final HiResDate NULL_DATE = new HiResDate(-1);

	/**
	 * when an application is capable of alternate time resolution processing modes,
	 * this method indicates the user preference
	 *
	 * @return yes/no
	 */
	public static boolean inHiResProcessingMode() {
		if (_hiResProcessing == null) {
			final String hiRes = System.getProperty(HiResDate.HI_RES_PROPERTY_NAME);
			if (hiRes == null) {
				_hiResProcessing = Boolean.FALSE;
			} else
				_hiResProcessing = new Boolean(hiRes);
		}

		return _hiResProcessing.booleanValue();

	}

	// the marker for incomplete hi-res changes:
	// HI-RES NOT DONE

	public static boolean isNotInitialized(final HiResDate myValueHiResDate) {
		return HiResDate.NULL_DATE.equals(myValueHiResDate) || myValueHiResDate.getMicros() == -1000000L;
	}

	/**
	 * Return the max of two HiResDate
	 *
	 * @param a
	 * @param b
	 * @return Return the max of two HiResDate
	 */
	public static HiResDate max(final HiResDate a, final HiResDate b) {
		if (a.compareTo(b) <= 0) {
			return a;
		} else {
			return b;
		}
	}

	/**
	 * Return the min of two HiResDate
	 *
	 * @param a
	 * @param b
	 * @return min of two HiResDate
	 */
	public static HiResDate min(final HiResDate a, final HiResDate b) {
		if (a.compareTo(b) > 0) {
			return a;
		} else {
			return b;
		}
	}

	/**
	 * convert NULL_DATE object back to null value, which we use in property editors
	 *
	 * @param date
	 * @return converted value
	 */
	public static HiResDate unwrapped(final HiResDate date) {
		return NULL_DATE.equals(date) ? null : date;
	}

	/**
	 * convert null date into the NULL_DATE object, which we use in property editors
	 *
	 * @param date
	 * @return converted value
	 */
	public static HiResDate wrapped(final HiResDate date) {
		return date == null ? NULL_DATE : date;
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	/**
	 * number of microseconds
	 *
	 */
	long _micros;

	public HiResDate() {
		this(new Date().getTime());
	}

	public HiResDate(final Date val) {
		this(val.getTime());
	}

	public HiResDate(final HiResDate other) {
		_micros = other._micros;
	}

	public HiResDate(final long millis) {
		this(millis, 0);
	}

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
	public HiResDate(final long millis, final long micros) {
		this._micros = millis * 1000 + micros;
	}

	/**
	 * compare the supplied date to us
	 *
	 * @param o other date
	 * @return whether we're later than it
	 */
	@Override
	public int compareTo(final HiResDate other) {
		int res = 0;
		if (this.greaterThan(other)) {
			res = 1;
		} else if (this.lessThan(other)) {
			res = -1;
		} else
			res = 0;
		return res;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HiResDate other = (HiResDate) obj;
		if (_micros != other._micros)
			return false;
		return true;
	}

	public Date getDate() {
		// throw new RuntimeException("not ready to handle long times");
		return new Date(_micros / 1000);
	}

	public long getMicros() {
		return _micros;
	}

	public boolean greaterThan(final HiResDate other) {
		return other != null && getMicros() > other.getMicros();
	}

	public boolean greaterThanOrEqualTo(final HiResDate other) {
		return getMicros() >= other.getMicros();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_micros ^ (_micros >>> 32));
		return result;
	}

	public boolean lessThan(final HiResDate other) {
		return getMicros() < other.getMicros();
	}

	public boolean lessThanOrEqualTo(final HiResDate other) {
		return getMicros() <= other.getMicros();
	}

	@Override
	public String toString() {
		return MWC.Utilities.TextFormatting.FormatRNDateTime.toString(this.getDate().getTime()) + ":"
				+ super.toString();
	}
	
	/**
	 * Method that copies the Hours, Minute, Second and Millisecond from source to target
	 * @param source
	 * @param target
	 * @return
	 */
	public static HiResDate copyOnlyTime(final HiResDate source, final HiResDate target) {
		final Calendar targetCalendar = Calendar.getInstance();
		targetCalendar.setTime(target.getDate());
		
		final Calendar sourceCalendar = Calendar.getInstance();
		sourceCalendar.setTime(source.getDate());
		
		targetCalendar.set(Calendar.HOUR_OF_DAY, sourceCalendar.get(Calendar.HOUR_OF_DAY));
		targetCalendar.set(Calendar.MINUTE, sourceCalendar.get(Calendar.MINUTE));
		targetCalendar.set(Calendar.SECOND, sourceCalendar.get(Calendar.SECOND));
		targetCalendar.set(Calendar.MILLISECOND, sourceCalendar.get(Calendar.MILLISECOND));
		
		return new HiResDate(targetCalendar.getTimeInMillis(), source._micros % 1000);
	}

	/**
	 * Method that copies the Year, Month and Day from source to target
	 * @param source
	 * @param target
	 * @return
	 */
	public static HiResDate copyOnlyDate(final HiResDate source, final HiResDate target) {
		final Calendar targetCalendar = Calendar.getInstance();
		targetCalendar.setTime(target.getDate());
		
		final Calendar sourceCalendar = Calendar.getInstance();
		sourceCalendar.setTime(source.getDate());
		
		targetCalendar.set(Calendar.YEAR, sourceCalendar.get(Calendar.YEAR));
		targetCalendar.set(Calendar.MONTH, sourceCalendar.get(Calendar.MONTH));
		targetCalendar.set(Calendar.DAY_OF_MONTH, sourceCalendar.get(Calendar.DAY_OF_MONTH));
		
		return new HiResDate(targetCalendar.getTimeInMillis(), target._micros % 1000);
	}
}
