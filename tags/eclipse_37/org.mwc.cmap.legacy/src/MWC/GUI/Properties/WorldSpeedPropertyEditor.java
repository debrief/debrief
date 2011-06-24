package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: WorldSpeedPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.1 $
// $Log: WorldSpeedPropertyEditor.java,v $
// Revision 1.1  2004/08/26 09:46:38  Ian.Mayo
// Add world speed property editors, and setter for Area corners
//
//

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.text.DecimalFormat;

import MWC.GenericData.WorldSpeed;

/**
 * abstract class providing core functionality necessary for editing a distance
 * value where units are provided (the return value is in minutes)
 */
abstract public class WorldSpeedPropertyEditor extends
  PropertyEditorSupport
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * the value we are editing (in minutes)
   */
  private WorldSpeed _myVal;

  /**
   * the formatting object used to write to screen
   */
  static protected DecimalFormat _formatter = new DecimalFormat("0.######");
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
   * indicate that we can't just be painted, we've got to be edited
   */
  public boolean isPaintable()
  {
    return false;
  }

  /**
   * build the editor
   */
  abstract public Component getCustomEditor();

  /**
   * update the GUI, following a new value assignment
   */
  abstract protected void updateGUI();

  /**
   * store the new value (in minutes)
   */
  public void setValue(Object p1)
  {
    // reset value
    _myVal = null;

    // try to catch if we are receiving a null (uninitialised) value
    if (p1 != null)
    {
      // check it's a Double
      if (p1 instanceof WorldSpeed)
      {
        // store the distance
        _myVal = (WorldSpeed) p1;

        // and update our data
        resetData();
      }
    }
  }


  /**
   * return flag to say that we'd rather use our own (custom) editor
   */
  public boolean supportsCustomEditor()
  {
    return true;
  }

  /**
   * extract the values currently stored in the text boxes
   */
  public Object getValue()
  {
    WorldSpeed val = null;
    try
    {
      // get the speed
      double duration = getSpeed();

      // get the units scale factor
      int units = getUnits();

      // scale the speed to our output units (minutes)
      val = new WorldSpeed(duration, units);
    }
    catch (NumberFormatException e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }
    catch (java.text.ParseException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe);
    }


    return val;
  }

  /**
   * put the data into the text fields, if they have been
   * created yet
   */
  public void resetData()
  {
    if (_myVal == null)
    {
      setSpeed(0);
      setUnits(WorldSpeed.M_sec);
    }
    else
    {
      // get the best units
      setUnits(_myVal.getUnits());
      setSpeed(_myVal.getValue());
    }
  }

  /**
   * get the duration text as a string
   */
  abstract protected double getSpeed() throws java.text.ParseException;

  /**
   * get the units text as a string
   */
  abstract protected int getUnits();

  /**
   * set the duration text in string form
   */
  abstract protected void setSpeed(double val);

  /**
   * set the units text in string form
   */
  abstract protected void setUnits(int val);

  /**
   * the the number of columns to use in the editor
   *
   * @param num the number of columns to show in the text field
   */
  public void setColumns(int num)
  {
    _numColumns = num;
  }

}
