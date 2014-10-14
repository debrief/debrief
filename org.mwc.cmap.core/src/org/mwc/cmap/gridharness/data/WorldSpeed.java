/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.gridharness.data;


/**
 * class which represents a speed as a value plus a set of units
 */
final public class WorldSpeed
{
  /////////////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////////////

  /**
   * the actual speed (in metres)
   */
  double _mySpeed;

  /**
   * the types of units we can handle
   */
  static public final int M_sec = 0;
  static public final int Kts = 1;
  static public final int ft_sec = 2;
  static public final int ft_min = 3;

  /**
   * the scale factors for the units compared to metres
   */
  static private double _scaleVals[] =
    {1,
     1/(1852d / 3600),
     0.3048,
     0.3048 * 60
    };

  /**
   * the units labels
   */
  static public final String[] UnitLabels =
    {"m/s",
     "kts",
     "ft/s",
     "ft/min"
    };


  /////////////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////////////

  /**
   * normal constructor
   *
   * @param value the distance in the supplied units
   * @param units the units used for this distance
   */
  public WorldSpeed(final double value, final int units)
  {
    _mySpeed = convert(units, WorldSpeed.M_sec, value);
  }

  /**
   * copy constructor
   */
  public WorldSpeed(final WorldSpeed other)
  {
    _mySpeed = other._mySpeed;
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
    return convert(WorldSpeed.M_sec, units, _mySpeed);
  }

  /**
   * get the SI units for  this type
   */
  public static int getSIUnits()
  {
    return M_sec;
  }

  /**
   * produce as a string
   */
  public String toString()
  {
    // so, what are the preferred units?
    final int theUnits = selectUnitsFor(_mySpeed);

    final double theValue = getValueIn(theUnits);

    final String res = theValue + " " + getLabelFor(theUnits);

    return res;
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
      double newVal = convert(WorldSpeed.M_sec, thisUnit, millis);

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
      goodUnits = WorldSpeed.M_sec;
    }

    // return the result
    return goodUnits;
  }

}
  
