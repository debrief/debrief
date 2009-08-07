// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AnalysisTote.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: AnalysisTote.java,v $
// Revision 1.7  2006/11/13 12:33:17  Ian.Mayo
// Only try to update the stepper if we've actually got time data.
//
// Revision 1.6  2004/11/25 10:24:10  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.5  2004/11/22 13:40:53  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.4  2004/09/09 10:22:56  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.3  2004/07/22 13:32:28  Ian.Mayo
// Add functionality to allow just tracks to be added to the tote
//
// Revision 1.2  2004/07/06 09:16:29  Ian.Mayo
// Constrain number of secondary tracks loaded in auto-generate
//
// Revision 1.1.1.2  2003/07/21 14:47:13  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.10  2003-07-01 09:51:54+01  ian_mayo
// More label-list retrieval to the correct point in code (tidying up)
//
// Revision 1.9  2003-06-30 13:52:08+01  ian_mayo
// Don't make it final, so we can over-ride it in our Swing implementation
//
// Revision 1.8  2003-05-14 16:11:08+01  ian_mayo
// corrections to how we move through labels
//
// Revision 1.7  2003-03-19 15:38:09+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.6  2002-09-24 10:54:38+01  ian_mayo
// Only try to remove secondary items, since only they have the Cut button
//
// Revision 1.5  2002-07-10 14:59:14+01  ian_mayo
// handle correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.4  2002-07-02 09:13:09+01  ian_mayo
// Add time in seconds
//
// Revision 1.3  2002-06-12 13:58:26+01  ian_mayo
// With only one secondary track, let primary show data relative to it
//
// Revision 1.2  2002-05-28 12:27:55+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:04+01  ian_mayo
// Initial revision
//
// Revision 1.6  2002-02-18 12:33:12+00  administrator
// Provide access to the GUI component
//
// Revision 1.5  2002-01-24 14:23:38+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.4  2002-01-17 15:03:44+00  administrator
// Reflect new interface to hide StepperListener class
//
// Revision 1.3  2001-10-01 12:49:50+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.2  2001-08-21 12:15:59+01  administrator
// Improve tidying
//
// Revision 1.1  2001-08-17 08:01:39+01  administrator
// Clear up memory leaks
//
// Revision 1.0  2001-07-17 08:41:40+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-21 21:34:28+00  novatech
// process addListener to help monitor colours in Tote
//
// Revision 1.1  2001-01-03 13:40:54+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:18  ianmayo
// initial import of files
//
// Revision 1.20  2000-11-08 11:47:58+00  ian_mayo
// reflect restructuring of TrackWrapper vs Layer
//
// Revision 1.19  2000-11-02 16:45:49+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer, also changed TrackWrapper so that it implements Layer,  and as we read in files, we put them into track and add Track to Layers, not to Layer then Layers
//
// Revision 1.18  2000-10-24 11:21:59+01  ian_mayo
// provide convenience method to allow access to the default highlighter in the tote
//
// Revision 1.17  2000-10-03 14:16:03+01  ian_mayo
// return the current highlgihter
//
// Revision 1.16  2000-09-26 09:53:10+01  ian_mayo
// extra support for RelativePlot (provide details of current status of primary track)
//
// Revision 1.15  2000-09-14 10:28:49+01  ian_mayo
// pass time to calculations
//
// Revision 1.14  2000-08-21 15:29:40+01  ian_mayo
// only make a track the secondary track if it isn't the primary
//
// Revision 1.13  2000-08-14 11:00:08+01  ian_mayo
// add new calculations
//
// Revision 1.12  2000-05-19 11:24:45+01  ian_mayo
// pass undoBuffer around, to undo TimeFilter operations
//
// Revision 1.11  2000-04-19 11:32:08+01  ian_mayo
// implement Close method, clear local storage. Also only add new Watchable if we haven't got it already
//
// Revision 1.10  2000-04-03 10:44:54+01  ian_mayo
// whitespace only
//
// Revision 1.9  2000-03-14 09:49:54+00  ian_mayo
// create auto-assignment of tracks
//
// Revision 1.8  2000-02-02 14:28:40+00  ian_mayo
// make getStatus() public, to match parent
//
// Revision 1.7  2000-01-12 15:36:31+00  ian_mayo
// update screen when tracks added/removed
//
// Revision 1.6  1999-12-03 14:41:35+00  ian_mayo
// pass the tote to the painter
//
// Revision 1.5  1999-11-26 15:51:42+00  ian_mayo
// tidying up
//
// Revision 1.4  1999-11-18 11:16:10+00  ian_mayo
// getPanel now returns Container, not Panel
//
// Revision 1.3  1999-10-21 10:17:01+01  ian_mayo
// Handle instance where watchable is not time related
//
// Revision 1.2  1999-10-15 12:37:17+01  ian_mayo
// improved management of watchables which don't hve time periods
//
// Revision 1.1  1999-10-12 15:34:23+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-04 10:53:02+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-12 08:09:23+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:19+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:09+01  sm11td
// Initial revision
//
// Revision 1.2  1999-06-16 15:34:36+01  sm11td
// <>
//
// Revision 1.1  1999-06-16 15:25:33+01  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:33:12+00  sm11td
// Initial revision
//


