/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.bitbucket.es4gwt.shared.elastic.filter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Mikael Couzic
 */
class StartBefore implements ElasticFilter {

	private final String date;

	/**
	 * @param dateString
	 *            The formatted date (for example : 2001-01-01 or 2011-12-31)
	 */
	public StartBefore(String date) {
		checkNotNull(date);
		this.date = date; // + "T23:59:59";
	}

	@Override
	public String toRequestString() {
		return "{\"range\":{\"start\":{\"lte\":\"" + date + "\"}}}";
	}

	@Override
	public String toString() {
		return toRequestString();
	}

}
