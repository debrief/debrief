package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.spec.FilterMode;

import com.google.common.collect.ImmutableSet;

/**
 * Smart "terms" filter. If it is passed only one value, it will behave as if it was a "term" (single) filter
 * 
 * @author Mikael Couzic
 */

class Terms implements ElasticFilter {

	private final ElasticFacet facet;
	private final Set<String> facetValues;
	private final FilterMode filterMode;

	Terms(ElasticFacet facet, Collection<String> facetValues) {
		this(facet, facetValues, FilterMode.ANY_OF);
	}

	Terms(ElasticFacet facet, Collection<String> facetValues, FilterMode filterMode) {
		checkNotNull(facet);
		checkNotNull(facetValues);
		checkArgument(!facetValues.isEmpty(), "Empty collection");
		this.facet = facet;
		this.facetValues = ImmutableSet.copyOf(facetValues);
		this.filterMode = filterMode;
	}

	@Override
	public String toRequestString() {
		if (facetValues.size() == 1) // No need for plural "terms", just return a singular "term"
			return new Term(facet, facetValues.iterator().next()).toRequestString();

		StringBuilder sb = new StringBuilder("{\"terms\":{\"" + facet.toRequestString() + "\":[");
		for (Iterator<String> i = facetValues.iterator(); i.hasNext();) {
			sb.append("\"" + i.next() + "\"");
			if (i.hasNext())
				sb.append(",");
		}
		sb.append("]");
		if (filterMode.equals(FilterMode.ALL_OF))
			sb.append(",\"execution\":\"and\"");
		return sb.append("}}").toString();
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
