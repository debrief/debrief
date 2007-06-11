// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTTote.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: AWTTote.java,v $
// Revision 1.5  2005/12/13 09:04:23  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2004/11/26 11:37:43  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.3  2004/11/25 10:24:00  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2004/11/22 13:40:49  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.1.1.2  2003/07/21 14:47:16  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-03-19 15:38:06+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-10 16:27:19+00  ian_mayo
// Reflect name change for get wrappable data
//
// Revision 1.3  2003-02-07 15:36:13+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.2  2002-05-28 12:27:57+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:03+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:40+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:53+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:30  ianmayo
// initial import of files
//
// Revision 1.7  2000-10-09 13:37:46+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.6  2000-09-14 10:47:49+01  ian_mayo
// correct syntax for calculation holder
//
// Revision 1.5  2000-09-14 10:28:35+01  ian_mayo
// pass time value to calculations
//
// Revision 1.4  2000-03-14 09:51:33+00  ian_mayo
// pass Layers data to Core tote class
//
// Revision 1.3  2000-02-02 14:28:17+00  ian_mayo
// make return types consistent (Container, not Panel)
//
// Revision 1.2  1999-11-18 11:16:52+00  ian_mayo
// getPanel now returns Container, not Panel
//
// Revision 1.1  1999-10-12 15:34:22+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-04 10:53:01+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-27 09:27:48+01  administrator
// general improvements
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
// Revision 1.1  1999-01-31 13:33:12+00  sm11td
// Initial revision
//

package Debrief.GUI.Tote.AWT;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

import Debrief.Tools.Tote.*;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.AWT.AWTPropertiesPanel;
import MWC.GenericData.HiResDate;

/** AWT implementation of a tote 
 * */
public final class AWTTote extends Debrief.GUI.Tote.AnalysisTote
{
  private final Panel _thePanel;
  private final AWTPropertiesPanel _theParent;
  
  private final Panel _theStatus;
  private final Panel _theTote;
  private final Panel _toteHolder;
  
  public AWTTote(final AWTPropertiesPanel theTabPanel,
                 final MWC.GUI.Layers theData, ToolParent theParent)
  {
		super(theData);
		
    // store the parent
    _theParent = theTabPanel;

    
    // create the panel we represent
    _thePanel = new Panel();
    _thePanel.setName("Tote");
    _thePanel.setLayout(new BorderLayout());

    // create the central tote portion
    _toteHolder = new Panel();
    _toteHolder.setLayout(new BorderLayout());
    _theTote = new Panel();
    _toteHolder.add("North", _theTote);
    _thePanel.add("Center", _toteHolder);
    
    // create the status panel
    _theStatus = new Panel();
    _thePanel.add("South", _theStatus);
    
    // create the stepping control
    final AWTStepControl aw = new AWTStepControl(theTabPanel, theParent);
    _thePanel.add("North", aw.getPanel());
    setStepper(aw);

  }
  
  public final java.awt.Container getPanel(){
    return _thePanel;
  }

  

  protected final void updateToteMembers()
  {
    // check we can do it properly
    if(_thePrimary == null)
      return;
    
    // we must now build up our grid of information
    
    // clear the current list of members
    super._theCalculations.removeAllElements();
    
    // and delete them from our panels
    _theTote.removeAll();
    
    // calculate the size that we need
    final int wid = 1 +  // label
              1 +  // primary
              super._theSecondary.size() +  // secondaries
              1;   // units
    
    // now do our grid
    _theTote.setLayout(new GridLayout(0, wid));
    
    // and now add the members (going across first)
    _theTote.add(new Label("  "));
    _theTote.add(new Label(_thePrimary.getName(), Label.CENTER));
    Enumeration iter = _theSecondary.elements();
    while(iter.hasMoreElements())
    {
      final WatchableList w = (WatchableList) iter.nextElement();
      _theTote.add(new Label(w.getName(), Label.CENTER));
    }
    _theTote.add(new Label("  "));
    
    // and now the data for each row
    iter = _theCalculationTypes.elements();
    while(iter.hasMoreElements())
    {
      final Class cl = (Class)iter.nextElement();
      try
      {
        final toteCalculation tc = (toteCalculation)cl.newInstance();
        
        // title
        _theTote.add(new Label(tc.getTitle(), Label.RIGHT));
        
        // primary
        final calcHolder cp = new calcHolder(tc, _thePrimary.getName());
        _theCalculations.addElement(cp);
        _theTote.add(cp);
        
        // secondaries
        final Enumeration secs = _theSecondary.elements();
        while(secs.hasMoreElements())
        {
          final WatchableList wl = (WatchableList)secs.nextElement();
          final toteCalculation ts = (toteCalculation)cl.newInstance();
          final calcHolder ch = new calcHolder(ts, wl.getName());
          _theCalculations.addElement(ch);
          _theTote.add(ch);
        }
        
        _theTote.add(new Label(tc.getUnits()));
      }
      catch(Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

    }

    // put on the list of remove buttons
    _theTote.add(new Label("  "));
    _theTote.add(new Label("  "));
    final Enumeration secs2 = _theSecondary.elements();
    while(secs2.hasMoreElements())
    {
      final WatchableList l = (WatchableList)secs2.nextElement();
      _theTote.add(new removeMe(l));
    }
    
    // and the figures
    updateToteInformation();
    
    _theParent.doLayout();
    
  }

    
  ////////////////////////////////////////////////////////////
  // nested class to put a calculation into a label
  ////////////////////////////////////////////////////////////
  static final class calcHolder extends Label implements toteCalculation
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final toteCalculation _myCalc;
    final String _myName;
    public calcHolder(final toteCalculation calc, final String name)
    {
      _myCalc = calc;
      _myName = name;
      setText("---");
    }

    public final double calculate(final Watchable primary,final Watchable secondary,final HiResDate thisTime)
    {
      return _myCalc.calculate(primary, secondary, thisTime);
    }
    
    public final String update(final Watchable primary,final Watchable secondary,final HiResDate thisTime)
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

    public final void paint(final Graphics p1)
    {
      super.paint(p1);
      final Dimension sz = this.getSize();
      p1.drawRect(1,1,sz.width-2, sz.height-2);
    }

    /** does this calculation require special bearing handling (prevent wrapping through 360 degs)
     *
     */
    public final boolean isWrappableData() {
      return false;
    }
  }
  
  ////////////////////////////////////////////////////
  // nested class for button to remove a tote participant
  ////////////////////////////////////////////////////
  final class removeMe extends Button implements ActionListener
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final WatchableList _thisL;
    public removeMe(final WatchableList theList)
    {
      super("remove");
      _thisL = theList;
      this.addActionListener(this);
    }
    
    public final void actionPerformed(final ActionEvent e)
    {
      removeParticipant(_thisL);
    }
    
  }
  
}
