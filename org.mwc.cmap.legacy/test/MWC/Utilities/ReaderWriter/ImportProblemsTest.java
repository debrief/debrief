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

package MWC.Utilities.ReaderWriter;

import static MWC.Utilities.ReaderWriter.ImportProblems.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.Utilities.ReaderWriter.ImportProblems.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import MWC.GUI.MessageProvider;
import MWC.GUI.Dialogs.DialogFactory;
import org.junit.jupiter.api.Test;

public class ImportProblemsTest {
	@Test
	public void testSummary() {
		final ImportProblems problems = new ImportProblems();
		assertTrue(problems.isEmpty());
		assertNull(problems.summary("file.rep"));
		for (int i = 1; i <= MAX_LISTED + 5; i++) {
			problems.add(i, "bad value", "line " + i);
		}
		problems.add("no line number");
		assertEquals(MAX_LISTED + 6, problems.size());
		final String summary = problems.summary("file.rep");
		assertTrue(summary.startsWith(MAX_LISTED + 6 + " problems found reading file.rep"), summary);
		assertTrue(summary.contains("Line 1: bad value [line 1]"), summary);
		assertTrue(summary.contains("...and 6 more"), summary);
		assertFalse(summary.contains("no line number"), summary);
	}
}
