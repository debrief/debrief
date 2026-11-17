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
package Debrief.ReaderWriter.BRT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.WormInHoleOffset;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;

public class BRTImporter {

	public static class ImportBRTAction implements Action {
		private final SensorWrapper sensorAdded;
		private final TrackWrapper track;

		public ImportBRTAction(final SensorWrapper sensorAdded, final TrackWrapper track) {
			super();
			this.sensorAdded = sensorAdded;
			this.track = track;
		}

		@Override
		public void execute() {
			track.add(sensorAdded);
		}

		@Override
		public boolean isRedoable() {
			return true;
		}

		@Override
		public boolean isUndoable() {
			return true;
		}

		@Override
		public void undo() {
			track.removeElement(sensorAdded);
		}

	}

	private static ImportBRTAction createImportAction(final BRTHelper helper, final BRTData brtData) {
		// find track
		final TrackWrapper track = helper.select();

		final SensorWrapper sensor = new SensorWrapper(helper.getName());

		// set default color
		sensor.setColor(helper.getColor());

		// set array offset (if needed)
		if (helper.isTowed()) {
			final WorldDistance offset = helper.arrayOffset();
			if (offset != null) {
				final WorldDistance.ArrayLength length = new WorldDistance.ArrayLength(offset);
				sensor.setSensorOffset(length);
			}
		}

		// populate the sensor
		loadThese(brtData, sensor, track, helper.defaultLength(), helper.isTowed());

		sensor.setVisible(helper.isVisible());
		return new ImportBRTAction(sensor, track);
	}

	public static TrackWrapper findTrack(final TrackWrapper[] allTracks) {
		if (allTracks.length == 1) {
			return allTracks[0];
		}
		int amountOfBlueTracks = 0;
		int indexOfBlueTrack = 0;
		for (int i = 0; i < allTracks.length; i++) {
			if (DebriefColors.BLUE.equals(allTracks[i].getTrackColor())) {
				++amountOfBlueTracks;
				indexOfBlueTrack = i;
			}
		}
		if (amountOfBlueTracks == 1) {
			return allTracks[indexOfBlueTrack];
		}
		return null;
	}

	/**
	 * loop through layers, find tracks
	 *
	 * @param layers Layers to loop
	 * @return Tracks available in the layers
	 */
	public static TrackWrapper[] getTracks(final Layers layers) {
		final List<TrackWrapper> result = new ArrayList<>();

		final Enumeration<Editable> layerIterator = layers.elements();

		while (layerIterator.hasMoreElements()) {
			final Layer layer = (Layer) layerIterator.nextElement();
			if (TrackWrapper.class.isAssignableFrom(layer.getClass())) {
				result.add((TrackWrapper) layer);
			}
		}

		return result.toArray(new TrackWrapper[] {});
	}

	private static void loadThese(final BRTData data, final SensorWrapper sensor, final TrackWrapper track,
			final WorldDistance defaultLength, final boolean isAmbiguous) {
		final int num = data.getTimes().length;
		for (int i = 0; i < num; i++) {
			final long thisT = data.getTimes()[i];
			final HiResDate dtg = new HiResDate(thisT);

			final Double course;
			final ArrayLength offset = sensor.getSensorOffset();

			// is it ambiguous, and do we have an offset?
			if (isAmbiguous && offset != null && offset.getValue() != 0) {
				// ok, walk back down the track for the relevant distnace

				// work out where the ship was when walk back through the
				// offset distance
				final FixWrapper fix = WormInHoleOffset.getWormOffsetFor(track, dtg, offset);

				// if we found a position, retrieve the course
				course = fix != null ? MWC.Algorithms.Conversions.Rads2Degs(fix.getCourse()) : null;
			} else {
				// just use the current location
				// find the ownship course at this time
				final Watchable[] nearest = track.getNearestTo(new HiResDate(thisT));
				if (nearest != null && nearest.length > 0) {
					final FixWrapper fix = (FixWrapper) nearest[0];
					course = MWC.Algorithms.Conversions.Rads2Degs(fix.getCourse());
				} else {
					course = null;
				}
			}

			// did we find a course?
			if (course != null) {
				final double relBearingDegs = data.getBearings()[i];

				// convert from rel brg to bearing by adding to course
				double bearingDegs = course + relBearingDegs;
				if (bearingDegs >= 360d) {
					bearingDegs -= 360d;
				}

				// if it's towed (ambiguous) we need to generate the
				// mirror (ambiguous) bearing
				Double ambigDegs = isAmbiguous ? course - relBearingDegs : null;
				if (ambigDegs != null && ambigDegs >= 360d) {
					ambigDegs -= 360d;
				}

				final SensorContactWrapper newS = new SensorContactWrapper(track.getName(), dtg, null, bearingDegs,
						ambigDegs, null, null, null, "pt:" + i, LineStylePropertyEditor.SOLID, sensor.getName());

				// was a length specified?
				if (defaultLength != null) {
					newS.setRange(defaultLength);
				}

				// ok, done.
				sensor.add(newS);
			}

		}
	}

	static BRTData readBRT(final InputStream is) throws NumberFormatException, IOException, Exception {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;

		final ArrayList<Long> times = new ArrayList<>();
		final ArrayList<Double> bearings = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			final String[] tokens = line.split(",");
			if (tokens.length != 2) {
				throw new Exception("Wrong format in BRT file. Please consult the user manual");
			}
			final Long time = (long) (Double.parseDouble(tokens[0]) * 1000);
			final Double bearing = Double.parseDouble(tokens[1]);
			times.add(time);
			bearings.add(bearing);
		}

		final BRTData brtData = new BRTData(times.toArray(new Long[] {}), bearings.toArray(new Double[] {}));

		return brtData;
	}

	/**
	 * Default Constructor
	 *
	 * @param hB      Contains the information given by the user using the UI.
	 * @param _layers layers available in the plot
	 */
	public BRTImporter() {
		super();
	}

	public ImportBRTAction importThis(final BRTHelper brtHelper, final InputStream is) throws Exception {
		final BRTData brtData = readBRT(is);

		return createImportAction(brtHelper, brtData);
	}
}
