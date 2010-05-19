// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingTote.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.9 $
// $Log: SwingTote.java,v $
// Revision 1.9  2006/01/18 15:04:03  Ian.Mayo
// Show interpolated calcs in italics
//
// Revision 1.8  2005/12/13 09:04:29  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.7  2004/11/26 11:37:46  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.6  2004/11/25 10:24:09  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.5  2004/11/22 13:40:52  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.4  2004/08/05 10:50:52  Ian.Mayo
// Allow slightly longer labels when lots of tracks on tote
//
// Revision 1.3  2004/08/02 13:57:22  Ian.Mayo
// If text label for pri/sec track too large then shorten, but place real label in tooltip
//
// Revision 1.2  2004/07/22 13:32:58  Ian.Mayo
// Add functionality to allow user to ask for just tracks to be placed on the tote
//
// Revision 1.1.1.2  2003/07/21 14:47:29  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.13  2003-07-16 12:52:58+01  ian_mayo
// Don't bother throwing error
//
// Revision 1.12  2003-07-16 12:50:24+01  ian_mayo
// Improve error comment description
//
// Revision 1.11  2003-07-16 09:36:30+01  ian_mayo
// Insert error handler to trap problem with loading tracks into tote
//
// Revision 1.10  2003-06-30 13:56:24+01  ian_mayo
// Amend so that the units label is updated after every update.
//
// Revision 1.9  2003-03-19 15:37:51+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.8  2003-02-17 14:52:48+00  ian_mayo
// Remove unnecessary imports
//
// Revision 1.7  2003-02-14 15:28:13+00  ian_mayo
// Trim track names when necessary
//
// Revision 1.6  2003-02-10 16:27:38+00  ian_mayo
// Reflect name change of get wrappable data
//
// Revision 1.5  2003-02-07 15:36:09+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.4  2002-09-24 10:54:15+01  ian_mayo
// Clear panel before we rebuild tote items
//
// Revision 1.3  2002-07-10 14:59:21+01  ian_mayo
// handle correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.2  2002-05-28 12:28:04+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:15+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:53+01  ian_mayo
// Initial revision
//
// Revision 1.6  2002-02-19 20:22:31+00  administrator
// Set GUI component names to assist JFCUnit testing
//
// Revision 1.5  2002-02-18 20:15:37+00  administrator
// Name the auto-generate button, to assist in JFCUnit processing
//
// Revision 1.4  2002-01-22 15:29:34+00  administrator
// Reflect changed signature in Toolbar so that it can float
//
// Revision 1.3  2001-10-01 12:49:48+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.2  2001-08-21 12:15:34+01  administrator
// Improve tidying
//
// Revision 1.1  2001-08-17 08:01:56+01  administrator
// Clear up memory leaks
//
// Revision 1.0  2001-07-17 08:41:37+01  administrator
// Initial revision
//
// Revision 1.4  2001-06-14 15:43:21+01  novatech
// give the floating toolbar a name
//
// Revision 1.3  2001-01-11 11:52:01+00  novatech
// tidying up
//
// Revision 1.2  2001-01-09 10:27:11+00  novatech
// use WatchableList aswell as TrackWrapper
//
// Revision 1.1  2001-01-03 13:40:51+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:46:13  ianmayo
// initial import of files
//
// Revision 1.20  2000-11-08 11:49:21+00  ian_mayo
// tidying up
//
// Revision 1.19  2000-10-31 15:36:50+00  ian_mayo
// perform tidying up to keep JBuilder happy
//
// Revision 1.18  2000-10-09 13:37:32+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.17  2000-10-03 14:16:58+01  ian_mayo
// add missing import of Painters
//
// Revision 1.16  2000-09-14 10:47:49+01  ian_mayo
// correct syntax for calculation holder
//
// Revision 1.15  2000-09-14 10:28:25+01  ian_mayo
// pass time value to calculation
//
// Revision 1.14  2000-08-21 15:29:50+01  ian_mayo
// tidying up
//
// Revision 1.13  2000-08-16 14:12:19+01  ian_mayo
// tidy up retrieval of images
//
// Revision 1.12  2000-08-15 15:29:48+01  ian_mayo
// tidy up implementation & put remove/copy buttons alongside each other instead of on top of each other
//
// Revision 1.11  2000-08-15 08:58:29+01  ian_mayo
// reflect Bean name changes
//
// Revision 1.10  2000-08-07 14:05:24+01  ian_mayo
// correct image naming
//
// Revision 1.9  2000-08-07 12:23:19+01  ian_mayo
// tidy icon filename
//
// Revision 1.8  2000-05-19 11:24:46+01  ian_mayo
// pass undoBuffer around, to undo TimeFilter operations
//
// Revision 1.7  2000-04-03 10:44:34+01  ian_mayo
// use new constructor. we need to know where the chart is so that we can trigger updates
//
// Revision 1.6  2000-03-14 09:49:25+00  ian_mayo
// assign icons
//
// Revision 1.5  2000-03-07 14:48:14+00  ian_mayo
// optimised algorithms
//
// Revision 1.4  2000-02-21 16:38:31+00  ian_mayo
// reduced overall font sizes
//
// Revision 1.3  1999-12-03 14:41:57+00  ian_mayo
// add copy button
//
// Revision 1.2  1999-11-25 16:54:57+00  ian_mayo
// implementing Swing components
//
// Revision 1.1  1999-11-18 11:12:23+00  ian_mayo
// new Swing versions
//

