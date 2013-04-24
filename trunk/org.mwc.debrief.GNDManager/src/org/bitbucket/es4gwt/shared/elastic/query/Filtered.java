package org.bitbucket.es4gwt.shared.elastic.query;

import static com.google.common.base.Preconditions.checkNotNull;

import org.bitbucket.es4gwt.shared.elastic.filter.ElasticFilter;

/**
 * Smart "filtered" query that behaves as its child query if it is passed a null filter
 * 
 * @author Mikael Couzic
 */
class Filtered implements ElasticQuery {

	private final ElasticQuery query;
	private final ElasticFilter filter;

	Filtered(ElasticQuery query, ElasticFilter filter) {
		checkNotNull(query);
		this.query = query;
		this.filter = filter;
	}

	@Override
	public String toRequestString() {
		if (filter == null) // No need for "filtered" query, just return the child query
			return query.toRequestString();
		else
			return "{\"filtered\":{\"query\":" + query.toRequestString()
					+ ",\"filter\":"
					+ filter.toRequestString()
					+ "}}";
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
