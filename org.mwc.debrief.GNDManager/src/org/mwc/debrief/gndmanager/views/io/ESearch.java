/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

import MWC.TacticalData.GND.GDataset;
import MWC.TacticalData.GND.GTrack;

public class ESearch implements SearchModel
{

	private final ObjectMapper _mapper;

	public ESearch()
	{
		_mapper = new ObjectMapper();
	}

	@Override
	public MatchList getMatches(final String indexURL, final String dbURL, final ManagerView view)
			throws IOException
	{
		final JsonNode query = createQuery(view);
		return fireSearch(indexURL, dbURL, query);
	}

	@Override
	public MatchList getAll(final String indexURL, final String dbURL) throws IOException
	{
		final ObjectNode queryObj = _mapper.createObjectNode();
		queryObj.put("match_all", _mapper.createObjectNode());

		return fireSearch(indexURL, dbURL, queryObj);
	}

	public JsonNode createQuery(final ManagerView view)
	{
		final ArrayNode facets = _mapper.createArrayNode();
		// ok, get collating the items.
		addThis(facets, view.getPlatforms().getSelectedItems(), "platform");
		addThis(facets, view.getPlatformTypes().getSelectedItems(), "platform_type");
		addThis(facets, view.getTrials().getSelectedItems(), "trial");

		// we also have to do the free search
		final String freeText = view.getFreeText();
		if (freeText != null)
		{
			final ObjectNode field = _mapper.createObjectNode();
			final ObjectNode term = _mapper.createObjectNode();
			term.put("query", freeText);
			field.put("query_string", term);
			facets.set(facets.size(), field);
		}

		final ObjectNode mustHolder = _mapper.createObjectNode();

		mustHolder.put("must", facets);
		final ObjectNode boolHolder = _mapper.createObjectNode();
		boolHolder.put("bool", mustHolder);

		return boolHolder;
	}

	private void addThis(final ArrayNode facets, final List<String> items, final String tag_name)
	{
		if (items.size() > 0)
		{
			final ObjectNode holder = _mapper.createObjectNode();
			final ObjectNode terms = _mapper.createObjectNode();
			final ArrayNode matches = _mapper.createArrayNode();
			for (final Iterator<String> iterator = items.iterator(); iterator.hasNext();)
			{
				final String thisItem = (String) iterator.next();

				// and store it
				matches.add(thisItem);
			}
			terms.put(tag_name, matches);
			terms.put("minimum_match", 1);
			holder.put("terms", terms);
			facets.add(holder);
		}
	}

	private MatchList fireSearch(final String root, final String dbURL, final JsonNode queryObj)
			throws IOException
	{
		MatchList res = null;

		// sort out the facets
		final ObjectNode facets = _mapper.createObjectNode();
		addFacetFor(facets, "platform");
		addFacetFor(facets, "platform_type");
		addFacetFor(facets, "trial");
		addFacetFor(facets, "sensor");
		addFacetFor(facets, "sensor_type");

		final ObjectNode qq = _mapper.createObjectNode();
		qq.put("size", 500);
		qq.put("query", queryObj);
		qq.put("facets", facets);

		try
		{
			URL url;
			url = new URL(root + "/_search?pretty=true&source=" + qq.toString());
			final JsonNode obj = _mapper.readValue(url, JsonNode.class);
			res = new MatchListWrap(obj, dbURL);
		}
		catch (final MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (final JsonParseException e)
		{
			e.printStackTrace();
		}
		catch (final JsonMappingException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	protected class MatchListWrap implements MatchList
	{

		private final JsonNode _node;
		private final String _dbURL;

		public MatchListWrap(final JsonNode list, final String dbURL)
		{
			_node = list;
			_dbURL = dbURL;
		}

		@Override
		public int getNumMatches()
		{
			final int hits = _node.get("hits").get("hits").size();
			return hits;
		}

		@Override
		public Match getMatch(final int index)
		{
			final JsonNode node = _node.get("hits").get("hits").get(index);
			return new MatchWrap(node, _dbURL);
		}

		@Override
		public Facet getFacet(final String name)
		{
			final JsonNode facets = _node.get("facets");
			return new FacetWrap(facets.get(name));
		}

	}

	public static class FacetWrap implements Facet
	{

		private final JsonNode _node;

		public FacetWrap(final JsonNode jsonNode)
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
		public String getName(final int index)
		{
			return _node.get("terms").get(index).get("term").getTextValue();
		}

		@Override
		public int getCount(final int index)
		{
			return _node.get("terms").get(index).get("count").asInt();
		}

		@Override
		public ArrayList<String> toList()
		{
			final ArrayList<String> res = new ArrayList<String>();
			for (int i = 0; i < size(); i++)
			{
				res.add(getName(i));
			}
			return res;
		}

	}

	protected class MatchWrap implements Match, IAdaptable
	{
		private final JsonNode _node;
		private final String _dbURL;
		private EditableWrapper _wrappedMe;

		public MatchWrap(final JsonNode item, final String dbURL)
		{
			_node = item;
			_dbURL = dbURL;
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
		public Object getAdapter(final Class adapter)
		{
			final Object res;
			if (adapter == EditableWrapper.class)
			{
				if (_wrappedMe == null)
				{
					final GTrack track = loadTrack(getId());
					_wrappedMe = new EditableWrapper(track, null, null);
				}
				res = _wrappedMe;
			}
			else
				res = null;
			return res;
		}

		public GTrack loadTrack(final String id)
		{
			GTrack res = null;
			JsonNode obj;
			try
			{
				final URL url = new URL(_dbURL + "/" + id);
				final URL databaseURL = new URL(_dbURL);
				obj = _mapper.readValue(url, JsonNode.class);
				final GDataset data = new GDataset(obj, databaseURL);
				res = new GTrack(data);
			}
			catch (final JsonParseException e)
			{
				e.printStackTrace();
			}
			catch (final JsonMappingException e)
			{
				e.printStackTrace();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}

			return res;
		}

	}

	/**
	 * create a term facet for the specified term
	 * 
	 * @param parent
	 * @param term
	 */
	private void addFacetFor(final ObjectNode parent, final String term)
	{
		final ObjectNode platform = _mapper.createObjectNode();
		final ObjectNode platTerm = _mapper.createObjectNode();
		platTerm.put("field", term);
		platTerm.put("size", 1000);
		platform.put("terms", platTerm);
		parent.put(term, platform);
	}

}
