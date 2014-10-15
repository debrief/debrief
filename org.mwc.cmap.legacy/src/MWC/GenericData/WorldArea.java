/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GenericData;

import java.io.Serializable;

/**
 * Represents a rectangular area in world coordinates, as implemented as in
 * {@link WorldLocation} Last registered: $Date: 2006/04/26 12:39:07 $
 */
public final class WorldArea implements Serializable
{

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	// keep track of versions
	static final long serialVersionUID = 1;

	/**
	 * BottomRight (South-East) corner
	 */
	private WorldLocation _bottomRight;

	/**
	 * TopLeft (North-West) corner
	 */
	private WorldLocation _topLeft;

	/**
	 * top right corner
	 */
	private WorldLocation _topRight;

	/**
	 * bottom left corner
	 */
	private WorldLocation _bottomLeft;

	/**
	 * use internal WorldLocation for centre, to stop us having to create a new
	 * one so frequently
	 */
	private WorldLocation _thisCentre = null;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * Constructor, takes the two coordinates. They don't really have to be the
	 * right way round, since we do a normalise anyway.
	 * 
	 * @see #normalise
	 */
	public WorldArea(final WorldLocation TopLeftVal, final WorldLocation BottomRightVal)
	{
		// remember to normalise
		_topLeft = new WorldLocation(TopLeftVal);
		_bottomRight = new WorldLocation(BottomRightVal);
		_topRight = new WorldLocation(_topLeft);
		_bottomLeft = new WorldLocation(_bottomRight);

		normalise();
	}

