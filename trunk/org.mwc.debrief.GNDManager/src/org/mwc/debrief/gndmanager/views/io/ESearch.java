package org.mwc.debrief.gndmanager.views.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.eclipse.core.runtime.IAdaptable;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.gndmanager.views.ManagerView;

import MWC.TacticalData.GND.GTrack;

public class ESearch implements SearchModel
{

	private final ObjectMapper _mapper;

	public ESearch()
	{
		_mapper = new ObjectMapper();
	}

	@Override
	public MatchList getMatches(String indexURL, String dbURL, ManagerView view) throws IOException
	{
		JsonNode query = createQuery(view);
		return fireSearch(indexURL, query);
	}

	@Override
	public MatchList getAll(String indexURL, String dbURL) throws IOException
	{
		ObjectNode queryObj = _mapper.createObjectNode();
		queryObj.put("match_all", _mapper.createObjectNode());

		return fireSearch(indexURL, queryObj);
	}

	public JsonNode createQuery(ManagerView view)
	{
		ArrayNode facets = _mapper.createArrayNode();
		// ok, get collating the items.
		addThis(facets, view.getPlatforms().getSelectedItems(), "platform");
		addThis(facets, view.getPlatformTypes().getSelectedItems(), "platform_type");
		addThis(facets, view.getTrials().getSelectedItems(), "trial");

		// we also have to do the free search
		String freeText = view.getFreeText();
		if (freeText != null)
		{
			ObjectNode field = _mapper.createObjectNode();
			ObjectNode term = _mapper.createObjectNode();
			term.put("query", freeText);
			field.put("query_string", term);
			facets.set(facets.size(), field);
		}

		ObjectNode mustHolder = _mapper.createObjectNode();

		mustHolder.put("must", facets);
		ObjectNode boolHolder = _mapper.createObjectNode();
		boolHolder.put("bool", mustHolder);

		return boolHolder;
	}

	private void addThis(ArrayNode facets, List<String> items, String tag_name)
	{
		if (items.size() > 0)
		{
			ObjectNode holder = _mapper.createObjectNode();
			ObjectNode terms = _mapper.createObjectNode();
			ArrayNode matches = _mapper.createArrayNode();
			for (Iterator<String> iterator = items.iterator(); iterator.hasNext();)
			{
				String thisItem = (String) iterator.next();

				// and store it
				matches.add(thisItem);
			}
			terms.put(tag_name, matches);
			terms.put("minimum_match", 1);
			holder.put("terms", terms);
			facets.add(holder);
		}
	}

	private MatchList fireSearch(String root, JsonNode queryObj)
			throws IOException
	{
		MatchList res = null;

		// sort out the facets
		ObjectNode facets = _mapper.createObjectNode();
		addFacetFor(facets, "platform");
		addFacetFor(facets, "platform_type");
		addFacetFor(facets, "trial");
		addFacetFor(facets, "sensor");
		addFacetFor(facets, "sensor_type");

		ObjectNode qq = _mapper.createObjectNode();
		qq.put("size", 500);
		qq.put("query", queryObj);
		qq.put("facets", facets);

		try
		{
			URL url;
			url = new URL(root + "/_search?pretty=true&source=" + qq.toString());
			JsonNode obj = _mapper.readValue(url, JsonNode.class);
			res = new MatchListWrap(obj);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return res;
	}

	protected  class MatchListWrap implements MatchList
	{

		private final JsonNode _node;

		public MatchListWrap(JsonNode list)
		{
			_node = list;
		}

		@Override
		public int getNumMatches()
		{
			int hits = _node.get("hits").get("hits").size();
			return hits;
		}

		@Override
		public Match getMatch(int index)
		{
			JsonNode node = _node.get("hits").get("hits").get(index);
			return new MatchWrap(node);
		}

		@Override
		public Facet getFacet(String name)
		{
			JsonNode facets = _node.get("facets");
			return new FacetWrap(facets.get(name));
		}

	}

	public static class FacetWrap implements Facet
	{

		private JsonNode _node;

		public FacetWrap(JsonNode jsonNode)
		{
			_node = jsonNode;
		}

		@Override
		public int size()
		{
			int res = 0;
			if (_node.has("terms"))
				res = _node.get("terms").size();
			return res;
		}

		@Override
		public String getName(int index)
		{
			return _node.get("terms").get(index).get("term").getTextValue();
		}

		@Override
		public int getCount(int index)
		{
			return _node.get("terms").get(index).get("count").asInt();
		}

		@Override
		public ArrayList<String> toList()
		{
			ArrayList<String> res = new ArrayList<String>();
			for (int i = 0; i < size(); i++)
			{
				res.add(getName(i));
			}
			return res;
		}

	}

	protected  class MatchWrap implements Match, IAdaptable
	{
		private JsonNode _node;

		public MatchWrap(JsonNode item)
		{
			_node = item;
		}

		@Override
		public String getName()
		{
			return _node.get("_source").get("name").getTextValue();
		}

		@Override
		public String getPlatform()
		{
			return _node.get("_source").get("platform").getTextValue();
		}

		@Override
		public String getPlatformType()
		{
			return _node.get("_source").get("platform_type").getTextValue();
		}

		@Override
		public String getTrial()
		{
			return _node.get("_source").get("trial").getTextValue();
		}

		@Override
		public String getId()
		{
			String res = _node.get("_id").getTextValue();
			res = res.split("_")[0];
			return res;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Object getAdapter(Class adapter)
		{
			final Object res;
			if (adapter == EditableWrapper.class)
			{
				GTrack item = loadTrack(_node);
				res = new EditableWrapper(item, null, null);
			}
			else
				res = null;
			return res;
		}
	}


	/**
	 * create a term facet for the specified term
	 * 
	 * @param parent
	 * @param term
	 */
	private void addFacetFor(ObjectNode parent, String term)
	{
		ObjectNode platform = _mapper.createObjectNode();
		ObjectNode platTerm = _mapper.createObjectNode();
		platTerm.put("field", term);
		platTerm.put("size", 1000);
		platform.put("terms", platTerm);
		parent.put(term, platform);
	}

	public GTrack loadTrack(JsonNode _node)
	{
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
