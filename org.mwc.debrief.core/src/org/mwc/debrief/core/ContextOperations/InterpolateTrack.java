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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.TimeStepPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

/**
 * @author ian.mayo
 */
public class InterpolateTrack implements RightClickContextItemGenerator {

	/**
	 * a track segment, plus the fixes it contained before the operation
	 */
	private static class SegmentContents {
		private final TrackSegment _segment;
		private final List<Editable> _fixes;

		private SegmentContents(final TrackSegment segment) {
			_segment = segment;
			_fixes = Collections.list(segment.elements());
		}
	}

	static class InterpolateTrackOperation extends CMAPOperation {

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;

		/**
		 * list of new fixes we're creating
		 */
		private Vector<FixWrapper> _newFixes;

		/**
		 * the original fixes in each segment, so undo can restore them
		 */
		private List<SegmentContents> _originalFixes;

		/**
		 * the track we're interpolating
		 */
		private final TrackWrapper _track;

		/**
		 * the step to interpolate against
		 */
		private final long _thisIntervalMicros;

		public InterpolateTrackOperation(final String title, final Layers layers, final TrackWrapper track,
				final String thisLabel, final long thisIntervalMicros) {
			super("At " + thisLabel + " interval");
			_layers = layers;
			_track = track;
			_thisIntervalMicros = thisIntervalMicros;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			final long startTime = _track.getStartDTG().getMicros();
			final long endTime = _track.getEndDTG().getMicros();

			// switch on track interpolation
			_track.setInterpolatePoints(true);

			// include the start and end times, so the resampled track covers the same
			// period as the original
			for (long thisTime = startTime; thisTime <= endTime; thisTime += _thisIntervalMicros) {
				// ok, generate the point at this interval
				if (_newFixes == null) {
					_newFixes = new Vector<FixWrapper>(0, 1);
				}

				final Watchable[] matches = _track.getNearestTo(new HiResDate(0, thisTime), false);
				if (matches.length > 0) {
					final FixWrapper interpFix = (FixWrapper) matches[0];

					// make it an normal FixWrapper, not an interpolated one. Copy the fix,
					// since at the start/end we may have been given an original fix
					final FixWrapper newFix = new FixWrapper(interpFix.getFix().makeCopy());

					// tidy the interpolated fix name
					newFix.resetName();

					_newFixes.add(newFix);
				}
			}

			if (_newFixes != null) {
				// remember the original fixes, so we can restore them on undo
				_originalFixes = new ArrayList<SegmentContents>();
				final Enumeration<Editable> segments = _track.getSegments().elements();
				while (segments.hasMoreElements()) {
					_originalFixes.add(new SegmentContents((TrackSegment) segments.nextElement()));
				}

				// the resampled fixes all go in the last segment. Get it now, since
				// the segments get re-sorted as they gain positions
				final TrackSegment target = (TrackSegment) _track.getSegments().last();

				// cool, it worked. clear them all out
				_track.clearPositions();

				// right, now add the fixes
				for (final Iterator<FixWrapper> iter = _newFixes.iterator(); iter.hasNext();) {
					final FixWrapper fix = iter.next();
					target.addFix(fix);
					fix.setTrackWrapper(_track);
				}
				_track.getSegments().resortIfNeeded();

				// we've bypassed the track when adding the fixes, so clear its caches
				_track.flushPeriodCache();
				_track.flushPositionCache();
			}

			// ok, switch off interpolation
			_track.setInterpolatePoints(false);

			// sorted, do the update
			_layers.fireExtended(null, _track);

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			if (_newFixes != null && _originalFixes != null) {
				// ditch the resampled fixes
				_track.clearPositions();

				// and restore the original ones, in their original segments
				for (final SegmentContents contents : _originalFixes) {
					for (final Editable fix : contents._fixes) {
						contents._segment.addFix((FixWrapper) fix);
					}
				}

				// the segments' start times changed while they were in the sorted
				// list, so re-sort it
				_track.getSegments().resortIfNeeded();
			}

			// and clear the new fixes list, ready for any redo
			_newFixes = null;
			_originalFixes = null;

			_layers.fireExtended(null, _track);

			return Status.OK_STATUS;
		}
	}

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	@Override
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects) {
		boolean goForIt = false;

		// we're only going to work with one item
		if (subjects.length == 1) {
			// is it a track?
			final Editable thisE = subjects[0];
			if (thisE instanceof TrackWrapper) {
				goForIt = true;
			}
		}

		// ok, is it worth going for?
		if (goForIt) {
			final String title = "Resample position data";

			// right,stick in a separator
			parent.add(new Separator());

			// and the new drop-down list of interpolation frequencies
			final MenuManager newMenu = new MenuManager(title);
			parent.add(newMenu);

			// ok, loop through the time steps, creating an
			// action for each one
			final TimeStepPropertyEditor pe = new TimeStepPropertyEditor();
			final String[] tags = pe.getTags();
			for (int i = 0; i < tags.length; i++) {
				final String thisLabel = tags[i];
				pe.setAsText(thisLabel);
				final Long thisIntLong = (Long) pe.getValue();
				final long thisIntervalMillis = thisIntLong.longValue();

				// yes, create the action
				final Action convertToTrack = new Action("At " + thisLabel + " interval") {
					@Override
					public void run() {
						// ok, go for it.
						// sort it out as an operation
						final IUndoableOperation convertToTrack1 = new InterpolateTrackOperation(title, theLayers,
								(TrackWrapper) subjects[0], thisLabel, thisIntervalMillis);

						// ok, stick it on the buffer
						CorePlugin.run(convertToTrack1);
					}
				};

				newMenu.add(convertToTrack);
			}
		}

	}
}
