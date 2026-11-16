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

import static Debrief.Wrappers.Formatters.CoreFormatItemListener.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Debrief.Wrappers.Formatters.CoreFormatItemListener.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Properties.AttributeTypePropertyEditor;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

public class CoreFormatItemListenerTest {
	private FixWrapper createFix(final int time) {
		final Fix newF = new Fix(new HiResDate(time), new WorldLocation(2, 2, 0), 22, 33);
		final FixWrapper fw = new FixWrapper(newF);
		return fw;

	}

	@Test
	public void testIntervals() {
		final CoreFormatItemListener cf = new CoreFormatItemListener("Test", null, null, 5000, false,
				AttributeTypePropertyEditor.SYMBOL, true) {

			/**
			*
			*/
			private static final long serialVersionUID = 1L;

			@Override
			protected void applyFormat(final FixWrapper fix) {
				fix.setSymbolShowing(true);
			}

			@Override
			protected void formatTrack(final TrackWrapper track, final HiResDate interval) {
				// skip
			}
		};
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("Name");
		final FixWrapper f1 = createFix(4000);
		final FixWrapper f2 = createFix(5000);
		final FixWrapper f3 = createFix(9000);
		final FixWrapper f4 = createFix(10000);
		final FixWrapper f5 = createFix(14100);

		cf.newItem(tw, f1, null);
		cf.newItem(tw, f2, null);
		cf.newItem(tw, f3, null);
		cf.newItem(tw, f4, null);
		cf.newItem(tw, f5, null);

		assertTrue(f1.getSymbolShowing());
		assertFalse(f2.getSymbolShowing());
		assertTrue(f3.getSymbolShowing());
		assertFalse(f4.getSymbolShowing());
		assertTrue(f5.getSymbolShowing());
	}

	@Test
	public void testRegularIntervals() {
		final CoreFormatItemListener cf = new CoreFormatItemListener("Test", null, null, 5000, true,
				AttributeTypePropertyEditor.SYMBOL, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void applyFormat(final FixWrapper fix) {
				fix.setSymbolShowing(true);
			}

			@Override
			protected void formatTrack(final TrackWrapper track, final HiResDate interval) {
				// skip
			}
		};
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("Name");
		final FixWrapper f1 = createFix(4000);
		final FixWrapper f2 = createFix(5000);
		final FixWrapper f3 = createFix(6000);
		final FixWrapper f4 = createFix(10000);
		final FixWrapper f5 = createFix(12000);

		cf.newItem(tw, f1, null);
		cf.newItem(tw, f2, null);
		cf.newItem(tw, f3, null);
		cf.newItem(tw, f4, null);
		cf.newItem(tw, f5, null);

		assertTrue(f1.getSymbolShowing());
		assertTrue(f2.getSymbolShowing());
		assertFalse(f3.getSymbolShowing());
		assertTrue(f4.getSymbolShowing());
		assertFalse(f5.getSymbolShowing());
	}
}
