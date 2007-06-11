package Debrief.Tools.FilterOperations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SetTimeZero.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: SetTimeZero.java,v $
// Revision 1.4  2004/11/29 16:04:31  Ian.Mayo
// Handle user cancelling entry of symbol frequency
//
// Revision 1.3  2004/11/25 10:24:29  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2004/11/22 14:05:05  Ian.Mayo
// Replace variable name previously used for counting through enumeration - now part of JDK1.5
//
// Revision 1.1.1.2  2003/07/21 14:48:25  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.7  2003-03-25 15:54:13+00  ian_mayo
// Implement "Reset me" buttons
//
// Revision 1.6  2003-03-24 11:05:43+00  ian_mayo
// Better processing for set time zero
//
// Revision 1.5  2003-03-19 15:37:05+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-07 09:02:40+00  ian_mayo
// remove unnecessary toda comments
//
// Revision 1.3  2002-07-10 14:59:26+01  ian_mayo
// handle correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.2  2002-05-28 12:28:21+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:58+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-10-01 12:49:49+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.0  2001-07-17 08:41:22+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-09 10:26:57+00  novatech
// use WatchableList instead of TrackWrapper
//
// Revision 1.1  2001-01-03 13:40:33+00  novatech
// Initial revision
//
// Revision 1.9  2000-11-24 10:53:15+00  ian_mayo
// tidying up
//
// Revision 1.8  2000-08-15 15:29:17+01  ian_mayo
// reflect Bean parameter name change
//
// Revision 1.7  2000-08-14 10:59:38+01  ian_mayo
// correct name of "do" button
//
// Revision 1.6  2000-08-11 08:41:02+01  ian_mayo
// tidy beaninfo
//
// Revision 1.5  2000-08-09 16:05:00+01  ian_mayo
// tidy formatting
//
// Revision 1.4  2000-08-07 14:22:22+01  ian_mayo
// added VCS headers
//

import Debrief.Tools.Tote.*;
import java.util.*;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import Debrief.Wrappers.*;

abstract public class SetTimeZero implements FilterOperation
{
  private HiResDate _start_time = null;
  private HiResDate _end_time = null;
  private java.util.Vector _theTracks = null;

  private final String _theSeparator = System.getProperties().getProperty("line.separator");

  // the string formatter
  private final java.text.DecimalFormat decimals = new java.text.DecimalFormat("0");


  public final String getDescription()
  {
    String res = "2. Select tracks to be updated";
    res += _theSeparator + "3. Select time-zero (Start slider)";
    res += _theSeparator + "4. Select extent of time to be updated (Finish slider)";
    res += _theSeparator + "5. Press 'Apply' button";
    res += _theSeparator + "6. Dialog boxes will pop up to allow symbol and label frequencies to"
      + " be specified (in seconds)";
    res += _theSeparator + 	"====================";
    res += _theSeparator + 	"This operation displays symbols and labels at specified frequencies, ";
    res += " together with their elapsed time (in seconds) from a time-zero";
    return res;
  }

  public final void setPeriod(HiResDate startDTG, HiResDate finishDTG)
  {
    _start_time = startDTG;
    _end_time = finishDTG;
  }

  public final void setTracks(java.util.Vector selectedTracks)
  {
    _theTracks = selectedTracks;
  }


  abstract public void setTimeZero(HiResDate newDate);

  /** the user has pressed RESET whilst this button is pressed
   *
   * @param startTime the new start time
   * @param endTime the new end time
   */
  public void resetMe(HiResDate startTime, HiResDate endTime)
  {
    setTimeZero(null);
  }

  public final void execute()
  {
  }

  public final MWC.GUI.Tools.Action getData()
  {
    // produce the list of modifications to be made
    SetTimeAction res = null;

    long symStep = 0;
    long labStep = 0;

    // check we have some tracks
    if(_theTracks == null)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Set Time Zero", "Please select one or more tracks");
      return null;
    }

    // we have remaining questions to find out.
    // first find out the symbol frequency we want
    Double tmpSymStep = MWC.GUI.Dialogs.DialogFactory.getDouble("Set Time Zero",
                                                               "Enter symbol frequency (seconds)",
                                                               5);

    if(tmpSymStep == null)
      return null;

    if(tmpSymStep.doubleValue() == 0)
      return null;

    // convert to seconds
    symStep = (long)(tmpSymStep.doubleValue() * 1000 * 1000);

    // now find out the label frequecy
    Double tmpLabStep = MWC.GUI.Dialogs.DialogFactory.getDouble("Set Time Zero",
                                                               "Enter label frequency (seconds)",
                                                               15);

    if(tmpLabStep == null)
      return null;

    if(tmpLabStep.doubleValue() == 0)
      return null;

    // convert to seconds
    labStep = (long)(tmpLabStep.doubleValue() * 1000 * 1000);

