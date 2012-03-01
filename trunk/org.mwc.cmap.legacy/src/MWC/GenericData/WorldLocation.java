package MWC.GenericData;

import java.awt.geom.Point2D;
import java.io.Serializable;

import MWC.Algorithms.EarthModels.CompletelyFlatEarth;

/**
 * represents a 3-point in space, using World/Earth coordinates. Others classes
 * also handle/maintain world coordinates.
 * 
 * @see WorldArea
 * @see WorldVector
 */
public class WorldLocation implements Serializable, Cloneable
{

	// /////////////////////////////////////////////////////
	// members
	// /////////////////////////////////////////////////////

	/**
	 * keep track of our earth model
	 */
	static private MWC.Algorithms.EarthModel _model;

	// keep track of versions
	static final long serialVersionUID = 1;

	/**
	 * depth in metres
	 */
	double _theDepth;
	/**
	 * Latitude in Degrees
	 */
	double _theLat;
	/**
	 * Longitude in Degrees
	 */
	double _theLong;

	/**
	 * our working version of world vector, to reduce object creation
	 */
	private static WorldVector _myWorldVector = new WorldVector(0, 0, 0);

	// //////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////

	/**
	 * SPECIAL constructor created to help Kryo get locations across the LAN for
	 * ASSET
	 */
	@SuppressWarnings("unused")
	private WorldLocation()
	{
	};

	/**
	 * create a new location, in World coordinates
	 * 
	 * @param LatVal
	 *          Latitude in degrees
	 * @param LongVal
	 *          Longitude in degrees
	 * @param DepthVal
	 *          Depth in metres
	 */
	public WorldLocation(double LatVal, double LongVal, double DepthVal)
	{
		_theLat = LatVal;
		_theLong = LongVal;
		_theDepth = DepthVal;
	}

	/**
	 * copy constructor
	 * 
	 * @param other
	 *          WorldLocation to copy
	 */
	public WorldLocation(WorldLocation other)
	{
		this(other._theLat, other._theLong, other._theDepth);
	}

	/**
	 * long winded constructor, taking raw arguments
	 */
	public WorldLocation(double latDegs, double latMin, double latSec,
			char latHem, double longDegs, double longMin, double longSec,
			char longHem, double theDepth)
	{
		// this constructor can compensate for decimal values of degs and mins,
		// cascading them as appropriate

		// just check if we've got decimal values for degs or minutes. If so,
		// cascade them to other units
		double dec = decimalComponentOf(latDegs);
		if (dec > 0)
		{
			latDegs -= dec;
			latMin += dec * 60d;
		}

		dec = decimalComponentOf(latMin);
		if (dec > 0)
		{
			latMin -= dec;
			latSec += dec * 60d;
		}

		// Now for longitude:
		//
		// just check if we've got decimal values for degs or minutes. If so,
		// cascade them to other units
		dec = decimalComponentOf(longDegs);
		if (dec > 0)
		{
			longDegs -= dec;
			longMin += dec * 60d;
		}

		dec = decimalComponentOf(longMin);
		if (dec > 0)
		{
			longMin -= dec;
			longSec += dec * 60d;
		}

		// ok - do the store
		_theLat = latDegs + latMin / 60 + latSec / (60 * 60);
		_theLong = longDegs + longMin / 60 + longSec / (60 * 60);
		_theDepth = theDepth;

		// switch the deg vals if in -ve hemishere
		if ((latHem == 'S') || (latHem == 's'))
		{
			_theLat = -1.0 * _theLat;
		}

		if ((longHem == 'W') || (longHem == 'w'))
		{
			_theLong = -1.0 * _theLong;
		}

	}

	// /////////////////////////////////////////////////////
	// getter/setters
	//

	/**
	 * return the decimal component of the supplied number
	 * 
	 */
	private double decimalComponentOf(double latDeg)
	{
		return latDeg - Math.floor(latDeg);
	}

	/**
	 * @return the latitude in degrees
	 */
	final public double getLat()
	{
		return _theLat;
	}

	/**
	 * @return the longitude in degrees
	 */
	final public double getLong()
	{
		return _theLong;
	}

	/**
	 * @return the depth in metres
	 */
	final public double getDepth()
	{
		return _theDepth;
	}

	/**
	 * @param val
	 *          - the latitude in degrees
	 */
	final public void setLat(double val)
	{
		_theLat = val;
	}

	/**
	 * @param val
	 *          - the longitude in degrees
	 */
	final public void setLong(double val)
	{
		_theLong = val;
	}

