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

	public static String toRequestString(SearchRequest request, ElasticFacet[] searchFields,
			ElasticFacet[] resultsFields) {
		SearchRequestConverter converter = new SearchRequestConverter(request);
		return converter.buildRequestString(searchFields, resultsFields);
	}

	public static String facetsOnly(ElasticFacet... facets) {
		return FACETS_ONLY_REQUEST_PREFIX + matchAll().toRequestString()
				+ ","
				+ new SearchFacets(facets).facets(new FilterBuilder())
				+ SUFFIX;
	}

	private final QueryBuilder queryBuilder;
	private final FilterBuilder filterBuilder;

	private SearchRequestConverter(SearchRequest spec) {
		this.queryBuilder = QueryConverter.toElasticQueryBuilder(spec);
		this.filterBuilder = FilterConverter.toElasticFilterBuilder(spec);
	}

	private String buildRequestString(ElasticFacet[] searchFields, ElasticFacet[] resultsFields) {
		StringBuilder sb = new StringBuilder(SEARCH_REQUEST_PREFIX_PART_1);
		for (Iterator<ElasticFacet> facets = Iterators.forArray(resultsFields); facets.hasNext();) {
			ElasticFacet facet = facets.next();
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
