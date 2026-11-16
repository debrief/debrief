/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.Utilities.ReaderWriter.json;

import static MWC.Utilities.ReaderWriter.json.GNDDocHandler.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.Utilities.ReaderWriter.json.GNDDocHandler.*;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.Track;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Test;

public class GNDDocHandlerTest {
	public static Track getTestTrack(final String theName) {
		double course = 135;
		double speed = 5;
		// put in some fixes
		final Vector<Fix> fixes = new Vector<Fix>();
		for (int i = 0; i < 30; i++) {
			@SuppressWarnings("deprecation")
			final HiResDate time = new HiResDate(new Date(112, 6, 6, 12, 5, i * 25));
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

	@Test
	public void testToGNDDoc() throws IOException {
		// ok, create a bit of a track
		final Track track = getTestTrack("some-name");

		assertNotNull(track, "track not found");

		final GNDDocument doc = new GNDDocument("NAME", track, "PLATFORM", "P_TYPE", "SENS", "S_TYPE", "TRIAL");

		// and check the results
		assertNotNull(doc, "No output received");

		// and check the contents
		final ObjectNode root = new GNDDocHandler().toJson("NAME", track, "PLATFORM", "P_TYPE", "SENS", "S_TYPE",
				"TRIAL");

		assertNotNull(root, "found root");

		final ObjectNode metadata = (ObjectNode) root.get("metadata");

		assertNotNull(metadata, "metadata not found");
		assertEquals("NAME", metadata.get("name").asText(), "name wrong");
		assertEquals("PLATFORM", metadata.get("platform").asText(), "platform wrong");
		assertEquals("P_TYPE", metadata.get("platform_type").asText(), "platform type wrong");
		assertEquals("SENS", metadata.get("sensor").asText(), "sensor wrong");
		assertEquals("S_TYPE", metadata.get("sensor_type").asText(), "sensor type wrong");
		assertEquals("TRIAL", metadata.get("trial").asText(), "trial wrong");
		// assertEquals("data types wrong",
		// "[\"lat\",\"long\",\"time\",\"elevation\"", metadata.get("data_type"));
		assertNotNull(metadata.get("geo_bounds"), "geo bounds wrong");
		assertNotNull(metadata.get("time_bounds"), "time bounds wrong");
	}

	/** a fixed track (getTestTrack is random and uses local time) */
	private static Track goldenTrack() {
		final Vector<Fix> fixes = new Vector<Fix>();
		for (int i = 0; i < 3; i++) {
			fixes.add(new Fix(new HiResDate(1341576300000L + i * 25000L),
					new WorldLocation(4d + i / 100d, 5d + i / 100d, 2.5 * i), 135 + i, 5 + i * 0.5));
		}
		final Track track = new Track(fixes);
		track.setName("golden");
		return track;
	}

	/** the expected strings were written by the Jackson 1.9.4 version of this class */
	@Test
	public void testTrackJsonGolden() throws IOException {
		final String expected = "{\"location\":{\"type\":\"MultiPoint\",\"coordinates\":[[5.0,4.0],[5.01,4.01],[5.02,4.02]]"

			+ "},\"time\":[\"2012-07-06T12:05:00Z\",\"2012-07-06T12:05:25Z\",\"2012-07-06T12:05:50Z\"],\""

			+ "elevation\":[-0.0,-2.5,-5.0],\"heading\":[135.0,136.0,137.0],\"speed\":[4.572000000000001,"

			+ "5.0292,5.4864000000000015],\"metadata\":{\"name\":\"NAME\",\"platform\":\"PLATFORM\",\"pla"

			+ "tform_type\":\"P_TYPE\",\"sensor\":\"SENS\",\"sensor_type\":\"S_TYPE\",\"trial\":\"TRIAL\""

			+ ",\"type\":\"track\",\"data_type\":[\"location\",\"time\",\"elevation\",\"heading\",\"speed"

			+ "\"],\"geo_bounds\":{\"coordinates\":[[4.02,5.0],[4.0,5.02]],\"type\":\"envelope\"},\"time_"

			+ "bounds\":{\"start\":\"2012-07-06T12:05:00Z\",\"end\":\"2012-07-06T12:05:50Z\"}}}";
		assertEquals(expected, asString(new GNDDocHandler().toJson("NAME", goldenTrack(), "PLATFORM", "P_TYPE",
				"SENS", "S_TYPE", "TRIAL")));
	}

	@Test
	public void testNarrativeJsonGolden() throws IOException {
		final List<NarrativeEntry> entries = new ArrayList<NarrativeEntry>();
		entries.add(new NarrativeEntry("trk", "obs-type", new HiResDate(1341576300000L), "first entry"));
		entries.add(new NarrativeEntry("trk", "obs-type", new HiResDate(1341576325000L),
				"second \"quoted\" entry"));
		final String expected = "{\"metadata\":{\"name\":\"NARR\",\"platform\":\"PLATFORM\",\"platform_type\":\"P_TYPE\",\""

			+ "sensor\":\"SENS\",\"sensor_type\":\"S_TYPE\",\"trial\":\"TRIAL\",\"type\":\"narrative\",\""

			+ "data_type\":[\"obs\",\"obs_type\"],\"time_bounds\":{\"start\":\"2012-07-06T12:05:00Z\",\"e"

			+ "nd\":\"2012-07-06T12:05:25Z\"}},\"obs\":[\"first entry\",\"second \\\"quoted\\\" entry\"],"

			+ "\"obs_type\":[\"obs-type\",\"obs-type\"],\"time\":[\"2012-07-06T12:05:00Z\",\"2012-07-06T1"

			+ "2:05:25Z\"]}";
		assertEquals(expected, asString(new GNDDocHandler().toJson("NARR", entries, "PLATFORM", "P_TYPE", "SENS",
				"S_TYPE", "TRIAL")));
	}

	/** the reader takes the older lat/lon layout, not the GeoJSON one toJson writes */
	private static final String LAT_LON_DOC = "{'metadata':{'name':'reader','platform':'plat',"
			+ "'data_type':['lat','lon','time','elevation','course','speed']},"
			+ "'lat':[4.0,4.5],'lon':[5.0,5.5],'time':['2012-07-06T12:05:00Z','2012-07-06T12:05:25Z'],"
			+ "'elevation':[-10.0,-20.0],'course':[1.5,2.5],'speed':[3.0,4.0]}";

	@Test
	public void testReadDocument() throws Exception {
		final GNDDocument doc = new GNDDocument(LAT_LON_DOC.replace('\'', '"'));
		assertEquals("reader", doc.getName());
		final Track track = doc.getTrack();
		final Enumeration<Fix> fixes = track.getFixes();
		final Fix first = fixes.nextElement();
		assertEquals(4.0, first.getLocation().getLat(), 1e-9);
		assertEquals(5.0, first.getLocation().getLong(), 1e-9);
		assertEquals(10.0, first.getLocation().getDepth(), 1e-9, "depth is minus elevation");
		assertEquals(1.5, first.getCourse(), 1e-9);
		assertEquals(1341576300000L, first.getTime().getDate().getTime());
		final Fix second = fixes.nextElement();
		assertEquals(5.5, second.getLocation().getLong(), 1e-9);
		assertEquals(4.0, second.getSpeed(), 1e-9);
		assertFalse(fixes.hasMoreElements());
	}

	@Test
	public void testReadDataset() throws Exception {
		final JsonNode node = new ObjectMapper().readTree(LAT_LON_DOC.replace('\'', '"'));
		final MWC.TacticalData.GND.GDataset data = new MWC.TacticalData.GND.GDataset(node, null);
		assertEquals("reader", data.getName());
		assertEquals("plat", data.getPlatform());
		assertEquals(6, data.getDataTypes().size());
		assertEquals(2, data.size());
		assertEquals(4.5, data.getDataset("lat")[1], 1e-9);
		assertEquals(1341576325000L, data.getTimes()[1].getTime());
	}
}
