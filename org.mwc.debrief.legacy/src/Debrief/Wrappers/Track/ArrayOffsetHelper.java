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
package Debrief.Wrappers.Track;

import java.util.ArrayList;
import java.util.List;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataFolder.DatasetOperator;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble2;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;

public class ArrayOffsetHelper {

	public static interface ArrayCentreMode {
		/**
		 * yes, all objects have a toString() method. But, we do want to be certain that
		 * this method is explicitly implemented. We don't want a default method.
		 *
		 * @return
		 */
		public String asString();
	}

	public static class DeferredDatasetArrayMode implements ArrayCentreMode {
		private final String _source;

		public DeferredDatasetArrayMode(final String source) {
			_source = source;
		}

		@Override
		public String asString() {
			return "DEFERRED LOADING FAILED";
		}

		public String getSourceName() {
			return _source;
		}
	}

	public static enum LegacyArrayOffsetModes implements ArrayCentreMode {
		PLAIN {

			@Override
			public String asString() {
				return "Plain";
			}
		},
		WORM {

			@Override
			public String asString() {
				return "Worm in hole";
			}
		};
	}

	public static class MeasuredDatasetArrayMode implements ArrayCentreMode {
		private final TimeSeriesDatasetDouble2 _source;
		private boolean _interpolatePositions = true;

		public MeasuredDatasetArrayMode(final TimeSeriesDatasetDouble2 source) {
			_source = source;
		}

		@Override
		public String asString() {
			return _source.getPath();
		}

		public TimeSeriesDatasetDouble2 getDataset() {
			return _source;
		}

		public boolean getInterpolatePositions() {
			return _interpolatePositions;
		}

		public void setInterpolatePositions(final boolean interpolatePositions) {
			this._interpolatePositions = interpolatePositions;
		}
	}

	public static List<ArrayCentreMode> getAdditionalArrayCentreModes(final SensorWrapper sensor) {
		final List<ArrayCentreMode> res = new ArrayList<ArrayCentreMode>();

		// start off with our legacy modes
		res.add(LegacyArrayOffsetModes.PLAIN);
		res.add(LegacyArrayOffsetModes.WORM);

		final Object measuredData = sensor.getAdditionalData().getThisType(DataFolder.class);
		if (measuredData != null) {
			// ok. walk the tree, and see if there are any datasets with location
			final DataFolder df = (DataFolder) measuredData;

			final DatasetOperator processor = new DataFolder.DatasetOperator() {
				@Override
				public void process(final TimeSeriesCore dataset) {
					// ok, is it a 2D dataset?
					if (dataset instanceof TimeSeriesDatasetDouble2) {
						final TimeSeriesDatasetDouble2 ts = (TimeSeriesDatasetDouble2) dataset;

						final String hisUnits = ts.getUnits();

						// is it suitable?
						if ("m".equals(hisUnits) || "\u00b0".equals(hisUnits)) {
							res.add(new MeasuredDatasetArrayMode(ts));
						}
					}
				}
			};

			df.walkThisDataset(processor);
		}

		return res;
	}

	public static WorldLocation getArrayCentre(final SensorWrapper sensor, final HiResDate time,
			WorldLocation hostLocation, final TrackWrapper track) {
		final WorldLocation centre;
		final ArrayCentreMode arrayCentre = sensor.getArrayCentreMode();
		if (arrayCentre instanceof LegacyArrayOffsetModes) {
			// ok, we need a sensor offset to do this.
			// check we have one
			ArrayLength len = sensor.getSensorOffset();
			if (len == null) {
				len = new ArrayLength(0);
			}
			// it's ok, we can use our old legacy worm in hole processing
			final boolean inWormMode = arrayCentre.equals(LegacyArrayOffsetModes.WORM);
			final FixWrapper trackPoint = track.getBacktraceTo(time, len, inWormMode);
			if (trackPoint != null) {
				centre = trackPoint.getLocation();
			} else {
				centre = null;
			}
		} else {
			// not in legacy mode, use measurements
			final MeasuredDatasetArrayMode meas = (MeasuredDatasetArrayMode) arrayCentre;

			// now try get the location from the measured dataset

			// do we know the host location?
			if (hostLocation != null) {
				// ok, get calculating
				centre = sensor.getMeasuredLocationAt(meas, time, hostLocation);
			} else {
				// ok, we'll have to find it
				final Watchable[] matches = track.getNearestTo(time);
				if (matches.length == 1) {
					hostLocation = matches[0].getLocation();
					centre = sensor.getMeasuredLocationAt(meas, time, hostLocation);
				} else {
					centre = null;
				}
			}
		}

		return centre;
	}

	public static ArrayCentreMode sortOutDeferredMode(final DeferredDatasetArrayMode dMode,
			final SensorWrapper sensor) {
		final String dName = dMode.getSourceName();

		final List<MeasuredDatasetArrayMode> matches = new ArrayList<MeasuredDatasetArrayMode>();

		final Object measuredData = sensor.getAdditionalData().getThisType(DataFolder.class);
		if (measuredData != null) {
			// ok. walk the tree, and see if there are any datasets with location
			final DataFolder df = (DataFolder) measuredData;

			final DatasetOperator processor = new DataFolder.DatasetOperator() {
				@Override
				public void process(final TimeSeriesCore dataset) {
					// ok, is it a 2D dataset?
					if (dataset instanceof TimeSeriesDatasetDouble2) {
						final TimeSeriesDatasetDouble2 ts = (TimeSeriesDatasetDouble2) dataset;

						if (ts.getPath().equals(dName)) {
							matches.add(new MeasuredDatasetArrayMode(ts));
						}
					}
				}
			};

			df.walkThisDataset(processor);
		}

		final ArrayCentreMode res;

		if (matches.size() == 1) {
			res = matches.get(0);
		} else {
			Application.logStack2(ToolParent.ERROR, "Failed to find measured data source to match:" + dName);
			res = null;
		}

		return res;
	}

}
