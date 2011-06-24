package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingWorldAccelerationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.1 $
// $Log: SwingWorldAccelerationPropertyEditor.java,v $
// Revision 1.1  2004/08/26 16:47:34  Ian.Mayo
// Implement more editable properties, add Acceleration property editor
//
// Revision 1.1  2004/08/26 09:46:37  Ian.Mayo
// Add world Acceleration property editors, and setter for Area corners
//
// Revision 1.2  2004/05/25 15:29:42  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:27  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-10-11 08:34:47+01  ian_mayo
// IntelliJ optimisations
//
// Revision 1.2  2002-05-28 09:25:47+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:34+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:56+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 14:01:27+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-01-22 12:42:26+00  administrator
// Reflect new way of reading in double values
//
// Revision 1.1  2002-01-17 20:41:12+00  administrator
// Remove scrap lines
//


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import MWC.GenericData.WorldAcceleration;

public class SwingWorldAccelerationPropertyEditor extends
  MWC.GUI.Properties.WorldAccelerationPropertyEditor implements FocusListener, ActionListener
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
   * field to edit the distance
   */
  JTextField _theAcceleration;

  /**
   * combo-box to select the units
   */
  JComboBox _theUnits;

  /**
   * panel to hold everything
   */
  JPanel _theHolder;

  /**
   * the former units used
   */
  int _oldUnits = -1;

  /**
   * the formatting object used to write to screen
   */
  protected DecimalFormat _formatter1 = new DecimalFormat("0.######");

  /**
   * property change support for objects which want to listen to us changing
   */
  protected java.beans.PropertyChangeSupport _pSupport = new java.beans.PropertyChangeSupport(this);

  /**
   * the amount of columns the users wants us to create
   */
  protected int _numColumns = 3;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * build the editor
   */
  public Component getCustomEditor()
  {
    _theHolder = new JPanel();

    BorderLayout bl1 = new BorderLayout();
    bl1.setVgap(0);
    bl1.setHgap(0);
    BorderLayout bl2 = new BorderLayout();
    bl2.setVgap(0);
    bl2.setHgap(0);

    JPanel lPanel = new JPanel();
    lPanel.setLayout(bl1);
    JPanel rPanel = new JPanel();
    rPanel.setLayout(bl2);

    _theHolder.setLayout(new BorderLayout());
    _theAcceleration = new JTextField();
    _theAcceleration.setToolTipText("the acceleration");
    _theAcceleration.setColumns(_numColumns);
    _theUnits = new JComboBox(WorldAcceleration.UnitLabels);
    _theUnits.setToolTipText("the Units");
    _theHolder.add("Center", _theAcceleration);
    _theHolder.add("East", _theUnits);

    // get the fields to select the full text when they're selected
    _theAcceleration.addFocusListener(this);
    _theUnits.addActionListener(this);

    resetData();
    return _theHolder;
  }

  /**
   * update the GUI, following a new value assignment
   */
  protected void updateGUI()
  {

  }

  /**
   * get the date text as a string
   */
  protected double getAcceleration() throws java.text.ParseException
  {
    double val = _formatter1.parse(_theAcceleration.getText()).doubleValue();
    return val;
  }

  /**
   * get the date text as a string
   */
  protected int getUnits()
  {
    return _theUnits.getSelectedIndex();
  }

  /**
   * set the date text in string form
   */
  protected void setAcceleration(double val)
  {
    if (_theHolder != null)
    {
      _theAcceleration.setText(_formatter1.format(val));
    }
  }

  /**
   * set the time text in string form
   */
  protected void setUnits(int val)
  {
    if (_theHolder != null)
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
    Component c = e.getComponent();
    if (c instanceof JTextField)
    {
      JTextField jt = (JTextField) c;
      jt.setSelectionStart(0);
      jt.setSelectionEnd(jt.getText().length());
    }
  }

  /**
   * Invoked when a component loses the keyboard focus.
   */
  public void focusLost(FocusEvent e)
  {
    Component c = e.getComponent();
    if (c instanceof JTextField)
    {
      JTextField jt = (JTextField) c;
      jt.setSelectionStart(0);
      jt.setSelectionEnd(jt.getText().length());
      _pSupport.firePropertyChange("Text", null, jt.getText());
    }
  }

  /**
   * the combo box label has been changed
   */
  public void actionPerformed(ActionEvent e)
  {
    // what are the new units?
    int newUnits = this._theUnits.getSelectedIndex();

    try
    {

      // convert to a new distance
      double newDist = WorldAcceleration.convert(_oldUnits, newUnits, getAcceleration());

      // and remember the units
      _oldUnits = newUnits;

      // and put the correct data in the distance
      setAcceleration(newDist);
    }
    catch (java.text.ParseException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe, "Whilst trying to read Acceleration value");
    }

    // ok, now fire an update
    _pSupport.firePropertyChange("Combo", -1, newUnits);
  }

  ////////////////////////////////////////////////////
  // property change support
  ////////////////////////////////////////////////////
  public void addPropertyChangeListener(java.beans.PropertyChangeListener listener)
  {
    _pSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(java.beans.PropertyChangeListener listener)
  {
    _pSupport.removePropertyChangeListener(listener);
  }


}
