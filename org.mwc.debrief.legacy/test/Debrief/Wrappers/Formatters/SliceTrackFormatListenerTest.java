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

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.Wrappers.Formatters.SliceTrackFormatListener.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.Wrappers.Formatters.SliceTrackFormatListener.*;
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
import org.junit.jupiter.api.Test;

public class SliceTrackFormatListenerTest {

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

	@Test
	public void testNoNames() {
		final List<String> names = new ArrayList<String>();
		final INewItemListener cf = new SliceTrackFormatListener("Test", 1000L, names);

		final TrackWrapper tOne = getTrackOne(cf);

		// and another track
		final TrackWrapper tTwo = getTrackTwo(cf);

		// and the end of file processing
		cf.fileComplete();

		// check the tracks got split
		assertEquals(2, tOne.getSegments().size(), "two segments");
		assertEquals(3, tTwo.getSegments().size(), "three segments");
	}

	@Test
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
		assertEquals(1, tOne.getSegments().size(), "still just one segment");
		assertEquals(3, tTwo.getSegments().size(), "three segments");
	}

	@Test
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
		assertEquals(2, tOne.getSegments().size(), "two segments");
		assertEquals(3, tTwo.getSegments().size(), "three segments");
	}
}
