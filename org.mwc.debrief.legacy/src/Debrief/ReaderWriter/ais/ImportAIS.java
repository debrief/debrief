package Debrief.ReaderWriter.ais;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import junit.framework.TestCase;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;

public class ImportAIS
{

	private final Layers _layers;
	private static HashMap<Integer, String> _nameLookups;
	private final ArrayList<FixWrapper> _queuedFixes;

	NumberFormat numF = new DecimalFormat("00");
	
	private Timestamp _lastTime = null;

	public ImportAIS(Layers target)
	{
		super();
		_layers = target;
		_queuedFixes = new ArrayList<FixWrapper>();
	}



	@SuppressWarnings("deprecation")
	public void importThis(String fName, InputStream is) throws Exception
	{

		// reset the timestamp, so we deduce the time
		_lastTime = null;

		AISParser parser = new AISParser();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		// ok, loop through the lines
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String nmea_sentence;
		while ((nmea_sentence = br.readLine()) != null)
		{

			// sort out the time leader
			// String leader = nmea_sentence.substring(0, 11);
			// Date date = sdf.parse(leader);

			IAISMessage res = parser.parse(nmea_sentence);

			if (res instanceof AISPositionA)
			{
				AISPositionA ar = (AISPositionA) res;

				// if(ar.getMmsi() == 563622000)
				// {
				// System.out.println(nmea_sentence + " secs:" +
				// ar.getMsgTimestamp().getSeconds());
				// }

				storeThis(ar.getLatitude(), ar.getLongitude(), ar.getCog(),
						ar.getSog(), ar.getMmsi(), ar.getMsgTimestamp().getSeconds(),
						_lastTime);
			}
			else if (res instanceof AISPositionB)
			{
				AISPositionB ar = (AISPositionB) res;
				storeThis(ar.getLatitude(), ar.getLongitude(), ar.getCog(),
						ar.getSog(), ar.getMmsi(), ar.getMsgTimestamp().getSeconds(),
						_lastTime);

				if (ar.getMmsi() == 563622000)
				{
					// System.out.println(nmea_sentence + " secs:" +
					// ar.getMsgTimestamp().getSeconds());
					// System.out.println(nmea_sentence);
				}

			}
			else if (res instanceof AISBaseStation)
			{
				AISBaseStation base = (AISBaseStation) res;
				// System.out.println(nmea_sentence);// + " BASE ");
				_lastTime = base.getTimestamp();

				System.out.println("     new Base time:" + _lastTime);

				// hey, we may have stacked up some positions while
				// they are waiting for the first data item
				if (_queuedFixes.size() > 0)
					processQueuedPositions(_lastTime);

			}
			else if (res instanceof AISVessel)
			{
				AISVessel vess = (AISVessel) res;

				// if(vess.getMmsi() == 220433000)
				// {
				// System.out.println(nmea_sentence + " VESSEL");
				// }

				// ok, store the id against the name
				if (_nameLookups == null)
					_nameLookups = new HashMap<Integer, String>();

				_nameLookups.put(vess.getMmsi(), vess.getName());

//				System.out.println("Named vessel:" + vess.getName() + " MMSI:"
//						+ vess.getMmsi());

				// ok, see if we can name this vessel
				Layer thisLayer = _layers.findLayer("" + vess.getMmsi());
				if (thisLayer != null)
				{
					thisLayer.setName(vess.getName());
				}
				else
				{
					// System.out.println("VESSEL NOT FOUND FOR ID:" + vess.getMmsi());
				}

			}
			else
			{
				// System.out.println(res);
			}

		}
	}

	@SuppressWarnings("deprecation")
	private void processQueuedPositions(Date lastTime)
	{

		if(_queuedFixes.isEmpty())
			return;
		
		System.out.println("PROCESSING QUEUE");
		
		Iterator<FixWrapper> iter = _queuedFixes.iterator();
		int lastSecs = lastTime.getSeconds();

		while (iter.hasNext())
		{
			FixWrapper fix = iter.next();

			// ok, we have to replace the time
			int secs = fix.getTime().getDate().getSeconds();

			// build the new date
			Date newDate = new Date(lastTime.getTime());

			// is this less than the queued secs
			if (isPreviousMinute(lastSecs, secs))
			{
				// ok, we have to decrement the minutes
				newDate.setMinutes(newDate.getMinutes() - 1);
			}
			else if(isNextMinute(lastSecs, secs))
			{
				// ok, we have to increment the minutes
				newDate.setMinutes(newDate.getMinutes() + 1);
			}

			// and the seconds
			newDate.setSeconds(secs);

			// and store it
			fix.getFix().setTime(new HiResDate(newDate));
			fix.resetName();
		}

		// done, clear the list
		_queuedFixes.clear();
	}

