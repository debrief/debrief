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

	public GNDStore(String url, String databaseName)
	{
		_url = url;
		_dbName = databaseName;
	}

	public String getAnId() throws JsonParseException, JsonMappingException,
			IOException
	{
		String res = null;
		String ONE_TRACK = "_design/" + _dbName +  "/_view/track_listing?limit=1";
		String theURL = _url + "/" + _dbName + "/" + ONE_TRACK;
		String theList = Http.get(theURL).text();
		ObjectMapper mapper = new ObjectMapper();
		Map<?, ?> root = mapper.readValue(theList, Map.class);

		@SuppressWarnings("unchecked")
		ArrayList<LinkedHashMap<String, String>> rows = (ArrayList<LinkedHashMap<String, String>>) root
				.get("rows");
		if (rows.size() > 0)
		{
			LinkedHashMap<String, String> first = rows.get(0);
			res = first.get("id");
		}

		return res;
	}

	public void put(JsonNode doc) throws JsonGenerationException,
			JsonMappingException, IOException
	{
		String THE_URL = _url + "/" + _dbName;
		String theDoc = GNDDocHandler.asString(doc);

		Post post = Http.post(THE_URL, theDoc).header("Content-type",
				"application/json");
		System.out.println("post res:" + post.responseCode() + " "
				+ post.responseMessage());

	}

	public GNDDocHandler.GNDDocument get(String name) throws JsonParseException,
			JsonMappingException, IOException, ParseException
	{
		Get get = Http.get(_url + "/" + _dbName + "/" + name);

		String content = get.text();
		GNDDocument doc = new GNDDocument(content);

		// do the get
		return doc;
	}

	public static class TestDatabase extends TestCase
	{
		public void testGet() throws JsonParseException, JsonMappingException,
				IOException, ParseException
		{
			String url = "http://gnd.iriscouch.com";
			String db = "tracks";

			GNDStore store = new GNDStore(url, db);

			String anId = store.getAnId();
			assertNotNull("found an id", anId);
			GNDDocument doc = store.get(anId);
			assertNotNull(doc);
			String theName = doc.getName();
			assertNotNull("name found", theName);

			// and the track?
			Track trk = doc.getTrack();
			assertNotNull("track found", trk);
			Enumeration<Fix> fixes = trk.getFixes();
			assertNotNull("has some fixes", fixes.hasMoreElements());
		}

		@SuppressWarnings("unused")
		public void testPost() throws JsonGenerationException,
				JsonMappingException, IOException
		{
			// get a track
			Track trk = GNDDocHandler.TestJSON.getTestTrack("tester");
			String url = "http://gnd.iriscouch.com";
			String db = "tracks";

			GNDStore store = new GNDStore(url, db);
			JsonNode theDoc = new GNDDocHandler().toJson(trk.getName(), trk,
					"tst_platform2", "test_plat_type", "test_sensor", "test_sensor_type",
					"test_trial");
			// store.put(theDoc);
		}

		public void testBulkPost1() throws JsonGenerationException,
				JsonMappingException, IOException
		{
			ArrayList<ObjectNode> tracks = new ArrayList<ObjectNode>();

			for (int i = 0; i < 10; i++)
			{
				// get a track
				Track trk = GNDDocHandler.TestJSON.getTestTrack("test_-" + i);

				ObjectNode js = new GNDDocHandler().toJson(trk.getName(), trk,
						"tst_platform2", "test_plat_type", "test_sensor",
						"test_sensor_type", "test_put_1");
				tracks.add(js);
			}
			String url = "http://gnd.iriscouch.com";
			String db = "tracks";
			GNDStore store = new GNDStore(url, db);

			System.out.println("before put:" + new Date());

			for (Iterator<ObjectNode> iterator = tracks.iterator(); iterator
					.hasNext();)
			{
				ObjectNode objectNode = (ObjectNode) iterator.next();
				store.put(objectNode);
			}

			System.out.println("after put:" + new Date());
		}

		public void testBulkPost2() throws JsonGenerationException,
				JsonMappingException, IOException
		{
			ArrayList<JsonNode> nodes = new ArrayList<JsonNode>();
			for (int i = 0; i < 10; i++)
			{
				// get a track
				Track trk = GNDDocHandler.TestJSON.getTestTrack("test2-" + i);
				ObjectNode js = new GNDDocHandler().toJson(trk.getName(), trk,
						"tst_platform2", "test_plat_type", "test_sensor",
						"test_sensor_type", "test_bulk_put");
				nodes.add(js);
			}
			String url = "http://gnd.iriscouch.com";
			String db = "tracks";
			GNDStore store = new GNDStore(url, db);

			System.out.println("before bulk put:" + new Date());

			store.bulkPut(nodes, 10);

			System.out.println("after bulk put:" + new Date());

		}

	}

	public void bulkPut(ArrayList<JsonNode> theTracks, int batchSize)
			throws IOException
	{
		// hmm, how many tracks are there?
		int len = theTracks.size();

		for (int i = 0; i < len; i += batchSize)
		{

			// ok, collate the bulk submit object
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode root = mapper.createObjectNode();
			ArrayNode trackArray = mapper.createArrayNode();

			for (int j = i; j < i + batchSize && j < len; j++)
			{
				trackArray.add(theTracks.get(j));
			}

			// trackArray.addAll(theTracks);
			root.put("docs", trackArray);

			// ok, now formulate the URL
			String THE_URL = _url + "/" + _dbName + "/_bulk_docs";
			String theDoc = GNDDocHandler.asString(root);

			System.out.println("ABOUT TO PUT:" + theDoc.length() + " chars");

			Post post = Http.post(THE_URL, theDoc).header("Content-type",
					"application/json");

			if (post.responseCode() == 201)
			{
				System.out.println("PUT complete");
				JsonParser jp = mapper.getJsonFactory().createJsonParser(post.text());
				ArrayNode docs = (ArrayNode) mapper.readTree(jp);
				for (int k = 0; k < docs.size(); k++)
				{
					JsonNode thisD = docs.get(k);
					TextNode reason = (TextNode) thisD.get("reason");
					if (reason != null)
						MWC.GUI.LoggingService.INSTANCE().logError(ErrorLogger.ERROR,
								"Upload failed:" + reason.asText(), null);
					else
					{
						
						String logMsg = "Post successful, added document:" +  thisD.get("id");
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
