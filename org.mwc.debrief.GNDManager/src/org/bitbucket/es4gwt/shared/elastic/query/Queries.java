/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.bitbucket.es4gwt.shared.elastic.query;

import static org.bitbucket.es4gwt.shared.elastic.filter.Filters.dateRange;

import org.bitbucket.es4gwt.shared.elastic.filter.ElasticFilter;

/**
 * @author Mikael Couzic
 */
public class Queries {

	public static ElasticQuery dateRangeFiltered(final String early, final String late) {
		return filtered(matchAll(), dateRange(early, late));
	}

	public static ElasticQuery field(final String fieldName, final String fieldValue) {
		return new Field(fieldName, fieldValue);
	}

	public static ElasticQuery filtered(final ElasticQuery query, final ElasticFilter filter) {
		return new Filtered(query, filter);
	}

	public static ElasticQuery matchAll() {
		return new MatchAll();
	}

	private Queries() {
	}

}