	@SuppressWarnings("deprecation")
	private void storeThis(double latitude, double longitude, double cog,
			double sog, int mmsi, int secs, Timestamp lastTime)
	{

		if (mmsi == 244198000)
		{
//			System.out.println("  secs:" + numF.format(secs) + " lastTime:" + lastTime);
		}
		else
		{
		//	return;
		}

		String layerName = null;

		// try to do a name lookup
		String lookupName = null;

		if (_nameLookups != null)
			lookupName = _nameLookups.get(mmsi);

		if (lookupName != null)
			layerName = lookupName;
		else
			layerName = "" + mmsi;

		// does this track exist
		Layer layer = _layers.findLayer(layerName);
		if (layer == null)
		{
			TrackWrapper tw = new TrackWrapper();
			tw.setColor(new Color(45, 97, 0));
			layer = tw;
			layer.setName(layerName);
			_layers.addThisLayer(layer);
		}

		final Date newDate;

		if (lastTime != null)
		{
			long theTime = lastTime.getTime();
			newDate = new Date(theTime);
			newDate.setSeconds(secs);

			if (isPreviousMinute(lastTime.getSeconds(), secs))
			{
				newDate.setMinutes(newDate.getMinutes() - 1);
			}
			else if(isNextMinute(lastTime.getSeconds(), secs))
			{
				newDate.setMinutes(newDate.getMinutes() + 1);
			}
		}
		else
		{
			newDate = new Date(secs * 1000);
			newDate.setSeconds(secs);
		}

		HiResDate hDate = new HiResDate(newDate);

		WorldLocation theLocation = new WorldLocation(latitude, longitude, 0);
		double theCourseRads = Math.toRadians(cog);
		double theSpeedYps = new WorldSpeed(sog, WorldSpeed.Kts)
				.getValueIn(WorldSpeed.ft_sec) / 3d;
		// ok, now add the position
		Fix newFix = new Fix(hDate, theLocation, theCourseRads, theSpeedYps);
		FixWrapper fixWrapper = new FixWrapper(newFix);

		// ok, do we have a time offset yet? if we don't we should queue up this fix
		if (lastTime == null)
			_queuedFixes.add(fixWrapper);
		else
		{
			fixWrapper.resetName();
		}

		layer.add(fixWrapper);

	}

	public static class TestImportAIS extends TestCase
	{
		public void testShortImport() throws Exception
		{
			testImport(
					"../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0854_trimmed.ais",
					1);
		}

		public void testFullImport() throws Exception
		{
			testImport(
					"../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0854.ais",
					6);
		}

		public void testImport(String testFile, int len) throws Exception
		{
			File testI = new File(testFile);
			assertTrue(testI.exists());

			InputStream is = new FileInputStream(testI);

			final Layers tLayers = new Layers();

			ImportAIS importer = new ImportAIS(tLayers);
			importer.importThis(testFile, is);

			// hmmm, how many tracks
			assertEquals("got new tracks", len, tLayers.size());

			TrackWrapper thisT = (TrackWrapper) tLayers.findLayer("BW LIONESS");
			Enumeration<Editable> fixes = thisT.getPositions();
			while (fixes.hasMoreElements())
			{
				FixWrapper thisF = (FixWrapper) fixes.nextElement();
				System.out.println(thisF.getDateTimeGroup().getDate() + " COG:"
						+ (int) Math.toDegrees(thisF.getCourse()) + " SOG:"
						+ (int) thisF.getSpeed());

			}

		}


		public void testNewImport() throws Exception
		{
			String testFile = "/home/ian/Downloads/AIS_TEST_FILES/150304_0814.txt";
			File testI = new File(testFile);
			assertTrue(testI.exists());

			InputStream is = new FileInputStream(testI);

			final Layers tLayers = new Layers();

			ImportAIS importer = new ImportAIS(tLayers);
			importer.importThis(testFile, is);

			// hmmm, how many tracks
			assertEquals("got new tracks", 1, tLayers.size());

			TrackWrapper thisT = (TrackWrapper) tLayers.findLayer("TINA");
			Enumeration<Editable> fixes = thisT.getPositions();
			while (fixes.hasMoreElements())
			{
				FixWrapper thisF = (FixWrapper) fixes.nextElement();
				System.out.println(thisF.getDateTimeGroup().getDate() + " COG:"
						+ (int) Math.toDegrees(thisF.getCourse()) + " SOG:"
						+ (int) thisF.getSpeed() + " loc:" + thisF.getLocation());

			}

		}
		
