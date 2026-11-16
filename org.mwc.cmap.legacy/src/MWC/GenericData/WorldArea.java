/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.GenericData;

import java.io.Serializable;

/**
 * Represents a rectangular area in world coordinates, as implemented as in
 * {@link WorldLocation} Last registered: $Date: 2006/04/26 12:39:07 $
 */
public final class WorldArea implements Serializable {

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	// keep track of versions
	static final long serialVersionUID = 1;

	/**
	 * Computes the distance from a point p to a line segment AB This is a modified
	 * version of the sources from the Java Topology Suite
	 *
	 * Note: NON-ROBUST!
	 *
	 * @param p the point to compute the distance for
	 * @param A one point of the line
	 * @param B another point of the line (must be different to A)
	 * @return the distance from p to line segment AB
	 */
	private static double distancePointLine(final WorldLocation A, final WorldLocation B, final WorldLocation p) {
		// if start==end, then use pt distance
		if (A.equals(B))
			return p.rangeFrom(A);

		// otherwise use comp.graphics.algorithms Frequently Asked Questions method
		/*
		 * (1) AC dot AB r = --------- ||AB||^2 r has the following meaning: r=0 P = A
		 * r=1 P = B r<0 P is on the backward extension of AB r>1 P is on the forward
		 * extension of AB 0<r<1 P is interior to AB
		 */
		/** NOTE: we use getLat for .y and getLong for .x */

		final double r = ((p.getLong() - A.getLong()) * (B.getLong() - A.getLong())
				+ (p.getLat() - A.getLat()) * (B.getLat() - A.getLat()))
				/ ((B.getLong() - A.getLong()) * (B.getLong() - A.getLong())
						+ (B.getLat() - A.getLat()) * (B.getLat() - A.getLat()));

		if (r <= 0.0)
			return p.rangeFrom(A);
		if (r >= 1.0)
			return p.rangeFrom(B);

		/*
		 * (2) (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay) s = ----------------------------- L^2
		 *
		 * Then the distance from C to P = |s|*L.
		 */

		final double s = ((A.getLat() - p.getLat()) * (B.getLong() - A.getLong())
				- (A.getLong() - p.getLong()) * (B.getLat() - A.getLat()))
				/ ((B.getLong() - A.getLong()) * (B.getLong() - A.getLong())
						+ (B.getLat() - A.getLat()) * (B.getLat() - A.getLat()));

		return Math.abs(s) * Math.sqrt(((B.getLong() - A.getLong()) * (B.getLong() - A.getLong())
				+ (B.getLat() - A.getLat()) * (B.getLat() - A.getLat())));
	}

	/**
	 * Creates an area that contains both parameters
	 *
	 * @param current Area a
	 * @param newArea Area b
	 * @return a + b
	 */
	public static WorldArea extend(WorldArea current, final WorldArea newArea) {
		if (newArea != null) {
			if (current == null)
				current = new WorldArea(newArea);
			else
				current.extend(newArea);
		}
		return current;
	}

	/**
	 * BottomRight (South-East) corner
	 */
	private WorldLocation _bottomRight;

	/**
	 * TopLeft (North-West) corner
	 */
	private WorldLocation _topLeft;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * top right corner
	 */
	private final WorldLocation _topRight;

	/**
	 * bottom left corner
	 */
	private final WorldLocation _bottomLeft;

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * use internal WorldLocation for centre, to stop us having to create a new one
	 * so frequently
	 */
	private WorldLocation _thisCentre = null;

	/**
	 * other constructor, takes copy of coordinates in target area
	 */
	public WorldArea(final WorldArea other) {
		this(other._topLeft, other._bottomRight);
	}

	/**
	 * Constructor, takes the two coordinates. They don't really have to be the
	 * right way round, since we do a normalise anyway.
	 *
	 * @see #normalise
	 */
	public WorldArea(final WorldLocation TopLeftVal, final WorldLocation BottomRightVal) {
		// remember to normalise
		_topLeft = new WorldLocation(TopLeftVal);
		_bottomRight = new WorldLocation(BottomRightVal);
		_topRight = new WorldLocation(_topLeft);
		_bottomLeft = new WorldLocation(_bottomRight);

		normalise();
	}

