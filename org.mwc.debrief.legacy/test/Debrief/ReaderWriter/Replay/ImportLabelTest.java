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

import static Debrief.ReaderWriter.Replay.ImportLabel.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.ReaderWriter.Replay.ImportLabel.*;
import java.text.ParseException;
import java.util.StringTokenizer;
import Debrief.Wrappers.LabelWrapper;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.junit.jupiter.api.Test;

// ///////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ImportLabelTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	@Test
	public void testLabel() {
		//
		final String str1 = ";TEXT: WB 21.72 0 0 N 21.52 0 0 W wreck symbol";
		final String str2 = ";TEXT: EB 21.66 0 0 N 21.52 0 0 W";

		final ImportLabel importer = new ImportLabel();
		final LabelWrapper res = (LabelWrapper) importer.readThisLine(str1);
		assertEquals("wreck symbol", res.getLabel(), "Correct label");

		final LabelWrapper res2 = (LabelWrapper) importer.readThisLine(str2);
		assertEquals("", res2.getLabel(), "Correct label");
	}
}
