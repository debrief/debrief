package Debrief.Tools.FilterOperations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CopyTimeDataToClipboard.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: CopyTimeDataToClipboard.java,v $
// Revision 1.6  2006/07/25 14:50:21  Ian.Mayo
// Strip line-breaks from displayed annotation names
//
// Revision 1.5  2006/06/01 10:58:07  Ian.Mayo
// Make class over-rideable
//
// Revision 1.4  2006/01/10 09:21:17  Ian.Mayo
// Don't clear the clipboard as we start
//
// Revision 1.3  2005/01/24 11:03:17  Ian.Mayo
// Add new tidier text formatter
//
// Revision 1.2  2004/11/25 10:24:26  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:48:22  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.8  2003-05-13 16:04:43+01  ian_mayo
// Add colour calculation value when exporting to clipboard
//
// Revision 1.7  2003-03-25 15:54:16+00  ian_mayo
// Implement "Reset me" buttons
//
// Revision 1.6  2003-03-19 15:37:03+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.5  2003-02-25 14:36:25+00  ian_mayo
// Just use \n as line-break when writing to clipboard
//
// Revision 1.4  2003-02-07 09:02:37+00  ian_mayo
// Remove unnecessary
//
// Revision 1.3  2002-07-10 14:59:23+01  ian_mayo
// handle correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.2  2002-05-28 12:28:20+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:59+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-10-01 12:49:49+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.0  2001-07-17 08:41:22+01  administrator
// Initial revision
//
// Revision 1.5  2001-05-09 06:20:11+01  novatech
// handle the fact that we may not have primary data for the indicated time value - print "n/a" if this happens.
//
// Revision 1.4  2001-01-17 09:47:59+00  novatech
// remove unnecessary import statements
//
// Revision 1.3  2001-01-09 11:17:01+00  novatech
// output the "name" columns
//
// Revision 1.2  2001-01-09 10:30:39+00  novatech
// WatchableLists to be used instead of TrackWrappers
//
// Revision 1.1  2001-01-03 13:40:34+00  novatech
// Initial revision
//
// Revision 1.5  2000-09-14 10:28:09+01  ian_mayo
// extended variety of calculations & removed nested calcs
//
// Revision 1.4  2000-08-14 11:01:39+01  ian_mayo
// tidy up description
//
// Revision 1.3  2000-08-09 16:03:57+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.2  2000-08-07 14:22:24+01  ian_mayo
// added VCS headers
//

import Debrief.Tools.Tote.Calculations.*;
import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.WatchableList;
import Debrief.Tools.Tote.toteCalculation;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;

import javax.swing.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.Iterator;


