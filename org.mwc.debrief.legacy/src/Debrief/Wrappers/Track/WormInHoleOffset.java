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
package Debrief.Wrappers.Track;

import java.util.Enumeration;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * class to calculate offset position of sensor back from host location by
 * travelling back along host track for specified distance
 * 
 * @author ianmayo
 * 
 */
public class WormInHoleOffset
{

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		private TrackWrapper getDummyTrack()
		{
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0.00000001, 0.000000001, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 0),
					loc_1.add(getVector(25, 0)), MWC.Algorithms.Conversions.Degs2Rads(0),
					110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 0), fw1
					.getLocation().add(getVector(0, 600)),
					MWC.Algorithms.Conversions.Degs2Rads(90), 120));
			fw2.setLabel("fw2");
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 0), fw2
					.getLocation().add(getVector(45, 849)),
					MWC.Algorithms.Conversions.Degs2Rads(180), 130));
			fw3.setLabel("fw3");
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 0), fw3
					.getLocation().add(getVector(90, 600)),
					MWC.Algorithms.Conversions.Degs2Rads(90), 140));
			fw4.setLabel("fw4");
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 0), fw4
					.getLocation().add(getVector(90, 1200)),
					MWC.Algorithms.Conversions.Degs2Rads(90), 700));
			fw5.setLabel("fw5");
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			// also give it some sensor data
			final SensorWrapper swa = new SensorWrapper("title one");
			swa.setSensorOffset(new ArrayLength(-400));
			final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa",
					new HiResDate(100, 0), null, null, null, null, null, 0, null);
			final SensorContactWrapper scwa2 = new SensorContactWrapper("bbb",
					new HiResDate(140, 0), null, null, null, null, null, 0, null);
			final SensorContactWrapper scwa3 = new SensorContactWrapper("ccc",
					new HiResDate(280, 0), null, null, null, null, null, 0, null);
			final SensorContactWrapper scwa4 = new SensorContactWrapper("ddd",
					new HiResDate(350, 0), null, null, null, null, null, 0, null);
			swa.add(scwa1);
			swa.add(scwa2);
			swa.add(scwa3);
			swa.add(scwa4);
			tw.add(swa);

			return tw;
		}

		/**
		 * @return
		 */
		private WorldVector getVector(final double courseDegs, final double distM)
		{
			return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(courseDegs),
					new WorldDistance(distM, WorldDistance.METRES), null);
		}

		private void outputSensorTrack(final SensorWrapper sensor)
		{
			final Enumeration<Editable> numer = sensor.elements();
			while (numer.hasMoreElements())
			{
				final SensorContactWrapper thisF = (SensorContactWrapper) numer.nextElement();
				final WorldLocation thisLoc = thisF.getLocation();
				System.out.println(MWC.Algorithms.Conversions.Degs2m(thisLoc.getLong())
						+ ", " + MWC.Algorithms.Conversions.Degs2m(thisLoc.getLat()));
			}
		}

		private void outputTrack(final TrackWrapper track)
		{
			final Enumeration<Editable> numer = track.getPositionIterator();
			while (numer.hasMoreElements())
			{
				final FixWrapper thisF = (FixWrapper) numer.nextElement();
				final WorldLocation theLoc = thisF.getLocation();
				writeLoc(theLoc);
			}
		}

		@SuppressWarnings("deprecation")
    public void testData() throws InterruptedException
		{
			final TrackWrapper track = getDummyTrack();

			assertNotNull("track generated", track);
			assertEquals("correct points", 5, track.numFixes());

			// get the sensor
			final SensorWrapper sw = (SensorWrapper) track.getSensors().elements()
					.nextElement();

			// find the position of the last fix
			final SensorContactWrapper scw = (SensorContactWrapper) sw.getNearestTo(sw
					.getEndDTG())[0];

			assertNotNull("fix found", scw);

			outputSensorTrack(sw);
			Thread.sleep(100);

			outputTrack(track);

			Thread.sleep(100);
			sw.setWormInHole(true);
			outputSensorTrack(sw);

			// get a location
			FixWrapper res = WormInHoleOffset.getWormOffsetFor(track,
					track.getEndDTG(), sw.getSensorOffset());
			assertNotNull("failed to find location", res);
			// give it another go, with a zero sensor offset
			sw.setSensorOffset(new ArrayLength(0d));

			res = WormInHoleOffset.getWormOffsetFor(track, track.getEndDTG(),
					sw.getSensorOffset());
			assertNotNull("failed to find location");

		}

		// TODO FIX-TEST
		public void NtestData2()
		{
			final TrackWrapper track = getDummyTrack();
			assertEquals("correct points", 5, track.numFixes());

			// ok, show us the track
			outputTrack(track);
			// get a location
			ArrayLength theLen = new ArrayLength(-500);
			FixWrapper res = WormInHoleOffset.getWormOffsetFor(track, new HiResDate(
					400), theLen);

			// and now the sensor location
			writeLoc(res.getLocation());

			String theLoc = loc2String(res.getLocation());
			assertEquals("correct location", "700.3297761249414, 1200.334768427379",
					theLoc);
			assertEquals("correct course", MWC.Algorithms.Conversions.Degs2Rads(90),
					res.getFix().getCourse(), 0.01);

			theLen = new ArrayLength(-100);
			res = WormInHoleOffset
					.getWormOffsetFor(track, new HiResDate(400), theLen);

			// and now the sensor location
			writeLoc(res.getLocation());

			theLoc = loc2String(res.getLocation());
			assertEquals("correct location", "1100.33178323392, 1200.334768427379",
					theLoc);
			assertEquals("correct course", MWC.Algorithms.Conversions.Degs2Rads(90),
					res.getFix().getCourse(), 0.01);

			theLen = new ArrayLength(-100);
			res = WormInHoleOffset
					.getWormOffsetFor(track, new HiResDate(440), theLen);

			// and now the sensor location
			writeLoc(res.getLocation());

			theLoc = loc2String(res.getLocation());
			assertEquals("correct location", "1580.325791764545, 1200.334768427379",
					theLoc);
			assertEquals("correct course", MWC.Algorithms.Conversions.Degs2Rads(90),
					res.getFix().getCourse(), 0.01);

			theLen = new ArrayLength(-100);
			res = WormInHoleOffset
					.getWormOffsetFor(track, new HiResDate(310), theLen);

			// and now the sensor location
			writeLoc(res.getLocation());
			theLoc = loc2String(res.getLocation());
			assertEquals("correct location", "572.0460521364838, 1172.0470464988248",
					theLoc);
			assertEquals("correct course", MWC.Algorithms.Conversions.Degs2Rads(45),
					res.getFix().getCourse(), 0.01);

			theLen = new ArrayLength(-300);
			res = WormInHoleOffset
					.getWormOffsetFor(track, new HiResDate(210), theLen);

			// and now the sensor location
			writeLoc(res.getLocation());
			theLoc = loc2String(res.getLocation());
			assertEquals("correct location", "1.1112E-4, 384.90111119999995", theLoc);
			assertEquals("correct course", MWC.Algorithms.Conversions.Degs2Rads(0),
					res.getFix().getCourse(), 0.01);

		}

		public void testInterpolate()
		{
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0.00000001, 0.000000001, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 0),
					loc_1.add(getVector(25, 0)), MWC.Algorithms.Conversions.Degs2Rads(0),
					110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 0), fw1
					.getLocation().add(getVector(45, 600)),
					MWC.Algorithms.Conversions.Degs2Rads(90), 120));
			fw2.setLabel("fw2");
			tw.addFix(fw1);
			tw.addFix(fw2);
			assertEquals("correct points", 2, tw.numFixes());

			// ok, show us the track
			outputTrack(tw);

			FixWrapper f3 = FixWrapper
					.interpolateFix(fw1, fw2, new HiResDate(101, 0));
			WorldVector offest = f3.getLocation().subtract(fw1.getFixLocation());
			double offM = MWC.Algorithms.Conversions.Degs2m(offest.getRange());
			assertEquals("near to f1", 10, offM, 10);

			f3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(199, 0));
			offest = f3.getLocation().subtract(fw2.getFixLocation());
			offM = MWC.Algorithms.Conversions.Degs2m(offest.getRange());
			assertEquals("near to f2", 10, offM, 10);

			writeLoc(f3.getLocation());

		}

	}

	/**
	 * 
	 * @param track
	 * @param dtg
	 * @param arrayOffset
	 * @return
	 */
	public static FixWrapper getWormOffsetFor(final TrackWrapper track, final HiResDate dtg,
			final WorldDistance arrayOffset)
	{

		FixWrapper res = null;
		double offsetM = -arrayOffset.getValueIn(WorldDistance.METRES);
		// double offsetM = Math.abs(arrayOffset.getValueIn(WorldDistance.METRES));

		// check we're in period
		if (dtg.lessThan(track.getStartDTG()) || dtg.greaterThan(track.getEndDTG()))
			return res;

		// get the position at the time, we need it to sort out the speed
		final FixWrapper currFix = (FixWrapper) track.getNearestTo(dtg)[0];

		// start off by bracketing the time. work back along the track legs until we
		// find the two positions either side
		// of the time we're looking for
		final Enumeration<Editable> enumer = track.getPositionIterator();

		final Vector<FixWrapper> backTrack = new Vector<FixWrapper>();
		FixWrapper nextPoint = null;

		while (enumer.hasMoreElements())
		{
			final FixWrapper thisP = (FixWrapper) enumer.nextElement();

			if ((backTrack.isEmpty()) && (thisP.getDateTimeGroup().equals(dtg)))
			{
				// right, this is the first point, and we've matched it already. Just
				// produce
				// a vector back down ownship path
				res = new FixWrapper(thisP.getFix().makeCopy());
				res.setLocation(new WorldLocation(thisP.getLocation().add(
						new WorldVector(thisP.getCourse(), MWC.Algorithms.Conversions
								.m2Degs(-offsetM), 0d))));
				return res;
			}
			else if (thisP.getDateTimeGroup().lessThan(dtg))
			{
				backTrack.add(thisP);
			}
			else
			{
				// we've passed the point
				nextPoint = thisP;
				break;
			}
		}

		// right, we have a back track. double-check we have our next point
		if (!backTrack.isEmpty())
		{
			// yup, we've bracketed the point. work out where ownship is at this DTG
			nextPoint = FixWrapper.interpolateFix(backTrack.lastElement(), nextPoint,
					dtg);

			for (int i = backTrack.size() - 1; i >= 0; i--)
			{
				final FixWrapper thisI = backTrack.elementAt(i);

				final double thisLen = nextPoint.getLocation()
						.subtract(thisI.getFixLocation()).getRange();
				final double thisLenM = MWC.Algorithms.Conversions.Degs2m(thisLen);

				// is this longer than our stub?
				if (thisLenM >= offsetM)
				{
					// so just interpolate along the path
					final double posDelta = offsetM / thisLenM;
					final long nextMicros = nextPoint.getDTG().getMicros();
					final long lastMicros = thisI.getDTG().getMicros();
					double timeDelta = (nextMicros - lastMicros);
					timeDelta *= posDelta;
					final double timeOffset = nextMicros - timeDelta;

					res = FixWrapper.interpolateFix(thisI, nextPoint, new HiResDate(0,
							(long) timeOffset));
					offsetM = 0;
					break;
				}
				else
				{
					// we still have array pending, reduce what we've just consumed
					offsetM -= thisLenM;
					nextPoint = thisI;
				}
			}
		}

		// do we have any array left to consume?
		if (offsetM > 0)
		{
			// are we on the first data point?
			if (nextPoint != null)
			{
				// yup, just use that one
				res = new FixWrapper(nextPoint.getFix().makeCopy());

				// offset by the array length along the heading
				res.setLocation(res.getLocation().add(
						new WorldVector(nextPoint.getCourse(), MWC.Algorithms.Conversions
								.m2Degs(-offsetM), 0d)));
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
	private static String loc2String(final WorldLocation theLoc)
	{
		return MWC.Algorithms.Conversions.Degs2m(theLoc.getLong()) + ", "
				+ MWC.Algorithms.Conversions.Degs2m(theLoc.getLat());
	}

	/**
	 * another diagnostic support method
	 * 
	 * @param msg
	 * @param theLoc
	 */
	private static void writeLoc(final String msg, final WorldLocation theLoc)
	{
		if (msg != null)
			System.out.print(msg + "|");
		System.out.println(loc2String(theLoc));
	}

	/**
	 * diagnostic support method
	 * 
	 * @param theLoc
	 */
	private static void writeLoc(final WorldLocation theLoc)
	{
		writeLoc(null, theLoc);
	}

}
