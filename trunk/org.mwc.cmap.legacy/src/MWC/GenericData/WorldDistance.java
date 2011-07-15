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
public class WorldDistance implements Serializable
{
  /////////////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////////////


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the actual distance (in unknown units)
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
  static public final int FT = 7;

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
     6076.11548     
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
     "kyds",
     "ft"};


  /////////////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////////////

  /** no-op constructor created in support of Kryo networking
   * 
   */
  protected WorldDistance(){};
  
  /**
   * normal constructor
   *
   * @param value the distance in the supplied units
   * @param units the units used for this distance
   */
  public WorldDistance(double value, int units)
  {
    _myDistance = value;
    _myUnits = units;
  }

  /**
   * copy constructor
   */
  public WorldDistance(WorldDistance other)
  {
    _myDistance = other._myDistance;
    _myUnits = other._myUnits;
  }


  public WorldDistance( WorldVector separation)
  {
  	 this(separation.getRange(), WorldDistance.DEGS);
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
	if (!(obj instanceof WorldDistance))
		return false;
	final WorldDistance other = (WorldDistance) obj;
	return this.getValueIn(WorldDistance.DEGS) == other.getValueIn(WorldDistance.DEGS);
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

  public int getUnits()
  {
  	return _myUnits;
  }
  
  public double getValue()
  {
  	return _myDistance;
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
   * get this actual distance, in selected units
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

  ////////////////////////////////////////////////////////////
  // comparison methods
  ////////////////////////////////////////////////////////////

  public boolean lessThan(WorldDistance other)
  {
    return this.getValueIn(METRES) < other.getValueIn(METRES);
  }

  public boolean greaterThan(WorldDistance other)
  {
    return this.getValueIn(METRES) > other.getValueIn(METRES);
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
      WorldDistance w1 = new WorldDistance(1, WorldDistance.NM);
      assertEquals("Minutes correct", w1.getValueIn(WorldDistance.NM), 1d, 0.000001);

      WorldDistance w2 = new WorldDistance(1852 * 2, WorldDistance.METRES);
      assertEquals("Metres correct", w2.getValueIn(WorldDistance.NM), 2d, 0.000001);

      WorldDistance w3 = new WorldDistance(5, WorldDistance.DEGS);
      assertEquals("Degrees correct", w3.getValueIn(WorldDistance.NM), 300d, 0.000001);

      WorldDistance w4 = new WorldDistance(1.852 / 22, WorldDistance.KM);
      assertEquals("Km correct", w4.getValueIn(WorldDistance.NM), 1 / 22d, 0.000001);

      assertEquals("Back to Metres correct", w2.getValueIn(WorldDistance.METRES), 1852 * 2d, 0.000001);

      WorldDistance w5 = new WorldDistance(w4);
      assertEquals("Copy constructor", w5.getValueIn(WorldDistance.NM), 1 / 22d, 0.000001);

      assertEquals("retrieve metres label", WorldDistance.getLabelFor(WorldDistance.METRES), "m");
      assertEquals("retrieve degs label", WorldDistance.getLabelFor(WorldDistance.DEGS), "degs");

      WorldDistance w6 = new WorldDistance(2000, WorldDistance.YARDS);
      assertEquals("Kyds correct", w6.getValueIn(WorldDistance.KYDS), 2d, 0.000001);

      
    }

    public final void testEquals()
    {
    	WorldDistance da = new WorldDistance(60, WorldDistance.MINUTES);
    	WorldDistance db = new WorldDistance(1, WorldDistance.DEGS);
    	Assert.assertEquals("distances should be equal", da, db);
    }
    
    public final void testStrings()
    {
      WorldDistance da = new WorldDistance(12, METRES);
      String res = da.toString();
      assertEquals("correct output format", res, "12.0 m");

      da = new WorldDistance(1.75, KM);
      res = da.toString();
      assertEquals("correct output format, received:" + res, res, "1.75 km");
    }
  }

	/** convenience class for lengths that are specifically to be edited with the Array Lengths editor
	 * 
	 * @author ianmayo
	 *
	 */
	public static class ArrayLength extends WorldDistance
	{
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public ArrayLength(double metres)
		{
			super(new WorldDistance(metres, METRES));
		}
		
		public ArrayLength(WorldDistance dist)
		{
			super(dist);
		}
	}

	public void setValues(double range, int degs2)
	{
		_myDistance = range;
		_myUnits = degs2;
	}

}
