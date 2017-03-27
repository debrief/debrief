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
// $RCSfile: TimeFrequencyPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: TimeFrequencyPropertyEditor.java,v $
// Revision 1.4  2004/11/25 11:05:15  Ian.Mayo
// Switch to HiResDate internally
//
// Revision 1.3  2004/11/24 16:05:28  Ian.Mayo
// Switch to hi-res timers
//
// Revision 1.2  2004/05/25 15:29:10  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-12-16 15:17:59+00  ian_mayo
// Tidy comments, extend frequency
//
// Revision 1.2  2002-05-28 09:25:42+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:43+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:45+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-10-01 12:48:59+01  administrator
// Use accessor methods to get at tags & labels (so that we can over-ride this class more easily)
//
// Revision 1.0  2001-07-17 08:43:52+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:49+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:21  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:53:02+01  ian_mayo
// Initial revision
//


package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

/**
 * class to provide list of time frequencies, together with ALL value
 */
public class TimeFrequencyPropertyEditor extends PropertyEditorSupport
{

  public static final long _60_MINS = 60 * 60 * 1000000l;

  public static final long _30_MINS = 30 * 60 * 1000000l;

  public static final long _15_MINS = 15 * 60 * 1000000l;

  public static final long _10_MINS = 10 * 60 * 1000000l;

  public static final long _5_MINS = 5 * 60 * 1000000l;

	/**
   * the value used to represent ALL items
   */
  public static long SHOW_ALL_FREQUENCY = TimePeriod.INVALID_TIME;

  /**
   * the currently selected frequency (in micros)
   */
  protected HiResDate _myFreq;

  // HI-RES NOT DONE
  // switch to two modes - with hi-res options when in hi-res mode

  /**
   * the list of tags shown in the drop-down list
   */
  private final String _stringTags[] =
    {
      "All",
      "1 Secs",
      "5 Secs",
      "10 Secs",
      "30 Secs",
      "1 Min",
      "2 Min",
      "3 Min",
      "5 Mins",
      "6 Mins",
      "10 Mins",
      "15 Mins",
      "30 Mins",
      "60 Mins",
      "2 Hours",
      "6 Hours",
      "12 Hours",
      "24 Hours",
      "48 Hours",
      "72 Hours",
      "None"};

  /**
   * the values to use for the tags in the list
   */
  private final long _freqs[] =
    {
      SHOW_ALL_FREQUENCY,
      1 * 1000000l,
      5 * 1000000l,
      10 * 1000000l,
      30 * 1000000l,
      1 * 60 * 1000000l,
      2 * 60 * 1000000l,
      3 * 60 * 1000000l,
      _5_MINS,
      6 * 60 * 1000000l,
      _10_MINS,
      _15_MINS,
      _30_MINS,
      _60_MINS,
      2 * _60_MINS,
      6 * _60_MINS,
      12 * _60_MINS,
      24 * _60_MINS,
      48 * _60_MINS,
      72 * _60_MINS,
      0};


  public String[] getTags()
  {
    return _stringTags;
  }

  public long[] getFreqs()
  {
    return _freqs;
  }

  public Object getValue()
  {
    return new HiResDate(_myFreq);
  }

  public void setValue(final Object p1)
  {
    if (p1 instanceof HiResDate)
    {
      _myFreq = new HiResDate((HiResDate) p1);
    }
    if (p1 instanceof String)
    {
      final String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(final String val)
  {
    final long[] freqs = getFreqs();
    final String[] tags = getTags();
    for (int i = 0; i < tags.length; i++)
    {
      final String thisS = tags[i];
      if (thisS.equals(val))
      {
        _myFreq = new HiResDate(0, freqs[i]);
        break;
      }
    }

  }

  public String getAsText()
  {
    String res = null;
    
    // check we have a freq
    if(_myFreq == null)
    	return res;
    
    final long[] freqs = getFreqs();
    final String[] tags = getTags();
    final long current = _myFreq.getMicros();
    for (int i = 0; i < freqs.length; i++)
    {
      final long v = freqs[i];
      if (v == current)
      {
        res = tags[i];
        break;
      }
    }
    
    // hmm, did we manage it?
    if (res == null)
    {
      res = tags[tags.length - 2];
    }

    return res;
  }
}

