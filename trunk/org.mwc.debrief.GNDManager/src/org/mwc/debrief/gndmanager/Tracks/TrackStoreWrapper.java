package org.mwc.debrief.gndmanager.Tracks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.spec.SearchDate;
import org.bitbucket.es4gwt.shared.spec.SearchRequest;
import org.bitbucket.es4gwt.shared.spec.SearchRequestConverter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.javalite.http.Get;
import org.javalite.http.Post;
import org.junit.Before;
import org.junit.Test;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class TrackStoreWrapper extends BaseLayer implements WatchableList,
		Serializable
{

	private final ObjectMapper _mapper;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String COUCHDB_LOCATION = "CouchDbURL";
	public static final String ES_LOCATION = "EsURL";
	private String _couchURL;
	private String _esURL;

	/**
	 * the time period we've been filtered to
	 * 
	 */
	private TimePeriod.BaseTimePeriod _currentFilterPeriod;

	private TimePeriod.BaseTimePeriod _dataLoaded = null;

	private final SimpleDateFormat iso;

	public TrackStoreWrapper(String couchURL, String esURL)
	{

		_mapper = new ObjectMapper();
		_couchURL = couchURL;
		_esURL = esURL;

		iso = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		iso.setTimeZone(TimeZone.getTimeZone("GMT"));

		if (_couchURL == "")
			throw new RuntimeException("database URL missing");
		if (_esURL == "")
			throw new RuntimeException("search URL missing");

	}

	private class CouchTrack implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JsonNode _doc;
		private TimePeriod _myCoverage;
		private ArrayList<Long> _timeVals;
		private ArrayNode _latArray;
		private ArrayNode _longArray;

		public CouchTrack(JsonNode theDoc)
		{
			_doc = theDoc;
		}

		public String getId()
		{
			return _doc.get("_id").asText();
		}

		public int length()
		{
			return getTimes().size();
		}

		private ArrayNode getArray(String arrName)
		{
			ArrayNode res = null;
			// ok, get the times
			JsonNode timeDat = _doc.get(arrName);
			if (timeDat != null)

			{
				if (timeDat.isArray())
				{
					res = (ArrayNode) timeDat;
				}
			}

			return res;
		}

		private ArrayNode getLats()
		{
			if (_latArray == null)
			{
				_latArray = getArray("lat");
			}

			return _latArray;
		}

		private ArrayNode getLongs()
		{
			if (_longArray == null)
			{
				_longArray = getArray("lon");
			}

			return _longArray;
		}

		private ArrayList<Long> getTimes()
		{
			if (_timeVals == null)
			{
				_timeVals = new ArrayList<Long>();

				ArrayNode timeArray = getArray("time");
				try
				{
					for (int i = 0; i < timeArray.size(); i++)
					{
						String thisT = timeArray.get(i).asText();
						Date thisD;
						thisD = iso.parse(thisT);
						_timeVals.add(thisD.getTime());
					}
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
			}

			return _timeVals;
		}

		public boolean overlaps(BaseTimePeriod targetPeriod)
		{
			// TODO: ACTUALLY CHECK THE METADATA PERIOD, IF WE HAVE IT!

			boolean overlaps = false;

			if (_myCoverage == null)
			{
				ArrayList<Long> times = getTimes();
				Date fDate = new Date(times.get(0));
				Date lDate = new Date(times.get(times.size() - 1));
				_myCoverage = new TimePeriod.BaseTimePeriod(new HiResDate(fDate),
						new HiResDate(lDate));
			}

			if (_myCoverage != null)
			{
				overlaps = _myCoverage.overlaps(targetPeriod);
			}

			// return overlaps;
			return true;
		}

		public WorldLocation getLocationAt(int i)
		{
			WorldLocation res = null;

			if (_latArray == null)
			{
				_latArray = getLats();
			}
			if (_longArray == null)
			{
				_longArray = getLongs();
			}

			if ((_latArray != null) && (_longArray != null))
			{
				res = new WorldLocation(_latArray.get(i).asDouble(), _longArray.get(i)
						.asDouble(), 0);
			}
			return res;
		}
	}

	private transient HashMap<String, CouchTrack> _myCache = new HashMap<String, CouchTrack>();

	/** our color
	 * 
	 */
	private Color _myColor = Color.red;

	@Override
	public void filterListTo(final HiResDate start, final HiResDate end)
	{

		// remember what hte current filter is
		_currentFilterPeriod = new TimePeriod.BaseTimePeriod(start, end);

		// here's where we re-query
		System.err.println("Filtering my documents to period:" + start.toString()
				+ " to " + end);

		// are we already storing more than this?
		if (_dataLoaded != null)
		{
			if (_dataLoaded.contains(start) && _dataLoaded.contains(end))
			{
				// no - we have all the data we need. get plotting.
				return;
			}
		}

		// make this processing async	
		Display.getDefault().asyncExec(new Runnable(){

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				// ok, collate the time fields
				String myFilter = collateDateFilter(start, end);

				// hmm, which tracks match our current time period?
				ArrayList<String> docsToDownload = getDownloadList(myFilter);

				// now get any new items
				if (docsToDownload.size() > 0)
				{
					// ok, we need some - go get them
					doTheDownload(docsToDownload);
				}

				// extend our period of coverage
				if (_dataLoaded == null)
					_dataLoaded = new TimePeriod.BaseTimePeriod(
							_currentFilterPeriod.getStartDTG(), _currentFilterPeriod.getEndDTG());
				else
				{
					_dataLoaded.extend(_currentFilterPeriod.getStartDTG());
					_dataLoaded.extend(_currentFilterPeriod.getEndDTG());
				}
				
				// and trigger size-updated
				firePropertyChange(SupportsPropertyListeners.FORMAT, null, this);
			}});
	}

	private void doTheDownload(ArrayList<String> docsToDownload)
	{
		String uri;
		// collate the ids
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		ArrayNode keys = mapper.createArrayNode();
		Iterator<String> iter = docsToDownload.iterator();

		// TESTING - just do a single id
		// keys.add("0d9500c05ef3a901397b5d7dda14d956");

		while (iter.hasNext())
		{
			String id = (String) iter.next();
			keys.add(id);
		}
		root.put("keys", keys);

		try
		{
			String cStr = root.toString();
			byte[] content = cStr.getBytes();
			uri = _couchURL + "/tracks/_all_docs?include_docs=true";
			System.err.println("Sending:  " + content);
			System.err.println("  to:  " + uri);
			Post doIt = new Post(uri, content, 1000, 10000);
			doIt.header("Content-Type", "application/json");
			int result = doIt.responseCode();
			if (result == 200)
			{
				byte[] resB = doIt.bytes();
				JsonNode list = mapper.readValue(resB, JsonNode.class);

				// ok, what happens next?
				JsonNode tmpRows = list.get("rows");
				if (tmpRows.isArray())
				{
					ArrayNode rows = (ArrayNode) tmpRows;
					for (int i = 0; i < rows.size(); i++)
					{
						JsonNode theNode = rows.get(i);
						JsonNode theDoc = theNode.get("doc");
						CouchTrack track = new CouchTrack(theDoc);
						_myCache.put(track.getId(), track);
					}
				}
			}
			else
			{
				CorePlugin.logError(Status.ERROR, "Get from CouchFailed. response is:"
						+ result, null);
			}
		}
		catch (JsonParseException e)
		{
			e.printStackTrace();
		}
		catch (JsonMappingException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public int size()
	{
		return _myCache.size();
	}

	private ArrayList<String> getDownloadList(String myFilter)
	{
		String uri = _esURL + "/data/dataset/_search?source=" + myFilter;
		ArrayList<String> docsToDownload = new ArrayList<String>();
		try
		{
			System.out.println("doing GET on :" + uri);
			Get doIt = new Get(uri, 1000, 10000);
			doIt.header("Content-Type", "application/json");
			int result = doIt.responseCode();
			if (result == 200)
			{
				// ok, we have data

				// get the doc ids
				byte[] resB = doIt.bytes();
				JsonNode list = _mapper.readValue(resB, JsonNode.class);

				// ok, what happens next?
				JsonNode items = list.get("hits");
				JsonNode rawRows = items.get("hits");
				if (rawRows.isArray())
				{
					ArrayNode rows = (ArrayNode) rawRows;
					for (int i = 0; i < rows.size(); i++)
					{
						JsonNode theNode = rows.get(i);
						String theDoc = theNode.get("_id").asText();

						// do we already have this document
						if (_myCache.containsKey(theDoc))
						{
							// don't worry, we've already got it
						}
						else
						{
							// we do want to load this file
							docsToDownload.add(theDoc.toString());
						}
					}
				}

			}
		}
		catch (MalformedURLException e)
		{
			CorePlugin.logError(Status.ERROR,
					"failed to create URI for ElasticSearch query", e);
		}
		catch (JsonParseException e)
		{
			CorePlugin.logError(Status.ERROR, "failed to parse returned ES rows", e);
		}
		catch (JsonMappingException e)
		{
			CorePlugin.logError(Status.ERROR, "failed to parse returned ES rows", e);
		}
		catch (IOException e)
		{
			CorePlugin.logError(Status.ERROR, "failed to parse returned ES rows", e);
		}
		return docsToDownload;
	}

	private String collateDateFilter(HiResDate start, HiResDate end)
	{

		SearchRequest request = new SearchRequest();
		request.after(new SearchDate(start.getDate(), iso.format(start.getDate())));
		request.before(new SearchDate(end.getDate(), iso.format(end.getDate())));

		String res = SearchRequestConverter.toRequestString(request,
				new ElasticFacet[]
				{}, new ElasticFacet[]
				{});

		return res;
	}

	@Override
	public void paint(CanvasType dest)
	{
		// check we have a filter
		if (_currentFilterPeriod == null)
		{
			CorePlugin.logError(Status.WARNING,
					"Track Store hasn't had filter applied yet", null);

		}

		// ok, loop through my tracks
		Iterator<CouchTrack> iter = _myCache.values().iterator();
		while (iter.hasNext())
		{
			TrackStoreWrapper.CouchTrack thisT = (TrackStoreWrapper.CouchTrack) iter
					.next();
			// ok, check it's visible at all
			if (thisT.overlaps(_currentFilterPeriod))
			{
				// yes. get painting
				paintThisTrack(dest, thisT, _currentFilterPeriod);
			}

		}
	}

	private static void paintThisTrack(CanvasType dest, CouchTrack thisT,
			BaseTimePeriod _currentFilterPeriod2)
	{

		dest.setColor(Color.red);

		long startTime = _currentFilterPeriod2.getStartDTG().getDate().getTime();
		long endTime = _currentFilterPeriod2.getEndDTG().getDate().getTime();

		int len = thisT.length();
		ArrayList<Long> times = thisT.getTimes();

		// loop through the track times
		int ctr = 0;
		while ((ctr < len) && (times.get(ctr) <= startTime))
		{
			ctr++;
		}

		// we're at the first entry that's inside. do we have a previous one?
		if (ctr > 0)
			ctr--;

		// cool, we're at the first point on or before the start date
		WorldLocation thisL = thisT.getLocationAt(ctr);

		if (thisL == null)
		{
			// hey, it's not a spatial track
			return;
		}

		// and convert a screen point
		Point lastLoc = dest.toScreen(thisL);

		// now we can continue forward
		while ((ctr < len - 1) && (times.get(++ctr) < endTime))
		{

			// put in a marker
			dest.drawRect(lastLoc.x - 1, lastLoc.y - 1, 3, 3);

			// get the
			thisL = thisT.getLocationAt(ctr);

			// do we have a location?
			if (thisL != null)
			{

				// convert to screen coords
				Point thisLoc = dest.toScreen(thisL);

				// draw teh line
				dest.drawLine(lastLoc.x, lastLoc.y, thisLoc.x, thisLoc.y);

				// remember this location
				lastLoc = new Point(thisLoc);
			}
		}
	}

	@Override
	public boolean hasEditor()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WorldArea getBounds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exportShape()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void append(Layer other)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setName(String val)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasOrderedChildren()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLineThickness()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void add(Editable point)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeElement(Editable point)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "Cloud Track Store";
	}

	@Override
	public HiResDate getStartDTG()
	{
		HiResDate res = null;

		if (_dataLoaded != null)
			res = _dataLoaded.getStartDTG();

		return res;
	}

	@Override
	public HiResDate getEndDTG()
	{
		HiResDate res = null;

		if (_dataLoaded != null)
			res = _dataLoaded.getEndDTG();

		return res;
	}

	@Override
	public Watchable[] getNearestTo(HiResDate DTG)
	{
		return new Watchable[]
		{};
	}

	@Override
	public Collection<Editable> getItemsBetween(HiResDate start, HiResDate end)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setColor(Color col)
	{
		_myColor = col;
		
	}
	
	@Override
	public Color getColor()
	{
		return _myColor ;
	}

	
	
	@Override
	public double rangeFrom(WorldLocation other)
	{
		// TODO: find the nearest (visible) track point
		return super.rangeFrom(other);
	}

	@Override
	public PlainSymbol getSnailShape()
	{
		return null;
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{

		TrackStoreWrapper tsw = new TrackStoreWrapper(
				"http://gnd.iriscouch.com:5984",
				"http://0d9fd05438a44abe882139135eb01048.found.no:9200");
		HiResDate startDate = new HiResDate(new Date(112, 6, 3, 12, 15, 14));
		HiResDate endDate = new HiResDate(new Date(112, 6, 8, 12, 18, 14));

		String tt = tsw.collateDateFilter(startDate, endDate);
		System.out.println(tt);

		tsw.filterListTo(startDate, endDate);

	}

	public static class TestTrackStore
	{
		private TrackStoreWrapper tts;
		private HiResDate startDate;
		private HiResDate endDate;

		@SuppressWarnings("deprecation")
		@Before
		public void setup()
		{
			tts = new TrackStoreWrapper("http://gnd.iriscouch.com:5984",
					"http://0d9fd05438a44abe882139135eb01048.found.no:9200");

			startDate = new HiResDate(new Date(112, 6, 3, 12, 15, 14));
			endDate = new HiResDate(new Date(112, 6, 7, 12, 15, 14));

		}

		@Test
		public void testFormat()
		{
			SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

			@SuppressWarnings("deprecation")
			String dateTest = iso.format(new Date(112, 6, 3, 12, 15, 14));
			System.out.println(dateTest);
		}

		@Test
		public void testFilter()
		{
			String st = tts.collateDateFilter(startDate, endDate);
			assertEquals(
					"correct string",
					"{\"size\":400,\"fields\":[],\"query\":{\"filtered\":{\"query\":{\"match_all\":{}},\"filter\":{\"and\":[{\"range\":{\"end\":{\"gte\":\"2012-07-03T11:15:14\"}}},{\"range\":{\"start\":{\"lte\":\"2012-07-07T11:15:14\"}}}]}}},\"facets\":{}}",
					st);
		}

		@Test
		public void testRunSearch()
		{
			// start off with duff period (where we don't have dates)
			String st = tts.collateDateFilter(startDate, endDate);
			ArrayList<String> theList = tts.getDownloadList(st);

			assertNotNull("should have found list", theList);
			assertEquals("fount correct num", 36, theList.size());
		}

		@SuppressWarnings("deprecation")
		@Test
		public void testGetDocuments()
		{
			// start off with duff period (where we don't have dates)
			String st = tts.collateDateFilter(startDate, endDate);
			ArrayList<String> theList = tts.getDownloadList(st);

			assertNotNull("should have found list", theList);
			assertEquals("fount correct num", 36, theList.size());

			assertEquals("starts empty", 0, tts._myCache.size());

			tts.doTheDownload(theList);

			assertEquals("has some docs", 36, tts._myCache.size());

			// continue, to test how they work
			CouchTrack firstDoc = tts._myCache.values().iterator().next();

			// check it's in the period

			assertTrue("does overlap",
					firstDoc.overlaps(new TimePeriod.BaseTimePeriod(startDate, endDate)));
			assertFalse("doesn't overlap",
					firstDoc.overlaps(new TimePeriod.BaseTimePeriod(new HiResDate(
							new Date(113, 6, 3, 12, 15, 14)), new HiResDate(new Date(113, 6,
							3, 12, 15, 14)))));
			assertFalse("doesn't overlap",
					firstDoc.overlaps(new TimePeriod.BaseTimePeriod(new HiResDate(
							new Date(111, 6, 3, 12, 15, 14)), new HiResDate(new Date(111, 6,
							3, 12, 15, 14)))));
			assertTrue("does overlap",
					firstDoc.overlaps(new TimePeriod.BaseTimePeriod(new HiResDate(
							new Date(111, 6, 3, 12, 15, 14)), new HiResDate(new Date(113, 6,
							3, 12, 15, 14)))));

			// try to get a location
			WorldLocation loc = firstDoc.getLocationAt(0);
			assertNotNull("should have produced loc", loc);
			System.out.println("location is " + loc);
		}

	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class TrackStoreInfo extends Editable.EditorType
	{
	
		public TrackStoreInfo(BaseLayer data)
		{
			super(data, data.getName(), "");
		}
	
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{
						prop("Visible", "the Layer visibility", VISIBILITY),
						prop("Name", "the name of the Layer", FORMAT),
						prop("LineThickness", "the thickness of lines in this layer",
								FORMAT),
						prop("Buffered", "whether to double-buffer Layer. ('Yes' for better performance)", FORMAT),
						prop("Color", "the color to plot the tracks", FORMAT),
						};
	
				res[2]
						.setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);
	
				return res;
	
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	
		@SuppressWarnings("rawtypes")
		public MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			Class c = BaseLayer.class;
			MethodDescriptor mds[] =
			{ method(c, "exportShape", null, "Export Shape"),
					method(c, "hideChildren", null, "Hide all children"),
					method(c, "revealChildren", null, "Reveal all children") };
			return mds;
		}
	
	}

}
