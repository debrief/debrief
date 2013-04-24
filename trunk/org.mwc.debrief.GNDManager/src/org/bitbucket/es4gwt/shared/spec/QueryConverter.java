package org.bitbucket.es4gwt.shared.spec;

import org.bitbucket.es4gwt.shared.elastic.query.ElasticQuery;
import org.bitbucket.es4gwt.shared.elastic.query.QueryBuilder;

/**
 * @author Mikael Couzic
 */
class QueryConverter {

	static QueryBuilder toElasticQueryBuilder(SearchRequest spec) {
		QueryBuilder builder = new QueryBuilder();
		if (spec.isTextDefined())
			builder.searchText(spec.getText(), spec.getTextSearchParams());
		if (spec.isStartDateDefined())
			builder.after(spec.getStartDate().toString());
		if (spec.isEndDateDefined())
			builder.before(spec.getEndDate().toString());
		return builder;
	}

	static ElasticQuery toElasticQuery(SearchRequest spec) {
		return toElasticQueryBuilder(spec).buildQuery();
	}
}