	/**
	 * other constructor, takes copy of coordinates in target area
	 */
	public WorldArea(final WorldArea other)
	{
		this(other._topLeft, other._bottomRight);
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * function to determine the centre of the area
	 * 
	 * @return WorldLocation - the centre of the Area
	 */
	final public WorldLocation getCentre()
	{
		final double dLat = getHeight() / 2.0;
		final double dLong = (_bottomRight.getLong() - _topLeft.getLong()) / 2.0;
		final double dDepth = getDepthRange() / 2.0;

		// do we need to create our centre?
		if (_thisCentre == null)
		{
			_thisCentre = new WorldLocation(0, 0, 0);
		}

		// update properties of centre
		_thisCentre.setLat(_bottomRight.getLat() + dLat);
		_thisCentre.setLong(_topLeft.getLong() + dLong);
		_thisCentre.setDepth(_bottomRight.getDepth() + dDepth);

		return _thisCentre;
	}

	/**
	 * function to shift the centre of the area
	 * 
	 * @param newCentre
	 *          the new centre of the area
	 */
	final public void setCentre(final WorldLocation newCentre)
	{
		// what's the area
		final double wid = this.getFlatEarthWidth();
		final double ht = this.getHeight();
		final double depth = this.getCentre().getDepth();

		final WorldLocation newTL = new WorldLocation(newCentre.getLat() + ht / 2,
				newCentre.getLong() - wid / 2, depth);

		final WorldLocation newBR = new WorldLocation(newCentre.getLat() - ht / 2,
				newCentre.getLong() + wid / 2, depth);

		_topLeft = newTL;
		_bottomRight = newBR;

		_thisCentre = null;
	}

	/**
	 * function to return the centre of the area (at zero depth)
	 * 
	 * @return WorldLocation the zero-depth centre of the area
	 */
	final public WorldLocation getCentreAtSurface()
	{
		final WorldLocation res = getCentre();
		res.setDepth(0);
		return res;
	}

	final public WorldLocation getTopLeft()
	{
		return _topLeft;
	}

	final public WorldLocation getBottomRight()
	{
		return _bottomRight;
	}

	final public void setBottomRight(final WorldLocation loc)
	{
		_bottomRight = loc;
	}

	final public void setTopLeft(final WorldLocation loc)
	{
		_topLeft = loc;
	}

	final public WorldLocation getTopRight()
	{
		return _topRight;
		// return new WorldLocation(_topLeft.getLat(), _bottomRight.getLong(),
		// _topLeft.getDepth());
	}

	final public WorldLocation getBottomLeft()
	{
		return _bottomLeft;
		// return new WorldLocation(_bottomRight.getLat(), _topLeft.getLong(),
		// _bottomRight.getDepth());
	}

	final public String toString()
	{
		String res = "";
		res += " Area TL:" + _topLeft + " BR:" + _bottomRight + " ("
				+ _topLeft.getLong() + ") ";
		return res;
	}

	/**
	 * see if the WorldLocation is in this area
	 * 
	 * @return flag for contains
	 */
	final public boolean contains(final WorldLocation other)
	{
		boolean res = true;
		if (this._topLeft._theLat < other._theLat)
			res = false;
		else if (this._topLeft._theLong > other._theLong)
			res = false;
		else if (this._bottomRight._theLat > other._theLat)
			res = false;
		else if (this._bottomRight._theLong < other._theLong)
			res = false;
		else if (this._topLeft._theDepth < other._theDepth)
			res = false;
		else if (this._bottomRight._theDepth > other._theDepth)
			res = false;

		return res;
	}

	/**
	 * find the range of the nearest corner (or the centre point) to the indicated
	 * point
	 */
	final public double rangeFrom(final WorldLocation other)
	{
		// first the TL/BR corners
		final double r1 = getTopLeft().rangeFrom(other);
		final double r2 = getBottomRight().rangeFrom(other);
		// now the centre
		final double r3 = getCentre().rangeFrom(other);
		// now the TR / BL corners
		final double r4 = getTopRight().rangeFrom(other);
		final double r5 = getBottomLeft().rangeFrom(other);

		// System.out.print("ranges:" + (int)(10000d * r1));
		// System.out.println(", " + (int)(r2 *10000d)+ ", " +
		// (int)(r3*10000d) + ", " + (int)(r4*10000d) + ", " + (int)(r5*10000d));

		double res = Math.min(r1, r2);
		res = Math.min(res, r3);
		res = Math.min(res, r4);
		res = Math.min(res, r5);

		// sort out the min
		return res;
	}

	/**
	 * find the range of the nearest corner (or the centre point) to the indicated
	 * point
	 */
	final public double rangeFromEdge(final WorldLocation other)
	{
		double res;

		if (this.contains(other))
			res = 0;
		else
		{
			// first the TL/BR corners
			final double r1 = distancePointLine(getTopLeft(), getTopRight(), other);
			final double r2 = distancePointLine(getTopRight(), getBottomRight(), other);
			final double r3 = distancePointLine(getBottomRight(), getBottomLeft(), other);
			final double r4 = distancePointLine(getBottomLeft(), getTopLeft(), other);

			res = Math.min(r1, r2);
			res = Math.min(res, r3);
			res = Math.min(res, r4);
		}
		// sort out the min
		return res;
	}
	
  /**
   * Computes the distance from a point p to a line segment AB
   * This is a modified version of the sources from the Java Topology Suite
   *
   * Note: NON-ROBUST!
   *
   * @param p the point to compute the distance for
   * @param A one point of the line
   * @param B another point of the line (must be different to A)
   * @return the distance from p to line segment AB
   */
  private static double distancePointLine(final WorldLocation A, final WorldLocation B, final WorldLocation p)
  {
    // if start==end, then use pt distance
    if (  A.equals(B) ) return p.rangeFrom(A);

    // otherwise use comp.graphics.algorithms Frequently Asked Questions method
    /*(1)     	      AC dot AB
                   r = ---------
                         ||AB||^2
		r has the following meaning:
		r=0 P = A
		r=1 P = B
		r<0 P is on the backward extension of AB
		r>1 P is on the forward extension of AB
		0<r<1 P is interior to AB
	*/
    /** NOTE: we use getLat for .y and getLong for .x */

    final double r = ( (p.getLong() - A.getLong()) * (B.getLong() - A.getLong()) + (p.getLat() - A.getLat()) * (B.getLat() - A.getLat()) )
              /
            ( (B.getLong() - A.getLong()) * (B.getLong() - A.getLong()) + (B.getLat() - A.getLat()) * (B.getLat() - A.getLat()) );

    if (r <= 0.0) return p.rangeFrom(A);
    if (r >= 1.0) return p.rangeFrom(B);


    /*(2)
		     (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay)
		s = -----------------------------
		             	L^2

		Then the distance from C to P = |s|*L.
	*/

    final double s = ((A.getLat() - p.getLat()) *(B.getLong() - A.getLong()) - (A.getLong() - p.getLong())*(B.getLat() - A.getLat()) )
              /
            ((B.getLong() - A.getLong()) * (B.getLong() - A.getLong()) + (B.getLat() - A.getLat()) * (B.getLat() - A.getLat()) );

    return
      Math.abs(s) *
      Math.sqrt(((B.getLong() - A.getLong()) * (B.getLong() - A.getLong()) + (B.getLat() - A.getLat()) * (B.getLat() - A.getLat())));
  }
	

	/**
	 * Returns the distance of p3 to the segment defined by p1,p2;
	 * 
	 * @return The distance of p3 to the segment defined by p1,p2
	 */
//	private static double distanceToSegment(WorldLocation p1, WorldLocation p2,
//			WorldLocation p3)
//	{
//
//		final double xDelta = p2.getLong() - p1.getLong();
//		final double yDelta = p2.getLat() - p1.getLat();
//		final double res;
//
//		if ((xDelta == 0) && (yDelta == 0))
//		{
//			// ok, the two corners are the same, we just treat this as a single point
//			res = p1.rangeFrom(p3);
//		}
//		else
//		{
//
//			final double u = ((p3.getLong() - p1.getLong()) * xDelta + (p3.getLat() - p1
//					.getLat()) * yDelta)
//					/ (xDelta * xDelta + yDelta * yDelta);
//
//			final WorldLocation closestPoint;
//			if (u < 0)
//			{
//				closestPoint = p1;
//			}
//			else if (u > 1)
//			{
//				closestPoint = p2;
//			}
//			else
//			{
//				closestPoint = new WorldLocation(p1.getLat() + u * yDelta, p1.getLong()
//						+ u * xDelta, 0);
//			}
//
//			res = closestPoint.rangeFrom(p3);
//		}
//		return res;
//	}

	/**
	 * see if the two areas overlap
	 * 
	 * @return flag for overlap
	 */
	final public boolean overlaps(final WorldArea other)
	{
		boolean res = true;

		// see if the bottom left/top right overlap
		if (other._bottomRight._theLat > this._topLeft._theLat)
			res = false;
		else if (other._topLeft._theLat < this._bottomRight._theLat)
			res = false;
		else if (other._bottomRight._theLong < this._topLeft._theLong)
			res = false;
		else if (other._topLeft._theLong > this._bottomRight._theLong)
			res = false;

		return res;
	}

	/**
	 * grow the area to include a border or the indicated degrees
	 */
	final public void grow(final double border_degs, final double depth_metres)
	{
		_topLeft.setLat(_topLeft.getLat() + border_degs);
		_topLeft.setLong(_topLeft.getLong() - border_degs);
		_topLeft.setDepth(_topLeft.getDepth() + depth_metres);
		_bottomRight.setLat(_bottomRight.getLat() - border_degs);
		_bottomRight.setLong(_bottomRight.getLong() + border_degs);
		_bottomRight.setDepth(_bottomRight.getDepth() - depth_metres);
	}

	/**
	 * extend the area to include this new point. includes a normalise operation
	 */
	final public void extend(final WorldLocation newPoint)
	{

		// check there is a valid point
		if (newPoint == null)
			return;

		if (this.contains(newPoint))
			return;

		// set the limits by hand

		// first the areas
		_topLeft._theLat = Math.max(_topLeft._theLat, newPoint._theLat);
		_topLeft._theLong = Math.min(_topLeft._theLong, newPoint._theLong);
		_bottomRight._theLat = Math.min(_bottomRight._theLat, newPoint._theLat);
		_bottomRight._theLong = Math.max(_bottomRight._theLong, newPoint._theLong);
		_topRight._theLat = Math.max(_topRight._theLat, newPoint._theLat);
		_topRight._theLong = Math.max(_topRight._theLong, newPoint._theLong);
		_bottomLeft._theLat = Math.min(_bottomLeft._theLat, newPoint._theLat);
		_bottomLeft._theLong = Math.min(_bottomLeft._theLong, newPoint._theLong);

		// now the depths - if this point has a valid depth
		if (newPoint.hasValidDepth())
		{
			_topLeft._theDepth = Math.max(_topLeft._theDepth, newPoint._theDepth);
			_bottomRight._theDepth = Math.min(_bottomRight._theDepth,
					newPoint._theDepth);
			_topRight._theDepth = Math.max(_topLeft._theDepth, newPoint._theDepth);
			_bottomLeft._theDepth = Math.min(_bottomRight._theDepth,
					newPoint._theDepth);
		}
	}

	/**
	 * extend the area to include this new area. Includes a normalise operation
	 */
	final public void extend(final WorldArea newArea)
	{
		// check we've received a valid area
		if (newArea == null)
			return;

		// extend for each corner of the incoming area
		extend(newArea._topLeft);
		extend(newArea._bottomRight);
	}

	/**
	 * make sure the corners are the correct way around.
	 */
	final public void normalise()
	{

		double maxLat, maxLong, maxDepth;
		double minLat, minLong, minDepth;

		// working variables
		if (_topLeft._theLat > _bottomRight._theLat)
		{
			maxLat = _topLeft._theLat;
			minLat = _bottomRight._theLat;
		}
		else
		{
			maxLat = _bottomRight._theLat;
			minLat = _topLeft._theLat;
		}

		if (_topLeft._theLong > _bottomRight._theLong)
		{
			maxLong = _topLeft._theLong;
			minLong = _bottomRight._theLong;
		}
		else
		{
			maxLong = _bottomRight._theLong;
			minLong = _topLeft._theLong;
		}

		if (_topLeft._theDepth > _bottomRight._theDepth)
		{
			maxDepth = _topLeft._theDepth;
			minDepth = _bottomRight._theDepth;
		}
		else
		{
			maxDepth = _bottomRight._theDepth;
			minDepth = _topLeft._theDepth;
		}

		/*
		 * maxLat = Math.max(_topLeft._theLat, _bottomRight._theLat); minLong =
		 * Math.min(_topLeft._theLong, _bottomRight._theLong); minLat =
		 * Math.min(_topLeft._theLat, _bottomRight._theLat); maxLong =
		 * Math.max(_topLeft._theLong, _bottomRight._theLong); maxDepth =
		 * Math.max(_topLeft._theDepth, _bottomRight._theDepth); minDepth =
		 * Math.min(_topLeft._theDepth, _bottomRight._theDepth);
		 */

		// see if we need to create new values
		if ((_topLeft == null) || (_bottomRight == null))
		{
			// assign new values
			_topLeft = new WorldLocation(maxLat, minLong, maxDepth);
			_bottomRight = new WorldLocation(minLat, maxLong, minDepth);

			_topRight = new WorldLocation(maxLat, maxLong, maxDepth);
			_bottomLeft = new WorldLocation(minLat, minLong, minDepth);

		}
		else
		{
			// update the values
			_topLeft._theLat = maxLat;
			_topLeft._theLong = minLong;
			_topLeft._theDepth = maxDepth;
			_bottomRight._theLat = minLat;
			_bottomRight._theLong = maxLong;
			_bottomRight._theDepth = minDepth;

			_topRight._theLat = maxLat;
			_topRight._theLong = maxLong;
			_topRight._theDepth = maxDepth;
			_bottomLeft._theLat = minLat;
			_bottomLeft._theLong = minLong;
			_bottomLeft._theDepth = minDepth;

		}

	}

	final public boolean equals(final Object tt)
	{
		if (!(tt instanceof WorldArea))
			return false;

		final WorldArea o = (WorldArea) tt;
		boolean res = true;
		if (!o.getTopLeft().equals(getTopLeft()))
			res = false;
		if (!o.getBottomRight().equals(getBottomRight()))
			res = false;

		return res;
	}

	/**
	 * get the width of this area in absolute degrees - not degrees at the equator
	 * 
	 * @return width in degrees at this point of latitide
	 */
	final public double getFlatEarthWidth()
	{
		return _bottomRight.getLong() - _topLeft.getLong();
	}

	/**
	 * return the width of the area (in degrees)
	 */
	final public double getWidth()
	{
		/**
		 * note we don't just return the value in degrees, we switch the width of
		 * this area to give a value in degrees at the equator
		 */

		// determine the mid-latitude
		double midLat = getHeight() / 2.0;
		midLat = _bottomRight.getLat() + midLat;

		// create the points we are going to work with
		final WorldLocation leftPoint = new WorldLocation(midLat, _topLeft.getLong(), 0.0);
		final WorldLocation rightPoint = new WorldLocation(midLat,
				_bottomRight.getLong(), 0.0);

		// and calculate the range
		final double res = leftPoint.rangeFrom(rightPoint);

		return res;
	}

	/**
	 * return the height of the area (in degrees)
	 */
	final public double getHeight()
	{
		return _topLeft.getLat() - _bottomRight.getLat();
	}

	final public double getDepthRange()
	{
		return _topLeft.getDepth() - _bottomRight.getDepth();
	}

	/**
	 * convenience method used to produce a distribution of locations within this
	 * area. Using the total number of locations, this method will use the counter
	 * to calculate ths location of this point
	 * 
	 * @param counter
	 *          - how far we are through the distribution
	 * @param total
	 *          - the total number we are going to produce
	 * @return this location in the distribution
	 */
	final public WorldLocation getDistributedLocation(final int counter, final int total)
	{

		WorldLocation res = null;

		// what's our area in metres?
		final double widthMetres = MWC.Algorithms.Conversions.Degs2m(this
				.getWidth());
		final double heightMetres = MWC.Algorithms.Conversions.Degs2m(this
				.getHeight());
		final double areaMetres = widthMetres * heightMetres;

		// what's the area of each cell
		final double cellArea = areaMetres / total;

		// and what's their width?
		final double spacing = Math.sqrt(cellArea);

		// ok, how many will fit across is this?
		final int acrossSpacing = (int) (widthMetres / spacing);

		// divide counter by this
		int numDown, numAcross;
		if (acrossSpacing > 0)
		{
			numDown = counter / acrossSpacing;
			numAcross = counter - (numDown * acrossSpacing);
		}
		else
		{
			numDown = counter;
			numAcross = 1;
		}

		// how many does this leave

		// and calculate hte point
		final double latOffset = MWC.Algorithms.Conversions.m2Degs(numDown * spacing);
		final double longOffset = MWC.Algorithms.Conversions.m2Degs(numAcross * spacing);

		// now add this to the bottom-left of the area
		res = new WorldLocation(getBottomLeft().getLat() + latOffset,
				getBottomLeft().getLong() + longOffset, getBottomLeft().getDepth());

		return res;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class WorldAreaTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";
		WorldLocation w1;
		WorldLocation w2;
		WorldLocation w3;
		WorldLocation w4;
		WorldLocation w5;
		WorldArea wa1;
		WorldArea wa2;
		WorldArea wa3;
		WorldArea wa4;
		private WorldArea wa5;

		public WorldAreaTest(final String val)
		{
			super(val);
		}

		public final void setUp()
		{
			// set the earth model we are expecting
			MWC.GenericData.WorldLocation
					.setModel(new MWC.Algorithms.EarthModels.FlatEarth());

			w1 = new WorldLocation(12, 12, 0);
			w2 = new WorldLocation(10, 10, 100);
			w3 = new WorldLocation(9, 9, 110);
			w4 = new WorldLocation(58, 12, 0);
			w5 = new WorldLocation(62, 10, 100);
			wa1 = new WorldArea(w1, w2);
			wa2 = new WorldArea(w1, w2);
			wa3 = new WorldArea(w1, w3);
			wa4 = new WorldArea(w4, w5);
			wa5 = new WorldArea(w2, w5);
		}

		public final void tearDown()
		{
			w1 = null;
			w2 = null;
			w3 = null;
			wa1 = null;
			wa2 = null;
			wa3 = null;
		}

		public final void testRangeFromEdge()
		{
			double dist1 = wa1.rangeFromEdge(new WorldLocation(13, 11, 0));
			assertEquals("correct range", 1d, dist1, 0.01);
			dist1 = wa1.rangeFromEdge(new WorldLocation(11, 11, 0));
			assertEquals("correct range", 0d, dist1, 0.01);
			dist1 = wa1.rangeFromEdge(new WorldLocation(9, 10.4, 0));
			assertEquals("correct range", 1d, dist1, 0.01);
			dist1 = wa1.rangeFromEdge(new WorldLocation(9, 11, 0));
			assertEquals("correct range", 1d, dist1, 0.01);
			dist1 = wa1.rangeFromEdge(new WorldLocation(11, 13, 0));
			assertEquals("correct range", 1d, dist1, 0.02);
			dist1 = wa1.rangeFromEdge(new WorldLocation(11.2, 13, 0));
			assertEquals("correct range", 1d, dist1, 0.02);
			dist1 = wa1.rangeFromEdge(new WorldLocation(11, 9, 0));
			assertEquals("correct range", 1d, dist1, 0.02);
			dist1 = wa5.rangeFromEdge(new WorldLocation(10, 11, 100));
			assertEquals("correct range", 1d, dist1, 0.02);

		}
		
		public final void testDiffRangeFromEdge()
		{
			final WorldArea was = new WorldArea(new WorldLocation(0.5, 4.05, 0), new WorldLocation(0, 4.05, 0));
			final WorldLocation tgt =  new WorldLocation(0.127, 0.5, 0);
			final double dist = was.rangeFrom(tgt);
			assertTrue(dist > 1);
		}

		public final void testConstructor()
		{
			final WorldArea ww1 = new WorldArea(w1, w2);
			final WorldArea ww2 = new WorldArea(wa1);
			assertEquals("constructor worked", ww1.equals(wa1), true);
			assertEquals("constructor worked", ww2.equals(wa1), true);
		}

		public final void testContains()
		{
			// assuming the area is TL: 12, 9, 110 and BR: 9, 12, 0
			//
			final WorldLocation offLeft = new WorldLocation(10, 8, 0);
			final WorldLocation offRight = new WorldLocation(10, 13, 0);
			final WorldLocation offTop = new WorldLocation(14, 9, 0);
			final WorldLocation offBottom = new WorldLocation(7, 9, 0);
			final WorldLocation tooShallow = new WorldLocation(9, 9, -5);
			final WorldLocation tooDeep = new WorldLocation(9, 9, 200);
			assertTrue("w1 in wa3", wa3.contains(w1));
			assertTrue("w2 in wa3", wa3.contains(w2));
			assertTrue("w3 in wa3", wa3.contains(w3));
			assertTrue("offLeft outside wa2", !wa2.contains(offLeft));
			assertTrue("offRight outside wa2", !wa2.contains(offRight));
			assertTrue("offTop outside wa2", !wa2.contains(offTop));
			assertTrue("offBottom outside wa2", !wa2.contains(offBottom));
			assertTrue("tooShallow outside wa2", !wa2.contains(tooShallow));
			assertTrue("tooDeep outside wa2", !wa2.contains(tooDeep));
		}

		public final void testEquals()
		{
			final WorldArea ww = new WorldArea(w2, w1);
			assertTrue("Identical areas", ww.equals(wa1));
			final WorldArea ww2 = new WorldArea(w3, w1);
			assertTrue("Different areas", !ww2.equals(wa1));
		}

		public final void testExtend()
		{
			final WorldArea ww4 = new WorldArea(wa1);
			final WorldArea ww5 = new WorldArea(wa1);
			ww4.extend(w3);
			assertTrue("Extending using location", ww4.equals(wa3));
			ww5.extend(wa3);
			assertTrue("Extending using area", ww5.equals(wa3));
		}

		public final void testAreaCalcs()
		{
			w4 = new WorldLocation(58, 12, 0);
			w5 = new WorldLocation(62, 10, 100);
			wa4 = new WorldArea(w4, w5);
			assertTrue(wa4.getBottomRight().equals(new WorldLocation(58, 12, 0)));
			assertTrue(wa4.getTopLeft().equals(new WorldLocation(62, 10, 100)));
			assertTrue(wa4.getTopRight().equals(new WorldLocation(62, 12, 100)));
			assertTrue(wa4.getBottomLeft().equals(new WorldLocation(58, 10, 0)));
			final WorldLocation first = wa4.getCentre();
			final WorldLocation other = new WorldLocation(60, 11, 50);
			assertTrue(first.equals(other));
			assertEquals("Check depth range of area", wa4.getDepthRange(), 100, 0d);
			assertEquals("Check height of area", wa4.getHeight(), 4.0, 0d);
			assertEquals("Check width of area", wa4.getWidth(), 1.0, 0d);
		}

		public final void testMissingDepthData()
		{
			w4 = new WorldLocation(58, 12, 0);
			w5 = new WorldLocation(62, 10, 100);
			final WorldLocation w6 = new WorldLocation(64, 9, Double.NaN);
			wa4 = new WorldArea(w4, w5);

			// ok, try to extend it
			assertEquals("shallow depth valid", wa4.getBottomRight().getDepth(), 0d,
					0.1d);
			assertEquals("deep depth valid", wa4.getTopLeft().getDepth(), 100d, 0.1d);

			wa4.extend(w6);

			// ok, try to extend it
			assertEquals("shallow depth valid", wa4.getBottomRight().getDepth(), 0d,
					0.1d);
			assertEquals("deep depth valid", wa4.getTopLeft().getDepth(), 100d, 0.1d);
			assertEquals("lat updated", wa4.getTopLeft().getLat(), 64d, 0.1d);
			assertEquals("lat updated", wa4.getBottomRight().getLat(), 58, 0.1d);
			assertEquals("long updated", wa4.getTopLeft().getLong(), 9d, 0.1d);
			assertEquals("long updated", wa4.getBottomRight().getLong(), 12d, 0.1d);

		}

		public final void testNormalise()
		{
			final WorldArea ww1 = new WorldArea(w1, w3);
			final WorldArea ww2 = new WorldArea(w3, w1);
			assertTrue("Checking normalise", ww1.equals(ww2));
		}

		public final void testOverlap()
		{
			final WorldLocation aa = new WorldLocation(4, 3, 0);
			final WorldLocation ab = new WorldLocation(2, 5, 0);
			final WorldLocation a1a = new WorldLocation(6, 1, 0);
			final WorldLocation a1b = new WorldLocation(4, 2.9, 0);
			final WorldLocation a2a = new WorldLocation(6, 5.1, 0);
			final WorldLocation a2b = new WorldLocation(4, 7, 0);
			final WorldLocation a3a = new WorldLocation(5, 2, 0);
			final WorldLocation a3b = new WorldLocation(3, 4, 0);
			final WorldLocation a4a = new WorldLocation(5, 4, 0);
			final WorldLocation a4b = new WorldLocation(3, 6, 0);
			final WorldLocation a5a = new WorldLocation(3.5, 3.5, 0);
			final WorldLocation a5b = new WorldLocation(4.5, 4.5, 0);
			final WorldLocation a6a = new WorldLocation(3, 2, 0);
			final WorldLocation a6b = new WorldLocation(1, 4, 0);
			final WorldLocation a7a = new WorldLocation(3, 4, 0);
			final WorldLocation a7b = new WorldLocation(1, 6, 0);
			final WorldLocation a8a = new WorldLocation(2, 1, 0);
			final WorldLocation a8b = new WorldLocation(0, 2.9, 0);
			final WorldLocation a9a = new WorldLocation(2, 5.1, 0);
			final WorldLocation a9b = new WorldLocation(0, 7, 0);

			final WorldArea a1 = new WorldArea(a1a, a1b);
			final WorldArea a2 = new WorldArea(a2a, a2b);
			final WorldArea a3 = new WorldArea(a3a, a3b);
			final WorldArea a4 = new WorldArea(a4a, a4b);
			final WorldArea a5 = new WorldArea(a5a, a5b);
			final WorldArea a6 = new WorldArea(a6a, a6b);
			final WorldArea a7 = new WorldArea(a7a, a7b);
			final WorldArea a8 = new WorldArea(a8a, a8b);
			final WorldArea a9 = new WorldArea(a9a, a9b);

			final WorldArea waa = new WorldArea(aa, ab);
			assertTrue("a1", !waa.overlaps(a1));
			assertTrue("a2", !waa.overlaps(a2));
			assertTrue("a3", waa.overlaps(a3));
			assertTrue("a4", waa.overlaps(a4));
			assertTrue("a5", waa.overlaps(a5));
			assertTrue("a6", waa.overlaps(a6));
			assertTrue("a7", waa.overlaps(a7));
			assertTrue("a8", !waa.overlaps(a8));
			assertTrue("a9", !waa.overlaps(a9));
		}

		public final void testRangeFrom()
		{
			final WorldLocation a1a = new WorldLocation(6, 1, 0);
			final WorldLocation a1b = new WorldLocation(4, 3, 0);
			final WorldLocation a2a = new WorldLocation(6, 5, 0);
			final WorldLocation a2b = new WorldLocation(4, 7, 0);
			final WorldArea a1 = new WorldArea(a1a, a1b);
			final double r1 = a1.rangeFrom(a2a);
			final double r2 = a1.rangeFrom(a2b);
			final double r3 = a2a.rangeFrom(new WorldLocation(6, 3, 0));
			final double r4 = new WorldLocation(4, 3, 0d).rangeFrom(a2b);
			assertEquals("Checking range A from", r3, r1, 0d);
			assertEquals("Checking range B from", r4, r2, 0d);
		}

		public final void testRangeFrom2()
		{
			final WorldLocation a1a = new WorldLocation(0, 0, 0);
			final WorldLocation a1b = new WorldLocation(8, 4, 0);
			final WorldLocation a2a = new WorldLocation(0, 2, 0);
			final WorldLocation a2b = new WorldLocation(8, 2, 0);
			final WorldArea a1 = new WorldArea(a1a, a1b);
			final double r1 = a1.rangeFrom(a2a);
			final double r2 = a1.rangeFrom(a2b);
			// double r3 = a2a.rangeFrom(new WorldLocation(6, 3, 0));
			// double r4 = new WorldLocation(4, 3, 0d).rangeFrom(a2b);
			assertEquals("Checking range A from", 2, r1, 0d);
			assertEquals("Checking range B from", 2, r2, 0.1d);
		}

		public final void testGrow()
		{
			final WorldLocation a1a = new WorldLocation(6, 1, 0);
			final WorldLocation a1b = new WorldLocation(4, 3, 0);
			final WorldLocation newTL = new WorldLocation(7, 0, 100);
			final WorldLocation newBR = new WorldLocation(3, 4, -100);
			final WorldArea a1 = new WorldArea(a1a, a1b);
			a1.grow(1, 100);
			assertEquals("Checking new top left", newTL, a1.getTopLeft());
			assertEquals("Checking new bottom right", newBR, a1.getBottomRight());
		}

		public void testChangeCentre()
		{
			final WorldLocation wa = new WorldLocation(15, 13, 0);
			final WorldLocation wb = new WorldLocation(13, 15, 0);
			final WorldLocation wcenter = new WorldLocation(14, 14, 0);
			final WorldLocation wcenter_b = new WorldLocation(1, 1, 0);
			final WorldArea w_a = new WorldArea(wa, wb);
			// check the centre
			assertEquals("original centre is right", w_a.getCentre(), wcenter);

			// shift it
			w_a.setCentre(wcenter_b);

			// check the centre
			assertEquals("new centre is right", w_a.getCentre(), wcenter_b);

			// shift it
			w_a.setCentre(wcenter);

			// check the centre
			assertEquals("new centre is right", w_a.getCentre(), wcenter);

			// and the corners
			assertEquals("new TL", wa.getLat(), w_a.getTopLeft().getLat(), 0.03);
			assertEquals("new TL", wa.getLong(), w_a.getTopLeft().getLong(), 0.03);
			assertEquals("new BR", wb.getLat(), w_a.getBottomRight().getLat(), 0.03);
			assertEquals("new BR", wb.getLong(), w_a.getBottomRight().getLong(), 0.03);

			final WorldLocation currentTL = new WorldLocation(w_a.getTopLeft());

			// go through the cycle once again
			// shift it
			w_a.setCentre(wcenter_b);

			// check the centre
			assertEquals("new centre is right", w_a.getCentre(), wcenter_b);

			// shift it
			w_a.setCentre(wcenter);

			// check the centre
			assertEquals("new centre is right", w_a.getCentre(), wcenter);

			// and the updated top-left
			assertEquals("tl same as last time", currentTL, w_a.getTopLeft());

		}

		public void testDistribution()
		{
			final WorldArea theArea = new WorldArea(new WorldLocation(2, 2, 0),
					new WorldLocation(3, 3, 0));
			final WorldLocation first = theArea.getDistributedLocation(0, 10);

			assertEquals("first point correct", 2, first.getLat(), 0);
			assertEquals("first point correct", 2, first.getLong(), 0);

		}
	}

	/**
	 * generate a random location, uniformly distributed within this area
	 * 
	 * @return
	 */
	public WorldLocation getRandomLocation()
	{
		double _lat, _long, _depth;

		_lat = _bottomLeft.getLat() + Math.random() * getHeight();
		_long = _bottomLeft.getLong() + Math.random() * getWidth();
		_depth = _bottomLeft.getDepth() + Math.random() * getDepthRange();

		return new WorldLocation(_lat, _long, _depth);
	}

	private void trimLocation(final WorldLocation loc)
	{
		loc.setLat(Math.min(loc.getLat(), 90));
		loc.setLat(Math.max(loc.getLat(), -90));
		
		loc.setLong(Math.min(loc.getLong(), 180));
		loc.setLong(Math.max(loc.getLong(), -180));
		
	}
	
	public void trim()
	{
		// do it, one corner at a time
		trimLocation(_topLeft);
		trimLocation(_bottomRight);
		
		// and sort out the other corners
		normalise();
	}

}
