/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