package Debrief.GUI.Tote;

import Debrief.Tools.Tote.Calculations.*;
import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.WatchableList;
import Debrief.Tools.Tote.toteCalculation;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.*;
import MWC.GenericData.HiResDate;

import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * parent class for analysis totes
 */
abstract public class AnalysisTote implements Pane,
  StepperListener,
  MWC.Algorithms.PlainProjection.RelativeProjectionParent,
  MWC.GUI.Layers.DataListener
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * the primary track we are watching
   */
  protected WatchableList _thePrimary;

  /**
   * the list of secondary tracks we are watching
   */
  protected final Vector<WatchableList> _theSecondary;

  /**
   * the stepping control we are watching
   */
  private StepControl _theStepper;

  /**
   * the current time
   */
  private HiResDate _theCurrentTime;

  /**
   * the list of types of calculations we want to do
   */
  protected final Vector<Class<?>> _theCalculationTypes;

  /**
   * the list of calculations we are actually doing
   */
  protected final Vector<toteCalculation> _theCalculations;

  /**
   * the current set of data (used for auto assignment of tracks)
   */
  private Layers _theData;


  /**
   * set a limit for the maximum number of secondary tracks we will plot
   */
  private static final int MAX_SECONDARIES = 10;

  /**
   * and the message to display
   */
  private static final String MAX_MESSAGE = "Too many tracks.  Only the first " + MAX_SECONDARIES + " secondary tracks have been assigned";


  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public AnalysisTote(final Layers theData)
  {
    _theSecondary = new Vector<WatchableList>(0, 1);

    _theCalculationTypes = new Vector<Class<?>>(0, 1);

    _theCalculations = new Vector<toteCalculation>(0, 1);

    addCalculations();

    _theData = theData;

    _theData.addDataReformattedListener(this);
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  private void addCalculations()
  {
    _theCalculationTypes.addElement(rangeCalc.class);
    _theCalculationTypes.addElement(bearingCalc.class);
    _theCalculationTypes.addElement(relBearingCalc.class);
    _theCalculationTypes.addElement(atbCalc.class);
    _theCalculationTypes.addElement(speedCalc.class);
    _theCalculationTypes.addElement(courseCalc.class);
    _theCalculationTypes.addElement(depthCalc.class);
    _theCalculationTypes.addElement(bearingRateCalc.class);
    _theCalculationTypes.addElement(timeSecsCalc.class);

  }

  /**
   * work through out items, and update the calculations to reflect the new "time" selected
   */
  protected void updateToteInformation()
  {

    // check that we've got a primary
    if (_thePrimary == null)
      return;

    // so, we the calculations have been added to the tote list
    // in order going across the page

    // get the primary ready,
    Watchable[] list = _thePrimary.getNearestTo(_theCurrentTime);
    Watchable pw = null;
    if (list.length > 0)
      pw = list[0];

    // prepare the list of secondary watchables
    final Vector<Watchable> secWatch = new Vector<Watchable>(0, 1);
    final Enumeration<WatchableList> secs = _theSecondary.elements();
    while (secs.hasMoreElements())
    {
      final WatchableList wl = (WatchableList) secs.nextElement();

      list = wl.getNearestTo(_theCurrentTime);

      Watchable nearest = null;
      if (list.length > 0)
        nearest = list[0];
      secWatch.addElement(nearest);
    }

    // get our list of calcs to be updated
    final Enumeration<toteCalculation> calcLabels = _theCalculations.elements();

    // so, we have to go across the table first
    while (calcLabels.hasMoreElements())
    {

      // primary first
      toteCalculation tc = (toteCalculation) calcLabels.nextElement();

      // special case - where there is only one secondary track, let the primary
      // track show data relative to it
      Watchable nearSec = null;
      if (_theSecondary.size() == 1)
      {
        final WatchableList secV = (WatchableList) _theSecondary.get(0);
        final Watchable[] nearSecs = secV.getNearestTo(_theCurrentTime);
        if (nearSecs.length > 0)
        {
          nearSec = nearSecs[0];
        }
      }

      tc.update(nearSec, pw, _theCurrentTime);

      // and the secondaries
      for (int i = 0; i < _theSecondary.size(); i++)
      {

        tc = (toteCalculation) calcLabels.nextElement();
        final Watchable nearestSecondary = (Watchable) secWatch.elementAt(i);
        tc.update(pw, nearestSecondary, _theCurrentTime);
      }
    }

  }

  /**
   * get the primary track for this tote
   */
  public final WatchableList getPrimary()
  {
    return _thePrimary;
  }

  /**
   * assign the primary track for the tote
   */
  public final void setPrimary(final WatchableList theList)
  {
    _thePrimary = theList;
    /** see if this item is time related
     */
    final HiResDate val = theList.getStartDTG();
    if (val != null)
    {
      _theStepper.addParticipant(theList,
                                 theList.getStartDTG(),
                                 theList.getEndDTG());
    }
    updateToteMembers();

    // hmm, do we have a current time?
    HiResDate cTime = getStepper().getCurrentTime();
    if(cTime != null)
    {
    	// yup, go for it.
    	getStepper().changeTime(cTime);
    }
  }

  /**
   * return the list of secondary tracks for the tote
   */
  public final Vector<WatchableList> getSecondary()
  {
    return _theSecondary;
  }

  /**
   * assign the secondary track for the tote
   */
  public final void setSecondary(final WatchableList theList)
  {
    // check that this list isn't our primary track
    if (theList == _thePrimary)
      return;

    // add to our list of secondary items
    _theSecondary.addElement(theList);

    /** see if this item is time related
     */
    final HiResDate val = theList.getStartDTG();
    if (val != null)
    {
      _theStepper.addParticipant(theList,
                                 theList.getStartDTG(),
                                 theList.getEndDTG());
    }
    updateToteMembers();
    
    // hmm, do we have a current time?
    HiResDate cTime = getStepper().getCurrentTime();
    if(cTime != null)
    {
    	// yup, go for it.
    	getStepper().changeTime(cTime);
    }
  }

  /**
   * assign the secondary track for the tote
   */
  public final void removeParticipant(final WatchableList theList)
  {
    // there isn't a remove button for the primary track,
    // so the user must have clicked on the secondary
    _theSecondary.removeElement(theList);

    // update the time period of the stepper
    _theStepper.removeParticpant(theList);

    // and update the screen
    updateToteMembers();
  }

  /**
   * set the step-producer which we are listening to
   */
  protected final void setStepper(final StepControl stepper)
  {
    _theStepper = stepper;
    stepper.addStepperListener(this);
  }

  /**
   * rebuild the list of members of the tote
   */
  abstract protected void updateToteMembers();

  public final void steppingModeChanged(final boolean on)
  {
    // not really interested, to be honest
  }

  public final void newTime(final HiResDate oldDTG, final HiResDate newDTG, final CanvasType canvas)
  {
    _theCurrentTime = newDTG;
    updateToteInformation();
  }

  // data called from the Pane parent
  public final void update()
  {
    updateToteInformation();
  }

  public final StepControl getStepper()
  {
    return _theStepper;
  }

  /**
   * retrieve the GUI component implementing this tote
   */
  abstract public Container getPanel();

  /**
   * return the current time
   */
  public final HiResDate getCurrentTime()
  {
    return _theCurrentTime;
  }


  /**
   * @param list             the list of items to process
   * @param onlyAssignTracks whether only TrackWrapper items should be placed on the list
   */
  private void processWatchableList(final WatchableList list, boolean onlyAssignTracks)
  {
    // check this isn't the primary
    if (list != getPrimary())
    {
      final WatchableList w = (WatchableList) list;
      // see if we need a primary setting
      if (getPrimary() == null)
      {
        if (w.getVisible())
          if ((!onlyAssignTracks) || (onlyAssignTracks) && (w instanceof TrackWrapper))
            setPrimary(w);
      }
      else
      {

        boolean haveAlready = false;

        // check that this isn't one of our secondaries
        final Enumeration<WatchableList> secs = _theSecondary.elements();
        while (secs.hasMoreElements())
        {
          final WatchableList secW = (WatchableList) secs.nextElement();
          if (secW == w)
          {
            // don't bother with it, we've got it already
            haveAlready = true;
            continue;
          }
        }

        if (!haveAlready)
        {
          if (w.getVisible())
            if ((!onlyAssignTracks) || (onlyAssignTracks) && (w instanceof TrackWrapper))
              setSecondary(w);
        }
      }

    }

  }


  /**
   * automatically pass through the data, and automatically assign the relevant watchable items to primary, secondary,
   * etc.
   *
   * @param onlyAssignTracks - as we scan through the layers, only put TrackWrappers onto the tote
   */
  public final void assignWatchables(boolean onlyAssignTracks)
  {
    // check we have some data to search
    if (_theData != null)
    {

      // pass through the data to find the WatchableLists
      for (int l = 0; l < _theData.size(); l++)
      {
        final Layer layer = _theData.elementAt(l);

        if (layer instanceof WatchableList)
        {
          // have we got our full set of secondarires yet?
          if (this._theSecondary.size() >= MAX_SECONDARIES)
          {
            MWC.GUI.Dialogs.DialogFactory.showMessage("Secondary limit reached", MAX_MESSAGE);
            return;
          }

          processWatchableList((WatchableList) layer, onlyAssignTracks);
        }
        else
        {
          final Enumeration<Editable> iter = layer.elements();
          while (iter.hasMoreElements())
          {
            final Plottable p = (Plottable) iter.nextElement();
            if (p instanceof WatchableList)
            {

              // have we got our full set of secondarires yet?
              if (this._theSecondary.size() >= MAX_SECONDARIES)
              {
                MWC.GUI.Dialogs.DialogFactory.showMessage("Secondary limit reached", MAX_MESSAGE);
                return;
              }

              processWatchableList((WatchableList) p, onlyAssignTracks);
            }
          }
        }
      }
    }
  }

  /**
   * get ready to close
   */
  public void closeMe()
  {
    // remove the secondaries
    final Enumeration<WatchableList> iter = _theSecondary.elements();
    while (iter.hasMoreElements())
    {
      final WatchableList wl = (WatchableList) iter.nextElement();
      removeParticipant(wl);
    }

    // lastly remove the primary
    if (getPrimary() != null)
      removeParticipant(getPrimary());

    // clear the GUI
    updateToteMembers();

    // stop listening for data reformatting
    _theData.removeDataReformattedListener(this);

    // ditch the stepper
    _theStepper.closeMe();

    // and the local parameters
    _theCalculations.clear();
    _theCalculationTypes.clear();
    _theData = null;
    _thePrimary = null;
    _theSecondary.clear();
    _theStepper = null;
  }

  /////////////////////////////////////////////////////////////////
  // accessor methods to fulfil responsiblities of RelativeProjectionParent
  /////////////////////////////////////////////////////////////////
  /**
   * return the current DTG
   */
  private HiResDate getDTG()
  {
    return _theCurrentTime;
  }

  /**
   * accessor method for the current highlighter (retrieve it from the stepper)
   */
  public final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter getCurrentHighlighter()
  {
    return _theStepper.getCurrentHighlighter();
  }

  public final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter getDefaultHighlighter()
  {
    return _theStepper.getDefaultHighlighter();
  }


  private Watchable getCurrentPrimary()
  {
    Watchable res = null;

    if (_thePrimary != null)
    {
      final Watchable[] list = _thePrimary.getNearestTo(getDTG());
      if (list.length > 0)
        res = list[0];
    }

    return res;
  }

  /**
   * return the current heading
   */
  public final double getHeading()
  {
    double res = 0;
    final Watchable cur = getCurrentPrimary();
    if (cur != null)
    {
      res = cur.getCourse();
    }
    return res;
  }

  /**
   * return the current origin for the plot
   */
  public final MWC.GenericData.WorldLocation getLocation()
  {
    MWC.GenericData.WorldLocation res = null;
    final Watchable cur = getCurrentPrimary();
    if (cur != null)
    {
      res = cur.getBounds().getCentre();
    }
    return res;
  }

  /**
   * some part of the data has been modified (not necessarily formatting though)
   *
   * @param theData the Layers containing the item of data which has been modified
   */
  public final void dataModified(final Layers theData, final Layer changedLayer)
  {
  }

  /**
   * a new piece of data has been edited
   *
   * @param theData the Layers which have had something edited
   */
  public final void dataExtended(final Layers theData)
  {
  }

  /**
   * some kind of formatting has been applied
   *
   * @param theData the Layers containing the data which has been reformatted
   */
  public final void dataReformatted(final Layers theData, final Layer changedLayer)
  {
    // we should redraw the tote members, to that any colour changes can be reflectedd
    updateToteMembers();
  }

}


