package Debrief.GUI.Tote;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: StepControl.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.15 $
// $Log: StepControl.java,v $
// Revision 1.15  2006/05/09 10:19:17  Ian.Mayo
// Clip to seconds rather than millis
//
// Revision 1.14  2005/12/02 10:03:21  Ian.Mayo
// Clip time slider to whole seconds
//
// Revision 1.13  2005/10/03 15:18:28  Ian.Mayo
// Tidy view activation/de-activation
//
// Revision 1.12  2005/04/21 09:17:39  Ian.Mayo
// Handle storing current time when we don't have start or end times
//
// Revision 1.11  2005/04/18 08:20:06  Ian.Mayo
// Handle plot being saved without having opened the toolbox.
//
// Revision 1.10  2005/02/10 09:55:45  Ian.Mayo
// When restoring a plot file from disk, there a (slim) chance that we have current toolbox slider times set, but no outer limits.  Put in handling to overcome these problems.
//
// Revision 1.9  2005/01/28 16:27:12  Ian.Mayo
// Try to trap problem in 3d update.
//
// Revision 1.8  2004/12/01 09:17:50  Ian.Mayo
// Set correct initial auto-step frequency (1 second, not 1000 seconds)
 //
// Revision 1.7  2004/11/29 15:33:38  Ian.Mayo
// Provide correct step-sizes when editing stepper for hi-res data
//
// Revision 1.6  2004/11/29 15:08:05  Ian.Mayo
// Minor reformatting
//
// Revision 1.5  2004/11/26 11:37:47  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.4  2004/11/25 14:24:59  Ian.Mayo
// Handle when we don't know the time
//
// Revision 1.3  2004/11/25 10:24:10  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2004/11/22 13:40:54  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.1.1.2  2003/07/21 14:47:14  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.11  2003-07-04 10:59:28+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.10  2003-05-14 16:12:12+01  ian_mayo
// make sure Swing component knows when we set start & end times
//
// Revision 1.9  2003-03-31 14:02:31+01  ian_mayo
// Format seconds value to use at least 3 digits
//
// Revision 1.8  2003-03-27 16:56:34+00  ian_mayo
// Improving support for invalid times
//
// Revision 1.7  2003-03-25 15:55:21+00  ian_mayo
// better support for time-zero, including values on time-var graphs
//
// Revision 1.6  2003-03-21 15:43:00+00  ian_mayo
// Replace stuff which shouldn't have been deleted by IntelliJ inspector
//
// Revision 1.5  2003-03-19 15:37:55+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-01-21 16:28:24+00  ian_mayo
// tidy comments
//
// Revision 1.3  2002-05-28 12:27:53+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:19+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-08 14:40:34+01  ian_mayo
// Tidy up property listening
//
// Revision 1.1  2002-04-23 12:30:05+01  ian_mayo
// Initial revision
//
// Revision 1.10  2002-02-18 20:13:21+00  administrator
// Put time marker in quotes
//
// Revision 1.9  2002-02-18 09:21:54+00  administrator
// Make DoStep method public instead of Protected, so that we can make MouseWheel listener trigger it
//
// Revision 1.8  2002-01-24 14:23:37+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.7  2002-01-17 15:03:28+00  administrator
// Reflect new interface to hide StepperListener class
//
// Revision 1.6  2001-10-03 16:06:30+01  administrator
// Rename cursor to display
//
// Revision 1.5  2001-08-31 13:25:35+01  administrator
// Provide support for T-Zero times, and supplying date formats to do this
//
// Revision 1.4  2001-08-29 19:18:09+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.3  2001-08-21 12:14:11+01  administrator
// Make tags static to support property editor
//
// Revision 1.2  2001-08-17 08:00:41+01  administrator
// Clear up memory leaks
//
// Revision 1.1  2001-08-06 17:00:25+01  administrator
// When a participant gets added, add ourselves as a listener to it, then listen out for time periods getting changed.
//
// Revision 1.0  2001-07-17 08:41:41+01  administrator
// Initial revision
//
// Revision 1.3  2001-02-01 09:30:22+00  novatech
// correctly reflect use of -1 as null time
//
// Revision 1.2  2001-01-15 11:20:55+00  novatech
// add the SymbolHighlighter
//
// Revision 1.1  2001-01-03 13:40:54+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:27  ianmayo
// initial import of files
//
// Revision 1.26  2000-12-01 10:15:35+00  ian_mayo
// allow editable date formatter
//
// Revision 1.25  2000-11-22 10:50:19+00  ian_mayo
// Ignore new string for painter if it is blank (null) anyway
//
// Revision 1.24  2000-11-17 09:14:27+00  ian_mayo
// allow external setting of current painter using Text string
//
// Revision 1.23  2000-10-27 14:51:02+01  ian_mayo
// corrected creation of DefaultHighlighter (which must be stored in a static structure
//
// Revision 1.22  2000-10-26 15:37:14+01  ian_mayo
// correct which highlighter gets returned when we request "default highlighter"
//
// Revision 1.21  2000-10-24 11:22:40+01  ian_mayo
// create default highlighter
//
// Revision 1.20  2000-10-09 13:37:42+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.19  2000-10-03 14:17:36+01  ian_mayo
// allow manipulation/setting of highlighters
//
// Revision 1.18  2000-09-27 14:46:41+01  ian_mayo
// name changes
//
// Revision 1.17  2000-09-21 09:05:20+01  ian_mayo
// make Editable.EditorType a transient parameter, to save it being written to file
//
// Revision 1.16  2000-09-18 09:14:49+01  ian_mayo
// GUI name changes
//
// Revision 1.15  2000-08-21 15:43:00+01  ian_mayo
// tidying up
//
// Revision 1.14  2000-08-18 13:34:05+01  ian_mayo
// Editable.EditorType
//
// Revision 1.13  2000-08-11 08:41:01+01  ian_mayo
// tidy beaninfo
//
// Revision 1.12  2000-08-09 16:03:56+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.11  2000-04-03 10:45:24+01  ian_mayo
// clip the time offered to a valid time
//
// Revision 1.10  2000-03-27 14:42:40+01  ian_mayo
// add event thrower for when the painterManager is defined
//
// Revision 1.9  2000-03-14 09:50:17+00  ian_mayo
// Allow user configuration of font size for time label
//
// Revision 1.8  2000-03-08 14:27:02+00  ian_mayo
// return additional beanInfo to cover FixPainter
//
// Revision 1.7  2000-03-07 14:48:13+00  ian_mayo
// optimised algorithms
//
// Revision 1.6  1999-12-03 14:37:42+00  ian_mayo
// check for null secondaries
//
// Revision 1.5  1999-12-02 09:47:41+00  ian_mayo
// provide method to return Vector of participants
//
// Revision 1.4  1999-11-26 15:51:41+00  ian_mayo
// tidying up
//
// Revision 1.3  1999-10-15 12:37:17+01  ian_mayo
// improved management of watchables which don't hve time periods
//
// Revision 1.2  1999-10-15 12:10:35+01  ian_mayo
// Improved 'recalcTimes' to allow for objects which are not time related
//
// Revision 1.1  1999-10-12 15:34:24+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-09-14 15:51:25+01  administrator
// automatic time stepping
//
// Revision 1.1  1999-08-04 10:53:02+01  administrator
// Initial revision
//