	/**
	 * see if the WorldLocation is in this area
	 *
	 * @return flag for contains
	 */
	final public boolean contains(final WorldLocation other) {
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

	@Override
	final public boolean equals(final Object tt) {
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
	 * extend the area to include this new area. Includes a normalise operation
	 */
	final public void extend(final WorldArea newArea) {
		// check we've received a valid area
		if (newArea == null)
			return;

		// extend for each corner of the incoming area
		extend(newArea._topLeft);
		extend(newArea._bottomRight);
	}

	/**
	 * extend the area to include this new point. includes a normalise operation
	 */
	final public void extend(final WorldLocation newPoint) {

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
		if (newPoint.hasValidDepth()) {
			_topLeft._theDepth = Math.max(_topLeft._theDepth, newPoint._theDepth);
			_bottomRight._theDepth = Math.min(_bottomRight._theDepth, newPoint._theDepth);
			_topRight._theDepth = Math.max(_topLeft._theDepth, newPoint._theDepth);
			_bottomLeft._theDepth = Math.min(_bottomRight._theDepth, newPoint._theDepth);
		}
	}

	final public WorldLocation getBottomLeft() {
		return _bottomLeft;
		// return new WorldLocation(_bottomRight.getLat(), _topLeft.getLong(),
		// _bottomRight.getDepth());
	}

	final public WorldLocation getBottomRight() {
		return _bottomRight;
	}

	/**
	 * function to determine the centre of the area
	 *
	 * @return WorldLocation - the centre of the Area
	 */
	final public WorldLocation getCentre() {
		final double dLat = getHeight() / 2.0;
		final double dLong = (_bottomRight.getLong() - _topLeft.getLong()) / 2.0;
		final double dDepth = getDepthRange() / 2.0;

		// do we need to create our centre?
		if (_thisCentre == null) {
			_thisCentre = new WorldLocation(0, 0, 0);
		}

		// update properties of centre
		_thisCentre.setLat(_bottomRight.getLat() + dLat);
		_thisCentre.setLong(_topLeft.getLong() + dLong);
		_thisCentre.setDepth(_bottomRight.getDepth() + dDepth);

		return _thisCentre;
	}

	/**
	 * function to return the centre of the area (at zero depth)
	 *
	 * @return WorldLocation the zero-depth centre of the area
	 */
	final public WorldLocation getCentreAtSurface() {
		final WorldLocation res = getCentre();
		res.setDepth(0);
		return res;
	}

	final public double getDepthRange() {
		return _topLeft.getDepth() - _bottomRight.getDepth();
	}

	/**
	 * convenience method used to produce a distribution of locations within this
	 * area. Using the total number of locations, this method will use the counter
	 * to calculate ths location of this point
	 *
	 * @param counter - how far we are through the distribution
	 * @param total   - the total number we are going to produce
	 * @return this location in the distribution
	 */
	final public WorldLocation getDistributedLocation(final int counter, final int total) {

		WorldLocation res = null;

		// what's our area in metres?
		final double widthMetres = MWC.Algorithms.Conversions.Degs2m(this.getWidth());
		final double heightMetres = MWC.Algorithms.Conversions.Degs2m(this.getHeight());
		final double areaMetres = widthMetres * heightMetres;

		// what's the area of each cell
		final double cellArea = areaMetres / total;

		// and what's their width?
		final double spacing = Math.sqrt(cellArea);

		// ok, how many will fit across is this?
		final int acrossSpacing = (int) (widthMetres / spacing);

		// divide counter by this
		int numDown, numAcross;
		if (acrossSpacing > 0) {
			numDown = counter / acrossSpacing;
			numAcross = counter - (numDown * acrossSpacing);
		} else {
			numDown = counter;
			numAcross = 1;
		}

		// how many does this leave

		// and calculate hte point
		final double latOffset = MWC.Algorithms.Conversions.m2Degs(numDown * spacing);
		final double longOffset = MWC.Algorithms.Conversions.m2Degs(numAcross * spacing);

		// now add this to the bottom-left of the area
		res = new WorldLocation(getBottomLeft().getLat() + latOffset, getBottomLeft().getLong() + longOffset,
				getBottomLeft().getDepth());

		return res;
	}

	/**
	 * get the width of this area in absolute degrees - not degrees at the equator
	 *
	 * @return width in degrees at this point of latitide
	 */
	final public double getFlatEarthWidth() {
		return _bottomRight.getLong() - _topLeft.getLong();
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
	 * return the height of the area (in degrees)
	 */
	final public double getHeight() {
		return _topLeft.getLat() - _bottomRight.getLat();
	}

	/**
	 * generate a random location, uniformly distributed within this area
	 *
	 * @return
	 */
	public WorldLocation getRandomLocation() {
		double _lat, _long, _depth;

		_lat = _bottomLeft.getLat() + Math.random() * getHeight();
		_long = _bottomLeft.getLong() + Math.random() * getWidth();
		_depth = _bottomLeft.getDepth() + Math.random() * getDepthRange();

		return new WorldLocation(_lat, _long, _depth);
	}

	final public WorldLocation getTopLeft() {
		return _topLeft;
	}

	final public WorldLocation getTopRight() {
		return _topRight;
		// return new WorldLocation(_topLeft.getLat(), _bottomRight.getLong(),
		// _topLeft.getDepth());
	}

	/**
	 * return the width of the area (in degrees)
	 */
	final public double getWidth() {
		/**
		 * note we don't just return the value in degrees, we switch the width of this
		 * area to give a value in degrees at the equator
		 */

		// determine the mid-latitude
		double midLat = getHeight() / 2.0;
		midLat = _bottomRight.getLat() + midLat;

		// create the points we are going to work with
		final WorldLocation leftPoint = new WorldLocation(midLat, _topLeft.getLong(), 0.0);
		final WorldLocation rightPoint = new WorldLocation(midLat, _bottomRight.getLong(), 0.0);

		// and calculate the range
		final double res = leftPoint.rangeFrom(rightPoint);

		return res;
	}

	/**
	 * grow the area to include a border or the indicated degrees
	 */
	final public void grow(final double border_degs, final double depth_metres) {
		_topLeft.setLat(_topLeft.getLat() + border_degs);
		_topLeft.setLong(_topLeft.getLong() - border_degs);
		_topLeft.setDepth(_topLeft.getDepth() + depth_metres);
		_bottomRight.setLat(_bottomRight.getLat() - border_degs);
		_bottomRight.setLong(_bottomRight.getLong() + border_degs);
		_bottomRight.setDepth(_bottomRight.getDepth() - depth_metres);
	}

	/**
	 * make sure the corners are the correct way around.
	 */
	final public void normalise() {

		double maxLat, maxLong, maxDepth;
		double minLat, minLong, minDepth;

		// working variables
		if (_topLeft._theLat > _bottomRight._theLat) {
			maxLat = _topLeft._theLat;
			minLat = _bottomRight._theLat;
		} else {
			maxLat = _bottomRight._theLat;
			minLat = _topLeft._theLat;
		}

		if (_topLeft._theLong > _bottomRight._theLong) {
			maxLong = _topLeft._theLong;
			minLong = _bottomRight._theLong;
		} else {
			maxLong = _bottomRight._theLong;
			minLong = _topLeft._theLong;
		}

		if (_topLeft._theDepth > _bottomRight._theDepth) {
			maxDepth = _topLeft._theDepth;
			minDepth = _bottomRight._theDepth;
		} else {
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

	/**
	 * see if the two areas overlap
	 *
	 * @return flag for overlap
	 */
	final public boolean overlaps(final WorldArea other) {
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
	 * find the range of the nearest corner (or the centre point) to the indicated
	 * point
	 */
	final public double rangeFrom(final WorldLocation other) {
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
	final public double rangeFromEdge(final WorldLocation other) {
		double res;

		if (this.contains(other))
			res = 0;
		else {
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

	final public void setBottomRight(final WorldLocation loc) {
		_bottomRight = loc;
	}

	/**
	 * function to shift the centre of the area
	 *
	 * @param newCentre the new centre of the area
	 */
	final public void setCentre(final WorldLocation newCentre) {
		// what's the area
		final double wid = this.getFlatEarthWidth();
		final double ht = this.getHeight();
		final double depth = this.getCentre().getDepth();

		final WorldLocation newTL = new WorldLocation(newCentre.getLat() + ht / 2, newCentre.getLong() - wid / 2,
				depth);

		final WorldLocation newBR = new WorldLocation(newCentre.getLat() - ht / 2, newCentre.getLong() + wid / 2,
				depth);

		_topLeft = newTL;
		_bottomRight = newBR;

		_thisCentre = null;
	}

	final public void setTopLeft(final WorldLocation loc) {
		_topLeft = loc;
	}

	@Override
	final public String toString() {
		String res = "";
		res += " Area TL:" + _topLeft + " BR:" + _bottomRight;
		return res;
	}

	public void trim() {
		// do it, one corner at a time
		trimLocation(_topLeft);
		trimLocation(_bottomRight);

		// and sort out the other corners
		normalise();
	}

	private void trimLocation(final WorldLocation loc) {
		loc.setLat(Math.min(loc.getLat(), 90));
		loc.setLat(Math.max(loc.getLat(), -90));

		loc.setLong(Math.min(loc.getLong(), 180));
		loc.setLong(Math.max(loc.getLong(), -180));

	}

	/**
	 * Increase the width or the area, keeping the centre in the same position.
	 * @param newWidth
	 */
	public void setCentredWidth(double newWidth) {
		final WorldLocation originalCenter = getCentre();
		
		final WorldLocation newTopRight = getTopRight().add(new WorldVector(newWidth - getWidth(), 0, 0));
		extend(newTopRight);
		
		setCentre(originalCenter);
	}

	/**
	 * Increase the height of the area, keeping the centre in the same position.
	 * @param newHeight
	 */
	public void setCentredHeight(double newHeight) {
		final WorldLocation originalCenter = getCentre();
		
		final WorldLocation newTopRight = getTopRight().add(new WorldVector(0, newHeight - getHeight(), 0));
		extend(newTopRight);
		
		setCentre(originalCenter);
		normalise();
	}

}
