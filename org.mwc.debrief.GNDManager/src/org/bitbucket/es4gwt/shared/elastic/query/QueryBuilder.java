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

package org.bitbucket.es4gwt.shared.elastic.query;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.and;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.dateRange;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.endAfter;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.or;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.startBefore;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.term;
import static org.bitbucket.es4gwt.shared.elastic.query.Queries.filtered;
import static org.bitbucket.es4gwt.shared.elastic.query.Queries.matchAll;

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
	private final List<ElasticFilter> dateFilters = Lists.newArrayList();

	// Text Search related fields
	private String text;
	private ElasticFacet[] searchParams;

	public QueryBuilder after(final String date) {
		dateFilters.add(endAfter(date));
		return this;
	}

	private ElasticQuery baseQuery() {
		if (text == null || searchParams == null) {
			return matchAll();
		} else {
			final Collection<ElasticFilter> filters = Lists.newArrayList();
			for (final ElasticFacet facet : searchParams) {
				filters.add(term(facet, text));
			}
			return filtered(matchAll(), or(filters));
		}
	}

	public QueryBuilder before(final String date) {
		dateFilters.add(startBefore(date));
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

	public String buildQueryString() {
		return buildQuery().toRequestString();
	}

	public QueryBuilder filter(final ElasticFacet facet, final String term) {
		checkNotNull(facet);
		checkNotNull(term);
		filterBuilder.withTerm(facet, term);
		return this;
	}

	public boolean isFiltered() {
		return !filterBuilder.isFiltered() || !dateFilters.isEmpty() || text != null;
	}

	public QueryBuilder searchText(final String text, final ElasticFacet[] searchParams) {
		checkNotNull(text);
		checkArgument(!text.isEmpty());
		this.text = text;
		checkNotNull(searchParams);
		checkArgument(searchParams.length > 0);
		this.searchParams = searchParams;
		return this;
	}

	@Override
	public String toString() {
		return buildQueryString();
	}

	public QueryBuilder withinDateRange(final String start, final String end) {
		this.dateFilters.add(dateRange(start, end));
		return this;
	}

}
