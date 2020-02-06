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

package org.bitbucket.es4gwt.shared.spec;

import static org.bitbucket.es4gwt.shared.elastic.query.Queries.matchAll;

import java.util.Iterator;

import org.bitbucket.es4gwt.shared.SearchConstants;
import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.elastic.SearchFacets;
import org.bitbucket.es4gwt.shared.elastic.filter.FilterBuilder;
import org.bitbucket.es4gwt.shared.elastic.query.QueryBuilder;

import com.google.common.collect.Iterators;

/**
 * @author Mikael Couzic
 */
public class SearchRequestConverter {

	private static final String SEARCH_REQUEST_PREFIX_PART_1 = "{\"size\":" + SearchConstants.MAX_RESULTS
			+ ",\"fields\":[";
	private static final String SEARCH_REQUEST_PREFIX_PART_2 = "],\"query\":";
	private static final String FACETS_ONLY_REQUEST_PREFIX = "{\"size\":0,\"query\":";
	private static final String SUFFIX = "}";

	public static String facetsOnly(final ElasticFacet... facets) {
		return FACETS_ONLY_REQUEST_PREFIX + matchAll().toRequestString() + ","
				+ new SearchFacets(facets).facets(new FilterBuilder()) + SUFFIX;
	}

	public static String toRequestString(final SearchRequest request, final ElasticFacet[] searchFields,
			final ElasticFacet[] resultsFields) {
		final SearchRequestConverter converter = new SearchRequestConverter(request);
		return converter.buildRequestString(searchFields, resultsFields);
	}

	private final QueryBuilder queryBuilder;
	private final FilterBuilder filterBuilder;

	private SearchRequestConverter(final SearchRequest spec) {
		this.queryBuilder = QueryConverter.toElasticQueryBuilder(spec);
		this.filterBuilder = FilterConverter.toElasticFilterBuilder(spec);
	}

	private String buildRequestString(final ElasticFacet[] searchFields, final ElasticFacet[] resultsFields) {
		final StringBuilder sb = new StringBuilder(SEARCH_REQUEST_PREFIX_PART_1);
		for (final Iterator<ElasticFacet> facets = Iterators.forArray(resultsFields); facets.hasNext();) {
			final ElasticFacet facet = facets.next();
			sb.append("\"" + facet.toRequestString() + "\"");
			if (facets.hasNext())
				sb.append(",");
		}
		sb.append(SEARCH_REQUEST_PREFIX_PART_2);
		sb.append(queryBuilder.buildQueryString());
		if (filterBuilder.isFiltered())
			sb.append(",\"filter\":" + filterBuilder.buildFilterString());
		sb.append("," + new SearchFacets(searchFields).facets(filterBuilder) + SUFFIX);
		return sb.toString();
	}
}