import Debrief.Tools.Tote.WatchableList;
import MWC.GUI.Editable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.StepperListener;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import java.beans.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

abstract public class StepControl implements Editable,
  MWC.Utilities.Timer.TimerListener,
  java.beans.PropertyChangeListener,
  StepperListener.StepperController

{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////



  /** the filter itself, only really used from Swing
   *
   */
  protected Debrief.GUI.Tote.Swing.TimeFilter.TimeEditorPanel _timeFilter;

  /**
   * small step size (in microseconds)
   */
  private long _smallStep;

  /**
   * large step size (in microseconds)
   */
  private long _largeStep;

  /**
   * the current time step
   */
  private HiResDate _currentTime = TimePeriod.INVALID_DATE;

  /**
   * the list of listeners to this control
   */
  private final Vector _listeners;

  /**
   * the start time for the stepping
   */
  private HiResDate _startTime;

  /**
   * the end time for the stepping
   */
  private HiResDate _endTime;

  /**
   * the list of participants from which we produce our time period.  This hashtable
   * actually contains a list of valid time periods for our watchables, indexed
   * by the watchables themselves.
   * The values for  time period (expressed as a somePeriod) for a participant are
   * updated following a property change from that item
   */
  private final Hashtable _participants;

  /**
   * the automatic timer we are using
   */
  private MWC.Utilities.Timer.Timer _theTimer;

  /**
   * remember whether the user last requested to go forwards or backwards
   */
  protected boolean _goingForward;

  /**
   * remember whether the user last requested a small or large time step
   */
  protected boolean _largeSteps;

  /**
   * do we have a painter manager?
   */
  protected Debrief.GUI.Tote.Painters.PainterManager _thePainterManager;

  /**
   * the font size for the time stepper label
   */
  protected int _fontSize = 12;

  /**
   * keep a local copy of the editable information for this object
   */
  transient private Editable.EditorType _myEditor = null;

  /**
   * the list of highlighters we know about
   */
  private static Vector _myHighlighters;

  /**
   * the highlighter currently selected
   */
  private Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter _currentHighlighter;

  /**
   * the default highlighter - primarily used to show secondary tracks
   */
  private Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter _defaultHighlighter;

  /**
   * the date format we are using.  Note, we do some wierd stuff in this.  When we
   * want to represent a date in a T-Zero style we store the text pattern (eg T+ mm:ss)
   * in this String, knowing it may be an invalid pattern
   */
  protected final java.text.SimpleDateFormat _dateFormatter;

  /**
   * the number format we use for elapsed seconds in t-zero display mode
   */
  private java.text.DecimalFormat _secondsFormat = null;

  /**
   * the time-zero value in use (if one has been set, that is)
   */
  private HiResDate _timeZero;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public StepControl(ToolParent parent)
  {

    // sort out the small & large time steps
    initialiseTimeStepSizes();

    _listeners = new Vector(0, 1);
    _participants = new Hashtable();

    /** the timer-related settings
     */
    _theTimer = new MWC.Utilities.Timer.Timer();
    _theTimer.stop();
    setAutoStep(1000);
    _theTimer.addTimerListener(this);

    _goingForward = true;
    _largeSteps = true;

    // create our default highlighter
    _defaultHighlighter = new Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter.RectangleHighlight();

    if (_myHighlighters == null)
    {
      _myHighlighters = new Vector(0, 1);
      _myHighlighters.add(_defaultHighlighter);
      _myHighlighters.add(new Debrief.GUI.Tote.Painters.Highlighters.RangeHighlighter());
      _myHighlighters.add(new Debrief.GUI.Tote.Painters.Highlighters.SymbolHighlighter());
    }

    _currentHighlighter = (Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter) _myHighlighters.elementAt(0);

    // initialise the date format
    _dateFormatter = new java.text.SimpleDateFormat(MWC.Utilities.TextFormatting.FormatRNDateTime.getExample());
    _dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /**
   * set the initial size of the time step
   */
  private void initialiseTimeStepSizes()
  {
    if (HiResDate.inHiResProcessingMode())
    {
      _smallStep =  100;
      _largeStep = 1000;
    }
    else
    {
      _smallStep =  60000 * 1000;
      _largeStep = 600000 * 1000;
    }
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * method to manage the clear up of this class
   */
  public void closeMe()
  {
    _defaultHighlighter = null;
    _listeners.removeAllElements();

    _participants.clear();
    _theTimer = null;
    _currentHighlighter = null;
    _defaultHighlighter = null;
  }

  abstract protected void initForm();

  abstract protected void updateForm(HiResDate DTG);

  abstract protected void formatTimeText();

  abstract protected MWC.GUI.Properties.PropertiesPanel getPropertiesPanel();

  abstract protected void doEditPainter();

  /**
   * handle new listeners (add)
   */
  public final void addStepperListener(final StepperListener l)
  {
    _listeners.addElement(l);

    if (l instanceof Debrief.GUI.Tote.Painters.PainterManager)
    {
      _thePainterManager = (Debrief.GUI.Tote.Painters.PainterManager) l;
      painterIsDefined();
    }
  }

  /**
   * method intended to be overwritten by concrete classes, to find
   * out that the painter has been defined
   */
  abstract protected void painterIsDefined();

  public final void removeStepperListener(final StepperListener l)
  {
    _listeners.removeElement(l);
  }

  //////////////////////////////////////////////////
  //
  /////////////////////////////////////////////////
  public final StepperListener getCurrentPainter()
  {
    return _thePainterManager.getCurrentPainterObject();
  }



  //////////////////////////////////////////////////
  // stepping related methods
  /////////////////////////////////////////////////


  public final void doStep(final boolean forwards, final boolean large)
  {

    // have we been initialised?
    if (_currentTime != null)
    {
      // yup, process the step.

      // remember the DTG
      long newDTG = -1;

      long step = 0;
      if (large)
        step = _largeStep;
      else
        step = _smallStep;

      long nowMicros = _currentTime.getMicros();     
      
      if (forwards)
      {
        newDTG = nowMicros + step;
      }
      else
      {
        newDTG = nowMicros - step;
      }
      
      // and check the limits, if there are any
      if (!validTime(newDTG))
      {
        if (newDTG < _startTime.getMicros())
        {
          newDTG = _startTime.getMicros();
        }
        if (newDTG > _endTime.getMicros())
        {
          newDTG = _endTime.getMicros();
        }
      }
      
      // we should now have a valid time
      changeTime(new HiResDate(0, newDTG));
    }

  }

  private boolean validTime(final long val)
  {
    boolean res = false;

    if ((val >= _startTime.getMicros()) && (val <= _endTime.getMicros()))
      res = true;

    return res;
  }

  public final String toString()
  {
    return getName();
  }

  public final String getName()
  {
    return "Step Control";
  }

  public final boolean hasEditor()
  {
    return true;
  }

  public final Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new StepControlInfo(this);

    return _myEditor;
  }

  /**
   * gets the small step size (in double millis)
   */
  public final long getStepSmall()
  {
    return _smallStep;
  }

  public final void setStepSmall(final long val)
  {
    _smallStep = (int) (val);
  }

  /**
   * gets the large step size (in double millis)
   */
  public final long getStepLarge()
  {
    return _largeStep;
  }

  public final void setStepLarge(final long val)
  {
    _largeStep = (int) (val);
  }

  /**
   * set the real-time interval on the timer, the value
   * is in millis
   *
   * @param val time interval in milliseconds
   */
  public final void setAutoStep(final int val)
  {
    _theTimer.setDelay(val);
  }

  /**
   * get the real-time interval on the timer, the value
   * is in millis
   *
   * @return time interval in milliseconds
   */
  public final int getAutoStep()
  {
    return (int) _theTimer.getDelay();
  }

  public final String getDateFormat()
  {
    return _dateFormatter.toPattern();
  }

  public final void setDateFormat(final String val)
  {
    // update the formatter
    _dateFormatter.applyPattern(val);

    // has our date been set?
    HiResDate tNow = this.getCurrentTime();
    if (tNow != null)
    {
      // and update the form
      updateForm(tNow);
    }
  }


  /***************************************************
   * set of methods to control the time displayed in the toolbox (of particular
   * use in remembering the T-Zero time)
   */

  /**
   * get the time in the start slider in the toolbox
   */
  abstract public HiResDate getToolboxStartTime();

  /**
   * get the time in the finish slider in the toolbox
   */
  abstract public HiResDate getToolboxEndTime();

  /**
   * set the time in the start slider in the toolbox
   */
  abstract public void setToolboxStartTime(HiResDate val);

  /**
   * set the time in the start slider in the toolbox
   */
  abstract public void setToolboxEndTime(HiResDate val);

  /**
   * set the time-zero value
   *
   * @param newVal the new time (or null for no Time Zero)
   */
  public void setTimeZero(HiResDate newVal)
  {
    _timeZero = newVal;
  }

  /**
   * get the time-zero value (or null if we don't have one)
   *
   * @return the date
   */
  public HiResDate getTimeZero()
  {
    return _timeZero;
  }


  public void setStartTime(final HiResDate val)
  {
    _startTime = val;
  }

  public void setEndTime(final HiResDate val)
  {
    _endTime = val;
  }

  public final void setCurrentTime(final HiResDate val)
  {
    _currentTime = val;
  }

  public final HiResDate getStartTime()
  {
    return _startTime;
  }

  public final HiResDate getEndTime()
  {
    return _endTime;
  }

  public final HiResDate getCurrentTime()
  {
    return _currentTime;
  }

  public final BoundedInteger getFontSize()
  {
    return new BoundedInteger(_fontSize, 1, 30);
  }

  public final void setFontSize(final BoundedInteger val)
  {
    _fontSize = val.getCurrent();
    formatTimeText();
  }

  public final String getHighlighter()
  {
    return _currentHighlighter.toString();
  }

  /**
   * retrieve the currently selected highlighter
   *
   * @return the current highlighter
   */
  public final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter getCurrentHighlighter()
  {
    return _currentHighlighter;
  }

  /**
   * retrieve the default highlighter - used primarily to show secondary highlights
   *
   * @return the default highlighter
   */
  public final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter getDefaultHighlighter()
  {
    return _defaultHighlighter;
  }

  public final void setHighlighter(final String val)
  {
    final Enumeration iter = _myHighlighters.elements();
    while (iter.hasMoreElements())
    {
      final Object l = iter.nextElement();
      if (l.toString().equals(val))
      {
        _currentHighlighter = (Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter) l;
        break;
      }
    }
  }

  public final void editHighlighter()
  {
    if (_currentHighlighter.hasEditor())
    {
      getPropertiesPanel().addEditor(_currentHighlighter.getInfo(), null);
    }
  }


  public final void editDisplay()
  {
    doEditPainter();
  }


  // one of the objects we're listening to may have changed.
  public void propertyChange(final PropertyChangeEvent evt)
  {
    // what sort is it?
    final String type = evt.getPropertyName();

    // is it one we're interested in?
    if (type.equals(Debrief.Tools.Tote.WatchableList.FILTERED_PROPERTY))
    {
      // see if we have received the new time period
      final Object newVal = evt.getNewValue();

      // is this a valid time period?
      if (newVal instanceof somePeriod)
      {
        final somePeriod newPeriod = (somePeriod) newVal;

        // remove the old values
        _participants.remove(evt.getSource());

        // add the new one
        _participants.put(evt.getSource(), newPeriod);
      }

      // finally trigger a recalculation of the time limits
      recalcTimes();
    }

  }

  //////////////////////////////////////////////
  // handle addition/removal of participants
  /////////////////////////////////////

  /**
   * add a new exercise participant to the list we monitor, providing
   * the start and stop times aswell
   *
   * @param participant
   * @param start
   * @param end
   */
  public final void addParticipant(final WatchableList participant, final HiResDate start, final HiResDate end)
  {
    // remember this participant
    _participants.put(participant, new somePeriod(start, end));

    // start listening out for property changes
    if (participant instanceof MWC.GUI.PlainWrapper)
    {
      final MWC.GUI.PlainWrapper val = (MWC.GUI.PlainWrapper) participant;
      val.addPropertyChangeListener(Debrief.Tools.Tote.WatchableList.FILTERED_PROPERTY, this);
    }

    // recalculate the start and end times
    recalcTimes();
  }

  public final void removeParticpant(final Object participant)
  {
    // remember this participant
    _participants.remove(participant);

    // stop listening out for property changes
    if (participant instanceof MWC.GUI.PlainWrapper)
    {
      final MWC.GUI.PlainWrapper val = (MWC.GUI.PlainWrapper) participant;
      val.removePropertyChangeListener(Debrief.Tools.Tote.WatchableList.FILTERED_PROPERTY, this);
    }
    // recalculate the start and end times
    recalcTimes();
  }

  /**
   * return the participants as a hashtable
   */
  public final java.util.Hashtable getParticipants()
  {
    return _participants;
  }


  /**
   * event handler for new selection of painter
   */
  public final void setPainter(final String val)
  {
    if (val != null)
    {
      _thePainterManager.setDisplay(val);

      // and fire the event in the painter manager
      _thePainterManager.getInfo().fireChanged(this, "Painter", null, val);
    }
  }


  public void recalcTimes()
  {
    // our results object
    somePeriod res = null;

    // working value
    somePeriod sp = null;

    // go through the participant data
    final Enumeration iter = _participants.elements();

    while (iter.hasMoreElements())
    {
      // get our data next item
      sp = (somePeriod) iter.nextElement();

      // are we in our first cycle?
      if (res == null)
        res = sp;
      else
      {
        // extend our period to include new data
        res.extend(sp);
      }
    }

    // check we have some data
    if (res != null)
    {
      // so, done now
      setStartTime(res._start);
      setEndTime(res._end);
    }

    // do we have a current time?
    if (_currentTime != null)
    {
      // do we know our start time?
      if(_startTime != null)
      {
        // and check that the current time is in range
        if (!validTime(_currentTime.getMicros()))
        {
          _currentTime = new HiResDate(_startTime);
        }
      }
    }
    else
    {
      // hmm, do we have a start time?
      if(_startTime != null)
      {
        // hey, we don't have a time, we might as well use thsi one
        _currentTime = new HiResDate(_startTime);
      }
    }

    // if we have a filter configured, reset it
    if(_timeFilter != null)
       _timeFilter.reIntitialise();

  }

  protected final void gotoStart()
  {
    changeTime(_startTime);
  }

  public final void gotoEnd()
  {
    changeTime(_endTime);
  }

  public final void changeTime(final HiResDate rawTime)
  {
    HiResDate oldTime;

    // do we currently have a time?
    if (_currentTime != null)
    {
      // yes, make a copy of it to use as the old time
      oldTime = new HiResDate(_currentTime);
    }
    else
    {
      // no, just pass a null object
      oldTime = null;
    }

    // find out the millis
    long timeMillis = rawTime.getDate().getTime();

    // trim to whole minutes
    long roundedTimeMillis = (timeMillis / 1000) * 1000;
//    long roundedTimeMillis = timeMillis;
    HiResDate roundedTime = new HiResDate(roundedTimeMillis, 0);   
    
    // update the GUI
    updateForm(roundedTime);

    // inform the listeners
    final Enumeration iter = _listeners.elements();
    while (iter.hasMoreElements())
    {
      final StepperListener l = (StepperListener) iter.nextElement();
      try
      {
        l.newTime(oldTime, roundedTime, null);
      }
      catch (Exception e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }

    // and update the val
    _currentTime = roundedTime;
  }

  // timer listener event
  public final void onTime(final java.awt.event.ActionEvent event)
  {
    // temporarily remove ourselves, to prevent being called twice
    _theTimer.removeTimerListener(this);

    // catch any exceptions raised here, it doesn't really
    // matter if we miss a time step
    try
    {

      // pass the step operation on to our parent
      doStep(_goingForward, _largeSteps);

    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    // register ourselves as a time again
    _theTimer.addTimerListener(this);
  }

  protected final void startTimer()
  {
    _theTimer.start();
  }

  protected final void stopTimer()
  {
    _theTimer.stop();
  }

  /**
   * private method to produce a time string from the indicated DTG
   * we've moved it out of the newTime box so that we can access
   * it from a tester
   */
  protected String getNewTime(final HiResDate DTG)
  {
    final String pattern = _dateFormatter.toPattern();

    String res = "";

    if (pattern.startsWith("'T+'"))
    {
      // hey, we're doing our "Special format".
      res = "T ";

      // get the T-zero time
      final HiResDate theTZero = getTimeZero();
      if (theTZero == null)
      {
        // no, return an error message
        return "N/A";
      }

      final long Tzero = theTZero.getMicros();

      // what's the elapsed time
      final long elapsed = DTG.getMicros() - Tzero;

      if (Math.abs(elapsed) > 1000000000000l)
      {
        res = "N/A";
      }
      else
      {

        // how many seconds is this?
        long secs = (long) (elapsed / 1000000d);

        // are we +ve?
        if (secs > 0)
        {
          res += "+";
        }
        else
        {
          res += "-";
        }

        // ok, we've handled the +ve/-ve make it absolute
        secs = Math.abs(secs);

        // which format do they want?
        String format = pattern.substring(pattern.indexOf(" "));

        // strip the format string
        format = format.trim();

        if (format.equals("SSS"))
        {
          // do we have our number formatter?
          if (_secondsFormat == null)
            _secondsFormat = new java.text.DecimalFormat("000s");


          res += _secondsFormat.format(secs);
        }
        else if (format.equals("MM:SS"))
        {
          // how many mins?
          final long mins = secs / 60;

          // and the seconds
          secs -= mins * 60;

          res += mins;

          res += ":";

          res += MWC.Utilities.TextFormatting.BriefFormatLocation.df2.format(secs);

        }
        else
        {
          MWC.Utilities.Errors.Trace.trace("Step control: invalid TZero format found:" + format);
        }
      }
    }
    else
    {
      // are we in hi-res mode or not?
      if (HiResDate.inHiResProcessingMode())
        res = DebriefFormatDateTime.formatMicros(DTG);
      else
        res = _dateFormatter.format(DTG.getDate());
    }

    return res;
  }


  /////////////////////////////////////////////////////////////
  // nested class storing start and end times for a participant
  ////////////////////////////////////////////////////////////
  static public final class somePeriod
  {
    public HiResDate _start;
    public HiResDate _end;

    public somePeriod(final HiResDate start, final HiResDate end)
    {
      _start = start;
      _end = end;
    }

    public final void extend(final somePeriod other)
    {
      if (other._start.lessThan(_start))
        _start = other._start;

      if (other._end.greaterThan(_end))
        _end = other._end;
    }

    public final String toString()
    {
      final String res = " start:" + DebriefFormatDateTime.toStringHiRes(_start) +
        " end:" + DebriefFormatDateTime.toStringHiRes(_end);
      return res;
    }
  }

  /////////////////////////////////////////////////////////////
  // nested class describing how to edit this class
  ////////////////////////////////////////////////////////////
  public final class StepControlInfo extends Editable.EditorType
  {

    public StepControlInfo(final StepControl data)
    {
      super(data, "Step Control", "");
    }

    public final BeanInfo[] getAdditionalBeanInfo()
    {
      BeanInfo[] res = null;
      if (_thePainterManager != null)
      {
        // see if the painter manager has any additionals aswell
        final BeanInfo pm = _thePainterManager.getInfo();
        final BeanInfo[] adds = pm.getAdditionalBeanInfo();
        if (adds != null)
        {
          final BeanInfo[] res3 = new BeanInfo[adds.length + 1];
          System.arraycopy(adds, 0, res3, 0, adds.length);
          res3[res3.length - 1] = pm;
          res = res3;
        }
        else
        {
          final BeanInfo[] res2 = {_thePainterManager.getInfo()};
          res = res2;
        }
      }
      return res;
    }

    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("StepSmall", "the small step size"),
          prop("StepLarge", "the large step size"),
          prop("AutoStep", "the automatic step"),
          prop("FontSize", "the font size for the time label"),
          prop("DateFormat", "the format to use for the date/time"),
          longProp("Highlighter", "the highlighter to use", TagListEditor.class)

        };

        // right, special processing here.  If we're in hi-res mode, we want to specify the very
        // hi res timers.
        if (HiResDate.inHiResProcessingMode())
        {
          // use hi res property editor
          res[0].setPropertyEditorClass(MWC.GUI.Properties.HiFreqTimeStepPropertyEditor.class);
          res[1].setPropertyEditorClass(MWC.GUI.Properties.HiFreqTimeStepPropertyEditor.class);
        }
        else
        {
          res[0].setPropertyEditorClass(MWC.GUI.Properties.TimeStepPropertyEditor.class);
          res[1].setPropertyEditorClass(MWC.GUI.Properties.TimeStepPropertyEditor.class);
        }


        res[2].setPropertyEditorClass(MWC.GUI.Properties.TimeIntervalPropertyEditor.class);
        res[4].setPropertyEditorClass(MyDateEditor.class);

        return res;
      }
      catch (Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
        return super.getPropertyDescriptors();
      }
    }

    public final MethodDescriptor[] getMethodDescriptors()
    {
      // just add the reset color field first
      final Class c = StepControl.class;
      final MethodDescriptor[] mds = {
        method(c, "editHighlighter", null, "Edit current highlighter"),
        method(c, "editDisplay", null, "Edit current display mode"),
      };
      return mds;
    }


  }

  ///////////////////////////////////////////////////////////
  // property editor to let us add our extra (T-Zero related) formats to the dates list
  ///////////////////////////////////////////////////////////
  public static final class MyDateEditor extends MWC.GUI.Properties.DateFormatPropertyEditor
  {
    public final String[] getTags()
    {
      final String[] parents = super.getTags();
      final String[] newS = new String[parents.length + 2];

      System.arraycopy(parents, 0, newS, 0, parents.length);
      newS[parents.length] = "'T+' SSS";
      newS[parents.length + 1] = "'T+' MM:SS";

      return newS;
    }
  }

  ////////////////////////////////////////////////////////////
  // property editor to return the painters as a combo box
  ////////////////////////////////////////////////////////////

  public static final class TagListEditor extends PropertyEditorSupport
  {

    // the working copy we are editing
    String current;

    /**
     * return a tag list of the current editors
     */
    public final String[] getTags()
    {

      String[] strings = null;
      final Vector res = new Vector(0, 1);
      final Enumeration iter = _myHighlighters.elements();
      while (iter.hasMoreElements())
      {
        final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter l = (Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter) iter.nextElement();
        res.addElement(l.toString());
      }

      // are there any results?
      if (res.size() > 0)
      {
        strings = new String[res.size()];
        res.copyInto(strings);
      }

      return strings;
    }


    public final Object getValue()
    {
      return current;
    }

    public final void setValue(final Object p1)
    {
      if (p1 instanceof String)
      {
        final String val = (String) p1;
        setAsText(val);
      }
    }

    public final void setAsText(final String p1)
    {
      current = p1;
    }

    public final String getAsText()
    {
      return current;
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testMe(String val)
    {
      super(val);
    }

    public final void testMyParams()
    {
      StepControl ed = new StepControl(null)
      {
        protected void doEditPainter()
        {
        }

        protected void formatTimeText()
        {
        }

        protected PropertiesPanel getPropertiesPanel()
        {
          return null;
        }

        public HiResDate getToolboxEndTime()
        {
          return null;
        }

        public HiResDate getToolboxStartTime()
        {
          return null;
        }

        protected void initForm()
        {
        }

        protected void painterIsDefined()
        {
        }

        public void setToolboxEndTime(HiResDate val)
        {
        }

        public void setToolboxStartTime(HiResDate val)
        {
        }

        protected void updateForm(HiResDate DTG)
        {
        }
      };

      editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}
