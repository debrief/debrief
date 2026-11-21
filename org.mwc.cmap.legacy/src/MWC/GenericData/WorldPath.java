
package MWC.GenericData;

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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public final class WorldPath implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////////////
	// Member Variables
	//////////////////////////////////////////////////////////////////////
	/**
	 * our points
	 */
	Vector<WorldLocation> _myPoints;

	/**
	 * the area we cover
	 */
	private WorldArea _myBounds;
	//////////////////////////////////////////////////////////////////////
	// Constructor
	//////////////////////////////////////////////////////////////////////

	public WorldPath() {
		_myPoints = new Vector<WorldLocation>(0, 1);
	}

	public WorldPath(final WorldLocation[] path) {
		_myPoints = new Vector<WorldLocation>(path.length, 1);
		for (int i = 0; i < path.length; i++) {
			_myPoints.add(new WorldLocation(path[i]));
		}
		refreshBounds();
	}

	public WorldPath(final WorldPath path) {
		_myPoints = new Vector<WorldLocation>(path.size(), 1);
		for (int i = 0; i < path.size(); i++) {
			_myPoints.add(new WorldLocation(path.getLocationAt(i)));
		}
		refreshBounds();
	}

	/**
	 * copy the other path into ourselves
	 *
	 * @param destinations path to copy
	 */
	public void add(final WorldPath destinations) {
		for (int i = 0; i < destinations.size(); i++) {
			addPoint(new WorldLocation(destinations.getLocationAt(i)));
		}

	}

	/**
	 * add this location
	 */
	public final void addPoint(final WorldLocation loc) {
		// check it's not null
		if (loc == null)
			return;

		_myPoints.addElement(loc);

		// update the bounds
		refreshBounds();
	}

	/**
	 * break the provided path into a number of straight legs
	 *
	 * @param numStops the number of legs to break down
	 * @return broken down path
	 */
	public WorldPath breakIntoStraightSections(final int numStops) {

		final double leg_length = getTotalDistance().getValueIn(WorldDistance.DEGS) / numStops;
		double needed = leg_length;

		int thisSection = 1;
		int thisLeg = 0;

		WorldVector sep = getSeparation(thisLeg);
		double currentBearing = sep.getBearing();
		double leg_remaining = sep.getRange();

		final WorldPath res = new WorldPath();
		WorldLocation lastOrigin = _myPoints.firstElement();

		while (thisSection < numStops) {
			// ok, is there enough left in this leg to finish off a point?
			if (leg_remaining >= needed) {
				// ok, we can mark off this point, and move on to the next

				// consume a little more
				leg_remaining -= needed;

				// create this intermediate point
				final WorldLocation newLoc = lastOrigin.add(new WorldVector(currentBearing, needed, 0));
				res.addPoint(new WorldLocation(newLoc));
				thisSection++;

				needed = leg_length;
				lastOrigin = newLoc;
			} else {
				// or do we need to move onto the next path point?
				// ok, use up the remaining distance
				needed -= leg_remaining;

				// ok, move on to the next point.
				thisLeg++;

				sep = getSeparation(thisLeg);
				currentBearing = sep.getBearing();
				leg_remaining = sep.getRange();

				lastOrigin = _myPoints.elementAt(thisLeg);

			}

		}

		// ok, dropped out. just use the last point as the final one
		res.addPoint(new WorldLocation(_myPoints.lastElement()));
//
//
//    // do some funny processing just to make use of getTotalDistance.
//    WorldDistance totalLen = getTotalDistance();
//    if (totalLen != null)
//      totalLen = null;
//
//
//    // @@ hack - we should be calculating this stuff.
//    WorldPath res = new WorldPath(this);

		return res;
	}

	/**
	 * equality operator
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (obj == null)
			return false;

		final WorldPath other = (WorldPath) obj;

		boolean equals = true;

		// how long is our list
		final int num = _myPoints.size();

		// see if they are the same size
		if (other.size() != num) {
			// different lengths, cannot be equals
			equals = false;
		} else {
			// so they're the same length
			for (int i = 0; i < num; i++) {
				//
				if (!_myPoints.elementAt(i).equals(other._myPoints.elementAt(i))) {
					// hey, they're different!
					equals = false;
					break;
				}
			}
		}

		return equals;
	}

	@Override
	public final int hashCode() {
		int res = _myPoints.size();
		for (final WorldLocation loc : _myPoints) {
			res = 31 * res + loc.hashCode();
		}
		return res;
	}

	/**
	 * get the bounds of the area
	 */
	public final WorldArea getBounds() {
		return _myBounds;
	}

	/**
	 * get the indicated location
	 */
	public final WorldLocation getLocationAt(final int i) {
		return _myPoints.elementAt(i);
	}

	//////////////////////////////////////////////////////////////////////
	// Member methods
	//////////////////////////////////////////////////////////////////////
	/**
	 * get the list of locations
	 */
	public final Collection<WorldLocation> getPoints() {
		return _myPoints;
	}

	private WorldVector getSeparation(final int thisLeg) {
		final WorldLocation lasPoint = _myPoints.elementAt(thisLeg);
		final WorldLocation nextPoint = _myPoints.elementAt(thisLeg + 1);
		final WorldVector sep = nextPoint.subtract(lasPoint);
		return sep;
	}

	/**
	 * calculate the total distance covered by this path
	 *
	 * @return total distance
	 */
	private WorldDistance getTotalDistance() {
		double degs = 0d;
		WorldLocation lastLocation = null;
		for (final Iterator<WorldLocation> iterator = _myPoints.iterator(); iterator.hasNext();) {
			final WorldLocation thisPoint = iterator.next();
			if (lastLocation != null) {
				final double thisDegs = thisPoint.rangeFrom(lastLocation);
				degs += thisDegs;
			}

			lastLocation = thisPoint;

		}

		// and convert the elapsed distance back
		final WorldDistance res = new WorldDistance(degs, WorldDistance.DEGS);

		return res; // To change body of created methods use File | Settings | File Templates.
	}

	/**
	 * get the index of a particular point
	 *
	 */
	public int indexOf(final WorldLocation loc) {
		return _myPoints.indexOf(loc);
	}

	/**
	 * put this location in at the indicated point
	 *
	 * @param loc
	 * @param index
	 */
	public void insertPointAt(final WorldLocation loc, final int index) {
		_myPoints.insertElementAt(loc, index);
	}

	/**
	 * move the indicated point down the list by one
	 */
	public final void moveDownward(final WorldLocation point) {
		final int index = _myPoints.indexOf(point);
		if (index != -1) {
			// ok, at least it's in the list, now remove it
			_myPoints.remove(point);

			// produce the new index
			final int newIndex = Math.min(_myPoints.size(), index + 1);

			// and put it in
			_myPoints.insertElementAt(point, newIndex);
		}
	}

	/**
	 * move the indicate point up the list by one
	 */
	public final void moveUpward(final WorldLocation point) {
		final int index = _myPoints.indexOf(point);
		if (index != -1) {
			// ok, at least it's in the list, now remove it
			_myPoints.remove(point);

			// produce the new index
			final int newIndex = Math.max(0, index - 1);

			// and put it in
			_myPoints.insertElementAt(point, newIndex);
		}
	}

	/**
	 * refresh the area covered
	 */
	public final void refreshBounds() {
		WorldArea res = null;
		final Iterator<WorldLocation> it = _myPoints.iterator();
		while (it.hasNext()) {
			final WorldLocation nx = it.next();
			if (res == null)
				res = new WorldArea(nx, nx);
			else
				res.extend(nx);
		}

		_myBounds = res;
	}

	/**
	 * remove the indicated point from the list
	 */
	public final void remove(final WorldLocation point) {
		_myPoints.remove(point);

		refreshBounds();
	}

	/**
	 * the length of the list
	 */
	public final int size() {
		return _myPoints.size();
	}

	@Override
	public String toString() {
		return getPoints().size() + " Points";
	}
	/***************************************************************
	 * main method, to test this class
	 ***************************************************************/

}