package Debrief.GUI.Tote.Swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import Debrief.Tools.Tote.*;
import Debrief.Wrappers.FixWrapper;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

/**
 * Swing implementation of a tote
 */
public final class SwingTote extends Debrief.GUI.Tote.AnalysisTote
{
  private JPanel _thePanel;
  private SwingPropertiesPanel _theParent;

  // private JPanel _theStatus;
  private JPanel _theTote;
  private JPanel _toteHolder;

  private final JButton _autoGenerate;
  private final JButton _autoGenerateTracks;

  ImageIcon _crossIcon;
  ImageIcon _copyIcon;

  private final int _fontSize = 10;

  private final MyMetalToolBarUI.ToolbarOwner _owner;

  private final ToolParent _myParent;

  /**
   * the set of labels we are showing.  We remember these so that we can update them after each step - just in case the
   * units have changed
   */
  private Vector<UnitLabel> _theLabels = new Vector<UnitLabel>();


  public SwingTote(final SwingPropertiesPanel theTabPanel,
                   final MWC.GUI.Layers theData,
                   final MWC.GUI.PlainChart theChart,
                   final MWC.GUI.Undo.UndoBuffer theUndoBuffer,
                   final MyMetalToolBarUI.ToolbarOwner owner,
                   ToolParent theParent)
  {
    super(theData);

    _myParent = theParent;

    _owner = owner;

    final java.lang.ClassLoader loader = getClass().getClassLoader();

    if (loader != null)
    {

      final java.net.URL crossURL = loader.getResource("images/cut.gif");
      final java.net.URL copyURL = loader.getResource("images/copy.gif");

      if ((crossURL != null) && (copyURL != null))
      {
        _crossIcon = new ImageIcon(getClass().getClassLoader().getResource("images/cut.gif"));
        _copyIcon = new ImageIcon(getClass().getClassLoader().getResource("images/copy.gif"));
      }
    }

    // store the parent
    _theParent = theTabPanel;

    // create the panel we represent
    _thePanel = new JPanel();
    _thePanel.setName("Tote");
    _thePanel.setLayout(new java.awt.BorderLayout());

    // create the central tote portion, putting it into a toolbar
    _toteHolder = new JPanel();
    _toteHolder.setName("Calculated data");
    _toteHolder.setLayout(new java.awt.BorderLayout());
    _theTote = new JPanel();
    _toteHolder.add("North", _theTote);
    _thePanel.add("Center", _toteHolder);

    // create the stepping control
    final SwingStepControl aw = new SwingStepControl(theTabPanel, theData, theChart, theUndoBuffer, _owner, _myParent);
    _thePanel.add("North", aw.getPanel());
    setStepper(aw);


    JPanel gennyHolder = new JPanel();
    gennyHolder.setLayout(new BorderLayout());

    _autoGenerate = new JButton("Auto Generate");
    _autoGenerate.setName(_autoGenerate.getText());
    _autoGenerate.setToolTipText("Add all applicable items onto the Tote");
    _autoGenerate.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        assignWatchables(false);
      }
    });


    _autoGenerateTracks = new JButton("Auto Generate (tracks)");
    _autoGenerateTracks.setName(_autoGenerateTracks.getText());
    _autoGenerateTracks.setToolTipText("Add all Tracks onto the Tote");
    _autoGenerateTracks.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        assignWatchables(true);
      }
    });


    gennyHolder.add("East", _autoGenerate);
    gennyHolder.add("West", _autoGenerateTracks);

    _thePanel.add("South", gennyHolder);

  }

  public final java.awt.Container getPanel()
  {
    return _thePanel;
  }

  /**
   * method to clear up local properties
   */
  public final void closeMe()
  {
    // get the parent to close
    super.closeMe();

    // and clear our references
    _thePanel = null;
    _theParent = null;
    _theTote = null;
    _toteHolder = null;

    // and our lists
    _theLabels.removeAllElements();
    _theLabels = null;

  }

  private static java.awt.Color getLabelColor(final WatchableList theList)
  {
    java.awt.Color res = null;
    res = theList.getColor();
    return res;
  }


  protected final void updateToteMembers()
  {
    // clear the current list of members
    super._theCalculations.removeAllElements();

    // clear the list of labels
    _theLabels.removeAllElements();

    // and delete them from our panels
    _theTote.removeAll();

    // check we can do it properly
    if (_thePrimary == null)
      return;

    // we must now build up our grid of information

    // calculate the size that we need
    final int wid = 1 + // label
      1 + // primary
      super._theSecondary.size() + // secondaries
      1;   // units

    // now do our grid
    _theTote.setLayout(new java.awt.GridLayout(0, wid));

    // decide if we are going to trim the track names (when we have
    // more than 4 tracks
    final boolean trimLabels = (_theSecondary.size() > 3);

    // and now add the members (going across first)
    _theTote.add(new sizeableJLabel("  "));
    final JLabel lh = new sizeableJLabel(_thePrimary.getName(),
                                         false,
                                         trimLabels);
    _theTote.add(lh);

    Enumeration<WatchableList> iter2 = _theSecondary.elements();
    while (iter2.hasMoreElements())
    {
      final WatchableList w = (WatchableList) iter2.nextElement();
      final JLabel jl = new sizeableJLabel(w.getName(), false, trimLabels);
      _theTote.add(jl);
    }
    _theTote.add(new sizeableJLabel("  "));

    // and now the data for each row
    Enumeration<Class<?>> iter = _theCalculationTypes.elements();
    while (iter.hasMoreElements())
    {
      final Class<?> cl = (Class<?>) iter.nextElement();
      try
      {
        final toteCalculation tc = (toteCalculation) cl.newInstance();

        // title
        final JLabel secL = new sizeableJLabel(tc.getTitle(), true, false);
        _theTote.add(secL);

        // primary
        final calcHolder cp = new calcHolder(tc, _thePrimary.getName());
        cp.setColor(getLabelColor(_thePrimary));
        _theCalculations.addElement(cp);
        _theTote.add(cp);

        // secondaries
        final Enumeration<WatchableList> secs = _theSecondary.elements();
        while (secs.hasMoreElements())
        {
          final WatchableList wl = (WatchableList) secs.nextElement();
          final toteCalculation ts = (toteCalculation) cl.newInstance();
          final calcHolder ch = new calcHolder(ts, wl.getName());
          ch.setColor(getLabelColor(wl));
          _theCalculations.addElement(ch);
          _theTote.add(ch);
        }

        // create the text label to show the calculation units
        UnitLabel thisLabel = new UnitLabel(tc, true, false);

        // add it to the tote
        _theTote.add(thisLabel);

        // and remember it, so we can update it later
        _theLabels.add(thisLabel);
      }
      catch (Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

    }

    // put on the list of remove and copy buttons
    _theTote.add(new sizeableJLabel("  "));
    _theTote.add(new sizeableJLabel("  "));
    final Enumeration<WatchableList> secs2 = _theSecondary.elements();
    while (secs2.hasMoreElements())
    {
      final WatchableList l = (WatchableList) secs2.nextElement();

      // create a holder for the two buttons
      final JPanel holder = new JPanel();
      holder.setLayout(new java.awt.GridLayout(0, 2));
      holder.add(new removeMe(l));
      holder.add(new copyMe(l, _thePrimary, _theCalculationTypes));

      // now add the holder
      _theTote.add(holder);
    }

    // and the figures
    updateToteInformation();

    _theParent.doLayout();

  }

  /**
   * over-ride the parent method so that we can update the units on the tote.
   */
  protected void updateToteInformation()
  {
    try
    {
      // let the parent do it's business
      super.updateToteInformation();
    }
    catch (java.util.NoSuchElementException er)
    {
      // don't worry about this error -
      // - I suspect it's to do with synchronisation, adding new tracks while still trying
      // to update the tote.
    }

    // now work through the tote updating the labels
    for (int i = 0; i < _theLabels.size(); i++)
    {
      // get the next label
      UnitLabel label = (UnitLabel) _theLabels.elementAt(i);

      // and update the units its showing
      label.updateUnits();
    }
  }

  ////////////////////////////////////////////////////////////
  // nested class to put a calculation into a label
  ////////////////////////////////////////////////////////////
  final class calcHolder extends sizeableJLabel implements toteCalculation
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final toteCalculation _myCalc;
    final String _myName;
    java.awt.Color _myColor = null;

    public calcHolder(final toteCalculation calc, final String name)
    {
      setForeground(java.awt.Color.black);
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
      return _myCalc.calculate(secondary, primary, thisTime);
    }

    public final String update(final Watchable primary, final Watchable secondary, final HiResDate thisTime)
    {
      final String val = " " + _myCalc.update(primary, secondary, thisTime);
      setText(val);
      
      // just sort out if this is an interpolated fix
      if(secondary instanceof FixWrapper.InterpolatedFixWrapper)
      	this.setFont(this.getFont().deriveFont(Font.ITALIC));
      else
      	this.setFont(this.getFont().deriveFont(Font.PLAIN));
      
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
      // paint the text
      super.paint(p1);

      java.awt.Color oldCol = null;

      if (_myColor != null)
      {
        oldCol = p1.getColor();
        p1.setColor(_myColor);
      }

      // and paint the border
      final java.awt.Dimension sz = this.getSize();
      p1.drawRect(1, 1, sz.width - 2, sz.height - 2);

      if (oldCol != null)
      {
        p1.setColor(oldCol);
      }

    }
  }

  ////////////////////////////////////////////////////
  // nested class for button to remove a tote participant
  ////////////////////////////////////////////////////
  final class removeMe extends sizeableJButton implements ActionListener
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final WatchableList _thisL;

    public removeMe(final WatchableList theList)
    {
      super("Remove this track");
      _thisL = theList;
      this.addActionListener(this);
      super.setIcon(_crossIcon);
    }

    public final void actionPerformed(final ActionEvent e)
    {
      removeParticipant(_thisL);
    }

  }

  ////////////////////////////////////////////////////
  // nested class for button to copy the values in a tote member
  ////////////////////////////////////////////////////
  final class copyMe extends sizeableJButton implements ActionListener
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final WatchableList _thisL;
    final WatchableList _thePrimary1;
    final Vector<Class<?>> _theCalculationTypes1;

    public copyMe(final WatchableList theList,
                  final WatchableList thePrimary,
                  final Vector<Class<?>> theCalculationTypes)
    {
      super("copy details to clipboard");
      super.setName("Copy Details");
      _thisL = theList;
      setIcon(_copyIcon);
      _thePrimary1 = thePrimary;
      _theCalculationTypes1 = theCalculationTypes;
      this.addActionListener(this);
    }

    public final void actionPerformed(final ActionEvent e)
    {

      // get the current time from the parent
      final HiResDate theCurrentTime = getCurrentTime();
      String res = "";

      // get the nearest watchable
      Watchable list[] = _thePrimary1.getNearestTo(theCurrentTime);

      Watchable priW = null;
      if (list.length > 0)
        priW = list[0];

      list = _thisL.getNearestTo(theCurrentTime);
      Watchable secW = null;
      if (list.length > 0)
        secW = list[0];

      // step through the calculations, getting the titles
      final Enumeration<Class<?>> enumT = _theCalculationTypes1.elements();
      while (enumT.hasMoreElements())
      {
        try
        {

          final Class<?> cl = (Class<?>) enumT.nextElement();
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
      final Enumeration<Class<?>> enum1 = _theCalculationTypes1.elements();
      while (enum1.hasMoreElements())
      {
        try
        {
          final Class<?> cl = (Class<?>) enum1.nextElement();
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

  }


  //////////////////////////////////////////////
  // nested class, a JLabel class which uses
  // the font size designator included from the parent
  ///////////////////////////////////////////////
  protected class sizeableJButton extends JButton
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public sizeableJButton(final String val)
    {
      super(val);
      // and set the font data
      final java.awt.Font myFont = getFont();
      final java.awt.Font newFont = new java.awt.Font("Sans Serif", myFont.getStyle(),
                                                      _fontSize);
      setFont(newFont);
    }

    public sizeableJButton(final String label, final ImageIcon icon)
    {
      super(icon);
      this.setToolTipText(label);
      this.setMargin(new java.awt.Insets(0, 0, 0, 0));
    }

    public final void setIcon(final ImageIcon icon)
    {
      this.setMargin(new java.awt.Insets(0, 0, 0, 0));
      super.setIcon(icon);
      if (icon != null)
      {
        super.setToolTipText(super.getText());
        super.setText(null);
      }
    }
  }

  //////////////////////////////////////////////
  // nested class, a JLabel class which uses
  // the font size designator included from the parent
  ///////////////////////////////////////////////
  protected class sizeableJLabel extends JLabel
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final static int _trimmedLength = 7;

    //////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////
    public sizeableJLabel()
    {
      this(null);
    }

    public sizeableJLabel(final String val)
    {
      this(val, false, false);
    }

    public sizeableJLabel(final String val, final boolean smallLabel, final boolean trimLabels)
    {
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
      final java.awt.Font myFont = getFont();
      java.awt.Font newFont = new java.awt.Font("Sans Serif",
                                                myFont.getStyle(),
                                                _fontSize);

      // do we shrink it?
      if (smallLabel)
      {
        newFont = newFont.deriveFont(newFont.getSize2D() - 1);
      }

      setFont(newFont);
    }
  }

  /**
   * embedded class to represent an updateable text label which shows the units of a tote calculation.
   */
  private class UnitLabel extends sizeableJLabel
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
     * the calculation we show.
     */
    private final toteCalculation _myCalc;

    /**
     * constructor to setup our label.
     *
     * @param calc       the calculation we show
     * @param smallLabel whether the font should be small
     * @param trimLabels whether to trim the text
     */
    public UnitLabel(toteCalculation calc, final boolean smallLabel, final boolean trimLabels)
    {
      super(calc.getUnits(), smallLabel, trimLabels);
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