	/**
	 * @param val
	 *          - the depth in metres
	 */
	final public void setDepth(double val)
	{
		_theDepth = val;
	}

	// /////////////////////////////////////////////////////
	// operations
	// ////////////////////////////////////////////////////

	/**
	 * check for valid depth
	 */
	final public boolean hasValidDepth()
	{
		return !Double.isNaN(_theDepth);
	}

	final public WorldVector subtract(WorldLocation other)
	{
		WorldVector res = new WorldVector(0, 0, 0);
		return subtract(other, res);
	}

	/**
	 * subtract the two points to produce a vector
	 * 
	 * @param other
	 *          the offset to add to this point
	 * @return a new point
	 */
	final public WorldVector subtract(WorldLocation other, WorldVector res)
	{
		// check we have our model
		if (_model == null)
			_model = new MWC.Algorithms.EarthModels.FlatEarth();

		res = _model.subtract(other, this, res);

		return res;
	}

	/**
	 * subtract the two points to produce a vector
	 * 
	 * @param other
	 *          the offset to add to this point
	 * @return a new point
	 */
	final public WorldDistance rangeFrom(WorldLocation other, WorldDistance res)
	{
		// check we have our model
		if (_model == null)
			_model = new MWC.Algorithms.EarthModels.FlatEarth();

		// ok, how far apart are they?
		WorldVector sep = _model.subtract(other, this);

		// update the results object
		res.setValues(sep.getRange(), WorldDistance.DEGS);

		// and return it.
		return res;
	}

	/**
	 * calculate the range from the other point (Degs)
	 * 
	 * @return the range (in degrees)
	 */
	final public double rangeFrom(WorldLocation other)
	{
		WorldVector res = subtract(other, _myWorldVector);

		return res.getRange();
	}

	/**
	 * calculate the range from the nearest point on the suppled line
	 * 
	 * @return the range
	 */
	final public WorldDistance rangeFrom(WorldLocation lineStart,
			WorldLocation lineEnd)
	{
		return perpendicularDistanceBetween(lineStart, lineEnd);
	}

	/**
	 * calculate the bearing from the other point (rads)
	 * 
	 * @return the bearing (rads)
	 */
	final public double bearingFrom(WorldLocation other)
	{
		WorldVector res = subtract(other, _myWorldVector);

		return res.getBearing();
	}

	/**
	 * create a new point that is this point rotated by set radians about the
	 * supplied axis
	 * 
	 * @param pOrigin
	 *          centre of rotation
	 * @param brg
	 *          angle rotated through (radians)
	 * @return new location
	 */
	final public WorldLocation rotatePoint(final WorldLocation pOrigin, double brg)
	{
		double resLong = pOrigin.getLong()
				+ (Math.cos((brg)) * (this.getLong() - pOrigin.getLong()) - Math
						.sin(brg) * (this.getLat() - pOrigin.getLat()));
		double resLat = pOrigin.getLat()
				+ (Math.sin((brg)) * (this.getLong() - pOrigin.getLong()) + Math
						.cos(brg) * (this.getLat() - pOrigin.getLat()));
		WorldLocation res = new WorldLocation(resLat, resLong, 0d);
		return res;
	}

	/**
	 * note that ADD returns a new object, this remains constant
	 * 
	 * @param delta
	 *          the offset to add to this point
	 * @return a new point
	 */
	final public WorldLocation add(WorldVector delta)
	{
		// check we have our model
		if (_model == null)
			_model = new MWC.Algorithms.EarthModels.FlatEarth();

		// do the calculation with our current model
		
		// do we have a range?
		final WorldLocation res;
		
		if((delta.getRange() == 0) && (delta.getDepth() == 0))
			res = new WorldLocation(this);
		else
			res = new WorldLocation(_model.add(this, delta));

		// and return the resutls
		return res;
	}

	/**
	 * provide setter method to allow us to override the earth model used for
	 * calculating separations of locations
	 */
	static final public void setModel(MWC.Algorithms.EarthModel theModel)
	{
		_model = theModel;
	}

	/**
	 * provide setter method to allow us to override the earth model used for
	 * calculating separations of locations
	 */
	static final public MWC.Algorithms.EarthModel getModel()
	{
		return _model;
	}

	/**
	 * note that addToMe changes this object
	 * 
	 * @param delta
	 *          the offset to add to this point
	 */
	public void addToMe(WorldVector delta)
	{
		// check we have our model
		if (_model == null)
		{
			_model = new MWC.Algorithms.EarthModels.FlatEarth();
		}

		// do the calculation with our model
		WorldLocation res = _model.add(this, delta);

		// update ourselves to the result
		setLat(res.getLat());
		setLong(res.getLong());
		setDepth(res.getDepth());

	}

