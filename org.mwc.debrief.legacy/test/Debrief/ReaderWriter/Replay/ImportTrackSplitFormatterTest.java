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

import static Debrief.ReaderWriter.Replay.ImportTrackSplitFormatter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Debrief.ReaderWriter.Replay.ImportTrackSplitFormatter.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.junit.jupiter.api.Test;
import Debrief.Wrappers.Formatters.SliceTrackFormatListener;
import MWC.GUI.Layers.INewItemListener;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;

/*
 * example: ;SPLIT_TRACK: One_Hour 3600000 TRACK_NAME TRACK_TWO ... example:
 * ;SPLIT_TRACK: One_Hour 3600000
 */

public class ImportTrackSplitFormatterTest {
	@Test
	public void testRead() {
		/*
		 * example:
		 */

		final ImportTrackSplitFormatter iff = new ImportTrackSplitFormatter();
		SliceTrackFormatListener res = (SliceTrackFormatListener) iff
				.readThisLine(";SPLIT_TRACK: One_Hour 3600000");
		assertNotNull(res);
		assertEquals("One_Hour", res.getName(), "correct name");
		assertEquals(3600000, res.getIntervalMillis(), "correct period");

		res = (SliceTrackFormatListener) iff.readThisLine(";SPLIT_TRACK: One_Second 1000");
		assertNotNull(res);
		assertEquals("One_Second", res.getName());
		assertEquals(1000, res.getIntervalMillis(), "correct period");

	}
}
