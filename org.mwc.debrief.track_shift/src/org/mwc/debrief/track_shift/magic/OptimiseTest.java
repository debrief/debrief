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

package org.mwc.debrief.track_shift.magic;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import flanagan.math.MinimisationFunction;

public class OptimiseTest {
	public static class DummyOffsetFunction implements MinimisationFunction {

		private final double _val1;
		private final double _val2;

		public DummyOffsetFunction(final double val1, final double val2) {
			_val1 = val1;
			_val2 = val2;
		}

		@Override
		public double function(final double[] param) {
			// ok, generate bearing
			final double brgDegs = param[0];
			final double rngM = param[1];

			final double res = (Math.abs(_val1 - brgDegs)) + Math.abs(_val2 - rngM);

			// do the calc
			return res;
		}

	}

	public static class TryOffsetFunction implements MinimisationFunction {
		private final TreeSet<Doublet> _doublets;

		public TryOffsetFunction(final TreeSet<Doublet> doublets) {
			_doublets = doublets;
		}

		@Override
		public double function(final double[] param) {
			// ok, generate bearing
			final double brgDegs = param[0];
			final double brgRads = MWC.Algorithms.Conversions.Degs2Rads(brgDegs);
			final double rngM = param[1];
			final double rngDegs = MWC.Algorithms.Conversions.m2Degs(rngM);

			// and the world vector
			final WorldVector offset = new WorldVector(brgRads, rngDegs, 0);

			// get shifting
			final TreeSet<Doublet> newD = shiftDoublets(offset);

			// do the math
			double res = 0;
			for (final Iterator<Doublet> iterator = newD.iterator(); iterator.hasNext();) {
				final Doublet thisD = iterator.next();
				final double measuredBearing = thisD.getMeasuredBearing();
				final double calculatedBearing = thisD.getCalculatedBearing(null, null);
				final double thisError = thisD.calculateBearingError(measuredBearing, calculatedBearing);
				final double score = Math.abs(thisError * thisError);

				res += score;
			}

			// do the calc
			return res;
		}

		/**
		 * produce a new set of positions by applying an offset to ourst
		 *
		 * @param thisOffset
		 * @return
		 */
		TreeSet<Doublet> shiftDoublets(final WorldVector thisOffset) {
			final TreeSet<Doublet> res = new TreeSet<Doublet>();

			// loop through the doublets
			final Iterator<Doublet> iter = _doublets.iterator();
			while (iter.hasNext()) {
				final Doublet doublet = iter.next();

				// get this fix
				final FixWrapper fix = doublet.getTarget();

				// clone it
				final FixWrapper newF = new FixWrapper(fix.getFix().makeCopy());

				// move it
				final WorldLocation newLoc = new WorldLocation(newF.getLocation());
				newLoc.addToMe(thisOffset);
				newF.setLocation(newLoc);

				// create a new doublet
				final Doublet newD = new Doublet(doublet.getSensorCut(), newF, doublet.getTargetTrack(),
						doublet.getHost());

				res.add(newD);
			}

			return res;
		}

	}

	static final String ONLY_ONE_TRACK = "Only one whole Track may be selected for the optimisation";
	private static final String ONLY_SECONDARY = "Optimisation is only supported for denoted Secondary Track, or it's child segments";

	private static final String INVALID_SELECTION = "Only Track or Track Segment items may be selected";

	static final String NO_VALID_SEL = "Please select a single Track, or multiple Track Segments from the Outline View to optimise";

	final static String NO_MIXED_ITEMS = "Sorry, you can't mix Track and Track-Segment items in the optimisation";

	/**
	 * go through the selection, pull out things we may optimise
	 *
	 * @param resultsList placeholder for the results
	 * @param selection   the current selection
	 * @param secTrack    the current secondary track
	 * @return
	 */
	public static String getDraggables(final Vector<DraggableItem> resultsList, final IStructuredSelection selection,
			final TrackWrapper secTrack) {
		String res = null;

		TrackWrapper track = null;

		final Vector<DraggableItem> tmpList = new Vector<DraggableItem>();

		// ok, get looping
		final Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			final Object object = iter.next();
			if (object instanceof EditableWrapper) {
				final EditableWrapper ew = (EditableWrapper) object;
				final Editable obj = ew.getEditable();
				if (obj instanceof TrackWrapper) {
					// is this something other than the secondary track?
					final TrackWrapper thisTrack = (TrackWrapper) obj;

					// is this our first item?
					if (tmpList.size() == 0) {
						if (!thisTrack.equals(secTrack)) {
							return ONLY_SECONDARY;
						}

						track = thisTrack;
						tmpList.add(track);
					} else {
						// do we alreay have a track
						if (track != null)
							return ONLY_ONE_TRACK;
						else
							return NO_MIXED_ITEMS;
					}
				} else if (obj instanceof TrackSegment) {
					// do we already have a track?
					if (track != null) {
						return NO_MIXED_ITEMS;
					}
					tmpList.add((DraggableItem) obj);
				} else {
					return INVALID_SELECTION;
				}
			}
		}

		if (tmpList.size() == 0) {
			res = NO_VALID_SEL;
		} else {
			resultsList.addAll(tmpList);
		}

		return res;
	}

	static Layers getTheData() throws FileNotFoundException {
		// the plain (non-Eclipse) reader: it needs no plot editor or viewport
		final DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(null);
		final Layers res = new Layers();
		final String path = "../org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/magic/";
		final String fName = "midflow2.xml";
		// importing into a set of layers reads a <layers> element, so hand it just
		// that part of the plot file (the whole plot needs a GUI session)
		final String plot;
		try {
			plot = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path + fName)),
					java.nio.charset.StandardCharsets.UTF_8);
		} catch (final java.io.IOException e) {
			throw new FileNotFoundException(path + fName + ": " + e.getMessage());
		}
		final String layersXml = plot.substring(plot.indexOf("<layers"),
				plot.indexOf("</layers>") + "</layers>".length());
		final InputStream is = new java.io.ByteArrayInputStream(layersXml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
		reader.importThis(fName, is, res);
		return res;
	}
}
