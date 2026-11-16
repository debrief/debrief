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

package org.mwc.debrief.core.ContextOperations;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.debrief.core.ContextOperations.ExportTrackAsCSV.*;
import org.mwc.debrief.core.ContextOperations.ExportTrackAsCSV.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.CSVExportDropdownRegistry;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.DropdownProvider;
import org.mwc.debrief.core.ContextOperations.ExportCSVPrefs.ExportCSVPreferencesPage;
import org.mwc.debrief.core.wizards.CSVExportWizard;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ExportTrackAsCSVTest {

	private static class DummyAttributes implements CSVAttributeProvider {
		@Override
		public String getCaseNumber() {
			return "21B";
		}

		@Override
		public String getClassification() {
			return "Public";
		}

		@Override
		public String getConfidence() {
			return "Hopefully";
		}

		@Override
		public String getDistributionStatement() {
			return "Distribution, Statement";
		}

		@Override
		public String getFilePath() {
			return "UNKNOWN";
		}

		@Override
		public String getFlag() {
			return "MAURETANIA";
		}

		@Override
		public String getLikelihood() {
			return "UNLIKELY";
		}

		@Override
		public String getProvenance() {
			return "Provenance";
		}

		@Override
		public String getPurpose() {
			return "Purpose String";
		}

		@Override
		public String getSemiMajorAxis() {
			return "2000";
		}

		@Override
		public String getSemiMinorAxis() {
			return "1000";
		}

		@Override
		public String getSensor() {
			return "SensorName";
		}

		@Override
		public String getSuppliedBy() {
			return "DeepBlue";
		}

		@Override
		public String getType() {
			return "OILER";
		}

		@Override
		public String getUnitName() {
			return "OWNSHIP_NAME, PART 2";
		}
	}

	@Test
	public void testClean() {
		assertEquals("A_12", ExportTrackToCSV.tidyMe("A:12"));
	}

	@Test
	public void testExport() throws IOException {
		final TrackWrapper track = new TrackWrapper();
		track.setName("OWNSHIP");
		track.addFix(new FixWrapper(new Fix(new HiResDate(1000000), new WorldLocation(2d, 4d, 0d), Math.PI, 10d)));
		track.addFix(
				new FixWrapper(new Fix(new HiResDate(1200000), new WorldLocation(3d, 6d, 0d), Math.PI / 2d, 20d)));

		final CSVAttributeProvider provider = new DummyAttributes();
		final List<String> strings = ExportTrackToCSV.outputStrings(track, provider);
		final StringBuffer bugger = new StringBuffer();
		for (final String s : strings) {
			bugger.append(s);
		}

		assertEquals(4, strings.size(), "correct num of lines");

		final Reader targetReader = new StringReader(bugger.toString());

		final Iterable<CSVRecord> tRecords = CSVFormat.DEFAULT.withQuote('\"').parse(targetReader);
		final Iterator<CSVRecord> iter = tRecords.iterator();
		final CSVRecord titleRow = iter.next();
		assertEquals(2, titleRow.size(), "correct num headings");

		final CSVRecord header = iter.next();

		// and the headings
		assertEquals(22, header.size(), "correct num headings");

		// and some checking
		assertEquals("# Lat", header.get(0), "Correct heading 0");
		assertEquals("Long", header.get(1), "Correct heading 1");
		assertEquals("DTG", header.get(2), "Correct heading 2");
		assertEquals("UnitName", header.get(3), "Correct heading 3");
		assertEquals("CaseNumber", header.get(4), "Correct heading 4");
		assertEquals("Type", header.get(5), "Correct heading 5");
		assertEquals("Flag", header.get(6), "Correct heading 6");
		assertEquals("Sensor", header.get(7), "Correct heading 7");
		assertEquals("MajorAxis", header.get(8), "Correct heading 8");
		assertEquals("SemiMajorAxis", header.get(9), "Correct heading 9");
		assertEquals("SemiMinorAxis", header.get(10), "Correct heading 10");
		assertEquals("Course", header.get(11), "Correct heading 11");
		assertEquals("Speed", header.get(12), "Correct heading 12");
		assertEquals("Depth", header.get(13), "Correct heading 13");
		assertEquals("Likelihood", header.get(14), "Correct heading 14");
		assertEquals("Confidence", header.get(15), "Correct heading 15");
		assertEquals("SuppliedBy", header.get(16), "Correct heading 16");
		assertEquals("Provenance", header.get(17), "Correct heading 17");
		assertEquals("InfoCutoffDate", header.get(18), "Correct heading 18");
		assertEquals("Purpose", header.get(19), "Correct heading 19");
		assertEquals("Classification", header.get(20), "Correct heading 20");
		assertEquals("DistributionStatement", header.get(21).trim(), "Correct heading 21");

		// and the content
		final CSVRecord rowOne = iter.next();
		assertEquals(22, rowOne.size(), "correct entries");
		assertEquals(2d, Double.valueOf(rowOne.get(0)), "correct val 0");
		assertEquals(4d, Double.valueOf(rowOne.get(1)), "correct val 1");
		assertEquals("19700101T001640Z", rowOne.get(2), "correct val 2");
		assertEquals("OWNSHIP_NAME, PART 2", rowOne.get(3), "correct val 3");
		assertEquals("21B", rowOne.get(4), "correct val 4");
		assertEquals("OILER", rowOne.get(5), "correct val 5");
		assertEquals("MAURETANIA", rowOne.get(6), "correct val 6");
		assertEquals("SensorName", rowOne.get(7), "correct val 7");
		assertEquals(4000d, Double.valueOf(rowOne.get(8)), "correct val 8");
		assertEquals(2000d, Double.valueOf(rowOne.get(9)), "correct val 9");
		assertEquals(1000d, Double.valueOf(rowOne.get(10)), "correct val 10");
		assertEquals(180d, Double.valueOf(rowOne.get(11)), "correct val 11");
		assertEquals(17.7745d, Double.valueOf(rowOne.get(12)), "correct val 12");
		assertEquals(0d, Double.valueOf(rowOne.get(13)), "correct val 13");
		assertEquals("UNLIKELY", rowOne.get(14), "correct val 14");
		assertEquals("Hopefully", rowOne.get(15), "correct val 15");
		assertEquals("DeepBlue", rowOne.get(16), "correct val 16");
		assertEquals("Provenance", rowOne.get(17), "correct val 17");
		assertEquals("19700101", rowOne.get(18), "correct val 18");
		assertEquals("Purpose String", rowOne.get(19), "correct val 19");
		assertEquals("Public", rowOne.get(20), "correct val 20");
		assertEquals("Distribution, Statement", rowOne.get(21), "correct val 21");

		// quick look at next row
		final CSVRecord rowTwo = iter.next();
		assertEquals(22, rowTwo.size(), "correct entries");
		assertEquals(3d, Double.valueOf(rowTwo.get(0)), "correct val 0");
		assertEquals(6d, Double.valueOf(rowTwo.get(1)), "correct val 1");
		assertEquals("19700101T002000Z", rowTwo.get(2), "correct val 2");

		targetReader.close();

	}
}
