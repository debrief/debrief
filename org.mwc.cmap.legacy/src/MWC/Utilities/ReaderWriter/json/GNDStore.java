/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.Utilities.ReaderWriter.json;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.javalite.http.Get;
import org.javalite.http.Http;
import org.javalite.http.Post;

import MWC.GUI.ErrorLogger;
import MWC.TacticalData.Fix;
import MWC.TacticalData.Track;
import MWC.Utilities.ReaderWriter.json.GNDDocHandler.GNDDocument;

public class GNDStore
{
	private final String _url;
	private final String _dbName;

	public GNDStore(final String url, final String databaseName)
	{
		_url = url;
		_dbName = databaseName;
	}

	public String getAnId() throws JsonParseException, JsonMappingException,
			IOException
	{
		String res = null;
		final String ONE_TRACK = "_design/" + _dbName +  "/_view/track_listing?limit=1";
		final String theURL = _url + "/" + _dbName + "/" + ONE_TRACK;
		final String theList = Http.get(theURL).text();
		final ObjectMapper mapper = new ObjectMapper();
		final Map<?, ?> root = mapper.readValue(theList, Map.class);

		@SuppressWarnings("unchecked")
		final
		ArrayList<LinkedHashMap<String, String>> rows = (ArrayList<LinkedHashMap<String, String>>) root
				.get("rows");
		if (rows.size() > 0)
		{
			final LinkedHashMap<String, String> first = rows.get(0);
			res = first.get("id");
		}

		return res;
	}

	public void put(final JsonNode doc) throws JsonGenerationException,
			JsonMappingException, IOException
	{
		final String THE_URL = _url + "/" + _dbName;
		final String theDoc = GNDDocHandler.asString(doc);

		final Post post = Http.post(THE_URL, theDoc).header("Content-type",
				"application/json");
		System.out.println("post res:" + post.responseCode() + " "
				+ post.responseMessage());

	}

	public GNDDocHandler.GNDDocument get(final String name) throws JsonParseException,
			JsonMappingException, IOException, ParseException
	{
		final Get get = Http.get(_url + "/" + _dbName + "/" + name);

		final String content = get.text();
		final GNDDocument doc = new GNDDocument(content);

		// do the get
		return doc;
	}

	public static class TestDatabase extends TestCase
	{
		// TODO FIX-TEST
		public void NtestGet() throws JsonParseException, JsonMappingException,
				IOException, ParseException
		{
			final String url = "http://gnd.iriscouch.com";
			final String db = "tracks";

			final GNDStore store = new GNDStore(url, db);

			final String anId = store.getAnId();
			assertNotNull("found an id", anId);
			final GNDDocument doc = store.get(anId);
			assertNotNull(doc);
			final String theName = doc.getName();
			assertNotNull("name found", theName);

			// and the track?
			final Track trk = doc.getTrack();
			assertNotNull("track found", trk);
			final Enumeration<Fix> fixes = trk.getFixes();
			assertNotNull("has some fixes", fixes.hasMoreElements());
		}

		@SuppressWarnings("unused")
		public void testPost() throws JsonGenerationException,
				JsonMappingException, IOException
		{
			// get a track
			final Track trk = GNDDocHandler.TestJSON.getTestTrack("tester");
			final String url = "http://gnd.iriscouch.com";
			final String db = "tracks";

			final GNDStore store = new GNDStore(url, db);
			final JsonNode theDoc = new GNDDocHandler().toJson(trk.getName(), trk,
					"tst_platform2", "test_plat_type", "test_sensor", "test_sensor_type",
					"test_trial");
			// store.put(theDoc);
		}

		// TODO FIX-TEST
		public void NtestBulkPost1() throws JsonGenerationException,
				JsonMappingException, IOException
		{
			final ArrayList<ObjectNode> tracks = new ArrayList<ObjectNode>();

			for (int i = 0; i < 10; i++)
			{
				// get a track
				final Track trk = GNDDocHandler.TestJSON.getTestTrack("test_-" + i);

				final ObjectNode js = new GNDDocHandler().toJson(trk.getName(), trk,
						"tst_platform2", "test_plat_type", "test_sensor",
						"test_sensor_type", "test_put_1");
				tracks.add(js);
			}
			final String url = "http://gnd.iriscouch.com";
			final String db = "tracks";
			final GNDStore store = new GNDStore(url, db);

			System.out.println("before put:" + new Date());

			for (final Iterator<ObjectNode> iterator = tracks.iterator(); iterator
					.hasNext();)
			{
				final ObjectNode objectNode = (ObjectNode) iterator.next();
				store.put(objectNode);
			}

			System.out.println("after put:" + new Date());
		}

		// TODO FIX-TEST
		public void NtestBulkPost2() throws JsonGenerationException,
				JsonMappingException, IOException
		{
			final ArrayList<JsonNode> nodes = new ArrayList<JsonNode>();
			for (int i = 0; i < 10; i++)
			{
				// get a track
				final Track trk = GNDDocHandler.TestJSON.getTestTrack("test2-" + i);
				final ObjectNode js = new GNDDocHandler().toJson(trk.getName(), trk,
						"tst_platform2", "test_plat_type", "test_sensor",
						"test_sensor_type", "test_bulk_put");
				nodes.add(js);
			}
			final String url = "http://gnd.iriscouch.com";
			final String db = "tracks";
			final GNDStore store = new GNDStore(url, db);

			System.out.println("before bulk put:" + new Date());

			store.bulkPut(nodes, 10);

			System.out.println("after bulk put:" + new Date());

		}

	}

	public void bulkPut(final ArrayList<JsonNode> theTracks, final int batchSize)
			throws IOException
	{
		// hmm, how many tracks are there?
		final int len = theTracks.size();

		for (int i = 0; i < len; i += batchSize)
		{

			// ok, collate the bulk submit object
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode root = mapper.createObjectNode();
			final ArrayNode trackArray = mapper.createArrayNode();

			for (int j = i; j < i + batchSize && j < len; j++)
			{
				trackArray.add(theTracks.get(j));
			}

			// trackArray.addAll(theTracks);
			root.put("docs", trackArray);

			// ok, now formulate the URL
			final String THE_URL = _url + "/" + _dbName + "/_bulk_docs";
			final String theDoc = GNDDocHandler.asString(root);

			System.out.println("ABOUT TO PUT:" + theDoc.length() + " chars");

			final Post post = Http.post(THE_URL, theDoc).header("Content-type",
					"application/json");

			if (post.responseCode() == 201)
			{
				System.out.println("PUT complete");
				final JsonParser jp = mapper.getJsonFactory().createJsonParser(post.text());
				final ArrayNode docs = (ArrayNode) mapper.readTree(jp);
				for (int k = 0; k < docs.size(); k++)
				{
					final JsonNode thisD = docs.get(k);
					final TextNode reason = (TextNode) thisD.get("reason");
					if (reason != null)
						MWC.GUI.LoggingService.INSTANCE().logError(ErrorLogger.ERROR,
								"Upload failed:" + reason.asText(), null);
					else
					{
						
						final String logMsg = "Post successful, added document:" +  thisD.get("id");
						MWC.GUI.LoggingService.INSTANCE().logError(ErrorLogger.INFO,
								logMsg, null);
					}
				}
			}
			else
			{
				MWC.GUI.LoggingService.INSTANCE().logError(ErrorLogger.ERROR,
						post.responseMessage(), null);
			}

		}

	}

}
