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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * @author ian.mayo
 *
 */
public class ConvertAbsoluteTmaToRelative implements RightClickContextItemGenerator {

	private static class ConvertToRelativeOperation extends CMAPOperation {

		protected static RelativeTMASegment createSegment(final Layers layers, final SuitableSegment seg,
				final TrackWrapper track, final AbsoluteTMASegment absSegment, final SensorWrapper sensor) {
			final HiResDate startTime = absSegment.getDTG_Start();

			// sort out the offset
			final WorldLocation sensorOrigin = sensor.getArrayCentre(startTime, null, track);

			// here is the previous version, which only worked for legacy array mode types
			// final WorldLocation sensorOrigin =
			// track.getBacktraceTo(absSegment.getDTG_Start(),
			// sensor.getSensorOffset(), sensor.getWormInHole()).getLocation();

			final WorldLocation sOrigin = absSegment.getTrackStart();

			final WorldVector offset = sOrigin.subtract(sensorOrigin);

			// create the relative segment
			final HiResDate endTime = absSegment.getDTG_End();
			final Collection<Editable> dataPoints = absSegment.getData();
			final RelativeTMASegment rSeg = new RelativeTMASegment(sensor, offset, absSegment.getSpeed(),
					absSegment.getCourse(), dataPoints, layers, track, startTime, endTime);

			return rSeg;
		}

		private final Layers _layers;
		private final List<SuitableSegment> _segments;
		private List<RelativeTMASegment> _replacements;

		private final TrackWrapper _commonParent;

		public ConvertToRelativeOperation(final Layers theLayers, final List<SuitableSegment> suitableSegments,
				final TrackWrapper commonParent) {
			super("Convert absolute segment(s) to relative");
			_layers = theLayers;
			_segments = suitableSegments;
			_commonParent = commonParent;
		}

		@Override
		public boolean canExecute() {
			return true;
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

			// loop through the segmenets
			final Iterator<SuitableSegment> iter = _segments.iterator();
			while (iter.hasNext()) {
				final ConvertAbsoluteTmaToRelative.SuitableSegment seg = iter.next();

				// calculate the data for the relative segment
				final AbsoluteTMASegment absSegment = seg._thisA;
				final SensorWrapper sensor = seg._sensor;
				final TrackWrapper track = sensor.getHost();

				final RelativeTMASegment rSeg = createSegment(_layers, seg, track, absSegment, sensor);
				rSeg.setName(absSegment.getName());

				// remember the relative segment
				if (_replacements == null) {
					_replacements = new ArrayList<RelativeTMASegment>();
				}
				_replacements.add(rSeg);

				// remove the absolute segment
				_commonParent.removeElement(absSegment);

				// add the relative segment
				_commonParent.add(rSeg);

			}
			// fire updated / extended
			_layers.fireExtended(null, _commonParent);

			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

			// loop through the segmenets
			final Iterator<SuitableSegment> iter = _segments.iterator();
			while (iter.hasNext()) {
				final ConvertAbsoluteTmaToRelative.SuitableSegment seg = iter.next();

				_commonParent.removeElement(seg._thisA);
			}

			final Iterator<RelativeTMASegment> rIter = _replacements.iterator();
			while (rIter.hasNext()) {
				final RelativeTMASegment relativeTMASegment = rIter.next();

				_commonParent.add(relativeTMASegment);
			}

			// fire updated / extended
			_layers.fireExtended(null, _commonParent);

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

			final Iterator<RelativeTMASegment> rIter = _replacements.iterator();
			while (rIter.hasNext()) {
				final RelativeTMASegment relativeTMASegment = rIter.next();

				_commonParent.removeElement(relativeTMASegment);
			}

			// loop through the segmenets
			final Iterator<SuitableSegment> iter = _segments.iterator();
			while (iter.hasNext()) {
				final ConvertAbsoluteTmaToRelative.SuitableSegment seg = iter.next();

				_commonParent.add(seg._thisA);
			}

			// fire updated / extended
			_layers.fireExtended(null, _commonParent);

			return Status.OK_STATUS;

		}

	}

	private static class ShowMessage extends Action {
		private final String _title;
		private final String _message;

		public ShowMessage(final String title, final String message) {
			super("Convert segment(s) from absolute to relative");
			_title = title;
			_message = message;
		}

		@Override
		public void run() {
			CorePlugin.errorDialog(_title, "Sorry can't convert segment: " + _message);
		}
	}

