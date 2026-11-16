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
package Debrief.Wrappers.Formatters;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;

public class TrackNameAtEndFormatListener extends PlainWrapper implements INewItemListener {

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	final public class TrackNameInfo extends Editable.EditorType implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public TrackNameInfo(final TrackNameAtEndFormatListener data) {
			super(data, data.getName(), "");
		}

		@Override
		final public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { displayProp("Name", "Name", "Name for this formatter"),
						displayProp("Visible", "Active", "Whether this formatter is active") };

				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String _formatName;
	private EditorType _myEditor;
	private final String[] _tracks;

	public TrackNameAtEndFormatListener(final String name, final String[] tracks) {
		_formatName = name;
		_tracks = tracks;
	}

	@Override
	public void fileComplete() {
	}

	@Override
	public WorldArea getBounds() {
		return null;
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null) {
			_myEditor = new TrackNameInfo(this);
		}
		return _myEditor;
	}

	@Override
	public String getName() {
		return _formatName;
	}

	public String[] getTracks() {
		return _tracks;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	public void newItem(final Layer parent, final Editable item, final String symbology) {
		// are we active
		if (!getVisible()) {
			return;
		}

		// just check if this is actually a new layer call
		if (parent instanceof TrackWrapper) {
			final TrackWrapper track = (TrackWrapper) parent;

			// ok, do we have a set of track names?
			if (_tracks == null || _tracks.length == 0) {
				// nope, just set it
				track.setNameAtStart(false);
			} else {
				// check if this is one of our tracks
				for (int i = 0; i < _tracks.length; i++) {
					final String thisT = _tracks[i];
					if (thisT.equals(track.getName())) {
						track.setNameAtStart(false);
					}
				}
			}
		}
	}

	@Override
	public void paint(final CanvasType dest) {
		// don't bother, it can't be plotted
	}

	@Override
	public void reset() {
		// ignore
	}

	@Override
	public void setName(final String name) {
		_formatName = name;
	}

	@Override
	public String toString() {
		return getName();
	}
}