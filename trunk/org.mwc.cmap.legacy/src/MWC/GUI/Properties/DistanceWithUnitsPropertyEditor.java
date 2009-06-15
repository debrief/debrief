package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DistanceWithUnitsPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: DistanceWithUnitsPropertyEditor.java,v $
// Revision 1.4  2004/05/25 15:28:52  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.3  2003/10/21 08:14:38  Ian.Mayo
// Tidy up comment history
//
// Revision 1.2  2003/10/21 08:13:31  Ian.Mayo
// Finish implementation
//
// Revision 1.1  2003/10/17 14:51:21  Ian.Mayo
// First version


import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.text.DecimalFormat;
import java.text.ParseException;

import MWC.GenericData.WorldDistance;

/** abstract class providing core functionality necessary for editing a distance
 * value where units are provided (the return value is in minutes)
 */

abstract public class DistanceWithUnitsPropertyEditor extends
           PropertyEditorSupport
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
	/** the value we are editing (in minutes)
	 */
  private WorldDistance _myVal;

  /** the formatting object used to write to screen
   *
   */
  static protected DecimalFormat _formatter = new DecimalFormat("0.######");



  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** indicate that we can't just be painted, we've got to be edited
   */
  public boolean isPaintable()
  {
    return false;
  }

	/** build the editor
	 */
  abstract public Component getCustomEditor();

	/** store the new value (in minutes)
	 */
  public void setValue(Object p1)
  {
    // reset value
    _myVal = null;

		// try to catch if we are receiving a null (uninitialised) value
		if(p1 != null)
    {
      // check it's a Double
      if(p1 instanceof WorldDistance)
      {
        // store the distance
        _myVal = (WorldDistance)p1;
      }
    }
  }

  /** method to find the smallest set of units which will show the
   * indicated value (in minutes) as a whole or 1/2 value
   */
  static public int selectUnitsFor(double minutes)
  {

    int goodUnits = -1;

    // how many set of units are there?
    int len = WorldDistance.UnitLabels.length;

    // count downwards from last value
    for(int thisUnit=len - 1; thisUnit>= 0; thisUnit--)
    {
      // convert to this value
      double newVal = WorldDistance.convert(WorldDistance.NM, thisUnit, minutes);

      // double the value, so that 1/2 values are valid
      newVal *= 2;

      // is this a whole number?
      if(newVal == (int)newVal)
      {
        goodUnits = thisUnit;
        break;
      }
    }

    //  did we find a match?
    if(goodUnits != -1)
    {
      // ok, it must have worked
    }
    else
    {
      //  no, just use metres
      goodUnits = WorldDistance.METRES;
    }

    // return the result
    return goodUnits;
  }

	/** return flag to say that we'd rather use our own (custom) editor
	 */
  public boolean supportsCustomEditor()
  {
    return true;
  }

	/** extract the values currently stored in the text boxes (distance in minutes)
	 */
  public Object getValue()
  {
		WorldDistance val=null;
		try{
      // get the distance
      double distance = getDistance();

      // get the units scale factor
      int units = getUnits();

      // scale the distance to our output units (minutes)
      val = new WorldDistance(distance, units);


		}
		catch(NumberFormatException e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}
    catch(ParseException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe);
    }

    return val;
  }

	/** put the data into the text fields, if they have been
	 * created yet
	 */
  public void resetData()
  {
    if(_myVal == null)
    {
      setDistance(0);
      setUnits(3);
    }
    else
    {
      // get the best units
      setUnits(_myVal.getUnits());
			setDistance(_myVal.getValue());
    }
  }

  /** get the distance text as a string
   */
  abstract protected double getDistance() throws ParseException;

  /** get the units text as a string
   */
  abstract protected int getUnits();

  /** set the distance text in string form
   */
  abstract protected void setDistance(double val);

  /** set the units text in string form
   */
  abstract protected void setUnits(int val);

}
