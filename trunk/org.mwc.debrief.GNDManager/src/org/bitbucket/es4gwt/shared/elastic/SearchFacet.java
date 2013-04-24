package org.bitbucket.es4gwt.shared.elastic;

import org.bitbucket.es4gwt.shared.elastic.filter.ElasticFilter;

/**
 * @author Mikael Couzic
 */
public class SearchFacet implements ElasticRequestElement {

	private static final int DEFAULT_NUM_RESULTS = 1000;

	private final ElasticFacet facet;
	private final ElasticFilter filter;

	public SearchFacet(ElasticFacet facet, ElasticFilter filter) {
		this.facet = facet;
		this.filter = filter;
	}

	@Override
	public String toRequestString() {
		// TODO Replace the use of the DEFAULT_NUM_RESULTS constant by a variable
		StringBuilder sb = new StringBuilder("\"" + facet
												+ "\":{\"terms\":{\"field\":\""
												+ facet
												+ "\",\"size\":"
												+ DEFAULT_NUM_RESULTS
												+ "}");
		if (filter != null)
			sb.append(",\"facet_filter\":" + filter.toRequestString());
		return sb.append("}").toString();
	}
}
