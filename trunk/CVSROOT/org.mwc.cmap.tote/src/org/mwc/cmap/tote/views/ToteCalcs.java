package org.mwc.cmap.tote.views;

import java.awt.Container;
import java.awt.event.*;
import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.mwc.cmap.core.property_support.ColorHelper;

import Debrief.Tools.Tote.*;
import Debrief.Tools.Tote.Calculations.*;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.*;
import MWC.GenericData.HiResDate;

abstract public class ToteCalcs
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
  protected final Vector _theSecondary;

  /**
   * the current time
   */
  private HiResDate _theCurrentTime;

  /**
   * the list of types of calculations we want to do
   */
  protected final Vector _theCalculationTypes;

  /**
   * the list of calculations we are actually doing
   */
  protected final Vector _theCalculations;

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
  public ToteCalcs()
  {
    _theSecondary = new Vector(0, 1);

    _theCalculationTypes = new Vector(0, 1);

    _theCalculations = new Vector(0, 1);

    addCalculations();
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  private void addCalculations()
  {
    _theCalculationTypes.addElement(rangeCalc.class);
    _theCalculationTypes.addElement(bearingCalc.class);
    _theCalculationTypes.addElement(relBearingCalc.class);
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
    final Vector secWatch = new Vector(0, 1);
    final Enumeration secs = _theSecondary.elements();
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
    final Enumeration calcLabels = _theCalculations.elements();

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

    updateToteMembers();
  }

  /**
   * return the list of secondary tracks for the tote
   */
  public final Vector getSecondary()
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
    updateToteMembers();
  }

  /**
   * assign the secondary track for the tote
   */
  protected final void removeParticipant(final WatchableList theList)
  {
    // there isn't a remove button for the primary track,
    // so the user must have clicked on the secondary
    _theSecondary.removeElement(theList);

    // and update the screen
    updateToteMembers();
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


  /**
   * return the current time
   */
  protected final HiResDate getCurrentTime()
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
        final Enumeration secs = _theSecondary.elements();
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
  protected final void assignWatchables(boolean onlyAssignTracks)
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
          final Enumeration iter = layer.elements();
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
    final Enumeration iter = _theSecondary.elements();
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

    // and the local parameters
    _theCalculations.clear();
    _theCalculationTypes.clear();
    _theData = null;
    _thePrimary = null;
    _theSecondary.clear();
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



  ////////////////////////////////////////////////////////////
  // nested class to put a calculation into a label
  ////////////////////////////////////////////////////////////
  final class calcHolder extends sizeableJLabel implements toteCalculation
  {
    final toteCalculation _myCalc;
    final String _myName;
    java.awt.Color _myColor = null;

    public calcHolder(Composite parent, final toteCalculation calc, final String name)
    {
      super(parent);
      super.setForeground(ColorHelper.getColor(java.awt.Color.black));
      _myCalc = calc;
      _myName = name;
      setText("---");
    }

    public final void setColor(final java.awt.Color theCol)
    {
      _myColor = theCol;
    }

    public final double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime)
    {
      return _myCalc.calculate(primary, secondary, thisTime);
    }

    public final String update(final Watchable primary, final Watchable secondary, final HiResDate thisTime)
    {
      final String val = " " + _myCalc.update(primary, secondary, thisTime);
      setText(val);
      return val;
    }

    public final void setPattern(final java.text.NumberFormat format)
    {
      _myCalc.setPattern(format);
    }

    public final String getTitle()
    {
      return _myCalc.getTitle();
    }

    public final String getUnits()
    {
      return _myCalc.getUnits();
    }

    /**
     * does this calculation require special bearing handling (prevent wrapping through 360 degs)
     */
    public final boolean isWrappableData()
    {
      return false;
    }

    public final void paint(final java.awt.Graphics p1)
    {
    	// todo: sort out color-coding the results data
//      // paint the text
//      super.paint(p1);
//
//      java.awt.Color oldCol = null;
//
//      if (_myColor != null)
//      {
//        oldCol = p1.getColor();
//        p1.setColor(_myColor);
//      }
//
//      // and paint the border
//      final java.awt.Dimension sz = this.getSize();
//      p1.drawRect(1, 1, sz.width - 2, sz.height - 2);
//
//      if (oldCol != null)
//      {
//        p1.setColor(oldCol);
//      }

    }
  }

  ////////////////////////////////////////////////////
  // nested class for button to remove a tote participant
  ////////////////////////////////////////////////////
  final class removeMe extends sizeableJButton
  {
    final WatchableList _thisL;

    public removeMe(Composite parent, final WatchableList theList)
    {
      super(parent, "Remove this track");
      _thisL = theList;
//      this.addActionListener(this);
//      super.setIcon(_crossIcon);
      this.addSelectionListener(new SelectionListener(){

				public void widgetSelected(SelectionEvent e)
				{
					removeParticipant(_thisL);
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
					// TODO Auto-generated method stub
					
				}});
    }
  }

  ////////////////////////////////////////////////////
  // nested class for button to copy the values in a tote member
  ////////////////////////////////////////////////////
  final class copyMe extends sizeableJButton implements ActionListener
  {
    final WatchableList _thisL;
    final WatchableList _thePrimary;
    final Vector _theCalculationTypes;

    public copyMe(Composite parent,
                  final WatchableList theList,
                  final WatchableList thePrimary, final Vector theCalculationTypes)
    {
      super(parent, "copy details to clipboard");
      super.setText("Copy Details");
      _thisL = theList;
//      setIcon(_copyIcon);
      _thePrimary = thePrimary;
      _theCalculationTypes = theCalculationTypes;
      this.addSelectionListener(new SelectionListener(){

				public void widgetSelected(SelectionEvent e)
				{
					 // get the current time from the parent
		      final HiResDate theCurrentTime = getCurrentTime();
		      String res = "";

		      // get the nearest watchable
		      Watchable list[] = _thePrimary.getNearestTo(theCurrentTime);

		      Watchable priW = null;
		      if (list.length > 0)
		        priW = list[0];

		      list = _thisL.getNearestTo(theCurrentTime);
		      Watchable secW = null;
		      if (list.length > 0)
		        secW = list[0];

		      // step through the calculations, getting the titles
		      final Enumeration enumT = _theCalculationTypes.elements();
		      while (enumT.hasMoreElements())
		      {
		        try
		        {

		          final Class cl = (Class) enumT.nextElement();
		          final toteCalculation ts = (toteCalculation) cl.newInstance();
		          res += ts.getTitle() + ", ";
		        }
		        catch (Exception t)
		        {
		          t.printStackTrace();
		        }
		      }


		      // add a new-line
		      res += System.getProperties().getProperty("line.separator");

		      // step through the calculations, getting the results
		      final Enumeration enum1 = _theCalculationTypes.elements();
		      while (enum1.hasMoreElements())
		      {
		        try
		        {
		          final Class cl = (Class) enum1.nextElement();
		          final toteCalculation ts = (toteCalculation) cl.newInstance();
		          final String thisV = ts.update(priW, secW, theCurrentTime);

		          res += thisV + ", ";

		        }
		        catch (Exception f)
		        {
		          f.printStackTrace();
		        }

		      }

		      // put the result on the clipboard
		      final java.awt.datatransfer.Clipboard cl = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
		      final java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(res);
		      cl.setContents(ss, ss);
					
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}});
    }

    public final void actionPerformed(final ActionEvent e)
    {

     
    }

  }


  //////////////////////////////////////////////
  // nested class, a JLabel class which uses
  // the font size designator included from the parent
  ///////////////////////////////////////////////
  protected class sizeableJButton extends Button
  {
    public sizeableJButton(Composite parent, final String val)
    {
      super(parent, SWT.NONE);
      super.setText(val);
      // and set the font data
//      final java.awt.Font myFont = getFont();
//      final java.awt.Font newFont = new java.awt.Font("Sans Serif", myFont.getStyle(),
//                                                      _fontSize);
//      setFont(newFont);
    }

    public sizeableJButton(Composite parent, final String label, final String icon)
    {
      super(parent, SWT.NONE);
      // sort out the icon
      this.setToolTipText(label);
//      this.setMargin(new java.awt.Insets(0, 0, 0, 0));
    }

    public final void setIcon(final String icon)
    {
//      this.setMargin(new java.awt.Insets(0, 0, 0, 0));
//      super.setIcon(icon);
//      if (icon != null)
//      {
//        super.setToolTipText(super.getText());
//        super.setText(null);
 //     }
    }
  }

  //////////////////////////////////////////////
  // nested class, a JLabel class which uses
  // the font size designator included from the parent
  ///////////////////////////////////////////////
  protected class sizeableJLabel extends Label
  {
    private final static int _trimmedLength = 7;

    //////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////
    public sizeableJLabel(Composite parent)
    {
      this(parent, null);
    }

    public sizeableJLabel(Composite parent, final String val)
    {
      this(parent, val, false, false);
    }

    public sizeableJLabel(Composite parent, final String val, final boolean smallLabel, final boolean trimLabels)
    {
    	super(parent, SWT.NONE);
      String lblText = val;

      if (trimLabels && val.length() > _trimmedLength)
      {
        // ok, store a shortened version of the string
        lblText = val.substring(0, _trimmedLength);

        // and set the full lengh label in the tooltext
        super.setToolTipText(val);

      }

      // ok, set the short text label
      super.setText(lblText);

      // and set the font data
      // todo: sort out this font sizing.
//      final java.awt.Font myFont = getFont();
//      java.awt.Font newFont = new java.awt.Font("Sans Serif",
//                                                myFont.getStyle(),
//                                                _fontSize);
//
//      // do we shrink it?
//      if (smallLabel)
//      {
//        newFont = newFont.deriveFont(newFont.getSize2D() - 1);
//      }
//
//      setFont(newFont);
    }
  }

  /**
   * embedded class to represent an updateable text label which shows the units of a tote calculation.
   */
  private class UnitLabel extends sizeableJLabel
  {
    /**
     * the calculation we show.
     */
    private final toteCalculation _myCalc;

    /**
     * constructor to setup our label.
     * @param parent TODO
     * @param calc       the calculation we show
     * @param smallLabel whether the font should be small
     * @param trimLabels whether to trim the text
     */
    public UnitLabel(Composite parent, toteCalculation calc, final boolean smallLabel, final boolean trimLabels)
    {
      super(parent, calc.getUnits(), smallLabel, trimLabels);
      _myCalc = calc;
    }

    /**
     * method to update the display of units (particularly for when we change range units).
     */
    public void updateUnits()
    {
      super.setText(_myCalc.getUnits());
    }
  }	
	
}
