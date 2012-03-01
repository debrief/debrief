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
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.GenericData.WorldDistance.ArrayLength;
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

	/**
	 * diagnostic support method
	 * 
	 * @param theLoc
	 */
	private static void writeLoc(WorldLocation theLoc)
	{
		writeLoc(null, theLoc);
	}

	/**
	 * another diagnostic support method
	 * 
	 * @param msg
	 * @param theLoc
	 */
	private static void writeLoc(String msg, WorldLocation theLoc)
	{
		if (msg != null)
			System.out.print(msg + "|");
		System.out.println(loc2String(theLoc));
	}

	/**
	 * format the location as a String
	 * 
	 * @param theLoc
	 * @return
	 */
	private static String loc2String(WorldLocation theLoc)
	{
		return MWC.Algorithms.Conversions.Degs2m(theLoc.getLong()) + ", "
				+ MWC.Algorithms.Conversions.Degs2m(theLoc.getLat());
	}

	/**
	 * 
	 * @param track
	 * @param dtg
	 * @param arrayOffset
	 * @return
	 */
	public static WorldLocation getWormOffsetFor(TrackWrapper track,
			HiResDate dtg, WorldDistance arrayOffset)
	{
		WorldLocation res = null;
		double offsetM = -arrayOffset.getValueIn(WorldDistance.METRES);
		// double offsetM = Math.abs(arrayOffset.getValueIn(WorldDistance.METRES));

		// check we're in period
		if (dtg.lessThan(track.getStartDTG()) || dtg.greaterThan(track.getEndDTG()))
			return res;

		// start off by bracketing the time. work back along the track legs until we
		// find the two positions either side
		// of the time we're looking for
		Enumeration<Editable> enumer = track.getPositions();

		Vector<FixWrapper> backTrack = new Vector<FixWrapper>();
		FixWrapper nextPoint = null;

		while (enumer.hasMoreElements())
		{
			FixWrapper thisP = (FixWrapper) enumer.nextElement();

			if ((backTrack.size() == 0) && (thisP.getDateTimeGroup().equals(dtg)))
			{
				// right, this is the first point, and we've matched it already. Just
				// produce
				// a vector back down ownship path
				res = new WorldLocation(thisP.getLocation().add(
						new WorldVector(thisP.getCourse(), MWC.Algorithms.Conversions
								.m2Degs(-offsetM), 0d)));
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
		if (backTrack.size() > 0)
		{
			// yup, we've bracketed the point. work out where ownship is at this DTG
			nextPoint = FixWrapper.interpolateFix(backTrack.lastElement(), nextPoint,
					dtg);

			for (int i = backTrack.size() - 1; i >= 0; i--)
			{
				FixWrapper thisI = backTrack.elementAt(i);

				double thisLen = nextPoint.getLocation()
						.subtract(thisI.getFixLocation()).getRange();
				double thisLenM = MWC.Algorithms.Conversions.Degs2m(thisLen);

				// is this longer than our stub?
				if (thisLenM >= offsetM)
				{
					// so just interpolate along the path
					double posDelta = offsetM / thisLenM;
					long nextMicros = nextPoint.getDTG().getMicros();
					long lastMicros = thisI.getDTG().getMicros();
					double timeDelta = (nextMicros - lastMicros);
					timeDelta *= posDelta;
					double timeOffset = nextMicros - timeDelta;

					FixWrapper newMidFix = FixWrapper.interpolateFix(thisI, nextPoint,
							new HiResDate(0, (long) timeOffset));
					res = newMidFix.getLocation();
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
				res = nextPoint.getLocation();

				// offset by the array length along the heading
				res = new WorldLocation(res.add(new WorldVector(nextPoint.getCourse(),
						MWC.Algorithms.Conversions.m2Degs(-offsetM), 0d)));
			}
		}

		return res;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
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

		public void testData2()
		{
			TrackWrapper track = getDummyTrack();
			assertEquals("correct points", 5, track.numFixes());

			// ok, show us the track
			outputTrack(track);
			// get a location
			ArrayLength theLen = new ArrayLength(-500);
			WorldLocation res = WormInHoleOffset.getWormOffsetFor(track,
					new HiResDate(400), theLen);

			// and now the sensor location
			writeLoc(res);

			String theLoc = loc2String(res);
			assertEquals("correct location", "700.3297761249414, 1200.334768427379",
					theLoc);

			theLen = new ArrayLength(-100);
			res = WormInHoleOffset
					.getWormOffsetFor(track, new HiResDate(400), theLen);

			// and now the sensor location
			writeLoc(res);

			theLoc = loc2String(res);
			assertEquals("correct location", "1100.33178323392, 1200.334768427379",
					theLoc);

			theLen = new ArrayLength(-100);
			res = WormInHoleOffset
					.getWormOffsetFor(track, new HiResDate(440), theLen);

			// and now the sensor location
			writeLoc(res);

			theLoc = loc2String(res);
			assertEquals("correct location", "1580.325791764545, 1200.334768427379",
					theLoc);

			theLen = new ArrayLength(-100);
			res = WormInHoleOffset
					.getWormOffsetFor(track, new HiResDate(310), theLen);

			// and now the sensor location
			writeLoc(res);

			theLoc = loc2String(res);
			assertEquals("correct location", "1580.325791764545, 1200.334768427379",
					theLoc);
			
		}

		public void testData() throws InterruptedException
		{
			TrackWrapper track = getDummyTrack();

			assertNotNull("track generated", track);
			assertEquals("correct points", 5, track.numFixes());

			// get the sensor
			SensorWrapper sw = (SensorWrapper) track.getSensors().elements()
					.nextElement();

			// find the position of the last fix
			SensorContactWrapper scw = (SensorContactWrapper) sw.getNearestTo(sw
					.getEndDTG())[0];

			assertNotNull("fix found", scw);

			outputSensorTrack(sw);
			Thread.sleep(100);

			outputTrack(track);

			Thread.sleep(100);
			sw.setWormInHole(true);
			outputSensorTrack(sw);

			// get a location
			WorldLocation res = WormInHoleOffset.getWormOffsetFor(track,
					track.getEndDTG(), sw.getSensorOffset());
			assertNotNull("failed to find location");
			// give it another go, with a zero sensor offset
			sw.setSensorOffset(new ArrayLength(0d));

			res = WormInHoleOffset.getWormOffsetFor(track, track.getEndDTG(),
					sw.getSensorOffset());
			assertNotNull("failed to find location");

		}

		private void outputTrack(TrackWrapper track)
		{
			Enumeration<Editable> numer = track.getPositions();
			while (numer.hasMoreElements())
			{
				FixWrapper thisF = (FixWrapper) numer.nextElement();
				WorldLocation theLoc = thisF.getLocation();
				writeLoc(theLoc);
			}
		}

		private void outputSensorTrack(SensorWrapper sensor)
		{
			Enumeration<Editable> numer = sensor.elements();
			while (numer.hasMoreElements())
			{
				SensorContactWrapper thisF = (SensorContactWrapper) numer.nextElement();
				WorldLocation thisLoc = thisF.getLocation();
				System.out.println(MWC.Algorithms.Conversions.Degs2m(thisLoc.getLong())
						+ ", " + MWC.Algorithms.Conversions.Degs2m(thisLoc.getLat()));
			}
		}

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
		private WorldVector getVector(double courseDegs, double distM)
		{
			return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(courseDegs),
					new WorldDistance(distM, WorldDistance.METRES), null);
		}

	}

}
