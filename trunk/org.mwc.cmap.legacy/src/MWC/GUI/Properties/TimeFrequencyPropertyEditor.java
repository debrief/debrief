// Copyright MWC 1999, Debrief 3 Project
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

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

import java.beans.PropertyEditorSupport;

/**
 * class to provide list of time frequencies, together with ALL value
 */
public class TimeFrequencyPropertyEditor extends PropertyEditorSupport
{

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
  private String _stringTags[] =
    {
      "All",
      "1 Secs",
      "5 Secs",
      "10 Secs",
      "15 Secs",
      "30 Secs",
      "1 Min",
      "5 Mins",
      "10 Mins",
      "15 Mins",
      "30 Mins",
      "60 Mins",
      "None"};

  /**
   * the values to use for the tags in the list
   */
  private long _freqs[] =
    {
      SHOW_ALL_FREQUENCY,
      1 * 1000000l,
      5 * 1000000l,
      10 * 1000000l,
      15 * 1000000l,
      30 * 1000000l,
      60 * 1000000l,
      5 * 60 * 1000000l,
      10 * 60 * 1000000l,
      15 * 60 * 1000000l,
      30 * 60 * 1000000l,
      60 * 60 * 1000000l,
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


  public void setValue(Object p1)
  {
    if (p1 instanceof HiResDate)
    {
      _myFreq = new HiResDate((HiResDate) p1);
    }
    if (p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(String val)
  {
    long[] freqs = getFreqs();
    String[] tags = getTags();
    for (int i = 0; i < tags.length; i++)
    {
      String thisS = tags[i];
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
    long[] freqs = getFreqs();
    String[] tags = getTags();
    long current = _myFreq.getMicros();
    for (int i = 0; i < freqs.length; i++)
    {
      long v = freqs[i];
      if (v == current)
      {
        res = tags[i];
        break;
      }

    }

    return res;
  }
}

