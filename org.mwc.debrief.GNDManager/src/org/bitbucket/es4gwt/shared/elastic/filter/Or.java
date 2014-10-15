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

import java.util.Collection;

/**
 * Smart "or" filter. If it is passed only one filter, it will behave as if it was that filter
 * 
 * @author Mikael Couzic
 */
class Or extends BooleanOperator {

	Or(Collection<ElasticFilter> filters) {
		super(filters);
	}

	@Override
	String getOperatorName() {
		return "or";
	}

}