	static class SuitableSegment {
		private final AbsoluteTMASegment _thisA;
		private final SensorWrapper _sensor;

		public SuitableSegment(final AbsoluteTMASegment thisA, final SensorWrapper sensor) {
			_thisA = thisA;
			_sensor = sensor;
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
		// so, see if it's something we can do business with
		if (subjects.length > 0) {
			TrackWrapper commonParent = null;
			List<SuitableSegment> suitableSegments = null;

			for (int i = 0; i < subjects.length; i++) {
				final Editable editable = subjects[i];
				if (editable instanceof CoreTMASegment) {
					if (editable instanceof AbsoluteTMASegment) {
						// cool, go for it.
						final AbsoluteTMASegment abs = (AbsoluteTMASegment) editable;

						// have a look at the parent
						final TrackWrapper thisParent = abs.getWrapper();

						// does it match?
						if (commonParent == null || commonParent == thisParent) {
							// cool, go for it
							commonParent = thisParent;
						} else {
							// don't bother, we didn't find anything useful
							return;
						}
					} else {
						// ok, we only work on a collection of abs segments
						return;
					}
				}
			}

			if (commonParent == null) {
				// ok, we didn't find any relative segments
				return;
			}

			// ok, loop through segments
			for (int i = 0; i < subjects.length; i++) {
				final AbsoluteTMASegment thisA = (AbsoluteTMASegment) subjects[i];

				// now check for peer relative segments
				RelativeTMASegment before = null;
				RelativeTMASegment after = null;
				final Enumeration<Editable> segs = commonParent.getSegments().elements();
				while (segs.hasMoreElements()) {
					final TrackSegment thisSeg = (TrackSegment) segs.nextElement();

					// is this one relative?
					if (thisSeg instanceof RelativeTMASegment) {
						final RelativeTMASegment thisRel = (RelativeTMASegment) thisSeg;
						// ok, is this one before us?
						if (thisSeg.endDTG().lessThan(thisA.getDTG_Start())) {
							before = thisRel;
						}

						// ready to look for after?
						if (before != null) {
							if (thisSeg.startDTG().greaterThan(thisA.getDTG_End())) {
								after = thisRel;
							}
						}
					}
				}

				if (before == null && after == null) {
					return;
				} else if (before != null || after != null) {
					SensorWrapper beforeS = null;
					SensorWrapper afterS = null;
					if (before != null)
						beforeS = before.getReferenceSensor();
					if (after != null)
						afterS = after.getReferenceSensor();

					if (beforeS == null && afterS == null) {
						return;
					}

					else if (beforeS != null && afterS != null && beforeS != afterS) {
						parent.add(new ShowMessage("Can't convert track",
								"Adjacent TMA Segments must be relative to same sensor"));
						return;
					}

					final SensorWrapper sensor;
					if (beforeS != null)
						sensor = beforeS;
					else
						sensor = afterS;

					// we must be ok, generate action
					final SuitableSegment suitableSegment = new SuitableSegment(thisA, sensor);

					if (suitableSegments == null) {
						suitableSegments = new ArrayList<SuitableSegment>();
					}
					suitableSegments.add(suitableSegment);
				}
			}

			if (suitableSegments != null) {
				final String phrase;
				if (suitableSegments.size() > 1) {
					phrase = "segments";
				} else {
					phrase = "segment";
				}

				// ok, generate it
				final IUndoableOperation action = getOperation(theLayers, suitableSegments, commonParent);

				final Action doIt = new Action("Convert " + phrase + " from absolute to relative") {
					@Override
					public void run() {
						runIt(action);
					}
				};

				// ok, go for it
				parent.add(doIt);
			}
		}

	}

	/**
	 * move the operation generation to a method, so it can be overwritten (in
	 * testing)
	 *
	 *
	 * @param theLayers
	 * @param suitableSegments
	 * @param commonParent
	 * @return
	 */
	protected IUndoableOperation getOperation(final Layers theLayers, final List<SuitableSegment> suitableSegments,
			final TrackWrapper commonParent) {
		return new ConvertToRelativeOperation(theLayers, suitableSegments, commonParent);
	}

	/**
	 * put the operation firer onto the undo history. We've refactored this into a
	 * separate method so testing classes don't have to simulate the CorePlugin
	 *
	 * @param operation
	 */
	protected void runIt(final IUndoableOperation operation) {
		CorePlugin.run(operation);
	}
}
