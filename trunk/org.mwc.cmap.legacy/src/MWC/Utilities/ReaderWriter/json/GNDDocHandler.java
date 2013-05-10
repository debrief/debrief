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
import java.util.List;
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
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
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

	private static String timeFor(Date date)
	{
		// do a bit of grooming of the data
		DateFormat df = new InternetDateFormat();
		String theTime = df.format(date);
		return theTime;
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

		// now the data arrays
		ArrayNode locationArr = mapper.createArrayNode();
		ArrayNode timeArr = mapper.createArrayNode();
		ArrayNode eleArr = mapper.createArrayNode();
		ArrayNode headArr = mapper.createArrayNode();
		ArrayNode speedArr = mapper.createArrayNode();

		WorldArea area = null;
		TimePeriod extent = null;
		Enumeration<Fix> fixes = track.getFixes();
		while (fixes.hasMoreElements())
		{
			Fix fix = fixes.nextElement();
			ArrayNode arr = mapper.createArrayNode();
			arr.add(fix.getLocation().getLong());
			arr.add(fix.getLocation().getLat());
			locationArr.add(arr);
			timeArr.add(timeFor(fix.getTime().getDate()));
			eleArr.add(-fix.getLocation().getDepth());
			headArr.add(fix.getCourse());

			// get m/s
			double yps = fix.getSpeed();
			double kts = MWC.Algorithms.Conversions.Yps2Kts(yps);
			double m_sec = MWC.Algorithms.Conversions.Kts2Mps(kts);
			speedArr.add(m_sec);
			
			if(area == null)
			{
				area = new WorldArea(fix.getLocation(), fix.getLocation());
				extent = new TimePeriod.BaseTimePeriod(fix.getTime(), fix.getTime());
			}
			else
			{
				area.extend(fix.getLocation());
				extent.extend(fix.getTime());
			}
		}

		ObjectNode locHolder = mapper.createObjectNode();
		locHolder.put("type", "MultiPoint");
		locHolder.put("coordinates", locationArr);
		
		root.put("location", locHolder);
		root.put("time", timeArr);
		root.put("elevation", eleArr);
		root.put("heading", headArr);
		root.put("speed", speedArr);
		
		
		// sort out the metadata
		ObjectNode metadata = doMetadata(name, platform, platformType, sensor,
				sensorType, trial, mapper);
		

		// store the metadata
		root.put("metadata", metadata);

		// now the type specific stuff
		metadata.put("type", "track");
		ArrayNode types = mapper.createArrayNode();
		types.add("location");
		types.add("time");
		types.add("elevation");
		types.add("heading");
		types.add("speed");
		metadata.put("data_type", types);

		// now the location bounds
		ObjectNode gBounds = mapper.createObjectNode();
		ArrayNode coords = mapper.createArrayNode();
		coords.add( locationFor(area.getTopLeft(), mapper));
		coords.add( locationFor(area.getBottomRight(), mapper));
		gBounds.put("coordinates", coords);
		gBounds.put("type", "envelope");
		metadata.put("geo_bounds", gBounds);

		// and the time bounds
		metadata.put("time_bounds", timeBoundsFor(track, mapper));

		return root;
	}

	public ObjectNode doMetadata(String name, String platform,
			String platformType, String sensor, String sensorType, String trial,
			ObjectMapper mapper)
	{
		ObjectNode metadata = mapper.createObjectNode();
		metadata.put("name", name);
		metadata.put("platform", platform);
		metadata.put("platform_type", platformType);
		metadata.put("sensor", sensor);
		metadata.put("sensor_type", sensorType);
		metadata.put("trial", trial);

		return metadata;
	}

	public ObjectNode toJson(String name, List<NarrativeEntry> entries,
			String platform, String platformType, String sensor, String sensorType,
			String trial) throws IOException
	{

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		ObjectNode root = mapper.createObjectNode();

		// sort out the metadata
		ObjectNode metadata = doMetadata(name, platform, platformType, sensor,
				sensorType, trial, mapper);

		// store the metadata
		root.put("metadata", metadata);

		// now the type specific stuff
		metadata.put("type", "narrative");
		ArrayNode types = mapper.createArrayNode();
		types.add("obs");
		types.add("obs_type");
		metadata.put("data_type", types);

		// now the data arrays
		ArrayNode obsArr = mapper.createArrayNode();
		ArrayNode obsTypeArr = mapper.createArrayNode();
		ArrayNode timeArr = mapper.createArrayNode();

		long first = -1;
		long last = 0;

		Iterator<NarrativeEntry> iter = entries.iterator();
		while (iter.hasNext())
		{
			NarrativeEntry entry = iter.next();

			obsArr.add(entry.getEntry());

			if (entry.getType() != null)
				obsTypeArr.add(entry.getType());

			HiResDate time = entry.getDTG();
			timeArr.add(timeFor(time.getDate()));
			long t = time.getDate().getTime();

			// store the first
			if (first == -1)
				first = t;

			// and the last
			last = Math.max(last, t);
		}

		root.put("obs", obsArr);
		if (obsTypeArr.size() > 0)
			root.put("obs_type", obsTypeArr);
		root.put("time", timeArr);

		// store the time bounds
		ObjectNode times = mapper.createObjectNode();
		times.put("start", timeFor(new Date(first)));
		times.put("end", timeFor(new Date(last)));
		metadata.put("time_bounds", times);

		return root;
	}

	public static String asString(JsonNode root) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, root);
		return writer.toString();
	}

	public final static class GNDDocument
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
			for (int i = 0; i < 30; i++)
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
			ObjectNode root = new GNDDocHandler().toJson("NAME", track, "PLATFORM",
					"P_TYPE", "SENS", "S_TYPE", "TRIAL");

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
