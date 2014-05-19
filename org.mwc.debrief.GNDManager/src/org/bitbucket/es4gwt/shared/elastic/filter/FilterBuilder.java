package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.*;
import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.*;

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

	public FilterBuilder(Multimap<ElasticFacet, String> terms) {
		this.terms = terms;
	}

	public FilterBuilder withTerm(ElasticFacet facet, String term) {
		checkNotNull(facet);
		checkNotNull(term);
		terms.put(facet, term);
		return this;
	}

	public FilterBuilder filterMode(ElasticFacet facet, FilterMode filterMode) {
		checkNotNull(facet);
		checkNotNull(filterMode);
		filterModes.put(facet, filterMode.name());
		return this;
	}

	public boolean isFiltered() {
		return !terms.isEmpty();
	}

	public ElasticFilter buildFilter() {
		return createFilter(terms.keySet());
	}

	public ElasticFilter buildFilterFor(ElasticFacet facet) {
		Set<ElasticFacet> otherFacets = Sets.newHashSet(terms.keySet());
		otherFacets.remove(facet);
		return createFilter(otherFacets);
	}

	public ElasticFilter buildFilterOrNullFor(ElasticFacet facet) {
		if (!hasFilterFor(facet))
			return null;
		else
			return buildFilterFor(facet);
	}

	private ElasticFilter createFilter(Set<ElasticFacet> facets) {
		checkArgument(!facets.isEmpty(), "Empty Set");
		Collection<ElasticFilter> filters = Lists.newArrayList();
		for (ElasticFacet facet : facets) {
			if (getFilterMode(facet).equals(FilterMode.ALL_OF)) {
				filters.add(allTerms(facet, terms.get(facet)));
			} else
				filters.add(terms(facet, terms.get(facet)));
		}
		return and(filters);
	}

	private FilterMode getFilterMode(ElasticFacet facet) {
		if (filterModes.containsKey(facet))
			return FilterMode.valueOf(filterModes.get(facet));
		else
			return FilterMode.ANY_OF;
	}

	public boolean hasFilterFor(ElasticFacet facet) {
		if (terms.isEmpty())
			return false;
		Set<ElasticFacet> keys = terms.keySet();
		return !(keys.size() == 1 && keys.contains(facet));
	}

	public String buildFilterString() {
		return buildFilter().toRequestString();
	}

	public String buildFilterStringFor(ElasticFacet facet) {
		return buildFilterFor(facet).toRequestString();
	}

	@Override
	public String toString() {
		return buildFilterString();
	}

}