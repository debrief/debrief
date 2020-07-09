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
import junit.framework.TestCase;

/**
 * @author ian.mayo
 */
public class ExportTrackAsCSV implements RightClickContextItemGenerator {
	public static interface CSVAttributeProvider {
		String getCaseNumber();

		String getClassification();

		String getConfidence();

		String getDistributionStatement();

		String getFilePath();

		String getFlag();

		String getLikelihood();

		String getProvenance();

		String getPurpose();

		String getSemiMajorAxis();

		String getSemiMinorAxis();

		String getSensor();

		String getSuppliedBy();

		String getType();

		String getUnitName();

	}

	private static class ExportTrackToCSV extends CMAPOperation {

		private static String clean(final String freeText) {
			return freeText.replace(",", "_COMMA_");
		}

		public static String getFileName(final LightweightTrackWrapper subject, final CSVAttributeProvider provider) {
			final DateFormat fileDateFormat = new GMTDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);

			final StringBuffer fileName = new StringBuffer();
			fileName.append(tidyMe(provider.getSuppliedBy()));
			fileName.append("_");
			fileName.append(tidyMe(fileDateFormat.format(new Date())));
			fileName.append("_");
			fileName.append(tidyMe(provider.getUnitName()));
			fileName.append("_");
			fileName.append(tidyMe("" + subject.numFixes()));
			fileName.append("_");
			fileName.append(tidyMe(provider.getClassification()));
			fileName.append(".csv");
			return fileName.toString();
		}

		private static List<String> outputStrings(final LightweightTrackWrapper subject,
				final CSVAttributeProvider provider) {
			final List<String> res = new ArrayList<String>();

			final DateFormat dateFormatter = new GMTDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
			final DateFormat cutOffFormatter = new GMTDateFormat("yyyyMMdd", Locale.ENGLISH);

			final NumberFormat numF = new DecimalFormat("0.0000");

			final String lineBreak = System.getProperty("line.separator");

			// find the last time on the track
			final Date lastDTG = subject.getEndDTG().getDate();
			final String infoCutoffDate = cutOffFormatter.format(lastDTG);

			// capture the constants
			final String provenance = cleanPlatform(provider.getProvenance());
			final String unitName = cleanPlatform(provider.getUnitName());
			final String caseNumber = provider.getCaseNumber();
			final String suppliedBy = provider.getSuppliedBy();
			final String purpose = provider.getPurpose();
			final String classification = provider.getClassification();
			final String distributionStatement = cleanPhrase(provider.getDistributionStatement());
			final String type = provider.getType();
			final String flag = provider.getFlag();
			final String sensor = provider.getSensor();
			final String semiMajorAxis = provider.getSemiMajorAxis();
			final String majorAxis = "" + Double.valueOf(semiMajorAxis) * 2d;
			final String semiMinorAxis = provider.getSemiMinorAxis();
			final String likelihood = provider.getLikelihood();
			final String confidence = provider.getConfidence();

			// ok, collate the data
			final StringBuffer header = new StringBuffer();

			// start with the comment markers

			// first the version num
			header.append("# UK TRACK EXCHANGE FORMAT, V1.0");
			header.append(lineBreak);
			res.add(header.toString());

			final StringBuffer fields = new StringBuffer();

			// now the fields
			fields.append("# Lat,Long,DTG,UnitName,CaseNumber,Type,Flag,Sensor,MajorAxis,"
					+ "SemiMajorAxis,SemiMinorAxis,Course,Speed,Depth,Likelihood,"
					+ "Confidence,SuppliedBy,Provenance,InfoCutoffDate,Purpose,"
					+ "Classification,DistributionStatement");
			fields.append(lineBreak);
			res.add(fields.toString());

			final Enumeration<Editable> iter = subject.getPositionIterator();
			while (iter.hasMoreElements()) {
				final StringBuffer lineOut = new StringBuffer();

				// note: in this export process we either wrap text in quotes, or clean it, to
				// remove
				// commas.

				final FixWrapper next = (FixWrapper) iter.nextElement();
				lineOut.append(write(next.getLocation()));
				lineOut.append(",");
				lineOut.append(write(next.getDTG(), dateFormatter));
				lineOut.append(",");
				lineOut.append(write(unitName));
				lineOut.append(",");
				lineOut.append(clean(caseNumber));
				lineOut.append(",");
				lineOut.append(write(type));
				lineOut.append(",");
				lineOut.append(clean(flag));
				lineOut.append(",");
				lineOut.append(write(sensor));
				lineOut.append(",");
				lineOut.append(clean(majorAxis));
				lineOut.append(",");
				lineOut.append(clean(semiMajorAxis));
				lineOut.append(",");
				lineOut.append(clean(semiMinorAxis));
				lineOut.append(",");
				lineOut.append(numF.format(MWC.Algorithms.Conversions.Rads2Degs(next.getCourse())));
				lineOut.append(",");
				lineOut.append(numF.format(next.getSpeed()));
				lineOut.append(",");
				lineOut.append(next.getLocation().getDepth());
				lineOut.append(",");
				lineOut.append(clean(likelihood));
				lineOut.append(",");
				lineOut.append(clean(confidence));
				lineOut.append(",");
				lineOut.append(write(suppliedBy));
				lineOut.append(",");
				lineOut.append(write(provenance));
				lineOut.append(",");
				lineOut.append(infoCutoffDate);
				lineOut.append(",");
				lineOut.append(write(purpose));
				lineOut.append(",");
				lineOut.append(clean(classification));
				lineOut.append(",");
				lineOut.append(write(distributionStatement));

				// and the newline
				lineOut.append(lineBreak);

				res.add(lineOut.toString());
			}

			return res;
		}

