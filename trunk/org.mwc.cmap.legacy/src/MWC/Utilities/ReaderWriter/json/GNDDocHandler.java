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

	private static JsonNode timeBoundsFor(final Track track, final ObjectMapper mapper)
	{
		final Date first = track.getStartDTG().getDate();
		final Date last = track.getEndDTG().getDate();

		final ObjectNode times = mapper.createObjectNode();
		times.put("start", timeFor(first));
		times.put("end", timeFor(last));

		return times;
	}

	private static String timeFor(final Date date)
	{
		// do a bit of grooming of the data
		final DateFormat df = new InternetDateFormat();
		final String theTime = df.format(date);
		return theTime;
	}

	private static JsonNode locationFor(final WorldLocation loc, final ObjectMapper mapper)
	{
		final ArrayNode locs = mapper.createArrayNode();
		locs.add(loc.getLat());
		locs.add(loc.getLong());
		return locs;
	}

	public ObjectNode toJson(final String name, final Track track, final String platform,
			final String platformType, final String sensor, final String sensorType, final String trial)
			throws IOException
	{
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		final ObjectNode root = mapper.createObjectNode();

		// now the data arrays
		final ArrayNode locationArr = mapper.createArrayNode();
		final ArrayNode timeArr = mapper.createArrayNode();
		final ArrayNode eleArr = mapper.createArrayNode();
		final ArrayNode headArr = mapper.createArrayNode();
		final ArrayNode speedArr = mapper.createArrayNode();

		WorldArea area = null;
		TimePeriod extent = null;
		final Enumeration<Fix> fixes = track.getFixes();
		while (fixes.hasMoreElements())
		{
			final Fix fix = fixes.nextElement();
			final ArrayNode arr = mapper.createArrayNode();
			arr.add(fix.getLocation().getLong());
			arr.add(fix.getLocation().getLat());
			locationArr.add(arr);
			timeArr.add(timeFor(fix.getTime().getDate()));
			eleArr.add(-fix.getLocation().getDepth());
			headArr.add(fix.getCourse());

			// get m/s
			final double yps = fix.getSpeed();
			final double kts = MWC.Algorithms.Conversions.Yps2Kts(yps);
			final double m_sec = MWC.Algorithms.Conversions.Kts2Mps(kts);
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

		final ObjectNode locHolder = mapper.createObjectNode();
		locHolder.put("type", "MultiPoint");
		locHolder.put("coordinates", locationArr);
		
		root.put("location", locHolder);
		root.put("time", timeArr);
		root.put("elevation", eleArr);
		root.put("heading", headArr);
		root.put("speed", speedArr);
		
		
		// sort out the metadata
		final ObjectNode metadata = doMetadata(name, platform, platformType, sensor,
				sensorType, trial, mapper);
		

		// store the metadata
		root.put("metadata", metadata);

		// now the type specific stuff
		metadata.put("type", "track");
		final ArrayNode types = mapper.createArrayNode();
		types.add("location");
		types.add("time");
		types.add("elevation");
		types.add("heading");
		types.add("speed");
		metadata.put("data_type", types);

		// now the location bounds
		final ObjectNode gBounds = mapper.createObjectNode();
		final ArrayNode coords = mapper.createArrayNode();
		coords.add( locationFor(area.getTopLeft(), mapper));
		coords.add( locationFor(area.getBottomRight(), mapper));
		gBounds.put("coordinates", coords);
		gBounds.put("type", "envelope");
		metadata.put("geo_bounds", gBounds);

		// and the time bounds
		metadata.put("time_bounds", timeBoundsFor(track, mapper));

		return root;
	}

	public ObjectNode doMetadata(final String name, final String platform,
			final String platformType, final String sensor, final String sensorType, final String trial,
			final ObjectMapper mapper)
	{
		final ObjectNode metadata = mapper.createObjectNode();
		metadata.put("name", name);
		metadata.put("platform", platform);
		metadata.put("platform_type", platformType);
		metadata.put("sensor", sensor);
		metadata.put("sensor_type", sensorType);
		metadata.put("trial", trial);

		return metadata;
	}

	public ObjectNode toJson(final String name, final List<NarrativeEntry> entries,
			final String platform, final String platformType, final String sensor, final String sensorType,
			final String trial) throws IOException
	{

		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		final ObjectNode root = mapper.createObjectNode();

		// sort out the metadata
		final ObjectNode metadata = doMetadata(name, platform, platformType, sensor,
				sensorType, trial, mapper);

		// store the metadata
		root.put("metadata", metadata);

		// now the type specific stuff
		metadata.put("type", "narrative");
		final ArrayNode types = mapper.createArrayNode();
		types.add("obs");
		types.add("obs_type");
		metadata.put("data_type", types);

		// now the data arrays
		final ArrayNode obsArr = mapper.createArrayNode();
		final ArrayNode obsTypeArr = mapper.createArrayNode();
		final ArrayNode timeArr = mapper.createArrayNode();

		long first = -1;
		long last = 0;

		final Iterator<NarrativeEntry> iter = entries.iterator();
		while (iter.hasNext())
		{
			final NarrativeEntry entry = iter.next();

			obsArr.add(entry.getEntry());

			if (entry.getType() != null)
				obsTypeArr.add(entry.getType());

			final HiResDate time = entry.getDTG();
			timeArr.add(timeFor(time.getDate()));
			final long t = time.getDate().getTime();

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
		final ObjectNode times = mapper.createObjectNode();
		times.put("start", timeFor(new Date(first)));
		times.put("end", timeFor(new Date(last)));
		metadata.put("time_bounds", times);

		return root;
	}

	public static String asString(final JsonNode root) throws IOException
	{
		final ObjectMapper mapper = new ObjectMapper();
		final StringWriter writer = new StringWriter();
		mapper.writeValue(writer, root);
		return writer.toString();
	}

	public final static class GNDDocument
	{
		private final String _name;
		private Track _track;
		@SuppressWarnings("unused")
		private final String _platform;
		@SuppressWarnings("unused")
		private final String _platformType;
		@SuppressWarnings("unused")
		private final String _sensor;
		@SuppressWarnings("unused")
		private final String _sensorType;
		@SuppressWarnings("unused")
		private final String _trial;

		public GNDDocument(final String name, final Track track, final String platform,
				final String platformType, final String sensor, final String sensorType, final String trial)
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
		public GNDDocument(final String content) throws JsonParseException,
				JsonMappingException, IOException, ParseException
		{
			final ObjectMapper mapper = new ObjectMapper();
			final Map<String, Object> root = mapper.readValue(content, Map.class);
			final LinkedHashMap<String, Object> meta = (LinkedHashMap<String, Object>) root
					.get("metadata");
			_name = (String) meta.get("name");
			_platform = (String) meta.get("platform");
			_platformType = (String) meta.get("platform_type");
			_sensor = (String) meta.get("sensor");
			_sensorType = (String) meta.get("sensor_type");
			_trial = (String) meta.get("trial");

			// ok, and populate the tracks
			final ArrayList<String> dTypes = (ArrayList<String>) meta.get("data_type");
			if (dTypes.contains("lat") && dTypes.contains("lon")
					&& dTypes.contains("time"))
			{
				// ok, go for it.
				final ArrayList<Double> latArr = (ArrayList<Double>) root.get("lat");
				final ArrayList<Double> lonArr = (ArrayList<Double>) root.get("lon");
				final ArrayList<String> timeArr = (ArrayList<String>) root.get("time");
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
				for (final Iterator<String> iterator = timeArr.iterator(); iterator.hasNext();)
				{
					final String string = iterator.next();
					final double lat = latArr.get(ctr);
					final double lon = lonArr.get(ctr);

					double depth = 0, course = 0, speed = 0;
					if (eleArr != null)
						depth = -eleArr.get(ctr);

					if (crseArr != null)
						course = crseArr.get(ctr);
					if (spdArr != null)
						speed = spdArr.get(ctr);

					final Date hd = timeFrom(string);
					final HiResDate dt = new HiResDate(hd);
					final WorldLocation theLoc = new WorldLocation(lat, lon, depth);
					final Fix thisF = new Fix(dt, theLoc, course, speed);
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

			final String format = "yyyy-MM-dd HH:mm:ss";

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
		public static Track getTestTrack(final String theName)
		{
			double course = 135;
			double speed = 5;
			// put in some fixes
			final Vector<Fix> fixes = new Vector<Fix>();
			for (int i = 0; i < 30; i++)
			{
				@SuppressWarnings("deprecation")
				final
				HiResDate time = new HiResDate(new Date(112, 6, 6, 12, 5, i * 25));
				final WorldLocation loc = new WorldLocation(4d + i / 100d, 5d + i / 100d, 0);
				course += (-3 + Math.random() * 5.5d);
				speed += (-1 + Math.random() * 2d);
				final Fix newF = new Fix(time, loc, course, speed);
				fixes.add(newF);
			}
			final Track track = new Track(fixes);
			track.setName(theName);
			return track;
		}

		public void testToGNDDoc() throws IOException
		{
			// ok, create a bit of a track
			final Track track = getTestTrack("some-name");

			assertNotNull("track not found", track);

			final GNDDocument doc = new GNDDocument("NAME", track, "PLATFORM", "P_TYPE",
					"SENS", "S_TYPE", "TRIAL");

			// and check the results
			assertNotNull("No output received", doc);

			// and check the contents
			final ObjectNode root = new GNDDocHandler().toJson("NAME", track, "PLATFORM",
					"P_TYPE", "SENS", "S_TYPE", "TRIAL");

			assertNotNull("found root", root);

			final ObjectNode metadata = (ObjectNode) root.get("metadata");

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
