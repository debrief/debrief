package MWC.Utilities.ReaderWriter.json;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
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

	public static class GNDDoc
	{
		private String _name;
		private Track _track;
		private String _platform;
		private String _platformType;
		private String _sensor;
		private String _sensorType;
		private String _trial;

		public GNDDoc(String name, Track track, String platform,
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

		public String toJSON() throws JsonGenerationException,
				JsonMappingException, IOException
		{
			String res = null;

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
			ObjectNode root = mapper.createObjectNode();
			root.put("metadata", getMetadata(mapper));

			// now the data arrays
			ArrayNode latArr = mapper.createArrayNode();
			ArrayNode longArr = mapper.createArrayNode();
			ArrayNode timeArr = mapper.createArrayNode();
			ArrayNode eleArr = mapper.createArrayNode();

			Enumeration<Fix> fixes = _track.getFixes();
			while (fixes.hasMoreElements())
			{
				Fix fix = (Fix) fixes.nextElement();
				latArr.add(fix.getLocation().getLat());
				longArr.add(fix.getLocation().getLong());
				timeArr.add(timeFor(fix.getTime().getDate()));
				eleArr.add(-fix.getLocation().getDepth());
			}

			root.put("lat", latArr);
			root.put("lon", longArr);
			root.put("time", timeArr);
			root.put("elevation", eleArr);

			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, root);
			res = writer.toString();

			return res;
		}

		public Track getTrack()
		{
			return _track;
		}

		public ObjectNode getMetadata(ObjectMapper mapper)
		{
			ObjectNode root = mapper.createObjectNode();
			root.put("name", _name);
			root.put("platform", _platform);
			root.put("platform_type", _platformType);
			root.put("sensor", _sensor);
			root.put("sensor_type", _sensorType);
			root.put("trial", _trial);
			root.put("type", "track");
			
			ArrayNode types = mapper.createArrayNode();
			types.add("lat");
			types.add("lon");
			types.add("time");
			types.add("elevation");
			root.put("data_type", types);

			// now the location bounds
			WorldArea theArea = geoBoundsFor(_track);
			ObjectNode gBounds = mapper.createObjectNode();
			gBounds.put("tl", toJsonObject(theArea.getTopLeft(), mapper ));
			gBounds.put("br", toJsonObject(theArea.getBottomRight(), mapper));
			root.put("geo_bounds", gBounds);
			
			// and the time bounds
			root.put("time_bounds", timeBoundsFor(_track, mapper));
			return root;
		}

		private JsonNode toJsonObject(WorldLocation loc, ObjectMapper mapper)
		{
			ArrayNode locs = mapper.createArrayNode();
			locs.add(loc.getLat());
			locs.add(loc.getLong());
			return locs;
		}

		private JsonNode timeBoundsFor(Track track, ObjectMapper mapper)
		{
			Date first = track.getStartDTG().getDate();
			Date last = track.getEndDTG().getDate();
			
			ObjectNode times = mapper.createObjectNode();
			times.put("start", timeFor(first));
			times.put("end", timeFor(last));
			
			
			return times;
		}

		private WorldArea geoBoundsFor(Track track)
		{
			WorldArea bounds = null;
			Enumeration<Fix> fixes = track.getFixes();
			while (fixes.hasMoreElements())
			{
				Fix fix = (Fix) fixes.nextElement();
				if (bounds == null)
					bounds = new WorldArea(fix.getLocation(), fix.getLocation());
				else
					bounds.extend(fix.getLocation());
			}
//			String res = "{\"tl\":" + toString(bounds.getTopLeft()) + ",\"br\":"
//					+ toString(bounds.getBottomRight()) + "}";
			return bounds;
		}

		private String timeFor(Date date)
		{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			return df.format(date);
		}

	}

	public String toJSON(Track theTrack, String thePlatform,
			String thePlatformType, String theSensor, String theSensorType,
			String theTrial) throws IOException
	{
		// ok, create the GND Doc
		GNDDoc doc = new GNDDoc(theTrack.getName(), theTrack, thePlatform,
				thePlatformType, theSensor, theSensorType, theTrial);

		// and now convert it
		return toJSON(doc);
	}

	public String toJSON(GNDDoc theDoc) throws IOException
	{
		return theDoc.toJSON();
	}

	public static class TestJSON extends TestCase
	{
		public static Track getTestTrack()
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
			return track;
		}

		public static String getTestString()
		{
			return null;
		}

		public void testToGNDDoc() throws IOException
		{
			// ok, create a bit of a track
			Track track = getTestTrack();

			assertNotNull("track not found", track);

			GNDDoc doc = new GNDDoc("NAME", track, "PLATFORM", "P_TYPE", "SENS",
					"S_TYPE", "TRIAL");

			// and check the results
			assertNotNull("No output received", doc);

			// and check the contents
			ObjectNode metadata = doc.getMetadata(new ObjectMapper());

			assertNotNull("metadata not found", metadata);
			assertEquals("name wrong", "NAME", metadata.get("name").asText());
			assertEquals("platform wrong", "PLATFORM", metadata.get("platform").asText());
			assertEquals("platform type wrong", "P_TYPE",
					metadata.get("platform_type").asText());
			assertEquals("sensor wrong", "SENS", metadata.get("sensor").asText());
			assertEquals("sensor type wrong", "S_TYPE", metadata.get("sensor_type").asText());
			assertEquals("trial wrong", "TRIAL", metadata.get("trial").asText());
	//		assertEquals("data types wrong",
		//			"[\"lat\",\"long\",\"time\",\"elevation\"", metadata.get("data_type"));
			assertNotNull("geo bounds wrong", metadata.get("geo_bounds"));
			assertNotNull("time bounds wrong", metadata.get("time_bounds"));

			// try the overall export
			String res = doc.toJSON();
			assertNotNull(res);
		}
	}

}
