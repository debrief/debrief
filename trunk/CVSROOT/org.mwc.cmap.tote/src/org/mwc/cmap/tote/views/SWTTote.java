// Copyright MWC 1999, Debrief 3 Project
// $RCSfile$
// @author $Author$
// @version $Revision$
// $Log$
// Revision 1.1  2005-05-31 13:24:53  Ian.Mayo
// Switch back to refactored version of Debrief tote (using labels)
//

package org.mwc.cmap.tote.views;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.mwc.cmap.core.property_support.ColorHelper;

import Debrief.Tools.Tote.*;
import MWC.GenericData.HiResDate;

/**
 * Swing implementation of a tote
 */
public final class SWTTote extends ToteCalcs
{

  // private JPanel _theStatus;
  private Composite _theTote;

  private final int _fontSize = 10;

  /**
   * the set of labels we are showing.  We remember these so that we can update them after each step - just in case the
   * units have changed
   */
  private Vector _theLabels = new Vector();


  public SWTTote()
  {
    super();
  }
  
  public Control createControl(Composite parent)
  {
    // create the panel we represent
  	_theTote = new Composite(parent, SWT.NONE);

  	return _theTote;

  }


  /**
   * method to clear up local properties
   */
  public final void closeMe()
  {
    // get the parent to close
    super.closeMe();

    // and clear our references
    _theTote = null;

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
  
  public Composite getPanel()
  {
  	return _theTote;
  }


  protected final void updateToteMembers()
  {
    // clear the current list of members
    super._theCalculations.removeAllElements();

    // clear the list of labels
    _theLabels.removeAllElements();

    // and delete them from our panels
    clearTote();

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
    GridLayout theGrid = new GridLayout();
    theGrid.numColumns = wid;
    _theTote.setLayout(theGrid);

    // decide if we are going to trim the track names (when we have
    // more than 4 tracks
    final boolean trimLabels = (_theSecondary.size() > 3);

    // and now add the members (going across first)
    sizeableJLabel sj = new sizeableJLabel(_theTote, "  ");
    final sizeableJLabel lh = new sizeableJLabel(_theTote, _thePrimary.getName(),
                                         false,
                                         trimLabels);

    Enumeration iter = _theSecondary.elements();
    while (iter.hasMoreElements())
    {
      final WatchableList w = (WatchableList) iter.nextElement();
      final sizeableJLabel jl = new sizeableJLabel(_theTote, w.getName(), false, trimLabels);
    }
    sizeableJLabel sj2 = new sizeableJLabel(_theTote, "  ");    

    // and now the data for each row
    iter = _theCalculationTypes.elements();
    while (iter.hasMoreElements())
    {
      final Class cl = (Class) iter.nextElement();
      try
      {
        final toteCalculation tc = (toteCalculation) cl.newInstance();

        // title
        final sizeableJLabel secL = new sizeableJLabel(_theTote,tc.getTitle(), true, false);

        // primary
        final calcHolder cp = new calcHolder(_theTote, tc, _thePrimary.getName());
        cp.setColor(getLabelColor(_thePrimary));
        _theCalculations.addElement(cp);

        // secondaries
        final Enumeration secs = _theSecondary.elements();
        while (secs.hasMoreElements())
        {
          final WatchableList wl = (WatchableList) secs.nextElement();
          final toteCalculation ts = (toteCalculation) cl.newInstance();
          final calcHolder ch = new calcHolder(_theTote, ts, wl.getName());
          ch.setColor(getLabelColor(wl));
          _theCalculations.addElement(ch);
        }

        // create the text label to show the calculation units
        UnitLabel thisLabel = new UnitLabel(_theTote, tc, true, false);

        // and remember it, so we can update it later
        _theLabels.add(thisLabel);
      }
      catch (Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

    }

    // put on the list of remove and copy buttons
    sizeableJLabel sj3 = new sizeableJLabel(_theTote, "  ");    
    sizeableJLabel sj4 = new sizeableJLabel(_theTote, "  ");        
    final Enumeration secs2 = _theSecondary.elements();
    while (secs2.hasMoreElements())
    {
      final WatchableList l = (WatchableList) secs2.nextElement();

      // create a holder for the two buttons
      final Composite holder = new Composite(_theTote, SWT.NONE);
      GridLayout g2 = new GridLayout();
      g2.numColumns = 2;
      removeMe rm = new removeMe(holder, l);
      
      removeMe r2 = new removeMe(holder, l);
      copyMe c2 = new copyMe(holder, l, _thePrimary, _theCalculationTypes);

    }

    // and the figures
    updateToteInformation();

  }

  // empty all of the children out of the tote
  private void clearTote()
	{
  	// work through the list
  	Control[] children = _theTote.getChildren();
  	for (int i = 0; i < children.length; i++)
		{
  		// get this one
			Control thisChild = children[i];
			
			// and tell it to ditch itself
			thisChild.dispose();
		}
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
    final toteCalculation _myCalc;
    final String _myName;
    java.awt.Color _myColor = null;

    public calcHolder(Composite parent, final toteCalculation calc, final String name)
    {
    	super(parent);
    	_myLabel.setForeground(ColorHelper.getColor(java.awt.Color.black));
      _myCalc = calc;
      _myName = name;
      _myLabel.setText("---");
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
      _myLabel.setText(val);
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
      _myButton.addSelectionListener(new SelectionListener(){

				public void widgetSelected(SelectionEvent e)
				{
		      removeParticipant(_thisL);
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}});
    }

  }

  ////////////////////////////////////////////////////
  // nested class for button to copy the values in a tote member
  ////////////////////////////////////////////////////
  final class copyMe extends sizeableJButton
  {
    final WatchableList _thisL;
    final WatchableList _thePrimary;
    final Vector _theCalculationTypes;

    public copyMe(Composite parent, final WatchableList theList,
                  final WatchableList thePrimary,
                  final Vector theCalculationTypes)
    {
    	super(parent, "Copy");
    	
    	_myButton.setToolTipText("copy details to clipboard");
      _thisL = theList;
      _thePrimary = thePrimary;
      _theCalculationTypes = theCalculationTypes;
      _myButton.addSelectionListener(new SelectionListener(){

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


  }


  //////////////////////////////////////////////
  // nested class, a JLabel class which uses
  // the font size designator included from the parent
  ///////////////////////////////////////////////
  protected class sizeableJButton
  {
  	protected Button _myButton;
  	
    public sizeableJButton(Composite parent, final String val)
    {
    	_myButton = new Button(parent, SWT.NONE);
    	_myButton.setText(val);
      // and set the font data
//      final java.awt.Font myFont = getFont();
//      final java.awt.Font newFont = new java.awt.Font("Sans Serif", myFont.getStyle(),
//                                                      _fontSize);
//      setFont(newFont);
    }

    public sizeableJButton(Composite parent, final String label, final String icon)
    {
    	this(parent, label);
//      super(icon);
    	_myButton.setToolTipText(label);
//      this.setMargin(new java.awt.Insets(0, 0, 0, 0));
    }

//    public final void setIcon(final ImageIcon icon)
//    {
//      this.setMargin(new java.awt.Insets(0, 0, 0, 0));
//      super.setIcon(icon);
//      if (icon != null)
//      {
//        super.setToolTipText(super.getText());
//        super.setText(null);
//      }
//    }
  }

  //////////////////////////////////////////////
  // nested class, a JLabel class which uses
  // the font size designator included from the parent
  ///////////////////////////////////////////////
  protected class sizeableJLabel 
  {
    private final static int _trimmedLength = 7;
    
    protected Label _myLabel;

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
    	_myLabel = new Label(parent, SWT.NONE);
      String lblText = val;

      if (trimLabels && val.length() > _trimmedLength)
      {
        // ok, store a shortened version of the string
        lblText = val.substring(0, _trimmedLength);

        // and set the full lengh label in the tooltext
        _myLabel.setToolTipText(val);

      }

      // ok, set the short text label
      if(lblText != null)
      	_myLabel.setText(lblText);

      // and set the font data
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
     *
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
    	_myLabel.setText(_myCalc.getUnits());
    }
  }
}
