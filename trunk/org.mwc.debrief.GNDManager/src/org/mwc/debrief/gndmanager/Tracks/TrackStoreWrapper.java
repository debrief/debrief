package org.mwc.debrief.gndmanager.Tracks;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.javalite.http.Get;
import org.javalite.http.Post;
import org.mwc.cmap.core.CorePlugin;

import Debrief.GUI.Frames.Application;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;

public class TrackStoreWrapper extends BaseLayer implements WatchableList
{

	private final ObjectMapper _mapper;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String COUCHDB_LOCATION = "CouchDb_Loc";
	public static final String ES_LOCATION = "ES_Loc";
	private String _couchURL;
	private String _esURL;

	/**
	 * the time period we've been filtered to
	 * 
	 */
	private TimePeriod.BaseTimePeriod _currentFilterPeriod;

	private TimePeriod.BaseTimePeriod _dataLoaded = null;

	public TrackStoreWrapper(String couchURL, String esURL)
	{

		_mapper = new ObjectMapper();
		_couchURL = couchURL;
		_esURL = esURL;

	}

	public TrackStoreWrapper()
	{
		this(Application.getThisProperty(COUCHDB_LOCATION), Application
				.getThisProperty(ES_LOCATION));
	}

	private class CouchTrack
	{

		private final JsonNode _doc;

		public CouchTrack(JsonNode theDoc)
		{
			_doc = theDoc;
		}

		public String getId()
		{
			return _doc.get("_id").asText();
		}

		public boolean overlaps(BaseTimePeriod _currentFilterPeriod)
		{
			// TODO is this track visible at all in the indicated period?
			
			// TODO cache the track start/end dates - we'll be using them a lot
			
			return false;
		}

	}

	private HashMap<String, CouchTrack> _myCache = new HashMap<String, CouchTrack>();

	@Override
	public void filterListTo(HiResDate start, HiResDate end)
	{

		// remember what hte current filter is
		_currentFilterPeriod = new TimePeriod.BaseTimePeriod(start, end);

		// here's where we re-query
		System.err.println("Filtering my documents to period:" + start.toString()
				+ " to " + end);

		boolean needToDownloadData = true;

		// are we already storing more than this?
		if (_dataLoaded != null)
		{
			if (_dataLoaded.contains(start) && _dataLoaded.contains(end))
			{
				// ok, we can don't need to load any data - we just do the filter
				needToDownloadData = false;
			}
		}

		if (needToDownloadData)
		{

			// ok, collate the time fields
			String myFilter = collateDateFilter(start, end);

			// ok, run the query
			String uri = _esURL + "/data/dataset/_search?source=" + myFilter;
			ArrayList<String> docsToDownload = new ArrayList<String>();
			try
			{
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
				CorePlugin
						.logError(Status.ERROR, "failed to parse returned ES rows", e);
			}
			catch (JsonMappingException e)
			{
				CorePlugin
						.logError(Status.ERROR, "failed to parse returned ES rows", e);
			}
			catch (IOException e)
			{
				CorePlugin
						.logError(Status.ERROR, "failed to parse returned ES rows", e);
			}

			// now get any new items
			if (docsToDownload.size() > 0)
			{
				// collate the ids
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode root = mapper.createObjectNode();
				ArrayNode keys = mapper.createArrayNode();
				Iterator<String> iter = docsToDownload.iterator();
				while (iter.hasNext())
				{
					String id = (String) iter.next();
					keys.add(id);
				}
				root.put("keys", keys);

				try
				{
					uri = _couchURL + "/tracks/_all_docs?include_docs=true";
					System.err.println("to:" + uri);
					String cStr = root.toString();
					byte[] content = cStr.getBytes();
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
				}
				catch (JsonParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (JsonMappingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// extend our period of coverage

		}
	}

	private String collateDateFilter(HiResDate start, HiResDate end)
	{
		SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd");
		iso.setTimeZone(TimeZone.getTimeZone("GMT"));

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
		if(_currentFilterPeriod == null)
		{
			CorePlugin.logError(Status.WARNING, "Track Store hasn't had filter applied yet",null);
			
		}
		
		// ok, loop through my tracks
		Iterator<CouchTrack> iter = _myCache.values().iterator();
		while (iter.hasNext())
		{
			TrackStoreWrapper.CouchTrack thisT = (TrackStoreWrapper.CouchTrack) iter
					.next();
			// ok, check it's visible at all
			if(thisT.overlaps(_currentFilterPeriod))
			{
				
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
		return "Track Store (" + _myCache.size() + " tracks)";
	}

	@Override
	public HiResDate getStartDTG()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HiResDate getEndDTG()
	{
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Color getColor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlainSymbol getSnailShape()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args)
	{
		SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		String dateTest = iso.format(new Date(112, 6, 3, 12, 15, 14));
		System.out.println(dateTest);

		TrackStoreWrapper tsw = new TrackStoreWrapper(
				"http://gnd.iriscouch.com:5984",
				"http://0d9fd05438a44abe882139135eb01048.found.no:9200");
		HiResDate startDate = new HiResDate(new Date(112, 6, 3, 12, 15, 14));
		HiResDate endDate = new HiResDate(new Date(112, 6, 8, 12, 18, 14));

		String tt = tsw.collateDateFilter(startDate, endDate);
		System.out.println(tt);

		tsw.filterListTo(startDate, endDate);

	}

}
