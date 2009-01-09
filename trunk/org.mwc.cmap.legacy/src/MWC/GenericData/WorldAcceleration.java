/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Jan 17, 2002
 * Time: 12:45:46 PM
 */
package MWC.GenericData;


/**
 * class which represents a speed as a value plus a set of units
 */
final public class WorldAcceleration
{
  /////////////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////////////

  /**
   * the actual speed (in minutes)
   */
  double _mySpeed;

  /**
   * the types of units we can handle
   */
  static public final int M_sec_sec = 0;
  static public final int Kts_sec = 1;

  /**
   * the scale factors for the units compared to minutes
   */
  static private double _scaleVals[] =
    {1,
     MWC.Algorithms.Conversions.Mps2Kts(1),
    };

  /**
   * the units labels
   */
  static public final String[] UnitLabels =
    {"m/s/s",
     "kts/s",
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
  public WorldAcceleration(double value, int units)
  {
    _mySpeed = convert(units, WorldAcceleration.M_sec_sec, value);
  }

  /**
   * copy constructor
   */
  public WorldAcceleration(WorldAcceleration other)
  {
    _mySpeed = other._mySpeed;
  }


  /////////////////////////////////////////////////////////////////
  // member methods
  /////////////////////////////////////////////////////////////////

  /**
   * perform a units conversion
   */
  static public double convert(int from, int to, double val)
  {
    // get this scale value
    double scaleVal = _scaleVals[from];

    // convert to mins
    double tmpVal = val / scaleVal;

    // get the new scale val
    scaleVal = _scaleVals[to];

    // convert to new value
    return tmpVal * scaleVal;
  }

  /**
   * get the string representing this set of units
   */
  static public String getLabelFor(int units)
  {
    return UnitLabels[units];
  }

  /**
   * get the index for this type of unit
   */
  static public int getUnitIndexFor(String units)
  {
    int res = 0;
    for (int i = 0; i < UnitLabels.length; i++)
    {
      String unitLabel = UnitLabels[i];
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
  public double getValueIn(int units)
  {
    return convert(WorldAcceleration.M_sec_sec, units, _mySpeed);
  }

  /**
   * get the SI units for  this type
   */
  public static int getSIUnits()
  {
    return M_sec_sec;
  }

  /**
   * produce as a string
   */
  public String toString()
  {
    // so, what are the preferred units?
    int theUnits = selectUnitsFor(_mySpeed);

    double theValue = getValueIn(theUnits);

    String res = theValue + " " + getLabelFor(theUnits);

    return res;
  }

  /**
   * method to find the smallest set of units which will show the
   * indicated value (in millis) as a whole or 1/2 value
   */
  static public int selectUnitsFor(double millis)
  {

    int goodUnits = -1;

    // how many set of units are there?
    int len = UnitLabels.length;

    // count downwards from last value
    for (int thisUnit = len - 1; thisUnit >= 0; thisUnit--)
    {
      // convert to this value
      double newVal = convert(WorldAcceleration.M_sec_sec, thisUnit, millis);

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
      goodUnits = WorldAcceleration.M_sec_sec;
    }

    // return the result
    return goodUnits;
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class AccelTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public AccelTest(String val)
    {
      super(val);
    }

    public final void testWorldDistanceUnits()
    {
      WorldAcceleration w1 = new WorldAcceleration(1, WorldAcceleration.M_sec_sec);
      assertEquals("m sec correct", w1.getValueIn(WorldAcceleration.M_sec_sec), 1d, 0.000001);

      WorldAcceleration w2 = new WorldAcceleration(1, WorldAcceleration.Kts_sec);
      assertEquals("correct value stored", 0.514444, w2._mySpeed, 0.001);
      assertEquals("m/sec correct", w2.getValueIn(WorldAcceleration.M_sec_sec), 0.5144444, 0.000001);
      assertEquals("kts correct", w2.getValueIn(WorldAcceleration.Kts_sec), 1, 0.000001);

      assertEquals("Back to kts correct", w2.getValueIn(WorldAcceleration.Kts_sec), 1, 0.000001);


      assertEquals("retrieve m/s label", WorldAcceleration.getLabelFor(WorldAcceleration.M_sec_sec), "m/s/s");
      assertEquals("retrieve kts label", WorldAcceleration.getLabelFor(WorldAcceleration.Kts_sec), "kts/s");

    }

    public final void testStrings()
    {
      WorldAcceleration da = new WorldAcceleration(12, Kts_sec);
      String res = da.toString();
      assertEquals("correct output format", res, "12.0 kts/s");

      da = new WorldAcceleration(1.75, M_sec_sec);
      res = da.toString();
      assertEquals("correct output format", res, "1.75 m/s/s");
    }
  }

}
