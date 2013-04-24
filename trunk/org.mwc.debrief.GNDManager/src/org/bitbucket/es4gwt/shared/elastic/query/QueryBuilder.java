package org.bitbucket.es4gwt.shared.elastic.query;

import static com.google.common.base.Preconditions.*;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.*;
import static org.bitbucket.es4gwt.shared.elastic.query.Queries.*;

import java.util.Collection;
import java.util.List;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.elastic.filter.ElasticFilter;
import org.bitbucket.es4gwt.shared.elastic.filter.FilterBuilder;

import com.google.common.collect.Lists;

/**
 * @author Mikael Couzic
 */
public class QueryBuilder {

	private final FilterBuilder filterBuilder = new FilterBuilder();
	private List<ElasticFilter> dateFilters = Lists.newArrayList();

	// Text Search related fields
	private String text;
	private ElasticFacet[] searchParams;

	public QueryBuilder filter(ElasticFacet facet, String term) {
		checkNotNull(facet);
		checkNotNull(term);
		filterBuilder.withTerm(facet, term);
		return this;
	}

	public QueryBuilder after(String date) {
		dateFilters.add(endAfter(date));
		return this;
	}

	public QueryBuilder before(String date) {
		dateFilters.add(startBefore(date));
		return this;
	}

	public QueryBuilder withinDateRange(String start, String end) {
		this.dateFilters.add(dateRange(start, end));
		return this;
	}

	public QueryBuilder searchText(String text, ElasticFacet[] searchParams) {
		checkNotNull(text);
		checkArgument(!text.isEmpty());
		this.text = text;
		checkNotNull(searchParams);
		checkArgument(searchParams.length > 0);
		this.searchParams = searchParams;
		return this;
	}

	public ElasticQuery buildQuery() {
		if (!filterBuilder.isFiltered() && dateFilters.isEmpty()) // No filter
			return baseQuery();
		else if (!filterBuilder.isFiltered()) // Date filter only
			return filtered(baseQuery(), and(dateFilters));
		else if (dateFilters.isEmpty()) // Terms filters only
			return filtered(baseQuery(), filterBuilder.buildFilter());
		else
			// Both Terms and Date filters
			throw new UnsupportedOperationException("Not implemented yet, no use case for the moment");
	}

	private ElasticQuery baseQuery() {
		if (text == null || searchParams == null) {
			return matchAll();
		} else {
			Collection<ElasticFilter> filters = Lists.newArrayList();
			for (ElasticFacet facet : searchParams) {
				filters.add(term(facet, text));
			}
			return filtered(matchAll(), or(filters));
		}
	}

	public String buildQueryString() {
		return buildQuery().toRequestString();
	}

	@Override
	public String toString() {
		return buildQueryString();
	}

	public boolean isFiltered() {
		return !filterBuilder.isFiltered() || !dateFilters.isEmpty() || text != null;
	}

}
