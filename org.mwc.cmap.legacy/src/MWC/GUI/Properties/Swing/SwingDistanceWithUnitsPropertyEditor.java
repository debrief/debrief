/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingDistanceWithUnitsPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: SwingDistanceWithUnitsPropertyEditor.java,v $
// Revision 1.3  2004/05/25 15:29:40  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.2  2003/10/21 08:14:37  Ian.Mayo
// Tidy up comment history
//
// Revision 1.1  2003/10/17 14:51:29  Ian.Mayo
// First version
//
// Revision 1.1.1.1  2003/07/17 10:07:26  Ian.Mayo
// Initial import
//


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import MWC.GenericData.WorldDistance;

public class SwingDistanceWithUnitsPropertyEditor extends
          MWC.GUI.Properties.DistanceWithUnitsPropertyEditor implements FocusListener, ActionListener
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
  protected DecimalFormat _formatter1 = new DecimalFormat("0.######");

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** build the editor
   */
  public Component getCustomEditor()
  {
    _theHolder = new JPanel();

    final BorderLayout bl1 = new BorderLayout();
    bl1.setVgap(0);
    bl1.setHgap(0);
    final BorderLayout bl2 = new BorderLayout();
    bl2.setVgap(0);
    bl2.setHgap(0);

    final JPanel lPanel = new JPanel();
    lPanel.setLayout(bl1);
    final JPanel rPanel = new JPanel();
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
  protected double getDistance() throws ParseException
  {
    final double val = _formatter1.parse(_theDistance.getText()).doubleValue();
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
  protected void setDistance(final double val)
  {
    if(_theHolder != null)
    {
     _theDistance.setText(_formatter1.format(val));
    }
  }

  /** set the time text in string form
   */
  protected void setUnits(final int val)
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
  public void focusGained(final FocusEvent e)
  {
    final Component c = e.getComponent();
    if(c instanceof JTextField)
    {
      final JTextField jt = (JTextField)c;
      jt.setSelectionStart(0);
      jt.setSelectionEnd(jt.getText().length());
    }
  }

  /**
   * Invoked when a component loses the keyboard focus.
   */
  public void focusLost(final FocusEvent e)
  {
  }

  /** the combo box label has been changed
   *
   */
  public void actionPerformed(final ActionEvent e)
  {
    // what are the new units?
    final int newUnits = this._theUnits.getSelectedIndex();

    try
    {
      // convert to a new distance
      final double newDist = WorldDistance.convert(_oldUnits, newUnits, getDistance());

      // and remember the units
      _oldUnits = newUnits;

      // and put the correct data in the distance
      setDistance(newDist);
    }
    catch(final ParseException te)
    {
      MWC.Utilities.Errors.Trace.trace(te);
    }
  }


}
