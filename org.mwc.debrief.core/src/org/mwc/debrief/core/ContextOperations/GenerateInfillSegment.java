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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

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
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class GenerateInfillSegment implements RightClickContextItemGenerator {

	public static class GenerateInfillOperation extends CMAPOperation {

		public static final String INSUFFICIENT_POINTS = "Legs must include at least two points for dynamic infill to be generated";
		public static final String INSUFFICIENT_TIME = "Insufficient time to insert data. Please try deleting some points.";
		public static final String OVERLAPPING = "Sorry, this operation cannot be performed for overlapping track sections\nPlease delete overlapping data points and try again";
		public static final String CANT_DELETE = "We can't delete points from the following track, there are too few.\nYou must manally delete them from the previous track";

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Layer _parentTrack;
		private final Vector<TrackSegment> _infills;
		private final Editable[] _segments;

		int segCtr = 1;
		private final ErrorLogger _logger;
		private final boolean _canDelete;
		private HashMap<TrackSegment, ArrayList<FixWrapper>> _deletedFixes = null;

		/**
		 *
		 * @param title       What to call this operation
		 * @param segments    the list of segments to join
		 * @param theLayers   the parent layers object
		 * @param parentTrack the parent track
		 * @param logger      error logger
		 * @param canDelete   whether we allow the alg to delete points
		 */
		public GenerateInfillOperation(final String title, final Editable[] segments, final Layers theLayers,
				final Layer parentTrack, final ErrorLogger logger, final boolean canDelete) {
			super(title);
			_segments = segments;
			_layers = theLayers;
			_parentTrack = parentTrack;
			_infills = new Vector<TrackSegment>();
			_logger = logger;
			_canDelete = canDelete;
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
			IStatus res = null;

			// ok, loop through the segments
			for (int i = 0; i < _segments.length - 1; i++) {
				// get the juicy pair we're looking at
				final TrackSegment trackOne = (TrackSegment) _segments[i];
				final TrackSegment trackTwo = (TrackSegment) _segments[i + 1];

				// check they're not infills
				if (trackOne instanceof DynamicInfillSegment || trackTwo instanceof DynamicInfillSegment) {
					// hold on - there's already an infill here. Move along
					continue;
				}

				// join them
				res = fillSegments(trackOne, trackTwo);

				// did it work?
				if (res != null) {
					// ok, see what the problem was
					if (_canDelete && INSUFFICIENT_TIME.equals(res.getMessage())) {
						while (res != null && INSUFFICIENT_TIME.equals(res.getMessage())) {
							// hmm, check the second track has enough points
							if (trackTwo.size() <= 2) {
								// ok, drop out. we can't delete any more

								// can we restore deleted fixes?
								if (_deletedFixes != null) {
									final ArrayList<FixWrapper> thesefixes = _deletedFixes.get(trackTwo);
									if (thesefixes != null) {
										for (final FixWrapper t : thesefixes) {
											trackTwo.add(t);
										}

										// ok, remove this from the undo operation
										_deletedFixes.remove(trackTwo);
									}
								}

								// ok, sort out failure state
								res = new Status(IStatus.ERROR, DebriefPlugin.PLUGIN_NAME, CANT_DELETE, null);

								// output the message
								_logger.logError(res.getSeverity(), res.getMessage(), null);

							} else {
								// ok, delete a point from the second leg
								final FixWrapper firstPoint = (FixWrapper) trackTwo.getData().iterator().next();

								if (_deletedFixes == null) {
									_deletedFixes = new HashMap<TrackSegment, ArrayList<FixWrapper>>();
								}

								// remember this item
								ArrayList<FixWrapper> fixes = _deletedFixes.get(trackTwo);

								if (fixes == null) {
									fixes = new ArrayList<FixWrapper>();
									_deletedFixes.put(trackTwo, fixes);
								}

								fixes.add(firstPoint);

								trackTwo.removeElement(firstPoint);

								res = fillSegments(trackOne, trackTwo);
							}
						}
					} else {
						// output the message
						_logger.logError(res.getSeverity(), res.getMessage(), null);

						// stop processing further infills
						break;
					}
				}
			}

			_layers.fireExtended(null, _parentTrack);

			if (res == null) {
				res = new Status(IStatus.OK, DebriefPlugin.PLUGIN_NAME, "generate infill successful", null);
			}
			return res;
		}

		/**
		 * create a joining infill segment for these two sections
		 *
		 * @param trackOne
		 * @param trackTwo
		 * @return null for ok, status message for fail
		 */
		private IStatus fillSegments(final TrackSegment trackOne, final TrackSegment trackTwo) {
			IStatus res = null;
			// now do the more detailed checks
			if (trackOne.endDTG().greaterThan(trackTwo.startDTG())) {
				// fail, they overlap
				res = new Status(IStatus.ERROR, DebriefPlugin.PLUGIN_NAME, OVERLAPPING, null);
			} else if (trackOne.size() < 2 || trackTwo.size() < 2) {
				// fail, too few points
				res = new Status(IStatus.ERROR, DebriefPlugin.PLUGIN_NAME, INSUFFICIENT_POINTS, null);
			} else {
				// cool, go for it
				// generate the new track segment
				final TrackSegment newSeg = new DynamicInfillSegment(trackOne, trackTwo);

				// aah, but, no but, are there points in the segment
				if (!newSeg.getData().isEmpty()) {
					storeSegment(newSeg);
				} else {
					res = new Status(IStatus.ERROR, DebriefPlugin.PLUGIN_NAME, INSUFFICIENT_TIME, null);

				}
			}

			return res;
		}

		private void storeSegment(final TrackSegment newSeg) {
			// correct the name
			newSeg.setName(newSeg.getName() + "_" + segCtr++);

			// add the track segment to the parent track
			_parentTrack.add(newSeg);

			// and remember it
			_infills.add(newSeg);
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			final Iterator<TrackSegment> iter = _infills.iterator();
			while (iter.hasNext()) {
				final TrackSegment thisSeg = iter.next();

				// right, just delete our new track segments
				_parentTrack.removeElement(thisSeg);
			}

			// and clera the infills
			_infills.clear();

			// and reinstore deleted fixes
			if (_deletedFixes != null) {
				for (final TrackSegment t : _deletedFixes.keySet()) {
					final ArrayList<FixWrapper> fixes = _deletedFixes.get(t);
					for (final FixWrapper f : fixes) {
						t.add(f);
					}
				}
			}

			// cool, tell everyone
			_layers.fireExtended(null, _parentTrack);

			// register success
			return new Status(IStatus.OK, DebriefPlugin.PLUGIN_NAME, "ditch infill successful", null);
		}
	}

	static final String DELETE_SUFFIX = " (delete from 2nd track if necessary)";

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	@Override
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects) {
		// we're only going to work with two or more items
		if (subjects.length > 1) {
			// track the parents
			final Layer firstParent = parentLayers[0];

			// is it a track?
			if (firstParent instanceof TrackWrapper) {
				final TrackWrapper parentTrack = (TrackWrapper) firstParent;

				// do they have the same parent layer?
				if (parentLayers[1] == parentLayers[0]) {
					// check what's selected, see if they are suitable
					boolean canDo = true;
					for (int i = 0; i < subjects.length; i++) {
						final Editable editable = subjects[i];
						if (editable instanceof TrackSegment) {
							canDo = !((TrackSegment)editable).isEmpty();
						} else {
							canDo = false;
						}
					}

					// ok, is it worth going for?
					if (canDo) {
						String title;

						// see if there are more than one segment to be generated
						if (subjects.length > 2) {
							title = "Generate infill segments";
						} else {
							title = "Generate infill segment";
						}

						final String finalTitle = title;

						final ErrorLogger logger = getLogger();

						// create this operation
						final Action doMerge = new Action(title) {
							@Override
							public void run() {
								final IUndoableOperation theAction = new GenerateInfillOperation(finalTitle, subjects,
										theLayers, parentTrack, logger, false);

								CorePlugin.run(theAction);
							}
						};
						doMerge.setImageDescriptor(CorePlugin.getImageDescriptor("icons/16/generate_infill.png"));
						final String secondTitle = finalTitle + DELETE_SUFFIX;
						final Action doMergeAllowDelete = new Action(secondTitle) {
							@Override
							public void run() {
								final IUndoableOperation theAction = new GenerateInfillOperation(secondTitle, subjects,
										theLayers, parentTrack, logger, true);

								CorePlugin.run(theAction);
							}
						};
						doMergeAllowDelete.setImageDescriptor(
								CorePlugin.getImageDescriptor("icons/16/generate_infill_delete.png"));
						parent.add(new Separator());
						parent.add(doMerge);
						parent.add(doMergeAllowDelete);
					}
				}
			}
		}

	}

	/**
	 * provide an error logger
	 *
	 * @return
	 */
	protected ErrorLogger getLogger() {
		return new ErrorLogger() {

			@Override
			public void logError(final int status, final String text, final Exception e) {
				CorePlugin.showMessage("Generate infill", text);
			}

			@Override
			public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
				logError(status, text, e);
			}

			@Override
			public void logStack(final int status, final String text) {
				logError(status, text, null);
			}
		};
	}
}
