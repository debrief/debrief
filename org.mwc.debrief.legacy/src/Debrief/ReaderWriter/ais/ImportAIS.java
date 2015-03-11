package Debrief.ReaderWriter.ais;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.PlainImporterBase;

public class ImportAIS extends PlainImporterBase
{

	public ImportAIS()
	{
		super();
		_myTypes = new String[] { ".ais" };
	}

	@Override
	public boolean canImportThisFile(String theFile)
	{
		boolean res = true;
		String theSuffix = null;
		final int pos = theFile.lastIndexOf(".");
		theSuffix = theFile.substring(pos, theFile.length());

		for (int i = 0; i < _myTypes.length; i++)
		{
			if (theSuffix.equalsIgnoreCase(_myTypes[i]))
			{
				res = true;
				break;
			}
		}

		return res;
	}

	@Override
	public void exportThis(Plottable item)
	{
	}

	@Override
	public void exportThis(String comment)
	{
	}

	//
	// public class AisHandler implements Consumer<AisMessage>
	// {
	//
	// private Date lastDate = null;
	//
	// public void accept(AisMessage aisMessage)
	// {
	// // System.out.println("type:" + aisMessage.getMsgId() + " is:" +
	// aisMessage.getUserId());
	//
	// if (aisMessage.getMsgId() == 4)
	// {
	// AisMessage4 a4 = (AisMessage4) aisMessage;
	//
	// lastDate = a4.getDate();
	// }
	// else if (aisMessage.getMsgId() == 1)
	// {
	// AisPositionMessage apm = (AisPositionMessage) aisMessage;
	//
	// // aah, do we know the last date?
	// if(lastDate != null)
	// {
	//
	// double lat = apm.getPos().getLatitudeDouble();
	// double lon = apm.getPos().getLongitudeDouble();
	// if (!apm.isPositionValid() || Math.abs(lat) >= 90.0
	// || Math.abs(lon) >= 180.0)
	// {
	// return;
	// }
	// String name = new Integer(apm.getUserId()).toString();
	// Layer layer = getLayerFor(name);
	// if (layer != null && !(layer instanceof TrackWrapper))
	// {
	// DebriefPlugin.logError(Status.WARNING, "Invalid layer in AIS file:"
	// + layer.getName(), null);
	// return;
	// }
	// TrackWrapper trackWrapper;
	// if (layer == null)
	// {
	// trackWrapper = new TrackWrapper();
	// addLayer(trackWrapper);
	// trackWrapper.setName(name);
	// trackWrapper.setColor(Color.BLUE);
	// trackWrapper.setSymbolColor(Color.BLUE);
	// }
	// else
	// {
	// trackWrapper = (TrackWrapper) layer;
	// }
	//
	// // ok, construct the time
	// theDate = new HiResDate(lastDate.getTime());
	//
	// WorldLocation loc = new WorldLocation(lat, lon, 0);
	// int course = apm.getCog();
	// int speed = apm.getSog();
	// Fix fix = new Fix(theDate, loc, course, speed);
	// FixWrapper fixWrapper = new FixWrapper(fix);
	// fixWrapper.setTrackWrapper(trackWrapper);
	// fixWrapper.setColor(Color.BLUE);
	// fixWrapper.setLabelFormat("ddHHmm.ss");
	// trackWrapper.addFix(fixWrapper);
	// }
	// }
	// }
	// }

	@Override
	public void importThis(String fName, InputStream is)
	{
		AISParser parser = new AISParser();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		// ok, loop through the lines
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is)))
		{

			String nmea_sentence;
			while ((nmea_sentence = br.readLine()) != null)
			{

				// sort out the time leader
				String leader = nmea_sentence.substring(0, 11);
				Date date = sdf.parse(leader);

				IAISMessage res = parser.parse(nmea_sentence);

				if (res instanceof AISPositionA)
				{
					AISPositionA ar = (AISPositionA) res;
					storeThis(date, ar.getLatitude(), ar.getLongitude(), ar.getCog(),
							ar.getSog(), ar.getMmsi());
				}
				else if (res instanceof AISPositionB)
				{
					AISPositionB ar = (AISPositionB) res;
					storeThis(date, ar.getLatitude(), ar.getLongitude(), ar.getCog(),
							ar.getSog(), ar.getMmsi());
				}
				else if (res instanceof AISBaseStation)
				{
					@SuppressWarnings("unused")
					AISBaseStation base = (AISBaseStation) res;
				}
				else if (res instanceof AISVessel)
				{
					@SuppressWarnings("unused")
					AISVessel vess = (AISVessel) res;
				}
				else
				{
					System.out.println(res);
				}

			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (AISParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void storeThis(Date date, double latitude, double longitude,
			double cog, double sog, int mmsi)
	{
		// does this track exist
		Layer layer = getLayerFor("" + mmsi);
		if (layer == null)
		{
			layer = new TrackWrapper();
			layer.setName("" + mmsi);
			addLayer(layer);
		}

		HiResDate theTime = new HiResDate(date);
		WorldLocation theLocation = new WorldLocation(latitude, longitude, 0);
		double theCourseRads = Math.toRadians(cog);
		double theSpeedYps = new WorldSpeed(sog, WorldSpeed.Kts)
				.getValueIn(WorldSpeed.ft_sec) / 3d;
		// ok, now add the position
		Fix newFix = new Fix(theTime, theLocation, theCourseRads, theSpeedYps);
		layer.add(new FixWrapper(newFix));
	}

	public static class TestImportAIS extends TestCase
	{
		public void testImport() throws FileNotFoundException
		{
			String testFile = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/150304_0854.ais";
			File testI = new File(testFile);
			assertTrue(testI.exists());

			InputStream is = new FileInputStream(testI);

			final Layers tLayers = new Layers();

			ImportAIS importer = new ImportAIS()
			{

				@Override
				public void addLayer(Layer theLayer)
				{
					tLayers.addThisLayer(theLayer);
				}

				@Override
				public Layer getLayerFor(String theName)
				{
					return tLayers.findLayer(theName);
				}

			};
			importer.importThis(testFile, is);

			// hmmm, how many tracks
			assertEquals("got new tracks", 6, tLayers.size());

		}
	}
}
