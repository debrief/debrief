/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Jan 17, 2002
 * Time: 12:45:46 PM
 */
package MWC.GenericData;

import java.io.Serializable;

import junit.framework.Assert;


/**
 * class which represents a distance as a value plus a set of units
 */
final public class WorldDistanceWithUnits implements Serializable
{
  /////////////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////////////


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the actual distance (in nm)
   */
  private double _myDistance;

  /**
   * the units selected
   */
  private int _myUnits;

  /**
   * the types of units we can handle
   */
  static public final int METRES = 0;
  static public final int YARDS = 1;
  static public final int KM = 2;
  static public final int NM = 3;
  static public final int MINUTES = 4;
  static public final int DEGS = 5;
  static public final int KYDS = 6;

  /**
   * the scale factors for the units compared to minutes
   */
  static private double _scaleVals[] =
    {1852,
     2025.371828,
     1.852,
     1,
     1,
     0.016666666666666666666667,
     2.025371828,
     };

  /**
   * the units labels
   */
  static public final String[] UnitLabels =
    {"m",
     "yds",
     "km",
     "nm",
     "minutes",
     "degs" ,
     "kyds"};


  /////////////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////////////

  /**
   * normal constructor
   *
   * @param value the distance in the supplied units
   * @param units the units used for this distance
   */
  public WorldDistanceWithUnits(double value, int units)
  {
    _myDistance = value;
    _myUnits = units;
  }

  /**
   * copy constructor
   */
  public WorldDistanceWithUnits(WorldDistanceWithUnits other)
  {
    _myDistance = other._myDistance;
    _myUnits = other._myUnits;
  }


  /////////////////////////////////////////////////////////////////
  // member methods
  /////////////////////////////////////////////////////////////////

  @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	long temp;
	temp = Double.doubleToLongBits(_myDistance);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	result = prime * result + _myUnits;
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (!(obj instanceof WorldDistanceWithUnits))
		return false;
	final WorldDistanceWithUnits other = (WorldDistanceWithUnits) obj;
	return this.getValueIn(WorldDistanceWithUnits.DEGS) == other.getValueIn(WorldDistanceWithUnits.DEGS);
}

/**
   * accessor to indicate if this distance represents an absolute or angular distance
   */
  public boolean isAngular()
  {
    return (_myUnits == DEGS) || (_myUnits == MINUTES);
  }

  /**
   * get a string representing the units in use
   */
  public String getUnitsLabel()
  {
    return getLabelFor(_myUnits);
  }

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
   * get the distance value (in whatever is the selected units)
   */
  public double getDistance()
  {
    return _myDistance;
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
    double res;
    if (units == _myUnits)
      res = _myDistance;
    else
      res = convert(_myUnits, units, _myDistance);

    return res;
  }

  /**
   * produce as a string
   */
  public String toString()
  {
    String res = _myDistance + " " + getLabelFor(_myUnits);

    return res;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class DistWithUnitsTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public DistWithUnitsTest(String val)
    {
      super(val);
    }

    public final void testWorldDistanceUnits()
    {
      WorldDistanceWithUnits w1 = new WorldDistanceWithUnits(1, WorldDistanceWithUnits.NM);
      assertEquals("Minutes correct", w1.getValueIn(WorldDistanceWithUnits.NM), 1d, 0.000001);

      WorldDistanceWithUnits w2 = new WorldDistanceWithUnits(1852 * 2, WorldDistanceWithUnits.METRES);
      assertEquals("Metres correct", w2.getValueIn(WorldDistanceWithUnits.NM), 2d, 0.000001);

      WorldDistanceWithUnits w3 = new WorldDistanceWithUnits(5, WorldDistanceWithUnits.DEGS);
      assertEquals("Degrees correct", w3.getValueIn(WorldDistanceWithUnits.NM), 300d, 0.000001);

      WorldDistanceWithUnits w4 = new WorldDistanceWithUnits(1.852 / 22, WorldDistanceWithUnits.KM);
      assertEquals("Km correct", w4.getValueIn(WorldDistanceWithUnits.NM), 1 / 22d, 0.000001);

      assertEquals("Back to Metres correct", w2.getValueIn(WorldDistanceWithUnits.METRES), 1852 * 2d, 0.000001);

      WorldDistanceWithUnits w5 = new WorldDistanceWithUnits(w4);
      assertEquals("Copy constructor", w5.getValueIn(WorldDistanceWithUnits.NM), 1 / 22d, 0.000001);

      assertEquals("retrieve metres label", WorldDistanceWithUnits.getLabelFor(WorldDistanceWithUnits.METRES), "m");
      assertEquals("retrieve degs label", WorldDistanceWithUnits.getLabelFor(WorldDistanceWithUnits.DEGS), "degs");

      WorldDistanceWithUnits w6 = new WorldDistanceWithUnits(2000, WorldDistanceWithUnits.YARDS);
      assertEquals("Kyds correct", w6.getValueIn(WorldDistanceWithUnits.KYDS), 2d, 0.000001);

      
    }

    public final void testEquals()
    {
    	WorldDistanceWithUnits da = new WorldDistanceWithUnits(60, WorldDistanceWithUnits.MINUTES);
    	WorldDistanceWithUnits db = new WorldDistanceWithUnits(1, WorldDistanceWithUnits.DEGS);
    	Assert.assertEquals("distances should be equal", da, db);
    }
    
    public final void testStrings()
    {
      WorldDistanceWithUnits da = new WorldDistanceWithUnits(12, METRES);
      String res = da.toString();
      assertEquals("correct output format", res, "12.0 m");

      da = new WorldDistanceWithUnits(1.75, KM);
      res = da.toString();
      assertEquals("correct output format, received:" + res, res, "1.75 km");
    }
  }

}