		public void testKnownImport() throws AISParseException
		{
			String test = "!AIVDM,1,1,,A,15RTgt0PAso;90TKcjM8h6g208CQ,0*4A";
			AISParser parser = new AISParser();
			IAISMessage res = parser.parse(test);
			@SuppressWarnings("unused")
			AISPositionA posA = (AISPositionA) res;
		}

		@SuppressWarnings("deprecation")
		public void testMissingTimes()
		{
			Layers layers = new Layers();
			ImportAIS ia = new ImportAIS(layers);
			Timestamp lastTime = null;
			int mmsi = 5;

			ia.storeThis(1, 1, 1, 1, mmsi, 50, lastTime);

			// check it got cached
			assertEquals("queue present", 1, ia._queuedFixes.size());

			lastTime = new Timestamp(2010, 6, 6, 6, 6, 2, 0);

			// ok - handle them
			ia.processQueuedPositions(lastTime);

			// note - we should interpret this value as being from the previous minute
			ia.storeThis(1, 1, 1, 1, mmsi, 5, lastTime);

			// check queue cleared
			assertEquals("queue present", 0, ia._queuedFixes.size());

			// check order of points
			TrackWrapper tw = (TrackWrapper) layers.findLayer("" + mmsi);

			// check the start time
			assertEquals("start time correct", 5, tw.getStartDTG().getDate()
					.getMinutes());
			assertEquals("start time correct", 50, tw.getStartDTG().getDate()
					.getSeconds());
			assertEquals("end time correct", 6, tw.getEndDTG().getDate().getMinutes());
			assertEquals("end time correct", 5, tw.getEndDTG().getDate().getSeconds());

			// ok now, we'll have a previous position appearing after the time stamp
			// note - we should interpret this value as being from the previous minute
			mmsi = 12;
			lastTime = null;
			ia.storeThis(1, 1, 1, 1, mmsi, 50, lastTime);

			lastTime = new Timestamp(2010, 6, 6, 6, 9, 2, 0);

			// ok - handle them
			ia.processQueuedPositions(lastTime);

			// note - we should interpret this value as being from the previous minute
			ia.storeThis(1, 1, 1, 1, mmsi, 57, lastTime);

			// note - we should interpret this value as being from the previous minute
			ia.storeThis(1, 1, 1, 1, mmsi, 12, lastTime);

			// check queue cleared
			assertEquals("queue present", 0, ia._queuedFixes.size());

			// check order of points
			tw = (TrackWrapper) layers.findLayer("" + mmsi);

			// check the start time
			System.out.println("start time:" + tw.getStartDTG().getDate());
			assertEquals("start time correct", 8, tw.getStartDTG().getDate()
					.getMinutes());
			assertEquals("start time correct", 50, tw.getStartDTG().getDate()
					.getSeconds());
			assertEquals("end time correct", 9, tw.getEndDTG().getDate().getMinutes());
			assertEquals("end time correct", 12, tw.getEndDTG().getDate()
					.getSeconds());

		}

		public void testBefore()
		{
			assertEquals("is before", true, isPreviousMinute(2, 57));
			assertEquals("is before", false, isPreviousMinute(22, 4));
			assertEquals("is before", true, isPreviousMinute(2, 33));
			assertEquals("is before", false, isPreviousMinute(1, 0));
			assertEquals("is before", true, isPreviousMinute(1, 57));
			assertEquals("is after", false, isPreviousMinute(2, 28));
			assertEquals("is after", false, isPreviousMinute(57, 2));
			assertEquals("is after", false, isPreviousMinute(4, 31));
			
			
			assertEquals("is after", true, isNextMinute(57, 2));
			assertEquals("is after", false, isNextMinute(5, 52));
			assertEquals("is after", false, isNextMinute(5, 15));
			
		}
	}
	
	public static boolean isNextMinute(int lastSecs, int newSecs)
	{
		final boolean res;

		if(newSecs > lastSecs)
		{
			res = false;
		}
		else
		{
			// so, newSecs is less than lastSecs
			if(lastSecs - newSecs > 30)
			{
				res = true;				
			}
			else
			{
				res = false;
			}
		}
		return res;
	}
	
	public static boolean isPreviousMinute(int lastSecs, int newSecs)
	{
		final boolean res;

		if(newSecs > lastSecs)
		{
			if((newSecs - lastSecs) > 30)
			{
				res = true;
			}
			else
			{
				res = false;
			}
		}
		else
		{
			res = false;
//			// so, newSecs is less than lastSecs
//			if(lastSecs - newSecs < 30)
//			{
//				res = false;				
//			}
//			else
//			{
//				res = true;
//			}
		}
		return res;
	}
}
