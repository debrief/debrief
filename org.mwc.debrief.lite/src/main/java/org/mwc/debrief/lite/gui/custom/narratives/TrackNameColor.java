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

package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Color;

public class TrackNameColor {
	private String _trackName;
	private Color _color;

	public TrackNameColor(final String _trackName, final Color _color) {
		super();
		this._trackName = _trackName;
		this._color = _color;
	}

	public Color getColor() {
		return _color;
	}

	public String getTrackName() {
		return _trackName;
	}

	public void setColor(final Color _color) {
		this._color = _color;
	}

	public void setTrackName(final String _trackName) {
		this._trackName = _trackName;
	}

}
