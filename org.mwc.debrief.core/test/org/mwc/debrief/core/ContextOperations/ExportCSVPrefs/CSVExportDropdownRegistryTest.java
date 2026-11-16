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

package org.mwc.debrief.core.ContextOperations.ExportCSVPrefs;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.CSVExportDropdownRegistry.*;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.CSVExportDropdownRegistry.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.core.CorePlugin;
import org.junit.jupiter.api.Test;

public class CSVExportDropdownRegistryTest {
	@Test
	public void testBadines() {
		final List<String> list = new ArrayList<String>();

		list.add("//// comment line");
		list.add("a-one");
		list.add("a-two");
		list.add("// SectionB");
		list.add("b-one");
		list.add("//SectionC");
		list.add("c-one");
		list.add("c-two");
		list.add("c-three");

		final Map<String, ArrayList<String>> fields = new HashMap<String, ArrayList<String>>();

		assertTrue(fields.keySet().isEmpty(), "should not be empty");

		processLines(list, fields);

		assertFalse(fields.keySet().isEmpty(), "should not be empty");

		assertEquals(3, fields.keySet().size(), 3, "has 3 sections");
		assertEquals(null, fields.get("SectionA"), "section two empty");
		assertEquals(3, fields.get("SectionC").size(), "has 3 lines");
	}

	@Test
	public void testLines() {
		final List<String> list = new ArrayList<String>();

		list.add("//// comment line");
		list.add("//SectionA");
		list.add("a-one");
		list.add("a-two");
		list.add("// SectionB");
		list.add("b-one");
		list.add("//SectionC");
		list.add("c-one");
		list.add("c-two");
		list.add("c-three");

		final Map<String, ArrayList<String>> fields = new HashMap<String, ArrayList<String>>();

		assertTrue(fields.keySet().isEmpty(), "should not be empty");

		processLines(list, fields);

		assertFalse(fields.keySet().isEmpty(), "should not be empty");

		assertEquals(3, fields.keySet().size(), 3, "has 3 sections");
		assertEquals(2, fields.get("SectionA").size(), "has 2 lines");
		assertEquals(1, fields.get("SectionB").size(), "has 1 lines");
		assertEquals(3, fields.get("SectionC").size(), "has 3 lines");
	}

	@Test
	public void testLoad() throws FileNotFoundException {
		final String filename = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/ExportWizard.csv";

		// check it exists
		assertTrue(new File(filename).exists(), "can't find test data-file");

		final CSVExportDropdownRegistry reg = new CSVExportDropdownRegistry(filename);

		// check empty
		assertTrue(reg.myFields.keySet().isEmpty(), "list object should be empty");

		reg.load();

		assertFalse(reg.myFields.keySet().isEmpty(), "no longer empty");
		assertEquals(9, reg.myFields.keySet().size(), "has sections");

		assertEquals(3, reg.getValuesFor("CLASSIFICATION").size(), "has 3 classifications");
	}
}
