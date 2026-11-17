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

package Debrief.ReaderWriter.Replay;

import static Debrief.ReaderWriter.Replay.ImportFixFormatter.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Replay.ImportFixFormatter.*;
import java.util.StringTokenizer;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.Formatters.CoreFormatItemListener;
import MWC.GUI.Properties.AttributeTypePropertyEditor;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import org.junit.jupiter.api.Test;

/*
 * example: ;FORMAT_FIX: 10_sec_sym SYMBOL NULL NULL TRUE 600000
 */

public class ImportFixFormatterTest {
	@Test
	public void testRead() {
		/*
		 * example:
		 */

		final ImportFixFormatter iff = new ImportFixFormatter();
		CoreFormatItemListener res = (CoreFormatItemListener) iff
				.readThisLine(";FORMAT_FIX: 10_sec_sym SYMBOL NULL NULL TRUE 600000");
		assertNotNull(res);
		assertNull(res.getLayerName(), "No track");
		assertNull(res.getSymbology(), "no sym");
		assertTrue(res.getVisible(), "active");
		assertNotNull(res.getName(), "has name");
		assertEquals(1, res.getAttributeType(), "has correct attr");
		assertEquals(600000, res.getInterval().getDate().getTime(), "has correct freq");
		assertTrue(res.getRegularIntervals(), "regular intervals");

		res = (CoreFormatItemListener) iff
				.readThisLine(";FORMAT_FIX: 10_sec_sym ARROW \"Track One\" @A FALSE 100000");
		assertNotNull(res);
		assertEquals("Track One", res.getLayerName(), "No track");
		assertNotNull(res.getSymbology(), "sym");
		assertTrue(res.getVisible(), "active");
		assertNotNull(res.getName(), "has name");
		assertEquals(0, res.getAttributeType(), "has correct attr");
		assertEquals(100000, res.getInterval().getDate().getTime(), "has correct freq");
		assertFalse(res.getRegularIntervals(), "regular intervals");

		res = (CoreFormatItemListener) iff
				.readThisLine(";FORMAT_FIX: \"10 arrow\" ARROW \"Track One\" BB FALSE 100000");
		assertNotNull(res);
		assertEquals("Track One", res.getLayerName(), "No track");
		assertNotNull(res.getSymbology(), "sym");
		assertEquals("BB", res.getSymbology(), "correct sym");
		assertTrue(res.getVisible(), "active");
		assertNotNull(res.getName(), "has name");

	}
}
