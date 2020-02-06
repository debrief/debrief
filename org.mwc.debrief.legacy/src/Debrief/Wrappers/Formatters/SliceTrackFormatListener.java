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

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class SliceTrackFormatListener extends PlainWrapper implements INewItemListener {

	public static class TestMe extends TestCase {

		private FixWrapper createFix(final int time) {
			final Fix newF = new Fix(new HiResDate(time), new WorldLocation(2, 2, 0), 22, 33);
			final FixWrapper fw = new FixWrapper(newF);
			return fw;
		}

		public TrackWrapper getTrackOne(final INewItemListener cf) {
			final TrackWrapper tOne = new TrackWrapper();
			tOne.setName("T-One");
			processFix(cf, tOne, 4000);
			processFix(cf, tOne, 4500);
			processFix(cf, tOne, 5000);
			processFix(cf, tOne, 5500);
			processFix(cf, tOne, 6000);
			processFix(cf, tOne, 6500);
			processFix(cf, tOne, 8000);
			processFix(cf, tOne, 8500);
			return tOne;
		}

		public TrackWrapper getTrackTwo(final INewItemListener cf) {
			final TrackWrapper tTwo = new TrackWrapper();
			tTwo.setName("Dobbin");

			processFix(cf, tTwo, 2000);
			processFix(cf, tTwo, 2500);
			processFix(cf, tTwo, 5000);
			processFix(cf, tTwo, 5500);
			processFix(cf, tTwo, 6000);
			processFix(cf, tTwo, 6500);
			processFix(cf, tTwo, 8000);
			processFix(cf, tTwo, 8500);
			return tTwo;
		}

		private void processFix(final INewItemListener cf, final TrackWrapper tw, final int time) {
			final FixWrapper fix = createFix(time);
			tw.addFix(fix);
			cf.newItem(tw, fix, null);
		}

		public void testNoNames() {
			final List<String> names = new ArrayList<String>();
			final INewItemListener cf = new SliceTrackFormatListener("Test", 1000L, names);

			final TrackWrapper tOne = getTrackOne(cf);

			// and another track
			final TrackWrapper tTwo = getTrackTwo(cf);

			// and the end of file processing
			cf.fileComplete();

			// check the tracks got split
			assertEquals("two segments", 2, tOne.getSegments().size());
			assertEquals("three segments", 3, tTwo.getSegments().size());
		}

		public void testOneNames() {
			final List<String> names = new ArrayList<String>();
			names.add("Dobbin");
			final INewItemListener cf = new SliceTrackFormatListener("Test", 1000L, names);

			final TrackWrapper tOne = getTrackOne(cf);

			// and another track
			final TrackWrapper tTwo = getTrackTwo(cf);

			// and the end of file processing
			cf.fileComplete();

			// check the tracks got split
			assertEquals("still just one segment", 1, tOne.getSegments().size());
			assertEquals("three segments", 3, tTwo.getSegments().size());
		}

		public void testTwoNames() {
			final List<String> names = new ArrayList<String>();
			names.add("Dobbin");
			names.add("T-One");
			names.add("Rover");

			final INewItemListener cf = new SliceTrackFormatListener("Test", 1000L, names);

			final TrackWrapper tOne = getTrackOne(cf);

			// and another track
			final TrackWrapper tTwo = getTrackTwo(cf);

			// and the end of file processing
			cf.fileComplete();

			// check the tracks got split
			assertEquals("two segments", 2, tOne.getSegments().size());
			assertEquals("three segments", 3, tTwo.getSegments().size());
		}
	}

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