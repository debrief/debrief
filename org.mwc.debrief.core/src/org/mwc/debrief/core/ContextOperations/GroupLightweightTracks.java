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
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class GroupLightweightTracks implements RightClickContextItemGenerator {

	static class GroupLightTracksOperation extends CMAPOperation {

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Editable[] _subjects;
		private final LightweightTrackWrapper _target;

		public GroupLightTracksOperation(final String title, final LightweightTrackWrapper target,
				final Layers theLayers, final Editable[] subjects) {
			super(title);
			_target = target;
			_layers = theLayers;
			_subjects = subjects;
		}

		@Override
		public boolean canRedo() {
			return false;
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			LightweightTrackWrapper.groupTracks(_target, _layers, _subjects);
			fireModified();
			return Status.OK_STATUS;
		}

		private void fireModified() {
			_layers.fireExtended();
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			CorePlugin.logError(IStatus.INFO, "Undo not permitted for merge operation", null);
			return null;
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

		// just check that some are lightweight
		boolean allHeavyweight = true;

		final List<LightweightTrackWrapper> tracks = new ArrayList<LightweightTrackWrapper>();

		// we're only going to work with two or more items, and we only put them into a
		// track wrapper
		if (subjects.length > 1) {
			// are they tracks, or track segments
			for (int i = 0; i < subjects.length; i++) {
				final Editable thisE = subjects[i];
				if (thisE instanceof LightweightTrackWrapper) {
					goForIt = true;
					tracks.add((LightweightTrackWrapper) thisE);

					// silly test, just to prevent the menu being shown
					// when they're all actually heavy tracks
					if (!(thisE instanceof TrackWrapper)) {
						allHeavyweight = false;
					}
				}

				if (!goForIt) {
					// may as well drop out - this item wasn't compliant
					continue;
				}
			}
		}

		// check we got some
		if (tracks.size() == 0)
			goForIt = false;

		// ok, is it worth going for?
		if (goForIt && !allHeavyweight) {
			// right,stick in a separator
			parent.add(new Separator());

			// put the tracks into chronological order
			Collections.sort(tracks, new Comparator<LightweightTrackWrapper>() {

				@Override
				public int compare(final LightweightTrackWrapper o1, final LightweightTrackWrapper o2) {
					return o1.getStartDTG().compareTo(o2.getStartDTG());
				}
			});

			// find the first track
			final LightweightTrackWrapper editable = tracks.get(0);
			final String title = "Group lightweight tracks into " + editable.getName();
			// create this operation
			final Action doMerge = new Action(title) {
				@Override
				public void run() {
					final IUndoableOperation theAction = new GroupLightTracksOperation(title, editable, theLayers,
							subjects);
					CorePlugin.run(theAction);
				}
			};
			parent.add(doMerge);
		}
	}
}
