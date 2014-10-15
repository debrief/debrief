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
package de.kupzog.ktable.renderers;

/**
 * Interface that provides access to a percentage value. This is currently only
 * used for the BarDiagramCellRenderer to determine the length of the bar to
 * render.
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public interface IPercentage {

	/**
	 * @return Returns a percentage value between 0 and 1.
	 */
	public float getPercentage();

	/**
	 * @return Returns the absolute value that was responsible for the
	 *         percentage value.
	 */
	public float getAbsoluteValue();
}
