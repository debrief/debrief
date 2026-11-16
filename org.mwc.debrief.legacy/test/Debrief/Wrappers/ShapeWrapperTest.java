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

package Debrief.Wrappers;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.Wrappers.ShapeWrapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.Wrappers.ShapeWrapper.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.PlottableSelection;
import MWC.GUI.Renamable;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.HasDraggableComponents;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor.CopyItem;
import MWC.GUI.Tools.Operations.RightClickPasteAdaptor.PasteItem;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ShapeWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private static int countItems(final Layers layers) {
		int ctr = 0;

		final Enumeration<Editable> lIter = layers.elements();
		while (lIter.hasMoreElements()) {
			final Editable next = lIter.nextElement();
			ctr++;
			if (next instanceof Layer) {
				@SuppressWarnings("unused")
				final Layer nextL = (Layer) next;
				final Enumeration<Editable> lIter2 = nextL.elements();
				while (lIter2.hasMoreElements()) {
					@SuppressWarnings("unused")
					final Editable next2 = lIter2.nextElement();
					ctr++;
				}
			}
		}
		return ctr;
	}


	@Test
	public void testCopyPaste() throws UnsupportedFlavorException, IOException {
		final Layers layers = new Layers();
		final Layer base1 = new BaseLayer();
		base1.setName("base 1");
		layers.addThisLayer(base1);
		final Layer base2 = new BaseLayer();
		base2.setName("base 2");
		layers.addThisLayer(base2);
		final WorldLocation tl = new WorldLocation(1, 2, 3);
		final WorldLocation br = new WorldLocation(2, 3, 4);
		final ShapeWrapper s1 = new ShapeWrapper("rect", new RectangleShape(tl, br), Color.red, null);
		base1.add(s1);

		assertEquals(3, countItems(layers), "got items");

		// ok, copy the item

		// first, try to copy/paste the leg
		final Clipboard clipboard = new Clipboard("Debrief");
		final UndoBuffer buffer = new UndoBuffer();

		// duplicate the track
		final CopyItem copier1 = new RightClickCutCopyAdaptor.CopyItem(s1, clipboard, null, layers, null, buffer);
		copier1.execute();

		final Transferable tr1 = clipboard.getContents(this);
		// see if there is currently a plottable on the clipboard

		// extract the plottable
		final Plottable theData1 = (Plottable) tr1.getTransferData(PlottableSelection.PlottableFlavor);
		final PasteItem paster1 = new PasteItem(theData1, clipboard, base2, layers, true);
		paster1.execute();

		// jave we now got 4 items?
		assertEquals(4, countItems(layers), "got more items");
	}

	@Test
	public final void testMyParams() {
		final MWC.GUI.Shapes.PlainShape ps = new MWC.GUI.Shapes.CircleShape(new WorldLocation(2d, 2d, 2d), 12);
		MWC.GUI.Editable ed = new ShapeWrapper("", ps, MWC.GUI.Properties.DebriefColors.RED, new HiResDate(0));
		editableTesterSupport.testParams(ed);
		ed = null;
	}

	@Test
	public final void testTimes() {

		final WorldLocation scrapLoc = new WorldLocation(1, 1, 1);
		final WorldLocation scrapLoc2 = new WorldLocation(1, 3, 1);

		final WatchableList.TestWatchables tw = new WatchableList.TestWatchables() {
			@Override
			public WatchableList getBothDates(final HiResDate startDate, final HiResDate endDate) {
				final ShapeWrapper sw = new ShapeWrapper("blank", new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
						MWC.GUI.Properties.DebriefColors.RED, null) {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected long getThreshold() {
						return 5000 * 1000;
					}
				};
				sw.setStartDTG(new HiResDate(startDate));
				sw.setEndDTG(new HiResDate(endDate));
				return sw;
			}

			@Override
			public WatchableList getNullDates() {
				final ShapeWrapper sw = new ShapeWrapper("blank", new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
						MWC.GUI.Properties.DebriefColors.RED, null) {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected long getThreshold() {
						return 5000 * 1000;
					}
				};
				sw.setStartDTG(null);
				sw.setEndDTG(null);
				return sw;
			}

			@Override
			public WatchableList getStartDateOnly(final HiResDate startDate) {
				final ShapeWrapper sw = new ShapeWrapper("blank", new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
						MWC.GUI.Properties.DebriefColors.RED, null) {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					@Override
					protected long getThreshold() {
						return 5000 * 1000;
					}
				};
				sw.setStartDTG(new HiResDate(startDate));
				sw.setEndDTG(null);
				return sw;
			}
		};

		tw.doTest();
	}
}