	public String toString()
	{
		String res = "";
		res += MWC.Utilities.TextFormatting.BriefFormatLocation.toString(this);
		res += " ";
		return res;
	}

	final public boolean equals(Object other)
	{
		boolean res = false;

		if (other instanceof WorldLocation)
		{
			res = this.equals((WorldLocation) other);
		}

		return res;
	}

	final public boolean equals(WorldLocation o)
	{

		boolean res = true;
		if (o._theDepth != _theDepth)
			res = false;
		// ok, let's allow a little flexibility here... lets
		// do it to the nearest 10e-10 meters (yes, a millionth of a meter)
		if (Math.abs(o._theLat - _theLat) > 1.0E-11)
			res = false;
		if (Math.abs(o._theLong - _theLong) > 1.0E-11)
			res = false;

		return res;
	}

	// make this location a copy of the indicated one
	final public void copy(WorldLocation other)
	{
		_theLat = other._theLat;
		_theLong = other._theLong;
		_theDepth = other._theDepth;
	}

	public boolean isValid()
	{
		boolean res = true;

		// first check lat
		double lat = getLat();
		if ((lat > 90) || (lat < -90))
		{
			res = false;
		}
		else
		{
			// now check long
			double lon = getLong();
			if ((lon > 180) || (lon < -180))
			{
				res = false;
			}
			// ignore the depth
		}

		return res;
	}

	/**
	 * work out the perpendicular distance between me and the supplied line
	 * segment
	 * 
	 * @param lineStart
	 *          start point of the line
	 * @param lineEnd
	 *          end point of the line
	 * @return perpendicular distance off track.
	 */
	protected WorldDistance perpendicularDistanceBetween(WorldLocation lineStart,
			WorldLocation lineEnd)
	{
		
		Point2D pStart = new Point2D.Double(lineStart.getLong(), lineStart.getLat());
		Point2D pEnd = new Point2D.Double(lineEnd.getLong(), lineEnd.getLat());
		Point2D tgt = new Point2D.Double(this.getLong(), this.getLat());
		
		double res = distanceToSegment(pStart, pEnd, tgt);
		WorldDistance distance = new WorldDistance(res, WorldDistance.DEGS);
		
		//Note: we were using an algorithm to calculate the dist to a point on a continuous line, not
		// a line segment.  The above code class the correct algorithm.
//		// sort out known angles
//		double thetaOne = lineEnd.bearingFrom(lineStart);
//		double thetaTwo = Math.PI - lineStart.bearingFrom(this);
//		double thetaThree = thetaOne + thetaTwo;
//
//		// and the single known distance
//		double rangeToP1 = lineStart.rangeFrom(this);
//
//		// now do our trig.
//		double sinThetaThree = Math.abs(Math.sin(thetaThree));
//		WorldDistance distance = new WorldDistance(rangeToP1 * sinThetaThree,
//				WorldDistance.DEGS);

		// sorted.
		return distance;
	}

