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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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
