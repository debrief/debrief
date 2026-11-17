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
import java.util.ArrayList;
import java.util.List;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldArea;

public class SliceTrackFormatListener extends PlainWrapper implements INewItemListener {

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	final public class TrackNameInfo extends Editable.EditorType implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public TrackNameInfo(final SliceTrackFormatListener data) {
			super(data, data.getName(), "");
		}

		@Override
		final public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { displayProp("Name", "Name", "Name for this formatter"),
						displayProp("Interval", "Interval", "Interval to split on"),
						displayProp("Visible", "Active", "Whether this formatter is active"), };

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
	private Duration _interval;

	private final List<String> _trackNames;
	private final List<TrackWrapper> _tracksToProcess;

	public SliceTrackFormatListener(final String name, final long interval, final List<String> trackNames) {
		_formatName = name;
		_interval = new Duration(interval, Duration.MILLISECONDS);
		_trackNames = trackNames;
		_tracksToProcess = new ArrayList<TrackWrapper>();
	}

	@Override
	public void fileComplete() {
		for (final TrackWrapper track : _tracksToProcess) {
			TrackWrapper_Support.splitTrackAtJumps(track, _interval.getMillis());
		}
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

	public Duration getInterval() {
		return _interval;
	}

	public long getIntervalMillis() {
		return _interval.getMillis();
	}

	@Override
	public String getName() {
		return _formatName;
	}

	public List<String> getTrackNames() {
		return _trackNames;
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
			boolean addIt = false;
			final TrackWrapper track = (TrackWrapper) parent;
			// do we have any track names?
			if (_trackNames == null || _trackNames.isEmpty()) {
				// ok, we cam just use it
				addIt = true;
			} else {
				// check if it's one of our names
				for (final String name : _trackNames) {
					if (name.equals(track.getName())) {
						addIt = true;
						break;
					}
				}
			}

			if (addIt) {
				if (!_tracksToProcess.contains(track)) {
					_tracksToProcess.add(track);
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

	public void setInterval(final Duration interval) {
		this._interval = interval;
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