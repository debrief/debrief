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
final public class WorldSpeed
{
  /////////////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////////////

  /**
   * the actual speed (in user specified minutes)
   */
  double _mySpeed;
  
  /** the selected units
   * 
   */
  int _myUnits = Kts;

  /**
   * the types of units we can handle
   */
  static public final int M_sec = 0;
  static public final int Kts = 1;
  static public final int ft_sec = 2;
  static public final int ft_min = 3;

  /**
   * the scale factors for the units compared to minutes
   */
  static private double _scaleVals[] =
    {1,
     MWC.Algorithms.Conversions.Mps2Kts(1),
     MWC.Algorithms.Conversions.m2ft(1),
     MWC.Algorithms.Conversions.m2ft(1 * 60)
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
  public WorldSpeed(double value, int units)
  {
    _mySpeed = value;
    _myUnits = units;
  }

  /**
   * copy constructor
   */
  public WorldSpeed(WorldSpeed other)
  {
    _mySpeed = other._mySpeed;
    _myUnits = other._myUnits;
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

  
  /** get my units, in string form
   * 
   * @return
   */
  public String getUnitsLabel()
  {
  	return getLabelFor(_myUnits);
  }
  
  /** get my units, in int counter form
   * 
   */
  public int getUnits()
  {
  	return _myUnits;
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
    return convert(_myUnits, units, _mySpeed);
  }

  /** get this speed, expressed in it's native units
   * 
   */
  public double getValue()
  {
  	return _mySpeed;
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
    return _mySpeed + " " + getUnitsLabel();
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class SpeedTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public SpeedTest(String val)
    {
      super(val);
    }

    public final void testWorldDistanceUnits()
    {
      WorldSpeed w1 = new WorldSpeed(1, WorldSpeed.M_sec);
      assertEquals("m sec correct", w1.getValueIn(WorldSpeed.M_sec), 1d, 0.000001);

      WorldSpeed w2 = new WorldSpeed(1, WorldSpeed.Kts);
      assertEquals("correct value stored", 1, w2._mySpeed, 0.001);
      assertEquals("correct unts stored", WorldSpeed.Kts, w2._myUnits, 0.001);
      assertEquals("m/sec correct", w2.getValueIn(WorldSpeed.M_sec), 0.5144444, 0.000001);
      assertEquals("kts correct", w2.getValueIn(WorldSpeed.Kts), 1, 0.000001);

      WorldSpeed w3 = new WorldSpeed(1, WorldSpeed.ft_sec);
      assertEquals("m/sec correct", 0.3048, w3.getValueIn(WorldSpeed.M_sec), 0.000001);
      assertEquals("ft/sec correct", 1, w3.getValueIn(WorldSpeed.ft_sec), 0.000001);

      WorldSpeed w4 = new WorldSpeed(1, WorldSpeed.ft_min);
      assertEquals("m/sec correct", 0.00508, w4.getValueIn(WorldSpeed.M_sec), 0.000001);
      assertEquals("ft/sec correct", 1, w4.getValueIn(WorldSpeed.ft_min), 0.000001);

      assertEquals("Back to kts correct", w2.getValueIn(WorldSpeed.Kts), 1, 0.000001);

      WorldSpeed w5 = new WorldSpeed(w3);
      assertEquals("Copy constructor", w5.getValueIn(WorldSpeed.ft_sec), 1, 0.000001);

      assertEquals("retrieve m/s label", WorldSpeed.getLabelFor(WorldSpeed.M_sec), "m/s");
      assertEquals("retrieve ft/s label", WorldSpeed.getLabelFor(WorldSpeed.ft_sec), "ft/s");
      assertEquals("retrieve kts label", WorldSpeed.getLabelFor(WorldSpeed.Kts), "kts");

      // test conversions
      WorldDistance w6 = new WorldDistance(10, WorldDistance.YARDS);
      assertEquals("valid units conversion", 30, w6.getValueIn(WorldDistance.FT), 0.001);
      
    }

    public final void testStrings()
    {
      WorldSpeed da = new WorldSpeed(12, Kts);
      String res = da.toString();
      assertEquals("correct output format", res, "12.0 kts");

      da = new WorldSpeed(1.75, M_sec);
      res = da.toString();
      assertEquals("correct output format", res, "1.75 m/s");
    }
  }

}
