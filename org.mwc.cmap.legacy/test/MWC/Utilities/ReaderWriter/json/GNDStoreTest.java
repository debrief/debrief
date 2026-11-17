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

import static org.junit.jupiter.api.Assertions.*;

import static MWC.Utilities.ReaderWriter.json.GNDStore.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import MWC.Utilities.ReaderWriter.json.GNDStore.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.javalite.http.Get;
import org.javalite.http.Http;
import org.javalite.http.Post;
import org.junit.jupiter.api.Test;
import MWC.GUI.ErrorLogger;
import MWC.TacticalData.Fix;
import MWC.TacticalData.Track;
import MWC.Utilities.ReaderWriter.json.GNDDocHandler.GNDDocument;

public class GNDStoreTest {
	// TODO FIX-TEST
	public void NtestBulkPost1() throws JsonGenerationException, JsonMappingException, IOException {
		final ArrayList<ObjectNode> tracks = new ArrayList<ObjectNode>();

		for (int i = 0; i < 10; i++) {
			// get a track
			final Track trk = GNDDocHandlerTest.getTestTrack("test_-" + i);

			final ObjectNode js = new GNDDocHandler().toJson(trk.getName(), trk, "tst_platform2", "test_plat_type",
					"test_sensor", "test_sensor_type", "test_put_1");
			tracks.add(js);
		}
		final String url = "http://gnd.iriscouch.com";
		final String db = "tracks";
		final GNDStore store = new GNDStore(url, db);

		System.out.println("before put:" + new Date());

		for (final Iterator<ObjectNode> iterator = tracks.iterator(); iterator.hasNext();) {
			final ObjectNode objectNode = iterator.next();
			store.put(objectNode);
		}

		System.out.println("after put:" + new Date());
	}

	// TODO FIX-TEST
	public void NtestBulkPost2() throws JsonGenerationException, JsonMappingException, IOException {
		final ArrayList<JsonNode> nodes = new ArrayList<JsonNode>();
		for (int i = 0; i < 10; i++) {
			// get a track
			final Track trk = GNDDocHandlerTest.getTestTrack("test2-" + i);
			final ObjectNode js = new GNDDocHandler().toJson(trk.getName(), trk, "tst_platform2", "test_plat_type",
					"test_sensor", "test_sensor_type", "test_bulk_put");
			nodes.add(js);
		}
		final String url = "http://gnd.iriscouch.com";
		final String db = "tracks";
		final GNDStore store = new GNDStore(url, db);

		System.out.println("before bulk put:" + new Date());

		store.bulkPut(nodes, 10);

		System.out.println("after bulk put:" + new Date());

	}

	// TODO FIX-TEST
	public void NtestGet() throws JsonParseException, JsonMappingException, IOException, ParseException {
		final String url = "http://gnd.iriscouch.com";
		final String db = "tracks";

		final GNDStore store = new GNDStore(url, db);

		final String anId = store.getAnId();
		assertNotNull(anId, "found an id");
		final GNDDocument doc = store.get(anId);
		assertNotNull(doc);
		final String theName = doc.getName();
		assertNotNull(theName, "name found");

		// and the track?
		final Track trk = doc.getTrack();
		assertNotNull(trk, "track found");
		final Enumeration<Fix> fixes = trk.getFixes();
		assertNotNull(fixes.hasMoreElements(), "has some fixes");
	}

	@SuppressWarnings("unused")
	@Test
	public void testPost() throws JsonGenerationException, JsonMappingException, IOException {
		// get a track
		final Track trk = GNDDocHandlerTest.getTestTrack("tester");
		final String url = "http://gnd.iriscouch.com";
		final String db = "tracks";

		final GNDStore store = new GNDStore(url, db);
		final JsonNode theDoc = new GNDDocHandler().toJson(trk.getName(), trk, "tst_platform2", "test_plat_type",
				"test_sensor", "test_sensor_type", "test_trial");
		// store.put(theDoc);
	}

}
