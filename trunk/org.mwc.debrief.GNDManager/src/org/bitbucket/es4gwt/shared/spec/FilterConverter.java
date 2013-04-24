package org.bitbucket.es4gwt.shared.spec;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;
import org.bitbucket.es4gwt.shared.elastic.filter.FilterBuilder;

/**
 * @author Mikael Couzic
 */
public class FilterConverter {

	public static FilterBuilder toElasticFilterBuilder(SearchRequest spec) {
		FilterBuilder builder = new FilterBuilder(spec.getFacetFilters().asMultimap());

		for (ElasticFacet facet : spec.getFilterModeFacets()) {
			builder.filterMode(facet, spec.getFilterMode(facet));
		}
		return builder;
	}
}
