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

package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.allTerms;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.and;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.terms;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.spec.FilterMode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Mikael Couzic
 */
public class FilterBuilder {

	private final Multimap<ElasticFacet, String> terms;
	private final Map<ElasticFacet, String> filterModes = Maps.newHashMap();

	public FilterBuilder() {
		this.terms = ArrayListMultimap.create();
	}

	public FilterBuilder(final Multimap<ElasticFacet, String> terms) {
		this.terms = terms;
	}

	public ElasticFilter buildFilter() {
		return createFilter(terms.keySet());
	}

	public ElasticFilter buildFilterFor(final ElasticFacet facet) {
		final Set<ElasticFacet> otherFacets = Sets.newHashSet(terms.keySet());
		otherFacets.remove(facet);
		return createFilter(otherFacets);
	}

	public ElasticFilter buildFilterOrNullFor(final ElasticFacet facet) {
		if (!hasFilterFor(facet))
			return null;
		else
			return buildFilterFor(facet);
	}

	public String buildFilterString() {
		return buildFilter().toRequestString();
	}

	public String buildFilterStringFor(final ElasticFacet facet) {
		return buildFilterFor(facet).toRequestString();
	}

	private ElasticFilter createFilter(final Set<ElasticFacet> facets) {
		checkArgument(!facets.isEmpty(), "Empty Set");
		final Collection<ElasticFilter> filters = Lists.newArrayList();
		for (final ElasticFacet facet : facets) {
			if (getFilterMode(facet).equals(FilterMode.ALL_OF)) {
				filters.add(allTerms(facet, terms.get(facet)));
			} else
				filters.add(terms(facet, terms.get(facet)));
		}
		return and(filters);
	}

	public FilterBuilder filterMode(final ElasticFacet facet, final FilterMode filterMode) {
		checkNotNull(facet);
		checkNotNull(filterMode);
		filterModes.put(facet, filterMode.name());
		return this;
	}

	private FilterMode getFilterMode(final ElasticFacet facet) {
		if (filterModes.containsKey(facet))
			return FilterMode.valueOf(filterModes.get(facet));
		else
			return FilterMode.ANY_OF;
	}

	public boolean hasFilterFor(final ElasticFacet facet) {
		if (terms.isEmpty())
			return false;
		final Set<ElasticFacet> keys = terms.keySet();
		return !(keys.size() == 1 && keys.contains(facet));
	}

	public boolean isFiltered() {
		return !terms.isEmpty();
	}

	@Override
	public String toString() {
		return buildFilterString();
	}

	public FilterBuilder withTerm(final ElasticFacet facet, final String term) {
		checkNotNull(facet);
		checkNotNull(term);
		terms.put(facet, term);
		return this;
	}

}