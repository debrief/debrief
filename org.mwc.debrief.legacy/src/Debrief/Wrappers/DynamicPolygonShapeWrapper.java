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

package Debrief.Wrappers;

import java.awt.Color;

import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GenericData.HiResDate;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class DynamicPolygonShapeWrapper extends PolygonWrapper implements IDynamicShapeWrapper {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String _theTrackName;

	public DynamicPolygonShapeWrapper(final String label, final PolygonShape shape, final Color theColor,
			final HiResDate startDate, final HiResDate endDate) {
		super(label, shape, theColor, startDate, endDate);
	}

	/**
	 * override the default sort order (name), since we wish to sort by DTG
	 */
	@Override
	public int compareTo(final Plottable o) {
		final int res;

		// the name check logic is quite complex
		final int nameCheck = this.getName().compareTo(o.getName());

		// if we can provide order, provide it. Otherwise give false order
		final int nameRes = nameCheck == 0 ? 1 : nameCheck;

		if (o == this) {
			// same object. duh.
			res = 0;
		} else if (o instanceof PolygonWrapper) {
			final PolygonWrapper shape = (PolygonWrapper) o;

			final HiResDate myStart = this.getStartDTG();
			// do I know my date?
			if (myStart != null) {
				final HiResDate hisStart = shape.getStartDTG();

				// do we know his date?
				if (hisStart != null) {
					// compare dates
					final int dateCompare = myStart.compareTo(shape.getStartDTG());
					if (dateCompare == 0) {
						res = nameRes;
					} else {
						res = dateCompare;
					}
				} else {
					// put me first
					res = -1;
				}
			} else {
				// compare names
				res = nameRes;
			}
		} else {
			res = nameRes;
		}
		return res;
	}

	@Override
	public String getTrackName() {
		return _theTrackName;
	}

	@Override
	public void setTrackName(final String trackName) {
		this._theTrackName = trackName;
	}

}
