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

package org.mwc.debrief.lite.gui.custom.graph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;

import Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder;
import Debrief.Wrappers.TrackWrapper;

public class JSelectTrackModel implements AbstractTrackConfiguration {

	public static final String TRACK_SELECTION = "TRACK_STATE_CHANGED";

	public static final String PRIMARY_CHANGED = "PRIMARY_CHANGED";

	public static final String TRACK_LIST_CHANGED = "TRACK_LIST_CHANGED";

	public static final String OPERATION_CHANGED = "OPERATION CHANGED";

	private TrackWrapper _primaryTrack;

	private final List<AbstractSelection<TrackWrapper>> _tracks = new ArrayList<>();

	private CalculationHolder _calculation;

	private final ArrayList<PropertyChangeListener> _stateListeners = new ArrayList<>();

	public JSelectTrackModel(final List<TrackWrapper> tracks, final CalculationHolder calculation) {
		setTracks(tracks);
		_calculation = calculation;
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this._stateListeners.add(listener);
	}

	@Override
	public CalculationHolder getOperation() {
		return _calculation;
	}

	@Override
	public TrackWrapper getPrimaryTrack() {
		return _primaryTrack;
	}

	@Override
	public List<AbstractSelection<TrackWrapper>> getTracks() {
		return _tracks;
	}

	@Override
	public boolean isRelativeEnabled() {
		return this._tracks.size() > 1;
	}

	private void notifyListenersStateChanged(final Object source, final String property, final Object oldValue,
			final Object newValue) {
		for (final PropertyChangeListener event : _stateListeners) {
			event.propertyChange(new PropertyChangeEvent(source, property, oldValue, newValue));
		}
	}

	@Override
	public void setActiveTrack(final TrackWrapper track, final boolean check) {
		// TODO this should be moved to somewhere else.
		Boolean oldValue = null;
		Boolean newValue = null;
		for (final AbstractSelection<TrackWrapper> currentTrack : _tracks) {
			if (currentTrack.getItem().equals(track)) {
				newValue = check;
				oldValue = currentTrack.isSelected();
				currentTrack.setSelected(newValue);
			}
		}

		if (newValue != null && !oldValue.equals(newValue)) {
			// we have the element changed.
			notifyListenersStateChanged(track, TRACK_SELECTION, oldValue, check);
		}
	}

	@Override
	public void setOperation(final CalculationHolder calculation) {
		final CalculationHolder oldCalculation = _calculation;
		this._calculation = calculation;
		if (calculation != null && !calculation.equals(oldCalculation)) {
			notifyListenersStateChanged(this, OPERATION_CHANGED, oldCalculation, calculation);
		}
	}

	@Override
	public void setPrimaryTrack(final TrackWrapper newPrimary) {

		final TrackWrapper oldPrimary = getPrimaryTrack();
		// Do we have it?
		for (final AbstractSelection<TrackWrapper> currentTrack : _tracks) {
			if (currentTrack.getItem().equals(newPrimary)) {
				this._primaryTrack = newPrimary;

				if (!currentTrack.isSelected()) {
					setActiveTrack(newPrimary, true);
				}
				notifyListenersStateChanged(this, PRIMARY_CHANGED, oldPrimary, newPrimary);
				return;
			}
		}
		// New primary track is null or not present in the options.
		_primaryTrack = null;
	}

	/**
	 *
	 * @param tracks Tracks to assign
	 * @return true if it was actually assigned. If they are the same, they are not
	 *         assigned.
	 */
	@Override
	public boolean setTracks(final List<TrackWrapper> tracks) {
		boolean isDifferent = false;

		final List<AbstractSelection<TrackWrapper>> deltaPlus = new ArrayList<>();
		final List<AbstractSelection<TrackWrapper>> deltaMinus = new ArrayList<>();

		final HashSet<TrackWrapper> oldTracksSet = new HashSet<>();
		for (final AbstractSelection<TrackWrapper> oldTrack : _tracks) {
			oldTracksSet.add(oldTrack.getItem());
		}
		for (final TrackWrapper track : tracks) {
			if (!oldTracksSet.contains(track)) {
				isDifferent = true;
				deltaPlus.add(new AbstractSelection<TrackWrapper>(track, false));
			}
		}
		for (final TrackWrapper oldTrackItem : oldTracksSet) {
			if (!tracks.contains(oldTrackItem)) {
				isDifferent = true;
				deltaMinus.add(new AbstractSelection<TrackWrapper>(oldTrackItem, false));
			}
		}
		if (isDifferent) {
			this._tracks.removeAll(deltaMinus);
			this._tracks.addAll(deltaPlus);

			if (_primaryTrack != null && !tracks.contains(_primaryTrack)) {
				setPrimaryTrack(null);
			}
			notifyListenersStateChanged(this, TRACK_LIST_CHANGED, null, tracks);
			if (tracks.size() == 1) {
				final TrackWrapper newPrimary = tracks.get(0);
				setActiveTrack(newPrimary, true);
				notifyListenersStateChanged(newPrimary, TRACK_SELECTION, null, true);
			}
		}
		return isDifferent;
	}
}
