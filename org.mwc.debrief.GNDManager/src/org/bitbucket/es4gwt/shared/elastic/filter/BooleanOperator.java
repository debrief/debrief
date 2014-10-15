/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @author Mikael Couzic
 */

abstract class BooleanOperator implements ElasticFilter {

	private final Set<ElasticFilter> filters;

	BooleanOperator(Collection<ElasticFilter> filters) {
		checkNotNull(filters);
		checkArgument(!filters.isEmpty(), "Empty collection");
		this.filters = ImmutableSet.copyOf(filters);
	}

	@Override
	public String toString() {
		return getClass().getName() + " : " + filters;
	}

	@Override
	public String toRequestString() {
		if (filters.size() == 1) // No need for boolean filter, just return the single filter
			return filters.iterator().next().toRequestString();

		StringBuilder sb = new StringBuilder().append("{\"" + getOperatorName() + "\":[");
		for (Iterator<ElasticFilter> i = filters.iterator(); i.hasNext();) {
			sb.append(i.next().toRequestString());
			if (i.hasNext())
				sb.append(",");
		}
		sb.append("]}");
		return sb.toString();
	}

	abstract String getOperatorName();

}