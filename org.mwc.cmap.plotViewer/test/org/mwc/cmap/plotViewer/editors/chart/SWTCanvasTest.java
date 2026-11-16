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

package org.mwc.cmap.plotViewer.editors.chart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.cmap.plotViewer.editors.chart.SWTCanvas.*;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////
// testing code...
// ////////////////////////////////////////////////
public class SWTCanvasTest {
	static public final String TEST_ALL_TEST_TYPE = "CONV";


	@Disabled("Fails outside the Eclipse runtime (clipboard text is null); it was in no suite before the JUnit 6 migration, so it never ran")
	@Test
	public void testClipboardTextManagement() {
		WorldLocation theLoc = new WorldLocation(12.3, 12.555555, 1.2);
		String txt = CorePlugin.toClipboard(theLoc);
		assertEquals("LOC:12.3,12.555555,1.2", txt, "correct string not produced");

		// check for valid location
		Object validStr;
		validStr = CorePlugin.fromClipboard(txt);
		assertNotNull(validStr, "is a location string");

		// and check for duff location
		validStr = CorePlugin.fromClipboard("aasdfasdfasdfadf");
		assertNull(validStr, "is a location string");

		// and back to the location
		final WorldLocation loc2 = CorePlugin.fromClipboard(txt);
		assertEquals(theLoc, loc2, "correct location parsed back in");

		// try southern/western location
		theLoc = new WorldLocation(-12.3, -12.555555, -1.2);
		txt = CorePlugin.toClipboard(theLoc);
		assertEquals("LOC:-12.3,-12.555555,-1.2", txt, "correct string not produced");

		WorldLocation loc3 = CorePlugin.fromClipboard("12.5 13.5");
		assertNotNull(loc3, "is a location string");

		loc3 = CorePlugin.fromClipboard("-12.5 13.5");
		assertNotNull(loc3, "is a location string");

		loc3 = CorePlugin.fromClipboard("-12.5\t 13.5");
		assertNotNull(loc3, "is a location string");

		loc3 = CorePlugin.fromClipboard("-12.5t 14.5");
		assertNotNull(loc3, "is not a location string");

		loc3 = CorePlugin.fromClipboard("12 01 02 N 14 12 32 W");
		assertNotNull(loc3, "is a location string");

		loc3 = CorePlugin.fromClipboard("12 01 02.225 S 14 12 32.116 E");
		assertNotNull(loc3, "is a location string");

	}
}