    // make our symbols and labels visible
    Enumeration iter = _theTracks.elements();
    while(iter.hasMoreElements())
    {
      WatchableList wl = (WatchableList)iter.nextElement();

      // is this a track?
      if(wl instanceof Debrief.Wrappers.TrackWrapper){

        TrackWrapper tw = (TrackWrapper)wl;

        long this_time = _start_time.getMicros();

        // first pass through, setting the labels visible
        while(this_time <= _end_time.getMicros())
        {
          Debrief.Tools.Tote.Watchable[] list = tw.getNearestTo(new HiResDate(0, this_time));
          FixWrapper fw = null;
          if(list.length > 0)
             fw = (FixWrapper)list[0];
          if(fw != null)
          {
            fw.setLabelShowing(true);
          }

          // produce the next time step
          this_time += labStep;
        }


        // now pass through making the symbols visible
        this_time = _start_time.getMicros();

        // first pass through, setting the symbols visible
        while(this_time <= _end_time.getMicros())
        {
          Debrief.Tools.Tote.Watchable[] list = tw.getNearestTo(new HiResDate(this_time));
          FixWrapper fw = null;
          if(list.length > 0)
             fw = (FixWrapper)list[0];

          if(fw != null)
          {
            // check that the track has positions showing
            if(tw.getPositionsVisible() == false)
              tw.setPositionsVisible(true);

            // and make this symbol visible
            fw.setSymbolShowing(true);
          }
          // produce the next time step
          this_time += symStep;
        }


      }
    }


    // now change the times of all of the visible labels
    iter = _theTracks.elements();
    while(iter.hasMoreElements())
    {
      WatchableList wl = (WatchableList)iter.nextElement();

      // is this a track?
      if(wl instanceof Debrief.Wrappers.TrackWrapper){

        TrackWrapper tw = (TrackWrapper)wl;

        // step through the track
        Collection ss = tw.getItemsBetween(tw.getStartDTG(),
                                          tw.getEndDTG());

        Iterator it = ss.iterator();

        while(it.hasNext())
        {
          FixWrapper fw = (FixWrapper)it.next();

          // is the label visible for this fix?
          if(fw.getLabelShowing())
          {
            // calculate the new time value
            long thisDTG = fw.getTime().getMicros();
            long delta = thisDTG - this._start_time.getMicros();

            // convert the delta to seconds
            double secs = delta / 1000.0;

            // calculate the new time label
            String val;
            if(secs < 0)
            {
              // we dont need to include the minus sign, since
              // it's on the front of the string
              val="T";
            }
            else
            {
              val = "T+";
            }

            val += decimals.format(secs);

            // retrieve the old time label
            String oldVal = fw.getName();

            // add this update to our action
            if(res == null)
            {
              res = new SetTimeAction(DebriefFormatDateTime.toStringHiRes(this._start_time),
                                      this._start_time);
            }

            res.addAction(fw,
                          val,
                          oldVal);

          }

        }

      }

    }
    // return the new action
    return res;
  }

  public final String getLabel()
  {
    return "Set time zero";
  }

  public final String getImage()
  {
    return null;
  }

  public final void actionPerformed(java.awt.event.ActionEvent p1)
  {

  }

  public final void close()
  {

  }

  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  final class SetTimeAction implements Action
  {
    private final String _theTimeString;
    private HiResDate _theTime;
    private final java.util.Vector _theChanges;

    public SetTimeAction(String theTimeString,
                         HiResDate theTime)
    {
      _theTimeString = theTimeString;
      _theTime = theTime;
      _theChanges = new java.util.Vector(0,1);
    }

    public final void addAction(FixWrapper fw,
                          String newLabel,
                          String oldLabel)
    {
      // add the item to our list
      _theChanges.addElement(new someUpdate(newLabel,
                                            oldLabel,
                                            fw));
    }

    /** specify is this is an operation which can be undone
     */
    public final boolean isUndoable()
    {
      return true;
    }

    /** specify is this is an operation which can be redone
     */
    public final boolean isRedoable()
    {
      return true;
    }

    /** return string describing this operation
     * @return String describing this operation
     */
    public final String toString()
    {
      return "Set times to t0 at " + _theTimeString;
    }

    /** take the shape away from the layer
     */
    public final void undo()
    {
      // work back through the list, removing the updates
      // work through the list, making the changes
      Enumeration iter = _theChanges.elements();
      while(iter.hasMoreElements())
      {
        someUpdate sm = (someUpdate)iter.nextElement();
        sm.theFix.setLabel(sm.oldVal);
      }

      setTimeZero(null);
    }

    /** make it so!
     */
    public final void execute()
    {
      // work through the list, making the changes
      Enumeration iter = _theChanges.elements();
      while(iter.hasMoreElements())
      {
        someUpdate sm = (someUpdate)iter.nextElement();
        sm.theFix.setLabel(sm.newVal);
      }

      setTimeZero(_theTime);
    }

    private final class someUpdate
    {
      public final String newVal;
      public final String oldVal;
      public final FixWrapper theFix;
      public someUpdate(String theNewVal,
                        String theOldVal,
                        FixWrapper theFixVal)
      {
        newVal = theNewVal;
        oldVal = theOldVal;
        theFix = theFixVal;
      }
    }
  }

}

