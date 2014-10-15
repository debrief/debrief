/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GenericData;

import java.io.Serializable;
import java.text.ParseException;
import java.util.StringTokenizer;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


/**
 * class which represents a time as a value plus a set of units
 */
final public class Duration implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////////////

  /**
   * the actual distance (in microseconds)
   */
  private final long _myDuration;

  /**
   * the types of units we can handle
   */
  static public final int MICROSECONDS = 0;
  static public final int MILLISECONDS = 1;
  static public final int SECONDS = 2;
  static public final int MINUTES = 3;
  static public final int HOURS = 4;
  static public final int DAYS = 5;

  /**
   * the scale factors for the units compared to minutes
   */
  static private double _scaleVals[] =
    {1d,
     1d / 1000d,
     1d / 1000d / 1000d,
     1d / 1000d / 1000d / 60d,
     1d / 1000d / 1000d / 60d / 60d,
     1d / 1000d / 1000d / 60d / 60d / 24d, };

  /**
   * the units labels
   */
  static public final String[] UnitLabels =
    {"micros",
     "millis",
     "seconds",
     "minutes",
     "hours",
     "days"};


  /////////////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////////////

  /**
   * normal constructor
   *
   * @param value the distance in the supplied units
   * @param units the units used for this distance
   */
  public Duration(final double value, final int units)
  {
    _myDuration = (long) convert(units, Duration.MILLISECONDS, value);
  }

  /**
   * copy constructor
   */
  public Duration(final Duration other)
  {
    _myDuration = other._myDuration;
  }


  /////////////////////////////////////////////////////////////////
  // member methods
  /////////////////////////////////////////////////////////////////


  final public boolean equals(final Object o)
  {

    boolean res = false;

    if (o instanceof Duration)
    {
      final Duration other = (Duration) o;
      if (other._myDuration == _myDuration)
      {
        res = true;
      }
    }


    return res;
  }

  /**
   * perform a units conversion
   */
  static public double convert(final int from, final int to, final double val)
  {
    // get this scale value
    double scaleVal = _scaleVals[from];

    // convert to millis
    double tmpVal = val / scaleVal;

    // round the millis
    tmpVal = round(tmpVal);

    // get the new scale val
    scaleVal = _scaleVals[to];

    // convert to new value
    final double res = tmpVal * scaleVal;

    return res;
  }

  /**
   * provide rounding function.  We can use a rounding function, since we are certain there
   * will be no units smaller than a millisecond.  Small errors may occur when we are switching
   * between very large units (days) and small ones (millis) - use of this function overcomes this.
   */
  static private double round(final double d)
  {
    long di = (long) Math.floor(d);
    if ((d - di) > 0.51)
      di++;

    return (double) di;
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
   * get this actual duration
   */
  public double getValueIn(final int units)
  {
    return convert(Duration.MILLISECONDS, units, _myDuration);
  }

  /**
   * get this duration as millis
   */
  public long getMillis()
  {
    return _myDuration;
  }

  /**
   * produce as a string
   */
  public String toString()
  {
    // so, what are the preferred units?
    final int theUnits = selectUnitsFor(_myDuration);

    final double theValue = getValueIn(theUnits);

    final String res = theValue + " " + getLabelFor(theUnits);

    return res;
  }
  
  public static Duration fromString(final String duration) throws ParseException
  {
  	Duration res = null;
  	
  	// trim the string
  	final String trimmed = duration.trim();
  	
  	// and break it down
    final StringTokenizer st = new StringTokenizer(trimmed);
    final String val = st.nextToken();
    final String units = st.nextToken();

    // parse the strings
    final int theUnits = getUnitIndexFor(units);
    final double theVal =  MWCXMLReader.readThisDouble(val);
    
    // collate the new value
    res = new Duration(theVal, theUnits);
  	
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
      double newVal = convert(MILLISECONDS, thisUnit, millis);

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
      goodUnits = MILLISECONDS;
    }

    // return the result
    return goodUnits;
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class DurationTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public DurationTest(final String val)
    {
      super(val);
    }

    public final void testWorldDistanceUnits()
    {
      final Duration w1 = new Duration(1, Duration.MILLISECONDS);
      assertEquals("millis correct", w1.getValueIn(Duration.MILLISECONDS), 1d, 0.000001);

      final Duration w2 = new Duration(2 / 1000d, Duration.SECONDS);
      assertEquals("seconds correct", w2.getValueIn(Duration.MILLISECONDS), 2d, 0.000001);

      final Duration w3 = new Duration(5, Duration.MINUTES);
      assertEquals("mins correct", w3.getValueIn(Duration.MILLISECONDS), 300000d, 0.000001);

      final Duration w4 = new Duration(5 / 1000d / 60d / 60d, Duration.HOURS);
      assertEquals("hours correct", w4.getValueIn(Duration.MILLISECONDS), 5d, 0.000001);

      assertEquals("Back to Metres correct", w2.getValueIn(Duration.SECONDS), 2 / 1000d, 0.000001);

      final Duration w5 = new Duration(w4);
      assertEquals("Copy constructor", w5.getValueIn(Duration.MILLISECONDS), 5d, 0.000001);

      assertEquals("retrieve mins label", Duration.getLabelFor(Duration.MINUTES), "minutes");
      assertEquals("retrieve days label", Duration.getLabelFor(Duration.DAYS), "days");

    }

    public void testToString() throws ParseException
    {
      Duration dd = new Duration(12, MINUTES);
      String res = dd.toString();
      assertEquals("Minutes correct", res, "12.0 minutes");
      dd = new Duration(1.25, MINUTES);
      res = dd.toString();
      assertEquals("Minutes correct", res, "75.0 seconds");
      
      // and now convert back
      final Duration d2 = Duration.fromString(res);
      assertEquals("converted back", dd, d2);
      
    }
    
  }

}
