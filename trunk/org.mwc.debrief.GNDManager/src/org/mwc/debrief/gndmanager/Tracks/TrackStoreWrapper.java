package org.mwc.debrief.gndmanager.Tracks;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
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
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.gndmanager.Activator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Plottable;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class TrackStoreWrapper extends BaseLayer implements WatchableList,
		Serializable
{

	/** the maximum number of tracks that we download in one go
	 * 
	 */
	private static final int MAX_ROWS_TO_IMPORT = 200;

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

	public class CouchTrack implements Serializable, Plottable
	{

		public class CouchTrackInfo extends Editable.EditorType
		{

			public CouchTrackInfo(CouchTrack data)
			{
				super(data, data.getName(), "");
			}

			public PropertyDescriptor[] getPropertyDescriptors()
			{
				try
				{
					PropertyDescriptor[] res =
					{ prop("Visible", "the Layer visibility", VISIBILITY),
							prop("Color", "the color for this track", FORMAT) };

					return res;

				}
				catch (IntrospectionException e)
				{
					return super.getPropertyDescriptors();
				}
			}
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JsonNode _doc;
		private TimePeriod _myCoverage;
		private ArrayList<Long> _timeVals;
		private ArrayNode _latArray;
		private Color _thisTrackColor = null;
		private boolean _isVisible = true;
		private ArrayNode _longArray;
		private WorldArea _myBounds = null;
		private String _myName = null;
		private EditorType _myEditor = null;

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
			int res = 0;
			if (getTimes() != null)
				res = getTimes().size();
			return res;
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

				if (timeArray != null)
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
			boolean overlaps = false;

			if (_myCoverage == null)
			{
				ArrayList<Long> times = getTimes();
				if (times != null && times.size() > 0)
				{
					Date fDate = new Date(times.get(0));
					Date lDate = new Date(times.get(times.size() - 1));
					_myCoverage = new TimePeriod.BaseTimePeriod(new HiResDate(fDate),
							new HiResDate(lDate));
				}
			}

			if (_myCoverage != null)
			{
				overlaps = _myCoverage.overlaps(targetPeriod);
			}

			return overlaps;
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

		public String getName()
		{
			if (_myName == null)
			{
				final String theID = _doc.get("_id").asText();
				JsonNode metadata = _doc.get("metadata");
				if (metadata != null)
				{
					JsonNode pNode = metadata.get("platform");
					if (pNode != null && pNode.asText().length() > 0)
						_myName = pNode.asText();

					if (_myName == null)
					{
						_myName = theID.substring(theID.length() - 7, theID.length());
					}
					else
						_myName = _myName + "-"
								+ theID.substring(theID.length() - 5, theID.length());
				}
			}

			return _myName;
		}

		public String toString()
		{
			return getName();
		}

		public TimePeriod getPeriod()
		{
			TimePeriod res = null;
			ArrayList<Long> times = getTimes();
			if (times != null)
			{
				HiResDate start = new HiResDate(times.get(0));
				HiResDate end = new HiResDate(times.get(times.size() - 1));
				res = new TimePeriod.BaseTimePeriod(start, end);
			}
			return res;
		}

		@Override
		public boolean hasEditor()
		{
			return true;
		}

		@Override
		public EditorType getInfo()
		{
			if (_myEditor == null)
				_myEditor = new CouchTrackInfo(this);
			return _myEditor;
		}

		@Override
		public int compareTo(Plottable arg0)
		{
			return getName().compareTo(arg0.getName());
		}

		@Override
		public void paint(CanvasType dest)
		{
			if (!getVisible())
				return;

			dest.setColor(getColor());

			long startTime = _currentFilterPeriod.getStartDTG().getDate().getTime();
			long endTime = _currentFilterPeriod.getEndDTG().getDate().getTime();

			int len = length();
			ArrayList<Long> times = getTimes();

			// if we don't have time, there's just no point!
			if (times == null)
				return;

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
			WorldLocation thisL = getLocationAt(ctr);

			if (thisL == null)
			{
				// hey, it's not a spatial track
				return;
			}

			// and convert a screen point
			Point lastLoc = dest.toScreen(thisL);

			// is this the first location?
			if (_showTrackName)
				dest.drawText(getName(), lastLoc.x + 10, lastLoc.y + 5);

			// now we can continue forward
			while ((ctr < len - 1) && (times.get(++ctr) < endTime))
			{

				// put in a marker
				dest.drawRect(lastLoc.x - 1, lastLoc.y - 1, 3, 3);

				// get the
				thisL = getLocationAt(ctr);

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
		public WorldArea getBounds()
		{
			if (_myBounds == null)
			{
				ArrayList<Long> times = getTimes();
				if ((times != null && (times.size() > 0)))
				{
					for (int i = 0; i < times.size(); i++)
					{
						WorldLocation thisLoc = getLocationAt(i);
						if (_myBounds == null)
							_myBounds = new WorldArea(thisLoc, thisLoc);
						else
							_myBounds.extend(thisLoc);
					}
				}
			}
			return _myBounds;
		}

		public Color getColor()
		{
			Color res = _thisTrackColor;
			if (_thisTrackColor == null)
				res = _myColor;
			return res;
		}

		@FireReformatted
		public void setColor(Color val)
		{
			_thisTrackColor = val;
		}

		@Override
		public boolean getVisible()
		{
			return _isVisible;
		}

		@Override
		@FireReformatted
		public void setVisible(boolean val)
		{
			_isVisible = val;
		}

		@Override
		public double rangeFrom(WorldLocation other)
		{
			// are we vis?
			if (!getVisible())
				return Plottable.INVALID_RANGE;

			double thisD = Double.MAX_VALUE;
			int len = length();
			for (int i = 0; i < len; i++)
			{
				WorldLocation thisL = getLocationAt(i);
				double thisR = thisL.rangeFrom(other);
				thisD = Math.min(thisD, thisR);
			}

			return thisD;
		}

		public TrackWrapper toDebriefTrack()
		{
			TrackWrapper res = null;
			// just check we have some data
			if (length() > 0)
			{
				if (getLocationAt(0) != null)
				{
					res = new TrackWrapper();
					res.setName(getName());
					ArrayNode courses = getArray("course");
					ArrayNode speeds = getArray("speed");
					for (int i = 0; i < length(); i++)
					{
						Long thisT = getTimes().get(i);
						WorldLocation loc = getLocationAt(i);
						double crse = 0;
						double spd = 0;
						// see if we have course data
						if (courses != null && courses.size() > 0)
						{
							crse = courses.get(i).asDouble();
							spd = speeds.get(i).asDouble();
						}
						Fix newF = new Fix(new HiResDate(thisT), loc, crse, spd);
						FixWrapper fw = new FixWrapper(newF);
						res.addFix(fw);
					}
				}
			}
			return res;
		}
	}

	private transient HashMap<String, CouchTrack> _myCache = new HashMap<String, CouchTrack>();

	/**
	 * our color
	 * 
	 */
	private Color _myColor = Color.red;

	/**
	 * whether these tracks should be made avaialble to bounds calculations
	 * 
	 */
	private boolean _includeInBounds = false;

	/**
	 * whether we should highlight the currente position when stepping through
	 * 
	 */
	private boolean _includeInTimeStep = false;

	/**
	 * whether we should show the track name at the start
	 * 
	 */
	private boolean _showTrackName;

	/**
	 * whether we should interpolate the track points when stepping through
	 * 
	 */
	private boolean _interpolatePoints = false;

	@Override
	public void add(Editable thePlottable)
	{
		if (thePlottable instanceof CouchTrack)
		{
			CouchTrack ct = (CouchTrack) thePlottable;
			_myCache.put(ct.getId(), ct);
			super.add(thePlottable);
		}
		else
			CorePlugin.logError(Status.ERROR, "Track store cannot add this element:"
					+ thePlottable, null);
	}

	@Override
	public void removeElement(Editable p)
	{
		if (p instanceof CouchTrack)
		{
			super.removeElement(p);

			// also remove the item
			_myCache.remove(((CouchTrack) p).getId());
		}
		else
			CorePlugin.logError(Status.ERROR,
					"Track store cannot remove this element:" + p, null);
	}

	@Override
	public void filterListTo(final HiResDate start, final HiResDate end)
	{
		// remember what hte current filter is
		_currentFilterPeriod = new TimePeriod.BaseTimePeriod(start, end);

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
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				// ok, collate the time fields
				String myFilter = collateDateFilter(start, end);

				// hmm, which tracks match our current time period?
				ArrayList<String> docsToDownload = getDownloadList(myFilter);

				// now get any new items
				if (docsToDownload.size() > 0)
				{
					// ok, we need some - go get them
					doTheDownload(docsToDownload);
					
					// extend our period of coverage
					if (_dataLoaded == null)
						_dataLoaded = new TimePeriod.BaseTimePeriod(_currentFilterPeriod
								.getStartDTG(), _currentFilterPeriod.getEndDTG());
					else
					{
						_dataLoaded.extend(_currentFilterPeriod.getStartDTG());
						_dataLoaded.extend(_currentFilterPeriod.getEndDTG());
					}
				}

				// and trigger size-updated
				firePropertyChange(SupportsPropertyListeners.FORMAT, null, this);
			}
		});
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
			CorePlugin.logError(Status.INFO, "CouchDb get: " + uri, null);
			Post doIt = new Post(uri, content, 5000, 10000);
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

						// put in our local hashmap
						_myCache.put(track.getId(), track);

						// and store in the parent
						super.add(track);

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
			CorePlugin.logError(Status.INFO, "ElasticSearch get: " + uri, null);

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
					int rowCount = rows.size();
					Activator.logError(Status.INFO, "ES found " + rowCount + " rows",
							null);
					if (rowCount < MAX_ROWS_TO_IMPORT)
					{
						for (int i = 0; i < rowCount; i++)
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
					else
						Activator.logError(Status.WARNING, "More than "+MAX_ROWS_TO_IMPORT+" rows returned!", null);
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
		// set the line thickness
		dest.setLineWidth(super.getLineThickness());
		
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
			if (thisT.getVisible())
				if (thisT.overlaps(_currentFilterPeriod))
				{
					// yes. get painting
					thisT.paint(dest);
				}
		}
	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	/**
	 * whether to return bounds objects for the tracks when requested (both
	 * temporal & spatial)
	 * 
	 */
	public void setIncludeInBounds(boolean val)
	{
		_includeInBounds = val;
	}

	/**
	 * whether to return bounds objects for the tracks when requested (both
	 * temporal & spatial)
	 * 
	 */
	public boolean getIncludeInBounds()
	{
		return _includeInBounds;
	}

	/**
	 * whether to interpolate the point marker when in time-stepping mode
	 * 
	 */
	public void setInterpolatePoints(boolean val)
	{
		_interpolatePoints = val;
	}

	/**
	 * whether to interpolate the point marker when in time-stepping mode
	 * 
	 * @return
	 */
	public boolean getInterpolatePoints()
	{
		return _interpolatePoints;
	}

	/**
	 * whether to return time step results, for time stepper
	 * 
	 */
	public void setIncludeInTimeStep(boolean val)
	{
		_includeInTimeStep = val;
	}

	/**
	 * whether to return time step results, for time stepper
	 * 
	 */
	public boolean getIncludeInTimeStep()
	{
		return _includeInTimeStep;
	}

	/**
	 * whether to plot the track name
	 * 
	 */
	public boolean getShowTrackName()
	{
		return _showTrackName;
	}

	/**
	 * whether to plot the track name
	 * 
	 */
	public void setShowTrackName(boolean showTrackName)
	{
		this._showTrackName = showTrackName;
	}

	@Override
	public WorldArea getBounds()
	{
		WorldArea res = null;
		if (_includeInBounds)
		{
			Iterator<CouchTrack> iter = _myCache.values().iterator();
			while (iter.hasNext())
			{
				TrackStoreWrapper.CouchTrack thisT = (TrackStoreWrapper.CouchTrack) iter
						.next();
				WorldArea thisB = thisT.getBounds();
				if (thisB != null)
				{
					if (res == null)
						res = new WorldArea(thisB);
					else
						res.extend(thisB);
				}
			}
		}

		return res;
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

		if (_includeInBounds)
			if (_dataLoaded != null)
				res = _dataLoaded.getStartDTG();

		return res;
	}

	@Override
	public HiResDate getEndDTG()
	{
		HiResDate res = null;

		if (_includeInBounds)
			if (_dataLoaded != null)
				res = _dataLoaded.getEndDTG();

		return res;
	}

	@Override
	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new TrackStoreInfo(this);

		return _myEditor;
	}

	@Override
	public Watchable[] getNearestTo(HiResDate DTG)
	{
		ArrayList<Watchable> res = new ArrayList<Watchable>();

		if (_includeInTimeStep)
		{
			// find items near this time

			// loop through our tracks
			Iterator<CouchTrack> iter = _myCache.values().iterator();

			while (iter.hasNext())
			{
				TrackStoreWrapper.CouchTrack thisT = (TrackStoreWrapper.CouchTrack) iter
						.next();
				// in period?
				TimePeriod thisP = thisT.getPeriod();

				if (thisT.getVisible())
					if (thisP != null)
						if (thisP.contains(DTG))
						{
							// find nearest point
							ArrayList<Long> times = thisT.getTimes();
							if (times != null)
							{
								int ctr = 0;
								while (ctr < times.size() - 1)
								{
									Long tNow = times.get(ctr);
									if (tNow > DTG.getDate().getTime())
									{
										break;
									}
									ctr++;
								}
								if (ctr < times.size() - 1)
								{

									// right, we're now looking at the time immediately after teh
									// time
									if (_interpolatePoints && (ctr > 0))
									{
										// ok, dodgy maths to interpolate the location at this time
										FixWrapper before = new FixWrapper(
												new Fix(new HiResDate(times.get(ctr - 1)),
														thisT.getLocationAt(ctr - 1), 0, 0));
										FixWrapper next = new FixWrapper(new Fix(new HiResDate(
												times.get(ctr)), thisT.getLocationAt(ctr), 0, 0));

										if (before.getTime().greaterThan(DTG))
											System.err.println("not before");
										if (next.getTime().lessThan(DTG))
											System.err.println("not after");

										FixWrapper newFix = FixWrapper.interpolateFix(before, next,
												new HiResDate(DTG));
										newFix.setColor(_myColor);
										res.add(newFix);

									}
									else
									{
										// easy, just return the point immediately after the
										// indicated
										// time
										res.add(new FixWrapper(new Fix(
												new HiResDate(times.get(ctr)),
												thisT.getLocationAt(ctr), 0, 0)));
									}
								}
							}
						}

			}
		}

		return res.toArray(new Watchable[]
		{});
	}

	@Override
	public Collection<Editable> getItemsBetween(HiResDate start, HiResDate end)
	{
		return null;
	}

	public void setColor(Color col)
	{
		_myColor = col;

	}

	@Override
	public Color getColor()
	{
		return _myColor;
	}

	@Override
	public PlainSymbol getSnailShape()
	{
		return null;
	}

	public class TrackStoreInfo extends Editable.EditorType
	{

		public TrackStoreInfo(TrackStoreWrapper data)
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
						prop(
								"Buffered",
								"whether to double-buffer Layer. ('Yes' for better performance)",
								FORMAT),
						prop("Color", "the color to plot the tracks", FORMAT),
						prop("IncludeInBounds",
								"include these tracks in spatial/temporal bounds calcs", FORMAT),
						prop("ShowTrackName",
								"show the track name at alongside the first point", FORMAT),
						prop("InterpolatePoints",
								"interpolate location markers when time stepping", FORMAT),
						prop("IncludeInTimeStep", "include these tracks in time stepping",
								TEMPORAL), };

				res[2]
						.setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

				return res;

			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		// @SuppressWarnings("rawtypes")
		// public MethodDescriptor[] getMethodDescriptors()
		// {
		// // just add the reset color field first
		// Class c = BaseLayer.class;
		// MethodDescriptor mds[] =
		// { method(c, "exportShape", null, "Export Shape"),
		// method(c, "hideChildren", null, "Hide all children"),
		// method(c, "revealChildren", null, "Reveal all children") };
		// return mds;
		//
		// }
	}

}
