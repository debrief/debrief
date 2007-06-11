// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: TimeStepPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: TimeStepPropertyEditor.java,v $
// Revision 1.5  2004/11/29 15:28:35  Ian.Mayo
// Reflect fact that stepper freqs are actually passed as longs
//
// Revision 1.4  2004/11/26 11:32:52  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.3  2004/11/24 16:05:29  Ian.Mayo
// Switch to hi-res timers
//
// Revision 1.2  2004/05/25 15:29:14  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:41+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:46+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-29 07:56:49+00  administrator
// Use MWC.Trace instead of System.out
//
// Revision 1.0  2001-07-17 08:43:53+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-19 15:23:36+00  novatech
// added 30 second step size
//
// Revision 1.1  2001-01-03 13:42:50+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:22  ianmayo
// initial version
//
// Revision 1.2  2000-12-01 10:14:02+00  ian_mayo
// include smaller steps
//
// Revision 1.1  2000-09-26 10:53:10+01  ian_mayo
// Initial revision
//


package MWC.GUI.Properties;

import MWC.GenericData.HiResDate;

import java.beans.PropertyEditorSupport;

public class TimeStepPropertyEditor extends PropertyEditorSupport
{

  /**
   * the time step (micros)
   */
  protected long _myStep;

  /**
   * the string tags representing selectable items
   */
  protected String _stringTags[];

  /**
   * the frequencies which the tags represent
   */
  protected long _freqs[];

  /** put the items into the lists.  We do it here so that
   * we can over-ride it to provide the hi-res timers
   */
  protected void initialiseLists()
  {
    if (_stringTags == null)
    {

      _stringTags = new String[]
      {
        "1/10 Sec",
        "1/2 Sec",
        "1 Sec",
        "5 Secs",
        "15 Secs",
        "30 Secs",
        "1 Min",
        "5 Mins",
        "10 Mins",
        "15 Mins",
        "30 Mins",
        "60 Mins"};

      _freqs =
        new long[]
        {
          100 * 1000,
          500 * 1000,
          1000 * 1000,
          5 * 1000 * 1000,
          15 * 1000 * 1000,
          30 * 1000 * 1000,
          1 * 60 * 1000 * 1000,
          5 * 60 * 1000 * 1000,
          10 * 60 * 1000 * 1000,
          15 * 60 * 1000 * 1000,
          30 * 60 * 1000 * 1000,
          60 * 60 * 1000 * 1000l,
        };
    }
  }


  public String[] getTags()
  {
    initialiseLists();

    return _stringTags;
  }

  public Object getValue()
  {
    return new Long(_myStep);
  }


  public void setValue(Object p1)
  {
    if (p1 instanceof Long)
    {
      _myStep = ((Long)p1).longValue();
    }
    else if (p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(String val)
  {
    initialiseLists();

    for (int i = 0; i < _stringTags.length; i++)
    {
      String thisS = _stringTags[i];
      if (thisS.equals(val))
      {
        _myStep = _freqs[i];
      }
    }

  }

  public String getAsText()
  {
    initialiseLists();

    String res = null;
    for (int i = 0; i < _freqs.length; i++)
    {
      double v = _freqs[i];
      if (v == _myStep)
      {
        res = _stringTags[i];
      }

    }

    return res;
  }
}