	/** algorithm taken from DistancePoint.java
	 * DistancePointSegmentExample, calculate distance to line
   * Copyright (C) 2008 Pieter Iserbyt <pieter.iserbyt@gmail.com>
   * Alogrithm found via Stack Overflow page at: http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
   * 
	 * @param lineStart point (in degs) for line start
	 * @param lineEnd point (in degs) for line end
	 * @param tgtPoint point (in degs) for point of interest
	 * @return distance in degs from line to point
	 */
	private static double distanceToSegment(Point2D lineStart, Point2D lineEnd, Point2D tgtPoint)
	{

		final double xDelta = lineEnd.getX() - lineStart.getX();
		final double yDelta = lineEnd.getY() - lineStart.getY();

		if ((xDelta == 0) && (yDelta == 0))
		{
			throw new IllegalArgumentException("lineStart and lineEnd cannot be the same point");
		}

		final double u = ((tgtPoint.getX() - lineStart.getX()) * xDelta + (tgtPoint.getY() - lineStart.getY())
				* yDelta)
				/ (xDelta * xDelta + yDelta * yDelta);

		final Point2D closestPoint;
		if (u < 0)
		{
			closestPoint = lineStart;
		}
		else if (u > 1)
		{
			closestPoint = lineEnd;
		}
		else
		{
			closestPoint = new Point2D.Double(lineStart.getX() + u * xDelta, lineStart.getY() + u
					* yDelta);
		}

		return closestPoint.distance(tgtPoint);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class LocationTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";
		private WorldLocation w1;
		private WorldLocation w2;
		private WorldVector wv1;
		private WorldLocation w4;
		private WorldLocation w5;

		public LocationTest(String val)
		{
			super(val);
		}

		public final void setUp()
		{
			// set the earth model we are expecting
			MWC.GenericData.WorldLocation
					.setModel(new MWC.Algorithms.EarthModels.FlatEarth());

			w1 = new WorldLocation(12.3, 12.4, 12.5);
			w2 = new WorldLocation(12.3, 12.4, 12.5);
			wv1 = new WorldVector(0, 1, 0);
			w4 = new WorldLocation(13.3, 12.4, 12.5);
			w5 = new WorldLocation(12.225, 12.275, 12.5);
		}

		public final void tearDown()
		{
			w1 = null;
			w2 = null;
			wv1 = null;
			w4 = null;
			w5 = null;
		}

		public final void testPerpDistanceFrom()
		{
			WorldLocation.setModel(new CompletelyFlatEarth());

			WorldLocation me = new WorldLocation(7, 0, 0);
			WorldLocation p1 = new WorldLocation(4, 4, 0);
			WorldLocation p2 = new WorldLocation(12, 4, 0);

			WorldDistance res = me.perpendicularDistanceBetween(p1, p2);
			assertEquals("off-track error is correct", 4.0,
					res.getValueIn(WorldDistance.DEGS), 0.001);

			p2 = new WorldLocation(9, 2, 0);
			res = me.perpendicularDistanceBetween(p1, p2);
			assertEquals("off-track error is correct", 2.5997,
					res.getValueIn(WorldDistance.DEGS), 0.001);

			p2 = new WorldLocation(-4, -4, 0);
			res = me.perpendicularDistanceBetween(p1, p2);
			assertEquals("off-track error is correct", 4.9497,
					res.getValueIn(WorldDistance.DEGS), 0.001);

			res = me.rangeFrom(p1, p2);
			assertEquals("off-track error is correct (using range from operator)",
					4.9497, res.getValueIn(WorldDistance.DEGS), 0.001);

		}

		public void testDecimalConstructor()
		{
			// test the secs component
			WorldLocation worldLoc = new WorldLocation(22, 0, 45, 'N', 22, 0, 1.45,
					'W', 0);
			assertEquals("right lat", 22.0125, worldLoc.getLat(), 0.001);
			assertEquals("right long", -22.000402, worldLoc.getLong(), 0.00001);

			// now the mins component
			worldLoc = new WorldLocation(22, 0.5, 00, 'N', 14, 0.5, 0, 'W', 0);
			assertEquals("right lat", 22.008, worldLoc.getLat(), 0.01);
			assertEquals("right long", -14.008333, worldLoc.getLong(), 0.00001);

			// now the degs component
			worldLoc = new WorldLocation(22.5, 0, 00, 'N', 14.5, 0, 0, 'W', 0);
			assertEquals("right lat", 22.5, worldLoc.getLat(), 0.01);
			assertEquals("right long", -14.5, worldLoc.getLong(), 0.00001);

			//
			// NOW LET'S REVERSE THE HEMISPHERE
			// test the secs component
			worldLoc = new WorldLocation(22, 0, 45, 'S', 22, 0, 1.45, 'E', 0);
			assertEquals("right lat", -22.0125, worldLoc.getLat(), 0.001);
			assertEquals("right long", 22.000402, worldLoc.getLong(), 0.00001);

			// now the mins component
			worldLoc = new WorldLocation(22, 0.5, 00, 'S', 14, 0.5, 0, 'E', 0);
			assertEquals("right lat", -22.008, worldLoc.getLat(), 0.01);
			assertEquals("right long", 14.008333, worldLoc.getLong(), 0.00001);

			// now the degs component
			worldLoc = new WorldLocation(22.5, 0, 00, 'S', 14.5, 0, 0, 'E', 0);
			assertEquals("right lat", -22.5, worldLoc.getLat(), 0.01);
			assertEquals("right long", 14.5, worldLoc.getLong(), 0.00001);

		}

		public final void testConstructor()
		{
			WorldLocation v1 = new WorldLocation(12.225, 12.275, 12.5);
			WorldLocation v2 = new WorldLocation(12, 13.5, 0, 'N', 12, 16.5, 0, 'E',
					12.5);
			WorldLocation v3 = new WorldLocation(12, 13, 30, 'N', 12, 16, 30, 'E',
					12.5);
			WorldLocation v4 = new WorldLocation(w5);

			assertTrue("v1 (d,d,d)", v1.equals(w5));
			assertTrue("v2 (i,d,c,i,d,c,d)", v2.equals(w5));
			assertTrue("v3 (i,i,d,c,i,i,d,c,d)", v3.equals(w5));
			assertTrue("v4 (WorldLoc)", v4.equals(w5));
		}

		public final void testEquals()
		{
			assertTrue(w1.equals(w2));
			assertTrue(!w1.equals(w5));
		}

		public final void testCopy()
		{
			WorldLocation w3 = new WorldLocation(0, 0, 0);
			w3.copy(w2);
			assertTrue(w2.equals(w3));
		}

		public final void testAdd()
		{
			WorldLocation ww = w1.add(wv1);
			assertTrue(w4.equals(ww));
		}

		public final void testAddToMe()
		{
			w1.addToMe(wv1);
			assertTrue(w1.equals(w4));
		}

		public final void testBearingFrom()
		{
			double brg = w4.bearingFrom(w1);
			assertTrue(brg == 0d);
		}

		public final void testGetDepth()
		{
			assertEquals(w1.getDepth(), 12.5, 0d);
		}

		public final void testGetLat()
		{
			assertEquals(w1.getLat(), 12.3, 0d);
		}

		public final void testGetLong()
		{
			assertEquals(w1.getLong(), 12.4, 0d);
		}

		public final void testRangeFrom()
		{
			double rng = w4.rangeFrom(w1);
			assertEquals(rng, 1.0, 0.0001d);
		}

		public final void setXXX()
		{
			double dep = w4.getDepth();
			double dLat = w4.getLat();
			double dLong = w4.getLong();
			w1.setDepth(dep);
			w1.setLat(dLat);
			w1.setLong(dLong);
			assertTrue(w1.equals(w4));
		}

		public final void setSubtract()
		{
			WorldVector wvv = w4.subtract(w1);
			assertTrue(wvv.equals(wv1));
		}

	}