		private static void performExport(final LightweightTrackWrapper subject, final CSVAttributeProvider provider) {
			FileWriter fos = null;
			try {
				// sort out the destination filename
				final File outFile = new File(provider.getFilePath(), getFileName(subject, provider));
				System.out.println("Writing data to:" + outFile.getAbsolutePath());
				fos = new FileWriter(outFile);

				final List<String> strings = outputStrings(subject, provider);

				// done.
				for (final String line : strings) {
					fos.write(line);
				}
			} catch (final IOException e) {
				CorePlugin.logError(IStatus.ERROR, "Error while writing to CSV exchange file", e);
			} catch (final NumberFormatException e) {
				CorePlugin.logError(IStatus.ERROR, "Error while calculating Major Axis", e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (final IOException e) {
						CorePlugin.logError(IStatus.ERROR, "Error while closing CSV exchange file", e);
					}
				}
			}
			System.out.println("File write complete");
		}

		protected static String tidyMe(final String input) {
			// replace any chars that are not a alphanumeric with an underscore
			return input.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}]", "_");
		}

		private static String write(final HiResDate dtg, final DateFormat dateFormat) {
			return dateFormat.format(dtg.getDate());
		}

		private static String write(final String freeText) {
			return "\"" + freeText + "\"";
		}

		private static Object write(final WorldLocation location) {
			return location.getLat() + ", " + location.getLong();
		}

		/**
		 * the parent to update on completion
		 */
		private final LightweightTrackWrapper _subject;

		public ExportTrackToCSV(final String title, final LightweightTrackWrapper subject) {
			super(title);
			_subject = subject;
		}

		@Override
		public boolean canRedo() {
			return false;
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// get the set of fields we need
			final DropdownProvider reg = CSVExportDropdownRegistry.getRegistry();

			if (reg == null) {
				return Status.CANCEL_STATUS;
			}

			// the wizard needs some other data
			final String unit = _subject.getName();

			// see if we can get the primary track
			final IEditorPart editor = CorePlugin.getActiveWindow().getActivePage().getActiveEditor();
			if (editor == null) {
				CorePlugin.logError(IStatus.ERROR, "Export to CSV couldn't find current editor", null);
				return Status.CANCEL_STATUS;
			}
			final TrackManager trackManager = editor.getAdapter(TrackManager.class);
			final String provenance;
			if (trackManager != null) {
				final WatchableList primary = trackManager.getPrimaryTrack();
				if (primary != null) {
					provenance = primary.getName();
				} else {
					provenance = null;
				}
			} else {
				provenance = null;
			}

			// WIZARD OPENS HERE
			final CSVExportWizard wizard = new CSVExportWizard(reg, unit, provenance);

			final WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.create();
			dialog.open();

			// did it work?
			if (dialog.getReturnCode() == Window.OK) {
				final CSVAttributeProvider provider = wizard;

				performExport(_subject, provider);
			}

			// return CANCEL so this event doesn't get put onto the undo buffer,
			// and unnecessarily block the undo queue
			return Status.CANCEL_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			CorePlugin.logError(IStatus.INFO, "Undo not relevant to export Track to CSV", null);
			return null;
		}
	}

	public static class TestExport extends TestCase {

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

		public void testClean() {
			assertEquals("A_12", ExportTrackToCSV.tidyMe("A:12"));
		}

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

			assertEquals("correct num of lines", 4, strings.size());

			final Reader targetReader = new StringReader(bugger.toString());

			final Iterable<CSVRecord> tRecords = CSVFormat.DEFAULT.withQuote('\"').parse(targetReader);
			final Iterator<CSVRecord> iter = tRecords.iterator();
			final CSVRecord titleRow = iter.next();
			assertEquals("correct num headings", 2, titleRow.size());

			final CSVRecord header = iter.next();

			// and the headings
			assertEquals("correct num headings", 22, header.size());

			// and some checking
			assertEquals("Correct heading 0", "# Lat", header.get(0));
			assertEquals("Correct heading 1", "Long", header.get(1));
			assertEquals("Correct heading 2", "DTG", header.get(2));
			assertEquals("Correct heading 3", "UnitName", header.get(3));
			assertEquals("Correct heading 4", "CaseNumber", header.get(4));
			assertEquals("Correct heading 5", "Type", header.get(5));
			assertEquals("Correct heading 6", "Flag", header.get(6));
			assertEquals("Correct heading 7", "Sensor", header.get(7));
			assertEquals("Correct heading 8", "MajorAxis", header.get(8));
			assertEquals("Correct heading 9", "SemiMajorAxis", header.get(9));
			assertEquals("Correct heading 10", "SemiMinorAxis", header.get(10));
			assertEquals("Correct heading 11", "Course", header.get(11));
			assertEquals("Correct heading 12", "Speed", header.get(12));
			assertEquals("Correct heading 13", "Depth", header.get(13));
			assertEquals("Correct heading 14", "Likelihood", header.get(14));
			assertEquals("Correct heading 15", "Confidence", header.get(15));
			assertEquals("Correct heading 16", "SuppliedBy", header.get(16));
			assertEquals("Correct heading 17", "Provenance", header.get(17));
			assertEquals("Correct heading 18", "InfoCutoffDate", header.get(18));
			assertEquals("Correct heading 19", "Purpose", header.get(19));
			assertEquals("Correct heading 20", "Classification", header.get(20));
			assertEquals("Correct heading 21", "DistributionStatement", header.get(21).trim());

			// and the content
			final CSVRecord rowOne = iter.next();
			assertEquals("correct entries", 22, rowOne.size());
			assertEquals("correct val 0", 2d, Double.valueOf(rowOne.get(0)));
			assertEquals("correct val 1", 4d, Double.valueOf(rowOne.get(1)));
			assertEquals("correct val 2", "19700101T001640Z", rowOne.get(2));
			assertEquals("correct val 3", "OWNSHIP_NAME, PART 2", rowOne.get(3));
			assertEquals("correct val 4", "21B", rowOne.get(4));
			assertEquals("correct val 5", "OILER", rowOne.get(5));
			assertEquals("correct val 6", "MAURETANIA", rowOne.get(6));
			assertEquals("correct val 7", "SensorName", rowOne.get(7));
			assertEquals("correct val 8", 4000d, Double.valueOf(rowOne.get(8)));
			assertEquals("correct val 9", 2000d, Double.valueOf(rowOne.get(9)));
			assertEquals("correct val 10", 1000d, Double.valueOf(rowOne.get(10)));
			assertEquals("correct val 11", 180d, Double.valueOf(rowOne.get(11)));
			assertEquals("correct val 12", 17.7745d, Double.valueOf(rowOne.get(12)));
			assertEquals("correct val 13", 0d, Double.valueOf(rowOne.get(13)));
			assertEquals("correct val 14", "UNLIKELY", rowOne.get(14));
			assertEquals("correct val 15", "Hopefully", rowOne.get(15));
			assertEquals("correct val 16", "DeepBlue", rowOne.get(16));
			assertEquals("correct val 17", "Provenance", rowOne.get(17));
			assertEquals("correct val 18", "19700101", rowOne.get(18));
			assertEquals("correct val 19", "Purpose String", rowOne.get(19));
			assertEquals("correct val 20", "Public", rowOne.get(20));
			assertEquals("correct val 21", "Distribution, Statement", rowOne.get(21));

			// quick look at next row
			final CSVRecord rowTwo = iter.next();
			assertEquals("correct entries", 22, rowTwo.size());
			assertEquals("correct val 0", 3d, Double.valueOf(rowTwo.get(0)));
			assertEquals("correct val 1", 6d, Double.valueOf(rowTwo.get(1)));
			assertEquals("correct val 2", "19700101T002000Z", rowTwo.get(2));

			targetReader.close();

		}
	}

	private static String cleanPhrase(final String statement) {
		final String res = statement; // no - allow commas statement.replace(",", "");
		return res;
	}

	private static String cleanPlatform(final String provenance) {
		final String res = provenance.replace(".", "_");
		return res;
	}

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	@Override
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects) {
		// see if use wants to see this command.
		final IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
		final boolean isEnabled = store.getBoolean(ExportCSVPreferencesPage.PreferenceConstants.INCLUDE_COMMAND);

		if (!isEnabled)
			return;

		LightweightTrackWrapper subject = null;

		// we're only going to work with two or more items
		if (subjects.length == 1) {
			final Editable item = subjects[0];
			if (item instanceof LightweightTrackWrapper) {
				subject = (LightweightTrackWrapper) item;
			}
		}

		// ok, is it worth going for?
		if (subject != null) {

			// right,stick in a separator
			parent.add(new Separator());

			final String theTitle = "Export Track to CSV Text format";
			final LightweightTrackWrapper finalItem = subject;

			// create this operation
			final Action doExport = new Action(theTitle) {
				@Override
				public void run() {
					final IUndoableOperation theAction = new ExportTrackToCSV(theTitle, finalItem);

					CorePlugin.run(theAction);
				}
			};
			doExport.setImageDescriptor(CorePlugin.getImageDescriptor("icons/16/export-csv.png"));
			parent.add(doExport);
		}
	}

}
