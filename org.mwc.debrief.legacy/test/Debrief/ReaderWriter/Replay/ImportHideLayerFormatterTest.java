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

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.Replay.ImportHideLayerFormatter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Debrief.ReaderWriter.Replay.ImportHideLayerFormatter.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.Formatters.HideLayerFormatListener;
import MWC.GUI.PlainWrapper;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import org.junit.jupiter.api.Test;

/*
 * example: ;FORMAT_FIX: 10_sec_sym SYMBOL NULL NULL TRUE 600000
 */

public class ImportHideLayerFormatterTest {
	@Test
	public void testRead() {
		/*
		 * example:
		 */

		final ImportHideLayerFormatter iff = new ImportHideLayerFormatter();
		HideLayerFormatListener res = (HideLayerFormatListener) iff
				.readThisLine(";FORMAT_LAYER_HIDE: \"format name\" \"layer a name1\" layer_name2");
		assertNotNull(res);
		assertEquals("format name", res.getName(), "has correct name");
		assertEquals(2, res.getLayers().length, "has correct freq");

		res = (HideLayerFormatListener) iff.readThisLine(";FORMAT_LAYER_HIDE: format_name");
		assertNotNull(res);
		assertEquals("format_name", res.getName(), "has correct name");
		assertEquals(0, res.getLayers().length, "has correct freq");

	}
}
