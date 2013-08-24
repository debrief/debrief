/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Jan 17, 2002
 * Time: 12:45:46 PM
 */
package org.mwc.cmap.gridharness.data;


/**
 * class which represents a distance as a value plus a set of units
 */
final public class WorldDistance
{
  /////////////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////////////

  /**
   * the actual distance (in minutes)
   */
  private double _myDistance;

  /**
   * the types of units we can handle
   */
  static public final int METRES = 0;
  static public final int YARDS = 1;
  static public final int KYDS = 2;
  static public final int KM = 3;
  static public final int NM = 4;
  static public final int DEGS = 5;
  static public final int FT = 6;

  /**
   * the scale factors for the units compared to minutes
   */
  static private double _scaleVals[] =
    {1852,
     2025.371828,
     2.025371828,
     1.852,
     1,
     0.016666666666666666666667,
     6076.11548
    };

  /**
   * the units labels
   */
  static public final String[] UnitLabels =
    {"m",
     "yds",
     "kyds",
     "km",
     "nm",
     "degs",
     "ft"};


  /////////////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////////////

  /**
   * normal constructor
   *
   * @param value the distance in the supplied units
   * @param units the units used for this distance
   */
  public WorldDistance(final double value, final int units)
  {
  	setValues(value, units);
  }

  /**
   * copy constructor
   */
  public WorldDistance(final WorldDistance other)
  {
    _myDistance = other._myDistance;
  }
  
  public void setValues(final double value, final int units)
  {
    _myDistance = convert(units, WorldDistance.NM, value);
  }

  /////////////////////////////////////////////////////////////////
  // member methods
  /////////////////////////////////////////////////////////////////

  /**
   * perform a units conversion
   */
  static public double convert(final int from, final int to, final double val)
  {
    // get this scale value
    double scaleVal = _scaleVals[from];

    // convert to mins
    final double tmpVal = val / scaleVal;

    // get the new scale val
    scaleVal = _scaleVals[to];

    // convert to new value
    return tmpVal * scaleVal;
  }

  /**
   * get the string representing this set of units
   */
  static public String getLabelFor(final int units)
  {
    return UnitLabels[units];
  }

  /**
   * get the index for this type of unit
   */
  static public int getUnitIndexFor(final String units)
  {
    int res = 0;
    for (int i = 0; i < UnitLabels.length; i++)
    {
      final String unitLabel = UnitLabels[i];
      if (units.equals(unitLabel))
      {
        res = i;
        break;
      }
    }
    return res;
  }

  /**
   * get this actual distance, expressed in minutes
   */
  public double getValueIn(final int units)
  {
    return convert(WorldDistance.NM, units, _myDistance);
  }

  /**
   * produce as a string
   */
  public String toString()
  {
    // so, what are the preferred units?
    final int theUnits = selectUnitsFor(_myDistance);

    final double theValue = getValueIn(theUnits);

    final String res = theValue + " " + getLabelFor(theUnits);

    return res;
  }


  /**
   * get the SI units for  this type
   */
  public static int getSIUnits()
  {
    return METRES;
  }  
  
  /**
   * method to find the smallest set of units which will show the
   * indicated value (in millis) as a whole or 1/2 value
   */
  static public int selectUnitsFor(final double millis)
  {

    int goodUnits = -1;

    // how many set of units are there?
    final int len = UnitLabels.length;

    // count downwards from last value
    for (int thisUnit = len - 1; thisUnit >= 0; thisUnit--)
    {
      // convert to this value
      double newVal = convert(NM, thisUnit, millis);

      // double the value, so that 1/2 values are valid
      newVal *= 2;

      // is this a whole number?
      if (Math.abs(newVal - (int) newVal) < 0.0000000001)
      {
        goodUnits = thisUnit;
        break;
      }
    }

    //  did we find a match?
    if (goodUnits != -1)
    {
      // ok, it must have worked
    }
    else
    {
      //  no, just use metres
      goodUnits = NM;
    }

    // return the result
    return goodUnits;
  }


  ////////////////////////////////////////////////////////////
  // comparison methods
  ////////////////////////////////////////////////////////////

  public boolean lessThan(final WorldDistance other)
  {
    return this.getValueIn(METRES) < other.getValueIn(METRES);
  }

  public boolean greaterThan(final WorldDistance other)
  {
    return this.getValueIn(METRES) > other.getValueIn(METRES);
  }

}
