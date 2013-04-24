package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkNotNull;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;

/**
 * @author Mikael Couzic
 */

class Term implements ElasticFilter {

	private final ElasticFacet facet;
	private final String facetValue;

	Term(ElasticFacet facet, String facetValue) {
		checkNotNull(facet);
		checkNotNull(facetValue);
		this.facet = facet;
		this.facetValue = facetValue;
	}

	@Override
	public String toRequestString() {
		return "{\"term\":{\"" + facet.toRequestString() + "\":\"" + facetValue + "\"}}";
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
