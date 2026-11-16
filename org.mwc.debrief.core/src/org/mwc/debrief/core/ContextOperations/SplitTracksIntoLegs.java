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

package org.mwc.debrief.core.ContextOperations;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class SplitTracksIntoLegs implements RightClickContextItemGenerator {

	/**
	 * remember which leg each fix was in before the split, so undo can restore the
	 * legs exactly
	 *
	 * @param track the track about to be split
	 * @return the index of the original leg for each fix
	 */
	private static Map<FixWrapper, Integer> originalLegsOf(final TrackWrapper track) {
		final Map<FixWrapper, Integer> res = new HashMap<FixWrapper, Integer>();
		final Enumeration<Editable> segs = track.getSegments().elements();
		int legIndex = 0;
		while (segs.hasMoreElements()) {
			final TrackSegment seg = (TrackSegment) segs.nextElement();
			final Enumeration<Editable> fixes = seg.elements();
			while (fixes.hasMoreElements()) {
				res.put((FixWrapper) fixes.nextElement(), legIndex);
			}
			legIndex++;
		}
		return res;
	}

	/**
	 * undo a split: merge the split segments back together, keeping fixes that
	 * came from different legs in different legs
	 *
	 * @param track        the track that was split
	 * @param splits       the segments created by the split
	 * @param originalLegs which leg each fix was in before the split
	 * @return how many segments were merged away
	 */
	private static int mergeBack(final TrackWrapper track, final List<TrackSegment> splits,
			final Map<FixWrapper, Integer> originalLegs) {
		// this track may not have been split
		if (splits == null || splits.isEmpty()) {
			return 0;
		}

		final SegmentList existingSegments = track.getSegments();

		// the segment we're merging into, for each original leg
		final Map<Integer, TrackSegment> targets = new HashMap<Integer, TrackSegment>();

		int ctr = 0;
		for (final TrackSegment segment : splits) {
			// check this is an existing segment for this track
			// if we've performed several split/merge operations
			// the list may now be out of sync
			if (segment.isEmpty() || !existingSegments.contains(segment)) {
				continue;
			}

			final FixWrapper firstFix = (FixWrapper) segment.elements().nextElement();
			final Integer leg = originalLegs == null ? null : originalLegs.get(firstFix);
			final Integer key = leg == null ? -1 : leg;
			final TrackSegment target = targets.get(key);
			if (target == null) {
				// first segment for this leg, merge the others into it
				targets.put(key, segment);
			} else {
				// remove the segment
				track.removeElement(segment);

				final Enumeration<Editable> fixes = segment.elements();
				while (fixes.hasMoreElements()) {
					final FixWrapper fix = (FixWrapper) fixes.nextElement();
					target.addFix(fix);
				}
				ctr++;
			}
		}

		// adding fixes may have changed the start times we sort legs by
		existingSegments.resortIfNeeded();

		return ctr;
	}

	static class SplitTracksOperation extends CMAPOperation {

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final List<TrackWrapper> _tracks;
		private final Long _period;
		final HashMap<TrackWrapper, List<TrackSegment>> _trackChanges;
		private final HashMap<TrackWrapper, Map<FixWrapper, Integer>> _originalLegs = new HashMap<TrackWrapper, Map<FixWrapper, Integer>>();

		public SplitTracksOperation(final String title, final Layers theLayers, final List<TrackWrapper> tracks,
				final Long period) {
			super(title);
			_layers = theLayers;
			_tracks = tracks;
			_period = period;
			_trackChanges = new HashMap<TrackWrapper, List<TrackSegment>>();
		}

		@Override
		public boolean canRedo() {
			return true;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			boolean modified = false;
			_trackChanges.clear();
			_originalLegs.clear();

			// loop through the tracks
			for (final TrackWrapper track : _tracks) {
				_originalLegs.put(track, originalLegsOf(track));
				final List<TrackSegment> newSegments = TrackWrapper_Support.splitTrackAtJumps(track, _period);
				modified = modified || !newSegments.isEmpty();
				_trackChanges.put(track, newSegments);
			}

			// did anything get changed
			if (modified) {
				fireModified();
			}
			return Status.OK_STATUS;
		}

		private void fireModified() {
			_layers.fireExtended();
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			int numChanges = 0;

			// ok, merge the segments
			for (final TrackWrapper track : _trackChanges.keySet()) {
				numChanges += mergeBack(track, _trackChanges.get(track), _originalLegs.get(track));
			}

			final boolean modified = numChanges > 0;

			// did anything get changed
			if (modified) {
				fireModified();
			}
			return Status.OK_STATUS;
		}
	}


	static class SpatialSplitTracksOperation extends CMAPOperation {

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final List<TrackWrapper> _tracks;
		private final double _factor;
		final HashMap<TrackWrapper, List<TrackSegment>> _trackChanges;
		private final HashMap<TrackWrapper, Map<FixWrapper, Integer>> _originalLegs = new HashMap<TrackWrapper, Map<FixWrapper, Integer>>();

		public SpatialSplitTracksOperation(final String title, final Layers theLayers, final List<TrackWrapper> tracks,
				final double factor) {
			super(title);
			_layers = theLayers;
			_tracks = tracks;
			_factor = factor;
			_trackChanges = new HashMap<TrackWrapper, List<TrackSegment>>();
		}

		@Override
		public boolean canRedo() {
			return true;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			boolean modified = false;
			_trackChanges.clear();
			_originalLegs.clear();

			// loop through the tracks
			for (final TrackWrapper track : _tracks) {
				_originalLegs.put(track, originalLegsOf(track));
				final List<TrackSegment> newSegments = TrackWrapper_Support.splitTrackAtSpatialJumps(track, _factor);
				modified = modified || !newSegments.isEmpty();
				_trackChanges.put(track, newSegments);
			}

			// did anything get changed
			if (modified) {
				fireModified();
			}
			return Status.OK_STATUS;
		}

		private void fireModified() {
			_layers.fireExtended();
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			int numChanges = 0;

			// ok, merge the segments
			for (final TrackWrapper track : _trackChanges.keySet()) {
				numChanges += mergeBack(track, _trackChanges.get(track), _originalLegs.get(track));
			}

			final boolean modified = numChanges > 0;

			// did anything get changed
			if (modified) {
				fireModified();
			}
			return Status.OK_STATUS;
		}
	}

	@Override
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects) {
		boolean goForIt = true;

		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();

		// we're only going to work with one or more items
		if (subjects.length > 0) {
			// are they tracks?
			for (int i = 0; i < subjects.length; i++) {
				final Editable thisE = subjects[i];
				if (thisE instanceof TrackWrapper) {
					goForIt = true;
					tracks.add((TrackWrapper) thisE);
				} else {
					goForIt = false;
				}
			}
		}

		// check we got more than one to group
		if (tracks.size() < 1)
			goForIt = false;

		// ok, is it worth going for?
		if (goForIt) {
			// right,stick in a separator
			parent.add(new Separator());

			final String msg = tracks.size() > 1 ? "tracks" : "track";

			final String fullMsg1 = "Split " + msg + " into segments on gaps over...";

			final MenuManager listing1 = new MenuManager(fullMsg1);

			final HashMap<Long, String> choices = new HashMap<Long, String>();
			choices.put(10 * 1000L, "10 Seconds");
			choices.put(60 * 1000L, "1 Minute");
			choices.put(60 * 60 * 1000L, "1 Hour");
			choices.put(24 * 60 * 60 * 1000L, "1 Day");
			choices.put(7 * 24 * 60 * 60 * 1000L, "1 Week");

			for (final Long period : choices.keySet()) {
				// get the time period
				final String label = choices.get(period);

				// create this operation
				final Action doMerge = new Action(label) {
					@Override
					public void run() {
						final IUndoableOperation theAction = new SplitTracksOperation(
								"Split tracks at jumps over " + label, theLayers, tracks, period);
						CorePlugin.run(theAction);
					}
				};
				listing1.add(doMerge);
			}
			parent.add(listing1);
			
			// now the spatial distance factor
			final String fullMsg2 = "Split " + msg + " into segments where distance increases by factor of ...";

			final MenuManager listing2 = new MenuManager(fullMsg2);

			final long factors[] = {2, 3, 5, 10, 20};
			for (final Long factor : factors) {

				// create this operation
				final Action doMerge = new Action("" + factor) {
					@Override
					public void run() {
						final IUndoableOperation theAction = new SpatialSplitTracksOperation(
								"Split tracks where distance increases by factor of " + factor, theLayers, tracks, factor);
						CorePlugin.run(theAction);
					}
				};
				listing2.add(doMerge);
			}
			parent.add(listing2);
		}
	}
}