public class CopyTimeDataToClipboard implements FilterOperation, ClipboardOwner
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /**
   * the period this operation covers
   */
  private HiResDate _start_time = null;
  private HiResDate _end_time = null;

  /**
   * the tracks we should cover
   */
  private java.util.Vector<WatchableList> _theTracks = null;

  /**
   * the primary track for the relative parameters
   */
  private Debrief.Tools.Tote.WatchableList _thePrimary = null;

  // the line-break character
  private final String _theSeparator = "\n";

  /**
   * the tab character
   */
  private final char tab = '\t';

  /**
   * the list of calculations we know how to perform
   */
  private final java.util.Vector<plainCalc> _theOperations;

  ///////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public CopyTimeDataToClipboard()
  {
    _theOperations = new java.util.Vector<plainCalc>(0, 1);

    _theOperations.addElement(new timeCalc());
    _theOperations.addElement(new tidyTimeCalc());
    _theOperations.addElement(new depthCalc());
    _theOperations.addElement(new speedCalc());
    _theOperations.addElement(new courseCalc());
    _theOperations.addElement(new rangeCalc());
    _theOperations.addElement(new bearingCalc());
    _theOperations.addElement(new relBearingCalc());
    _theOperations.addElement(new bearingRateCalc());
    _theOperations.addElement(new colorCalc());
  }

  public final String getDescription()
  {
    String res = "2. Select tracks to be recorded";
    res += _theSeparator + "3. Select time period to cover";
    res += _theSeparator + "4. Press 'Apply' button";
    res += _theSeparator + "5. Select primary track when requested";
    res += _theSeparator + "The data will appear in comma-separated form on the clipboard";
    return res;
  }

  public final void setPeriod(HiResDate startDTG, HiResDate finishDTG)
  {
    _start_time = startDTG;
    _end_time = finishDTG;
  }

  public final void setTracks(java.util.Vector<WatchableList> selectedTracks)
  {
    _theTracks = selectedTracks;
  }

  /**
   * the user has pressed RESET whilst this button is pressed
   *
   * @param startTime the new start time
   * @param endTime   the new end time
   */
  public void resetMe(HiResDate startTime, HiResDate endTime)
  {
  }

  public final void execute()
  {
  }

  public WatchableList getPrimary()
  {
    WatchableList res = null;

    // check we have some tracks selected
    if (_theTracks != null)
    {
      Object[] opts = new Object[_theTracks.size()];
      _theTracks.copyInto(opts);
      res = (WatchableList) JOptionPane.showInputDialog(null,
                                                        "Which is the primary track?",
                                                        "Copy time variables",
                                                        JOptionPane.QUESTION_MESSAGE,
                                                        null,
                                                        opts,
                                                        null);
    }
    else
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Track Selector",
                                                "Please select one or more tracks");
    }
    return res;
  }

	public final MWC.GUI.Tools.Action getData()
  {
    // retrieve the necessary input data
    _thePrimary = getPrimary();

    // check it worked
    if (_thePrimary != null)
    {

      // the big string we are writing into
      StringBuffer str = new StringBuffer(2000);

      // test string

      // produce the header line
      str.append("Track" + tab);

      java.util.Enumeration<plainCalc> it = _theOperations.elements();
      while (it.hasMoreElements())
      {

        toteCalculation cl = it.nextElement();
        str.append(cl.getTitle() + "(" + cl.getUnits() + ")" + tab);
      }

      // add the title for the 'name' column
      str.append("Name" + tab);

      // add the title for the 'primary' column
      str.append("PrimaryName" + tab);

      // and finish the header line
      str.append(_theSeparator);

      // now produce the rows of data themselves

      // sort the data to the correct time period
      java.util.Enumeration<WatchableList> er = _theTracks.elements();
      while (er.hasMoreElements())
      {
        WatchableList tw = er.nextElement();
        Collection<Editable> ss = tw.getItemsBetween(_start_time, _end_time);

        Iterator<Editable> ss_it = ss.iterator();
        while (ss_it.hasNext())
        {
          // put in the boat name
          str.append(tidyUp(tw.getName()) + tab);

          // and move through the fixes in the valid period
          Watchable fw = (Watchable) ss_it.next();

          // find the fix on the primary track which is nearest in
          // time to this one
          Watchable[] list = _thePrimary.getNearestTo(fw.getTime());

          Watchable primary = null;
          if (list.length > 0)
            primary = list[0];

          java.util.Enumeration<plainCalc> ops = _theOperations.elements();
          while (ops.hasMoreElements())
          {
            toteCalculation cv = ops.nextElement();
            str.append(cv.update(primary, fw, fw.getTime()) + tab);
          }

          // append the name of the nearest item in this list
          str.append(tidyUp(fw.getName()) + tab);

          // append the name of the primary item we are being calculated against
          if (primary != null)
            str.append(primary.getName() + tab);
          else
            str.append("n/a" + tab);

          // and onto the next line
          str.append(_theSeparator);
        }
      }

      // create the clipboard buffer
      Clipboard clip = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();

      // put the string in a holder
      StringSelection sel = new java.awt.datatransfer.StringSelection(str.toString());

      // and put it on the clipboard
      clip.setContents(sel, this);

    } // whether a valid value was returned

    // return the new action
    return null;
  }

  private static String tidyUp(String txt)
  {
  	txt = txt.replace('\n', ' ');
  	return txt;
  }
  
  public final String getLabel()
  {
    return "Copy data to the clipboard";
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
  // nested classes
  ///////////////////////////////////////////////////////


  public final void lostOwnership(Clipboard clipboard, Transferable contents)
  {
    // don't bother really
  }
}

