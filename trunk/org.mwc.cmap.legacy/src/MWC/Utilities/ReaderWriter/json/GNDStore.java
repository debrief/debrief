package MWC.Utilities.ReaderWriter.json;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.javalite.http.Get;
import org.javalite.http.Http;
import org.javalite.http.Post;

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
		String ONE_TRACK = "_design/tracks/_view/track_listing?limit=1";
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
			store.put(theDoc);
		}

		@SuppressWarnings("unused")
		public void testBulkPost() throws JsonGenerationException,
				JsonMappingException, IOException
		{
			for (int i = 0; i < 10; i++)
			{
				// get a track
				Track trk = GNDDocHandler.TestJSON.getTestTrack("test-" + i);
				String url = "http://gnd.iriscouch.com";
				String db = "speed_test";

				GNDStore store = new GNDStore(url, db);
				GNDDocument theDoc = new GNDDocument(trk.getName(), trk,
						"tst_platform2", "test_plat_type", "test_sensor",
						"test_sensor_type", "test_trial");
				// store.put(theDoc);
			}
		}

	}

}
