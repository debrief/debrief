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

package Debrief.Wrappers.Track;

import java.util.Enumeration;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * class to calculate offset position of sensor back from host location by
 * travelling back along host track for specified distance
 *
 * @author ianmayo
 *
 */
public class WormInHoleOffset {

	/**
	 *
	 * @param track
	 * @param dtg
	 * @param arrayOffset
	 * @return
	 */
	public static FixWrapper getWormOffsetFor(final TrackWrapper track, final HiResDate dtg,
			final WorldDistance arrayOffset) {

		FixWrapper res = null;
		double offsetM = -arrayOffset.getValueIn(WorldDistance.METRES);
		// double offsetM = Math.abs(arrayOffset.getValueIn(WorldDistance.METRES));

		// check we're in period
		if (dtg.lessThan(track.getStartDTG()) || dtg.greaterThan(track.getEndDTG()))
			return res;

		// get the position at the time, we need it to sort out the speed
		final Watchable[] matches = track.getNearestTo(dtg);

		// did we find any fixes?
		// NOTE: we've already done the lessThan/greaterThan operation. But,
		// getNearestTo only returns visible fixes. So, all the fixes
		// may actually be hidden.
		if (matches == null || matches.length == 0) {
			return res;
		}

		final FixWrapper currFix = (FixWrapper) matches[0];

		// start off by bracketing the time. work back along the track legs until we
		// find the two positions either side
		// of the time we're looking for
		final Enumeration<Editable> enumer = track.getPositionIterator();

		final Vector<FixWrapper> backTrack = new Vector<FixWrapper>();
		FixWrapper nextPoint = null;

		while (enumer.hasMoreElements()) {
			final FixWrapper thisP = (FixWrapper) enumer.nextElement();

			if ((backTrack.isEmpty()) && (thisP.getDateTimeGroup().equals(dtg))) {
				// right, this is the first point, and we've matched it already. Just
				// produce
				// a vector back down ownship path
				res = new FixWrapper(thisP.getFix().makeCopy());
				res.setLocation(new WorldLocation(thisP.getLocation()
						.add(new WorldVector(thisP.getCourse(), MWC.Algorithms.Conversions.m2Degs(-offsetM), 0d))));
				return res;
			} else if (thisP.getDateTimeGroup().lessThan(dtg)) {
				backTrack.add(thisP);
			} else {
				// we've passed the point
				nextPoint = thisP;
				break;
			}
		}

		// right, we have a back track. double-check we have our next point
		if (!backTrack.isEmpty()) {
			// yup, we've bracketed the point. work out where ownship is at this DTG
			nextPoint = FixWrapper.interpolateFix(backTrack.lastElement(), nextPoint, dtg);

			for (int i = backTrack.size() - 1; i >= 0; i--) {
				final FixWrapper thisI = backTrack.elementAt(i);

				final double thisLen = nextPoint.getLocation().subtract(thisI.getFixLocation()).getRange();
				final double thisLenM = MWC.Algorithms.Conversions.Degs2m(thisLen);

				// is this longer than our stub?
				if (thisLenM >= offsetM) {
					// so just interpolate along the path
					final double posDelta = offsetM / thisLenM;
					final long nextMicros = nextPoint.getDTG().getMicros();
					final long lastMicros = thisI.getDTG().getMicros();
					double timeDelta = (nextMicros - lastMicros);
					timeDelta *= posDelta;
					final double timeOffset = nextMicros - timeDelta;

					res = FixWrapper.interpolateFix(thisI, nextPoint, new HiResDate(0, (long) timeOffset));
					offsetM = 0;
					break;
				} else {
					// we still have array pending, reduce what we've just consumed
					offsetM -= thisLenM;
					nextPoint = thisI;
				}
			}
		}

		// do we have any array left to consume?
		if (offsetM > 0) {
			// are we on the first data point?
			if (nextPoint != null) {
				// yup, just use that one
				res = new FixWrapper(nextPoint.getFix().makeCopy());

				// offset by the array length along the heading
				res.setLocation(res.getLocation()
						.add(new WorldVector(nextPoint.getCourse(), MWC.Algorithms.Conversions.m2Degs(-offsetM), 0d)));
			}
		}

		// put the current speed into the fix we return
		if (res != null)
			res.setSpeed(currFix.getSpeed());

		return res;
	}

	/**
	 * format the location as a String
	 *
	 * @param theLoc
	 * @return
	 */
	static String loc2String(final WorldLocation theLoc) {
		return MWC.Algorithms.Conversions.Degs2m(theLoc.getLong()) + ", "
				+ MWC.Algorithms.Conversions.Degs2m(theLoc.getLat());
	}

	/**
	 * another diagnostic support method
	 *
	 * @param msg
	 * @param theLoc
	 */
	static void writeLoc(final String msg, final WorldLocation theLoc) {
		if (msg != null)
			System.out.print(msg + "|");
		System.out.println(loc2String(theLoc));
	}

	/**
	 * diagnostic support method
	 *
	 * @param theLoc
	 */
	static void writeLoc(final WorldLocation theLoc) {
		writeLoc(null, theLoc);
	}

}
