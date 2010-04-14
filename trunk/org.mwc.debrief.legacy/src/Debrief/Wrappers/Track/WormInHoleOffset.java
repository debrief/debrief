package Debrief.Wrappers.Track;

import java.util.Enumeration;

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

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		public void testData() throws InterruptedException
		{
			TrackWrapper track = getDummyTrack();

			assertNotNull("track generated", track);
			assertEquals("correct points", 5, track.numFixes());

			// get the sensor
			SensorWrapper sw = track.getSensors().nextElement();

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

		}

		private void outputTrack(TrackWrapper track)
		{
			Enumeration<Editable> numer = track.getPositions();
			while (numer.hasMoreElements())
			{
				FixWrapper thisF = (FixWrapper) numer.nextElement();
				System.err.println(MWC.Algorithms.Conversions.Degs2m(thisF
						.getLocation().getLong())
						+ ", "
						+ MWC.Algorithms.Conversions.Degs2m(thisF.getLocation().getLat()));
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
						+ ", "
						+ MWC.Algorithms.Conversions.Degs2m(thisLoc.getLat()));
			}
		}

		private TrackWrapper getDummyTrack()
		{
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 0),
					loc_1.add(getVector(0, 0)), MWC.Algorithms.Conversions.Degs2Rads(0), 110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 0),
					loc_1.add(getVector(0, 600)), MWC.Algorithms.Conversions.Degs2Rads(90), 120));
			fw2.setLabel("fw2");
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 0),
					loc_1.add(getVector(45, 849)), MWC.Algorithms.Conversions.Degs2Rads(180), 130));
			fw3.setLabel("fw3");
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 0),
					loc_1.add(getVector(90, 600)), MWC.Algorithms.Conversions.Degs2Rads(90), 140));
			fw4.setLabel("fw4");
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 0),
					loc_1.add(getVector(90, 1200)), MWC.Algorithms.Conversions.Degs2Rads(90), 700));
			fw5.setLabel("fw5");
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			// also give it some sensor data
			final SensorWrapper swa = new SensorWrapper("title one");
			swa.setSensorOffset(new ArrayLength(200));
			final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa",
					new HiResDate(100, 0), null, 0, null, null, null, 0, null);
			final SensorContactWrapper scwa2 = new SensorContactWrapper("bbb",
					new HiResDate(250, 0), null, 0, null, null, null, 0, null);
			final SensorContactWrapper scwa3 = new SensorContactWrapper("ccc",
					new HiResDate(350, 0), null, 0, null, null, null, 0, null);
			swa.add(scwa1);
			swa.add(scwa2);
			swa.add(scwa3);
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
