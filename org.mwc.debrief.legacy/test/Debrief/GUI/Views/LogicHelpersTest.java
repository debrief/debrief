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

package Debrief.GUI.Views;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.GUI.Views.LogicHelpers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Debrief.GUI.Views.LogicHelpers.*;
import java.util.ArrayList;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Plottable;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import org.junit.jupiter.api.Test;

public class LogicHelpersTest {
	@Test
	public void testClipboardNotEmptyFail() {
		final EnabledTest test = getClipboardNotEmptyTest();
		final Helper helper = new Helper() {

			@Override
			public ArrayList<Plottable> getClipboardContents() {
				final ArrayList<Plottable> res = new ArrayList<Plottable>();
				return res;
			}

			@Override
			public ArrayList<Plottable> getSelection() {
				return null;
			}
		};
		assertTrue(!test.isEnabled(helper));
	}

	@Test
	public void testClipboardNotEmptyPass() {
		final EnabledTest test = getClipboardNotEmptyTest();
		final Helper helper = new Helper() {

			@Override
			public ArrayList<Plottable> getClipboardContents() {
				final ArrayList<Plottable> res = new ArrayList<Plottable>();
				res.add(new BaseLayer());
				return res;
			}

			@Override
			public ArrayList<Plottable> getSelection() {
				return null;
			}
		};
		assertTrue(test.isEnabled(helper));
	}

	@Test
	public void testSelectionEmptyFail() {
		final EnabledTest test = getSelectionEmptyTest();
		final Helper helper = new Helper() {

			@Override
			public ArrayList<Plottable> getClipboardContents() {
				return null;
			}

			@Override
			public ArrayList<Plottable> getSelection() {
				final ArrayList<Plottable> res = new ArrayList<Plottable>();
				return res;
			}
		};
		assertTrue(test.isEnabled(helper));
	}

	@Test
	public void testSelectionEmptyPass() {
		final EnabledTest test = getSelectionEmptyTest();
		final Helper helper = new Helper() {

			@Override
			public ArrayList<Plottable> getClipboardContents() {
				return null;
			}

			@Override
			public ArrayList<Plottable> getSelection() {
				final ArrayList<Plottable> res = new ArrayList<Plottable>();
				res.add(new BaseLayer());
				return res;
			}
		};
		assertTrue(!test.isEnabled(helper));
	}

}
