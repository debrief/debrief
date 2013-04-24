package org.bitbucket.es4gwt.shared.elastic.query;

import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.dateRange;

import org.bitbucket.es4gwt.shared.elastic.filter.ElasticFilter;

/**
 * @author Mikael Couzic
 */
public class Queries {

	private Queries() {
	}

	public static ElasticQuery matchAll() {
		return new MatchAll();
	}

	public static ElasticQuery field(String fieldName, String fieldValue) {
		return new Field(fieldName, fieldValue);
	}

	public static ElasticQuery filtered(ElasticQuery query, ElasticFilter filter) {
		return new Filtered(query, filter);
	}

	public static ElasticQuery dateRangeFiltered(String early, String late) {
		return filtered(matchAll(), dateRange(early, late));
	}

}
