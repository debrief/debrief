package org.bitbucket.es4gwt.shared.elastic;

import org.bitbucket.es4gwt.shared.elastic.filter.FilterBuilder;

import com.google.common.base.Preconditions;

/**
 * @author Mikael Couzic
 */
public class SearchFacets {

	private final ElasticFacet[] facets;

	public SearchFacets(ElasticFacet[] facets) {
		Preconditions.checkNotNull(facets);
		this.facets = facets;
	}

	public String facets(FilterBuilder filterBuilder) {
		StringBuilder sb = new StringBuilder("\"facets\":{");
		for (int i = 0; i < facets.length; i++) {
			sb.append(new SearchFacet(facets[i], filterBuilder.buildFilterOrNullFor(facets[i])).toRequestString());
			if (i + 1 < facets.length)
				sb.append(",");
		}
		return sb.append("}").toString();
	}

}
