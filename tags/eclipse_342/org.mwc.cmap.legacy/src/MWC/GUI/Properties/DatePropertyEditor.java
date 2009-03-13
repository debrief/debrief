package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DatePropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: DatePropertyEditor.java,v $
// Revision 1.5  2005/02/09 16:02:11  Ian.Mayo
// Don't accidentally give a date a value (-1), when it's actually a null
//
// Revision 1.4  2004/11/26 11:32:50  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.3  2004/11/25 10:23:01  Ian.Mayo
// Fixing more tests
//
// Revision 1.2  2004/05/25 15:28:47  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:44+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:37+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-03-14 08:06:25+00  administrator
// Switch to uniform formatting of hours/min/sec
//
// Revision 1.2  2002-03-13 08:59:18+00  administrator
// Manage occasion when duff data entered deliberately (to set dates to null)
//
// Revision 1.1  2002-01-22 12:43:13+00  administrator
// use different symbol for seconds separator
//
// Revision 1.0  2001-07-17 08:43:51+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-12 12:06:17+01  novatech
// make date formatters visible to child classes
//
// Revision 1.2  2001-01-17 09:41:35+00  novatech
// factor generic processing to parent class, and provide support for NULL values
//
// Revision 1.1  2001-01-03 13:42:46+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:10  ianmayo
// initial version
//
// Revision 1.8  2000-10-09 13:35:49+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.7  2000-09-28 12:25:00+01  ian_mayo
// switch all Date manipulation to GMT
//
// Revision 1.6  2000-04-12 10:44:43+01  ian_mayo
// correct time format
//
// Revision 1.5  2000-04-03 10:57:55+01  ian_mayo
// correct SimpleDateFormat used
//
// Revision 1.4  2000-02-02 14:25:06+00  ian_mayo
// correct package naming
//
// Revision 1.3  1999-11-18 11:10:03+00  ian_mayo
// move AWT/Swing specific behaviour into separate classes
//
// Revision 1.2  1999-11-11 18:16:09+00  ian_mayo
// new class, now working
//
// Revision 1.1  1999-10-12 15:36:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:05:48+01  administrator
// Initial revision
//

import MWC.GenericData.HiResDate;

import java.awt.*;
import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

abstract public class DatePropertyEditor extends
  PropertyEditorSupport
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * the value we are editing
   */
  protected Date _myVal;

  /**
   * the microsecond portion of the date
   */
  protected int _theMicros;

  /**
   * field to edit the date
   */
  protected TextField _theDate;

  /**
   * field to edit the time
   */
  protected TextField _theTime;

  /**
   * panel to hold everything
   */
  protected Panel _theHolder;

  static protected final String NULL_DATE = "dd/MM/yy";
  static protected final String NULL_TIME = "HH:mm:ss";

  /**
   * static formats
   */
  static protected DateFormat _dateF = new SimpleDateFormat(NULL_DATE);
  static protected DateFormat _timeF = new SimpleDateFormat(NULL_TIME);


  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * indicate that we can't just be painted, we've got to be edited
   */
  public boolean isPaintable()
  {
    return false;
  }

  /**
   * build the editor
   */
  abstract public java.awt.Component getCustomEditor();

  /**
   * store the new value
   */
  public void setValue(Object p1)
  {
    // check the formats are in the correct time zone
    _dateF.setTimeZone(TimeZone.getTimeZone("GMT"));
    _timeF.setTimeZone(TimeZone.getTimeZone("GMT"));

    // reset value
    _myVal = null;

    // try to catch if we are receiving a null (uninitialised) value
    if (p1 != null)
    {
      // check it's a date
      if (p1 instanceof HiResDate)
      {
        HiResDate val = (HiResDate) p1;

        // extract the date portion
        _myVal = val.getDate();

        // just check if the date contains a duff micros
        if (val.getMicros() != -1000)
        {
          // and the microsecond portion
          _theMicros = (int) (val.getMicros() % 1000000);
        }

        // @@ we're no longer checking whether the date has been set.
        // check that the date value has been set
        //        long timeVal = val.getDate().getTime();
        //        if(timeVal != -1)
      }
    }
  }

  /**
   * return flag to say that we'd rather use our own (custom) editor
   */
  public boolean supportsCustomEditor()
  {
    return true;
  }

  /**
   * extract the values currently stored in the text boxes
   */
  public Object getValue()
  {
    HiResDate res = null;

    // see if we still have null values
    String dateVal = getDateText();
    String timeVal = getTimeText();

    long theTime = 0;

    try
    {
      if (!dateVal.equals(NULL_DATE))
        theTime += _dateF.parse(dateVal).getTime() * 1000;

      if (!timeVal.equals(NULL_TIME))
        theTime += _timeF.parse(timeVal).getTime() * 1000;

      // also add any micros
      theTime += _theMicros;
    }
    catch (ParseException e)
    {
      theTime = 0;
    }

    if (theTime != 0)
      res = new HiResDate(0, theTime);
    else
      res = null;

    return res;
  }

  /**
   * put the data into the text fields, if they have been
   * created yet
   */
  public void resetData()
  {
    if (_myVal == null)
    {
      setDateText(NULL_DATE);
      setTimeText(NULL_TIME);
    }
    else
    {
      setDateText(_dateF.format(_myVal));
      setTimeText(_timeF.format(_myVal));

      // are we in hi-res mode?
      if (HiResDate.inHiResProcessingMode())
        setMicroText(_theMicros);
    }
  }

  /**
   * show the user how many microseconds there are
   *
   * @param val
   */
  abstract protected void setMicroText(long val);

  /**
   * get the date text as a string
   */
  abstract protected String getDateText();

  /**
   * get the date text as a string
   */
  abstract protected String getTimeText();

  /**
   * set the date text in string form
   */
  abstract protected void setDateText(String val);

  /**
   * set the time text in string form
   */
  abstract protected void setTimeText(String val);

}
