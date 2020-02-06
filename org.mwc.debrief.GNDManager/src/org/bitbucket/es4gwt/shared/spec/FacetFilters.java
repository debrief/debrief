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

package org.bitbucket.es4gwt.shared.spec;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Mikael Couzic
 */
class FacetFilters implements Iterable<Entry<ElasticFacet, Collection<String>>> {

	private final Multimap<ElasticFacet, String> filters = ArrayListMultimap.create();

	public Multimap<ElasticFacet, String> asMultimap() {
		return filters;
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof FacetFilters) {
			final FacetFilters that = (FacetFilters) object;
			return this.filters.equals(that.filters);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return filters.hashCode();
	}

	boolean isEmpty() {
		return filters.isEmpty();
	}

	@Override
	public Iterator<Entry<ElasticFacet, Collection<String>>> iterator() {
		return filters.asMap().entrySet().iterator();
	}

	void with(final ElasticFacet facet, final String filterValue) {
		filters.put(facet, filterValue);
	}

}
