package Debrief.ReaderWriter.ais;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
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
	private HashMap<Integer, String> _nameLookups;
	
	private Timestamp _lastTime = null;
	private ArrayList<Fix> _queuedFixes;
	
	public ImportAIS(Layers target)
	{
		super();
		_layers = target;
	}

	public void importThis(String fName, InputStream is) throws Exception
	{
		_nameLookups = new HashMap<Integer, String>();
		
		// reset the timestamp, so we deduce the time
		_lastTime = null;		
		_queuedFixes = new ArrayList<Fix>();
		
		AISParser parser = new AISParser();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		// ok, loop through the lines
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String nmea_sentence;
			while ((nmea_sentence = br.readLine()) != null)
			{

				// sort out the time leader
	//			String leader = nmea_sentence.substring(0, 11);
//				Date date = sdf.parse(leader);

				IAISMessage res = parser.parse(nmea_sentence);

				if (res instanceof AISPositionA)
				{
					AISPositionA ar = (AISPositionA) res;
					
//					if(ar.getMmsi() == 563622000)
//					{
//						System.out.println(nmea_sentence + " secs:" + ar.getMsgTimestamp().getSeconds());
//					}
					
					storeThis(ar.getLatitude(), ar.getLongitude(), ar.getCog(), ar.getSog(),
							ar.getMmsi(), ar.getMsgTimestamp().getSeconds());
				}
				else if (res instanceof AISPositionB)
				{
					AISPositionB ar = (AISPositionB) res;
					storeThis(ar.getLatitude(), ar.getLongitude(), ar.getCog(), ar.getSog(),
							ar.getMmsi(), ar.getMsgTimestamp().getSeconds());
					
					if(ar.getMmsi() == 563622000)
					{
	//					System.out.println(nmea_sentence + " secs:" + ar.getMsgTimestamp().getSeconds());
//						System.out.println(nmea_sentence);
					}

				}
				else if (res instanceof AISBaseStation)
				{
					AISBaseStation base = (AISBaseStation) res;
					System.out.println(nmea_sentence);// + " BASE ");
					_lastTime = base.getTimestamp();
					
			//		System.out.println("new Base time:" + _lastTime + " MMSI:" + base.getMmsi());
					
					// hey, we may have stacked up some positions while
					// they are waiting for the first data item
					if(_queuedFixes.size() > 0)
						processQueuedPositions();
					
					
				}
				else if (res instanceof AISVessel)
				{
					AISVessel vess = (AISVessel) res;

//					if(vess.getMmsi() == 220433000)
//					{
//						System.out.println(nmea_sentence + " VESSEL");
//					}

					// ok, store the id against the name
					_nameLookups.put(vess.getMmsi(), vess.getName());
					
//					System.out.println("Named vessel:" + vess.getName() + " MMSI:" + vess.getMmsi());

					// ok, see if we can name this vessel
					Layer thisLayer = _layers.findLayer("" + vess.getMmsi());
					if(thisLayer != null)
					{
						thisLayer.setName(vess.getName());
					}
					else
					{
			//			System.out.println("VESSEL NOT FOUND FOR ID:" + vess.getMmsi());
					}
					
				}
				else
				{
			//		System.out.println(res);
				}

			}
	}

	private void processQueuedPositions()
	{
		Iterator<Fix> iter = _queuedFixes.iterator();
		int lastSecs = _lastTime.getSeconds();
		
		while(iter.hasNext())
		{
			Fix fix = iter.next();
			
			// ok, we have to replace the time
			int secs = fix.getTime().getDate().getSeconds();

			// build the new date
			Date newDate = new Date(_lastTime.getTime());

			// is this less than the queued secs
			if(secs > lastSecs)
			{
				// ok, we have to decrement the minutes
				newDate.setMinutes(newDate.getMinutes()-1);
			}

			// and the seconds
			newDate.setSeconds(secs);
			
			// and store it
			fix.setTime(new HiResDate(newDate));
		}
		
		// done, clear the list
		_queuedFixes.clear();
	}

	private void storeThis(double latitude, double longitude, double cog,
			double sog, int mmsi, int secs)
	{
		String layerName = null;
		
		// try to do a name lookup
		String lookupName = _nameLookups.get(mmsi);
		
		if(lookupName != null)
			layerName = lookupName;
		else
			layerName = "" + mmsi;
		
		// does this track exist
		Layer layer = _layers.findLayer(layerName);
		if (layer == null)
		{
			TrackWrapper tw = new TrackWrapper();
			tw.setColor(new Color(45, 97, 0));
			layer =tw;
			layer.setName(layerName);
			_layers.addThisLayer(layer);
		}

		long theTime;
		
		if(_lastTime != null)
		{
			theTime = _lastTime.getTime();
		}
		else
		{
			theTime = 0;
		}
		
		Date newDate = new Date(theTime);		
		newDate.setSeconds(secs);		
		
		HiResDate hDate = new HiResDate(newDate);
		
		WorldLocation theLocation = new WorldLocation(latitude, longitude, 0);
		double theCourseRads = Math.toRadians(cog);
		double theSpeedYps = new WorldSpeed(sog, WorldSpeed.Kts)
				.getValueIn(WorldSpeed.ft_sec) / 3d;
		// ok, now add the position
		Fix newFix = new Fix(hDate, theLocation, theCourseRads, theSpeedYps);	
		FixWrapper fixWrapper = new FixWrapper(newFix);
		fixWrapper.setLabel(newDate.toString());
		
		// ok, do we have a time offset yet? if we don't we should queue up this fix
		if(_lastTime == null)
			_queuedFixes.add(newFix);
		else
		{
			// see if there's already a fix after this one - in which case we should
			// move back a minute
			TrackWrapper tw = (TrackWrapper) layer;
			HiResDate lastTime = tw.getEndDTG();			
			if((lastTime != null) && (lastTime.greaterThan(newFix.getTime())))
			{
				// ok, we really need to move it backwards
				TrackSegment seg = (TrackSegment) tw.getSegments().first();
				Enumeration<Editable> numer = seg.elements();
				while (numer.hasMoreElements())
				{
					FixWrapper oldF = (FixWrapper) numer.nextElement();
					if(oldF.getDTG().getDate().getSeconds() > newFix.getTime().getDate().getSeconds())
					{
						Date tmpDate = new Date(newFix.getTime().getDate().getTime());
						tmpDate.setMinutes(tmpDate.getMinutes()-1);
						tmpDate.setSeconds(secs);
						oldF.getFix().setTime(new HiResDate(tmpDate.getTime()));
					}
				}
			}
		}
		
		layer.add(fixWrapper);		

	}

	public static class TestImportAIS extends TestCase
	{
		public void testShortImport() throws Exception
		{
			testImport("../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0854_trimmed.ais", 1);
		}
		
		public void testFullImport() throws Exception
		{
			testImport("../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0854.ais", 6);
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
				System.out.println(thisF.getDateTimeGroup().getDate() +" COG:" + 
				(int)Math.toDegrees(thisF.getCourse()) +" SOG:" + (int)thisF.getSpeed());
				
			}
			
		}
	}
}
