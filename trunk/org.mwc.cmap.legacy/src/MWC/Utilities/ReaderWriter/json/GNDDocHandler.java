package MWC.Utilities.ReaderWriter.json;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.TacticalData.Track;

public class GNDDocHandler
{

	private static JsonNode timeBoundsFor(Track track, ObjectMapper mapper)
	{
		Date first = track.getStartDTG().getDate();
		Date last = track.getEndDTG().getDate();

		ObjectNode times = mapper.createObjectNode();
		times.put("start", timeFor(first));
		times.put("end", timeFor(last));

		return times;
	}

	private static WorldArea geoBoundsFor(Track track)
	{
		WorldArea bounds = null;
		Enumeration<Fix> fixes = track.getFixes();
		while (fixes.hasMoreElements())
		{
			Fix fix = fixes.nextElement();
			if (bounds == null)
				bounds = new WorldArea(fix.getLocation(), fix.getLocation());
			else
				bounds.extend(fix.getLocation());
		}
		return bounds;
	}

	private static String timeFor(Date date)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		return df.format(date);
	}

	private static JsonNode locationFor(WorldLocation loc, ObjectMapper mapper)
	{
		ArrayNode locs = mapper.createArrayNode();
		locs.add(loc.getLat());
		locs.add(loc.getLong());
		return locs;
	}

	public ObjectNode toJson(String name, Track track, String platform,
			String platformType, String sensor, String sensorType, String trial)
			throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		ObjectNode root = mapper.createObjectNode();

		ObjectNode metadata = mapper.createObjectNode();
		metadata.put("name", name);
		metadata.put("platform", platform);
		metadata.put("platform_type", platformType);
		metadata.put("sensor", sensor);
		metadata.put("sensor_type", sensorType);
		metadata.put("trial", trial);
		metadata.put("type", "track");

		ArrayNode types = mapper.createArrayNode();
		types.add("lat");
		types.add("lon");
		types.add("time");
		types.add("elevation");
		types.add("course");
		types.add("speed");
		metadata.put("data_type", types);

		// now the location bounds
		WorldArea theArea = geoBoundsFor(track);
		ObjectNode gBounds = mapper.createObjectNode();
		gBounds.put("tl", locationFor(theArea.getTopLeft(), mapper));
		gBounds.put("br", locationFor(theArea.getBottomRight(), mapper));
		metadata.put("geo_bounds", gBounds);

		// and the time bounds
		metadata.put("time_bounds", timeBoundsFor(track, mapper));

		root.put("metadata", metadata);

		// now the data arrays
		ArrayNode latArr = mapper.createArrayNode();
		ArrayNode longArr = mapper.createArrayNode();
		ArrayNode timeArr = mapper.createArrayNode();
		ArrayNode eleArr = mapper.createArrayNode();
		ArrayNode crseArr = mapper.createArrayNode();
		ArrayNode speedArr = mapper.createArrayNode();

		Enumeration<Fix> fixes = track.getFixes();
		while (fixes.hasMoreElements())
		{
			Fix fix = fixes.nextElement();
			latArr.add(fix.getLocation().getLat());
			longArr.add(fix.getLocation().getLong());
			timeArr.add(timeFor(fix.getTime().getDate()));
			eleArr.add(-fix.getLocation().getDepth());
			crseArr.add(fix.getCourse());
			
			
			// get m/s
			double yps = fix.getSpeed();
			double kts = MWC.Algorithms.Conversions.Yps2Kts(yps);
			double m_sec = MWC.Algorithms.Conversions.Kts2Mps(kts);
			speedArr.add(m_sec);
		}

		root.put("lat", latArr);
		root.put("lon", longArr);
		root.put("time", timeArr);
		root.put("elevation", eleArr);
		root.put("course",  crseArr);
		root.put("speed", speedArr);

		return root;
	}
	
	public static String asString(JsonNode root) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, root);
		return  writer.toString();
	}
	
	public final  static class GNDDocument
	{
		private String _name;
		private Track _track;
		@SuppressWarnings("unused")
		private String _platform;
		@SuppressWarnings("unused")
		private String _platformType;
		@SuppressWarnings("unused")
		private String _sensor;
		@SuppressWarnings("unused")
		private String _sensorType;
		@SuppressWarnings("unused")
		private String _trial;

		public GNDDocument(String name, Track track, String platform,
				String platformType, String sensor, String sensorType, String trial)
		{
			_name = name;
			_track = track;
			_platform = platform;
			_platformType = platformType;
			_sensor = sensor;
			_sensorType = sensorType;
			_trial = trial;
		}

		/**
		 * construct ourselves from the object
		 * 
		 * @param content
		 * @throws IOException
		 * @throws JsonMappingException
		 * @throws JsonParseException
		 * @throws ParseException
		 */
		@SuppressWarnings("unchecked")
		public GNDDocument(String content) throws JsonParseException,
				JsonMappingException, IOException, ParseException
		{
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> root = mapper.readValue(content, Map.class);
			LinkedHashMap<String, Object> meta = (LinkedHashMap<String, Object>) root
					.get("metadata");
			_name = (String) meta.get("name");
			_platform = (String) meta.get("platform");
			_platformType = (String) meta.get("platform_type");
			_sensor = (String) meta.get("sensor");
			_sensorType = (String) meta.get("sensor_type");
			_trial = (String) meta.get("trial");

			// ok, and populate the tracks
			ArrayList<String> dTypes = (ArrayList<String>) meta.get("data_type");
			if (dTypes.contains("lat") && dTypes.contains("lon")
					&& dTypes.contains("time"))
			{
				// ok, go for it.
				ArrayList<Double> latArr = (ArrayList<Double>) root.get("lat");
				ArrayList<Double> lonArr = (ArrayList<Double>) root.get("lon");
				ArrayList<String> timeArr = (ArrayList<String>) root.get("time");
				ArrayList<Double> eleArr = null;
				ArrayList<Double> crseArr = null;
				ArrayList<Double> spdArr = null;

				if (dTypes.contains("elevation"))
					eleArr = (ArrayList<Double>) root.get("elevation");
				if (dTypes.contains("course"))
					crseArr = (ArrayList<Double>) root.get("course");
				if (dTypes.contains("speed"))
					spdArr = (ArrayList<Double>) root.get("speed");

				_track = new Track();
				_track.setName(_name);

				int ctr = 0;
				for (Iterator<String> iterator = timeArr.iterator(); iterator.hasNext();)
				{
					String string = iterator.next();
					double lat = latArr.get(ctr);
					double lon = lonArr.get(ctr);

					double depth = 0, course = 0, speed = 0;
					if (eleArr != null)
						depth = -eleArr.get(ctr);

					if (crseArr != null)
						course = crseArr.get(ctr);
					if (spdArr != null)
						speed = spdArr.get(ctr);

					Date hd = timeFrom(string);
					HiResDate dt = new HiResDate(hd);
					WorldLocation theLoc = new WorldLocation(lat, lon, depth);
					Fix thisF = new Fix(dt, theLoc, course, speed);
					_track.addFix(thisF);

					ctr++;
				}

			}

		}

		public Track getTrack()
		{
			return _track;
		}

		private Date timeFrom(String str) throws ParseException
		{

			String format = "yyyy-MM-dd HH:mm:ss";

			if (str.indexOf("T") != -1)
			{
				str = str.replace("T", " ");
			}

			if (str.indexOf("Z") != -1)
			{
				str = str.replace("Z", "");
			}

			return new SimpleDateFormat(format).parse(str);

		}

		public String getName()
		{
			return _name;
		}

	}

	public static class TestJSON extends TestCase
	{
		public static Track getTestTrack(String theName)
		{
			double course = 135;
			double speed = 5;
			// put in some fixes
			Vector<Fix> fixes = new Vector<Fix>();
			for (int i = 0; i < 3; i++)
			{
				@SuppressWarnings("deprecation")
				HiResDate time = new HiResDate(new Date(112, 6, 6, 12, 5, i * 25));
				WorldLocation loc = new WorldLocation(4d + i / 100d, 5d + i / 100d, 0);
				course += (-3 + Math.random() * 5.5d);
				speed += (-1 + Math.random() * 2d);
				Fix newF = new Fix(time, loc, course, speed);
				fixes.add(newF);
			}
			Track track = new Track(fixes);
			track.setName(theName);
			return track;
		}

		public void testToGNDDoc() throws IOException
		{
			// ok, create a bit of a track
			Track track = getTestTrack("some-name");

			assertNotNull("track not found", track);

			GNDDocument doc = new GNDDocument("NAME", track, "PLATFORM", "P_TYPE",
					"SENS", "S_TYPE", "TRIAL");

			// and check the results
			assertNotNull("No output received", doc);

			// and check the contents
			ObjectNode root = new GNDDocHandler().toJson("NAME", track, "PLATFORM", "P_TYPE",
					"SENS", "S_TYPE", "TRIAL");		

			assertNotNull("found root", root);
			
			ObjectNode metadata = (ObjectNode) root.get("metadata");

			assertNotNull("metadata not found", metadata);
			assertEquals("name wrong", "NAME", metadata.get("name").asText());
			assertEquals("platform wrong", "PLATFORM", metadata.get("platform")
					.asText());
			assertEquals("platform type wrong", "P_TYPE",
					metadata.get("platform_type").asText());
			assertEquals("sensor wrong", "SENS", metadata.get("sensor").asText());
			assertEquals("sensor type wrong", "S_TYPE", metadata.get("sensor_type")
					.asText());
			assertEquals("trial wrong", "TRIAL", metadata.get("trial").asText());
			// assertEquals("data types wrong",
			// "[\"lat\",\"long\",\"time\",\"elevation\"", metadata.get("data_type"));
			assertNotNull("geo bounds wrong", metadata.get("geo_bounds"));
			assertNotNull("time bounds wrong", metadata.get("time_bounds"));
		}
	}

}
