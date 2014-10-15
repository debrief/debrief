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

/**
 * @author Mikael Couzic
 */
public enum FilterMode {

	ANY_OF,
	ALL_OF;

	@Override
	public String toString() {
		switch (this) {
		case ANY_OF:
			return "At least one of... ";
		case ALL_OF:
			return "All of... ";
		default:
			throw new UnsupportedOperationException("toString() method is not supported for FilterMode." + name());
		}
	};

}
