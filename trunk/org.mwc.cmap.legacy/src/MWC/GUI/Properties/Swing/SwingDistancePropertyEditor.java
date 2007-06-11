package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingDistancePropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingDistancePropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:29:38  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:26  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:47+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:34+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:54+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 14:01:26+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-22 12:42:26+00  administrator
// Reflect new way of reading in double values
//
// Revision 1.0  2002-01-17 14:45:20+00  administrator
// Initial revision
//
// Revision 1.1  2001-08-31 10:36:55+01  administrator
// Tidied up layout, so all data is displayed when editor panel is first opened
//
// Revision 1.0  2001-07-17 08:43:31+01  administrator
// Initial revision
//
// Revision 1.4  2001-07-12 12:06:59+01  novatech
// use tooltips to show the date format
//
// Revision 1.3  2001-01-21 21:38:23+00  novatech
// handle focusGained = select all text
//
// Revision 1.2  2001-01-17 09:41:37+00  novatech
// factor generic processing to parent class, and provide support for NULL values
//
// Revision 1.1  2001-01-03 13:42:39+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:37  ianmayo
// initial version
//
// Revision 1.5  2000-10-09 13:35:47+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.4  2000-04-03 10:48:57+01  ian_mayo
// squeeze up the controls
//
// Revision 1.3  2000-02-02 14:25:07+00  ian_mayo
// correct package naming
//
// Revision 1.2  1999-11-23 11:05:03+00  ian_mayo
// further introduction of SWING components
//
// Revision 1.1  1999-11-16 16:07:19+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-11-16 16:02:29+00  ian_mayo
// Initial revision
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

import java.beans.*;
import MWC.GUI.*;
import MWC.GenericData.WorldDistance;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.*;

public class SwingDistancePropertyEditor extends
          MWC.GUI.Properties.DistancePropertyEditor implements FocusListener, ActionListener
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /** field to edit the distance
   */
  JTextField _theDistance;

  /** combo-box to select the units
   */
  JComboBox _theUnits;

  /** panel to hold everything
   */
  JPanel _theHolder;

  /** the former units used
   *
   */
  int _oldUnits = -1;

  /** the formatting object used to write to screen
   *
   */
  protected java.text.DecimalFormat _formatter = new java.text.DecimalFormat("0.######");

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** build the editor
   */
  public java.awt.Component getCustomEditor()
  {
    _theHolder = new JPanel();

    java.awt.BorderLayout bl1 = new java.awt.BorderLayout();
    bl1.setVgap(0);
    bl1.setHgap(0);
    java.awt.BorderLayout bl2 = new java.awt.BorderLayout();
    bl2.setVgap(0);
    bl2.setHgap(0);

    JPanel lPanel = new JPanel();
    lPanel.setLayout(bl1);
    JPanel rPanel = new JPanel();
    rPanel.setLayout(bl2);

		_theHolder.setLayout(new BorderLayout());
		_theDistance = new JTextField();
    _theDistance.setToolTipText("the distance");
		_theUnits = new JComboBox(WorldDistance.UnitLabels);
    _theUnits.setToolTipText("the Units");
		_theHolder.add("Center",_theDistance);
		_theHolder.add("East",_theUnits);

    // get the fields to select the full text when they're selected
    _theDistance.addFocusListener(this);
    _theUnits.addActionListener(this);

    resetData();
    return _theHolder;
  }

  /** get the date text as a string
   */
  protected double getDistance() throws java.text.ParseException
  {
    double val = _formatter.parse(_theDistance.getText()).doubleValue();
    return val;
  }

  /** get the date text as a string
   */
  protected int getUnits()
  {
    return _theUnits.getSelectedIndex();
  }

  /** set the date text in string form
   */
  protected void setDistance(double val)
  {
    if(_theHolder != null)
    {
     _theDistance.setText(_formatter.format(val));
    }
  }

  /** set the time text in string form
   */
  protected void setUnits(int val)
  {
		if(_theHolder != null)
		{
      // temporarily stop listening to the combo box
      _theUnits.removeActionListener(this);

      // select this item in the combo box
      _theUnits.setSelectedIndex(val);

      // continue listening to the combo box
      _theUnits.addActionListener(this);

      // remember the units
      _oldUnits = val;
		}
  }

  /////////////////////////////
  // focus listener support classes
  /////////////////////////////


  /**
   * Invoked when a component gains the keyboard focus.
   */
  public void focusGained(FocusEvent e)
  {
    java.awt.Component c = e.getComponent();
    if(c instanceof JTextField)
    {
      JTextField jt = (JTextField)c;
      jt.setSelectionStart(0);
      jt.setSelectionEnd(jt.getText().length());
    }
  }

  /**
   * Invoked when a component loses the keyboard focus.
   */
  public void focusLost(FocusEvent e)
  {
  }

  /** the combo box label has been changed
   *
   */
  public void actionPerformed(ActionEvent e)
  {
    // what are the new units?
    int newUnits = this._theUnits.getSelectedIndex();

    try
    {
      // convert to a new distance
      double newDist = WorldDistance.convert(_oldUnits, newUnits, getDistance());

      // and remember the units
      _oldUnits = newUnits;

      // and put the correct data in the distance
      setDistance(newDist);
    }
    catch(java.text.ParseException te)
    {
      MWC.Utilities.Errors.Trace.trace(te);
    }
  }


}