	/**
	 * *******************************************************************
	 * convenience class which allows a WorldLocation to be created using a world
	 * distance instead of a double for depth/heught
	 * *******************************************************************
	 */
	public static class LocalLocation extends WorldLocation
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param lat
		 *          the latitude
		 * @param longVal
		 *          the longitude
		 * @param height
		 *          the height
		 */
		public LocalLocation(double lat, double longVal, WorldDistance height)
		{
			super(lat, longVal, -height.getValueIn(WorldDistance.METRES));
		}

		/**
		 * @param north
		 *          - how far north we are
		 * @param east
		 *          - how far east we are
		 * @param height
		 *          the height
		 */
		public LocalLocation(WorldDistance north, WorldDistance east,
				WorldDistance height)
		{
			super(north.getValueIn(WorldDistance.DEGS), east
					.getValueIn(WorldDistance.DEGS), -height
					.getValueIn(WorldDistance.METRES));
		}

		/**
		 * @param north
		 *          - how far north we are
		 * @param east
		 *          - how far east we are
		 * @param height
		 *          the height
		 */
		public LocalLocation(WorldDistance north, WorldDistance east, double height)
		{
			super(north.getValueIn(WorldDistance.DEGS), east
					.getValueIn(WorldDistance.DEGS), -height);
		}

		/**
		 * @param lat
		 *          the latitude
		 * @param longVal
		 *          the longitude
		 */
		public LocalLocation(double lat, double longVal)
		{
			super(lat, longVal, 0);
		}

		/**
		 * long winded constructor, taking raw arguments
		 */
		public LocalLocation(int latDegs, int latMin, double latSec, char latHem,
				int longDegs, int longMin, double longSec, char longHem,
				WorldDistance height)
		{
			super(latDegs, latMin, latSec, latHem, longDegs, longMin, longSec,
					longHem, -height.getValueIn(WorldDistance.METRES));
		}

		/**
		 * long winded constructor, taking raw arguments
		 */
		public LocalLocation(int latDegs, int latMin, double latSec, char latHem,
				int longDegs, int longMin, double longSec, char longHem)
		{
			super(latDegs, latMin, latSec, latHem, longDegs, longMin, longSec,
					longHem, 0);
		}
	}

	public static void main(String[] args)
	{
		LocationTest lt = new LocationTest("here");
		lt.testPerpDistanceFrom();
	}

}
 
