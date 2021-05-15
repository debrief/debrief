/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2021, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package Debrief.Wrappers.Track;

import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Plottables;
import MWC.GUI.Plottables.IteratorWrapper;
import MWC.GenericData.HiResDate;

public class FixWrapperCollisionCheck {

	/**
	 * We are creating this method because the following ticket
	 * https://github.com/debrief/debrief/issues/4894
	 *
	 * FixWrapper were initially adding with a +1 time delay to avoid collisions.
	 *
	 * Collision check modifies the time in the fixWrapper given to adapt it to
	 * avoid collision.
	 *
	 * @param _fixWrapper       FixWrapper to modify
	 * @param _currentPlotables List of all the Editables
	 */
	public static void correctTimeCollision(final FixWrapper _fixWrapper, final Plottables _currentPlotables) {
		long timeToAdd = _fixWrapper.getTime().getMicros();

		// First check if the exact instance is the list.
		if (_currentPlotables.contains(_fixWrapper)) {
			final Enumeration<Editable> elementsEnum = new IteratorWrapper(
					_currentPlotables.subSet(_fixWrapper, true, _fixWrapper, true).iterator());
			while (elementsEnum.hasMoreElements()) {
				final Editable element = elementsEnum.nextElement();
				if (element == _fixWrapper) {
					// We have found it.
					return;
				}
			}
		}

		while (_currentPlotables.contains(_fixWrapper)) {
			timeToAdd += 1000; // add 1 millisecond.
			_fixWrapper.getFix().setTime(new HiResDate(timeToAdd / 1000L));
		}
	}
}
