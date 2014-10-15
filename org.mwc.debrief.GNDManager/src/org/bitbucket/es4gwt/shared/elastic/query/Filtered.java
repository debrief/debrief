/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
