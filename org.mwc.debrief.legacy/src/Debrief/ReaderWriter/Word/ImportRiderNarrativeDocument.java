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
/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package Debrief.ReaderWriter.Word;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.TacticalData.TrackDataProvider;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportRiderNarrativeDocument {

	static class DocHelper implements WordHelper {
		final private Table _table;

		private int rowCount = 2;

		DocHelper(final Table table) {
			_table = table;
		}

		@Override
		public String getCell(final int i) {
			final TableRow row = _table.getRow(rowCount);
			final String res;
			if (row.numCells() <= i) {
				res = null;
			} else {
				res = row.getCell(i).text().trim();
			}
			return res;
		}

		@Override
		public String getFirstCell() {
			final TableRow headerRow = _table.getRow(0);
			final TableCell cell = headerRow.getCell(0);
			return cell.text().trim();
		}

		@Override
		public String getHeaderCell(final int num) {
			final TableRow headerRow = _table.getRow(1);
			final TableCell cell = headerRow.getCell(num);
			return cell.text().trim();
		}

		@Override
		public boolean hasMoreEntries() {
			final int rows = _table.numRows();
			return ++rowCount < rows;
		}

	}

	static class DocXHelper implements WordHelper {
		final private XWPFTable _table;

		private int rowCount = 2;

		DocXHelper(final XWPFTable table) {
			_table = table;
		}

		@Override
		public String getCell(final int i) {
			final XWPFTableRow row = _table.getRow(rowCount);
			final List<XWPFTableCell> cells = row.getTableCells();
			final String res;
			if (cells.size() <= i) {
				res = null;
			} else {
				res = row.getCell(i).getText().trim();
			}
			return res;
		}

		@Override
		public String getFirstCell() {
			final XWPFTableRow headerRow = _table.getRow(0);
			final XWPFTableCell cell = headerRow.getCell(0);
			return cell.getText().trim();
		}

		@Override
		public String getHeaderCell(final int num) {
			final XWPFTableRow headerRow = _table.getRow(1);
			final XWPFTableCell cell = headerRow.getCell(num);
			return cell.getText().trim();
		}

		@Override
		public boolean hasMoreEntries() {
			final int rows = _table.getRows().size();
			return ++rowCount < rows;
		}

	}

	static class Header {
		final Date startDate;
		final String platform;

		public Header(final Date date, final String platform) {
			startDate = date;
			this.platform = platform;
		}
	}

	private static class NoHostPlatformException extends Exception {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	}

	static class RiderEntry {
		final Date date;
		final Integer bearing;
		final Integer ambig;
		final String beam;
		final String text;

		public RiderEntry(final Date date, final Integer bearing, final Integer ambig, final String beam,
				final String text) {
			this.date = date;
			this.bearing = bearing;
			this.ambig = ambig;
			this.beam = beam;
			this.text = ImportNarrativeDocument.removeBadChars(text);
		}

		@Override
		public String toString() {
			// first the bearing bits
			final String ambigStr = ambig == null ? "" : "/" + ambig;
			final String brgStr = bearing == null ? "" : "Brg:" + bearing + ambigStr;
			final String beamStr = beam == null ? "" : "Beam:" + beam;
			final String separator = !"".equals(brgStr) && !"".equals(beamStr) ? ", " : "";
			final String res = "".equals(beamStr) && "".equals(brgStr) ? text
					: "[" + brgStr + separator + beamStr + "] " + text;
			return res;
		}
	}

	public static class TableBreakdown {
		final Header header;
		final List<RiderEntry> entries;

		public TableBreakdown(final Header header, final List<RiderEntry> entries) {
			this.header = header;
			this.entries = entries;
		}
	}

	private static interface WordHelper {
		public String getCell(int i);

		public String getFirstCell();

		public String getHeaderCell(int num);

		public boolean hasMoreEntries();
	}

	static final String DATE_FORMAT_STR = "ddHHmm'Z' MMM yy";

	static final String DTG_MISSING_STR = "[DTG missing. Re-using previous DTG]";

	public static final String RIDER_SOURCE = "Rider";

	static final String NARRATIVE_CUTS_SENSOR = "FromNarrative";

	private static List<String> SkipNames = null;

	static boolean canImport(final WordHelper helper) {
		boolean isRider = false;
		String firstCell = helper.getFirstCell();

		if (firstCell != null) {
			// this block of text may have new-line or whitespace in it. Clean the text
			// before we do the compare
			firstCell = firstCell.trim();
			firstCell = firstCell.replace(" ", "");
			firstCell = firstCell.replace("\n", "");
			firstCell = firstCell.replace("\r", "");

			if (firstCell.toUpperCase().equals("DTGSTART")) {
				isRider = true;
			}
		}

		return isRider;
	}

	static long dateFor(final Date start, final Date time) {
		return start.getTime() + time.getTime();
	}

	private static List<RiderEntry> entriesFor(final WordHelper helper, final Header header,
			final SimpleDateFormat timeFormat) throws ParseException {
		final List<RiderEntry> res = new ArrayList<RiderEntry>();

		// track the last date, in case a date is missing
		Date lastDate = null;

		while (helper.hasMoreEntries()) {
			// ok, parse this row
			final String dateStr = helper.getCell(0);
			final String bearingStr = helper.getCell(1);
			final String beamStr = helper.getCell(2);
			final String text = helper.getCell(3);

			// just check if we have enough data
			if (dateStr == null || bearingStr == null || beamStr == null || text == null) {
				// ok, go to the next row
				continue;
			}

			// ability to store errors
			String warningStr = "";

			final Date date;
			if (!"".equals(dateStr)) {
				date = timeFormat.parse(dateStr);

				// cache the date;
				lastDate = date;
			} else {
				if (lastDate != null) {
					date = new Date(lastDate.getTime() + 1);
					warningStr += DTG_MISSING_STR;
				} else {
					date = null;
					logError(ErrorLogger.ERROR, "Unable to find date in first row. Skipping this row", null);
					continue;
				}
			}

			final Integer bearing;
			final Integer ambig;
			if (bearingStr.isEmpty() || bearingStr.equalsIgnoreCase("NO B")) {
				bearing = null;
				ambig = null;
			} else {
				// hmm, do we have ambig data?
				if (bearingStr.contains("/")) {
					final String[] items = bearingStr.trim().split("/");
					if (items.length == 2) {
						bearing = Integer.parseInt(items[0].trim());
						ambig = Integer.parseInt(items[1].trim());
					} else {
						bearing = null;
						ambig = null;
						logThisError(ErrorLogger.ERROR, "Not correct number of bearings to be ambiguous", null);
					}
				} else if (bearingStr.contains("-")) {
					// it's an arc, ignore the bearing
					bearing = null;
					ambig = null;
					warningStr += "[Bearing represents arc (" + bearingStr + "). Not imported.]";
				} else {
					bearing = Integer.parseInt(bearingStr);
					ambig = null;
				}
			}

			final String beam;
			if (beamStr.isEmpty() || beamStr.equalsIgnoreCase("NO BM")) {
				beam = null;
			} else {
				beam = beamStr;
			}

			final Date newDate = new Date(dateFor(header.startDate, date));

			final String finalText = warningStr + text;

			final RiderEntry entry = new RiderEntry(newDate, bearing, ambig, beam, finalText);
			res.add(entry);

		}
		return res;
	}

	static SensorWrapper findOurSensor(final String name, final TrackWrapper parent,
			final boolean allowCreate) {
		final BaseLayer sensors = parent.getSensors();
		final Enumeration<Editable> sIter = sensors.elements();
		while (sIter.hasMoreElements()) {
			final SensorWrapper thisS = (SensorWrapper) sIter.nextElement();
			if (thisS.getName().equals(name)) {
				return thisS;
			}
		}
		if (allowCreate) {
			// aah. we didn't find it. make one
			final SensorWrapper newS = new SensorWrapper(name);
			parent.add(newS);
			return newS;
		} else {
			return null;
		}
	}

	private static Header headerFor(final WordHelper helper, final SimpleDateFormat dateFormat) throws ParseException {
		final String dateText = helper.getHeaderCell(0).trim();

		// ok. we've encountered a document where the DTG was 220001, not 220000
		// this resulted in one minute being added to all resulting times.

		final String platform = helper.getHeaderCell(4);
		final Date date = tidyDate(dateText, dateFormat);
		final Header header = new Header(date, platform);
		return header;
	}

	private static void logError(final int status, final String msg, final Exception e) {
		logThisError(status, msg, e);
	}

	private static void logThisError(final int status, final String msg, final Exception e) {
		Application.logError3(status, msg, e, true);
	}

	static Date tidyDate(final String dateStr, final DateFormat dateFormat) throws ParseException {
		// if the date looks like this: "220001Z JUL 09" change it to this "220000Z JUL
		// 09"
		final Date date = dateFormat.parse(dateStr);

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);

		return calendar.getTime();
	}

	/**
	 * keep track of which track-source combinations we've asked about
	 *
	 */
	private final List<String> askedAbout = new ArrayList<String>();

	/**
	 * flag for if we've informed user that we couldn't find host track
	 *
	 */
	private boolean _declaredNoHostFound = false;

	/**
	 * where we write our data
	 *
	 */
	private final Layers _layers;

	/**
	 * keep track of track names that we have matched
	 *
	 */
	Map<String, String> nameMatches = new HashMap<String, String>();

	final SimpleDateFormat DATE_FORMAT;

	final SimpleDateFormat TIME_FORMAT;

	private final TrackDataProvider _trackProvider;

	/**
	 *
	 * @param target        where to dump the data
	 * @param trackProvider how to find the primary track
	 */
	public ImportRiderNarrativeDocument(final Layers target, final TrackDataProvider trackProvider) {
		_layers = target;
		_trackProvider = trackProvider;

		DATE_FORMAT = new GMTDateFormat(DATE_FORMAT_STR);
		TIME_FORMAT = new GMTDateFormat("HH:mm:ss");

		if (SkipNames == null) {
			SkipNames = new ArrayList<String>();
			SkipNames.add("HMS");
			SkipNames.add("Hms");
			SkipNames.add("USS");
			SkipNames.add("RNAS");
			SkipNames.add("HNLMS");
		}
	}

	private void addCut(final RiderEntry thisN, final String platform) throws NoHostPlatformException {
		String hisTrack = trackFor(platform, platform);

		// did we find a track? Don't worry if we didn't just use the raw text
		if (hisTrack == null) {
			hisTrack = platform;
		}

		// can we find the parent?
		final TrackWrapper parent = (TrackWrapper) _layers.findLayer(hisTrack);

		if (parent != null) {
			// find our sensor
			final SensorWrapper ourSensor = findOurSensor(NARRATIVE_CUTS_SENSOR, parent, true);

			final HiResDate theDate = new HiResDate(thisN.date.getTime());

			final Double ambigBearing = thisN.ambig != null ? new Double(thisN.ambig) : null;

			final SensorContactWrapper cut = new SensorContactWrapper(parent.getName(), theDate, null,
					new Double(thisN.bearing), ambigBearing, null, null, DebriefColors.RED, "NARRATIVE", 0, hisTrack);

			ourSensor.add(cut);
		}
	}

	private void addEntry(final RiderEntry thisN, final String platform, final NarrativeWrapper narrative)
			throws NoHostPlatformException {
		String hisTrack = trackFor(platform, platform);

		// did we find a track? Don't worry if we didn't just use the raw text
		if (hisTrack == null) {
			hisTrack = platform;
		}

		final String textBit = thisN.toString();

		// sort out the time
		final long correctedDTG = thisN.date.getTime();

		final NarrativeEntry ne = new NarrativeEntry(hisTrack, RIDER_SOURCE, new HiResDate(correctedDTG), textBit);

		// shade all rider's narratives black
		ne.setColor(DebriefColors.BLACK);

		// and store it
		narrative.add(ne);
	}

	private NarrativeWrapper getNarrativeLayer() {
		NarrativeWrapper nw = (NarrativeWrapper) _layers.findLayer(LayerHandler.NARRATIVE_LAYER);

		if (nw == null) {
			nw = new NarrativeWrapper(LayerHandler.NARRATIVE_LAYER);
			_layers.addThisLayer(nw);
		}

		return nw;
	}

	public void handleImport(final String fileName, final InputStream inputStream) {
		// ok, read it into a document
		HWPFDocument doc = null;

		try {
			doc = new HWPFDocument(inputStream);
		} catch (final IllegalArgumentException xw) {
			logError(ErrorLogger.WARNING, ".Doc file appears to contain .Docx data. Switching to other importer", xw);
			// ok, it's .docx data in a .doc file
			try {
				final FileInputStream is2 = new FileInputStream(fileName);
				handleImportX(fileName, is2);
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

		if (doc != null) {
			// ok, now have a see if it's a rider's narrative
			final Range range = doc.getRange();
			final TableIterator tIter = new TableIterator(range);

			final boolean isRider = tIter.hasNext() && canImport(new DocHelper(tIter.next()));

			if (isRider) {
				final TableBreakdown data = this.importFromWord(doc);
				this.processThese(data);
			} else {
				final ImportNarrativeDocument iw = new ImportNarrativeDocument(_layers);
				final ArrayList<String> strings = ImportNarrativeDocument.importFromWord(doc);
				handleOtherImport(strings, iw);
			}
		}
	}

	public void handleImportX(final String fileName, final InputStream inputStream) {
		// ok, read it into a document
		XWPFDocument doc = null;

		try {
			doc = new XWPFDocument(inputStream);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// ok, now have a see if it's a rider's narrative
		final List<XWPFTable> tables = doc.getTables();
		final boolean isRider = (tables != null && tables.size() > 0 && canImport(new DocXHelper(tables.get(0))));

		if (isRider) {
			final TableBreakdown data = this.importFromWordX(doc);
			this.processThese(data);
		} else {
			final ImportNarrativeDocument iw = new ImportNarrativeDocument(_layers);
			final ArrayList<String> strings = ImportNarrativeDocument.importFromWordX(doc);
			handleOtherImport(strings, iw);
		}
	}

	private void handleOtherImport(final ArrayList<String> strings, final ImportNarrativeDocument iw) {
		// ok, just do a quick check that thse aren't in the ASW DAta format
		if (ImportASWDataDocument.canImport(strings)) {
			final ImportASWDataDocument idw = new ImportASWDataDocument(_layers);
			System.out.println("IMPORTING ASW DOCUMENT");
			idw.processThese(strings);
		} else {
			iw.processThese(strings);
		}
	}

	TableBreakdown importFromWord(final HWPFDocument doc) {
		TableBreakdown data = null;

		try {
			final Range range = doc.getRange();

			final TableIterator tIter = new TableIterator(range);

			final Table table = tIter.next();

			final DocHelper helper = new DocHelper(table);

			final Header header = headerFor(helper, DATE_FORMAT);
			final List<RiderEntry> entries = entriesFor(helper, header, TIME_FORMAT);
			data = new TableBreakdown(header, entries);

		} catch (final ParseException e) {
			e.printStackTrace();
		}

		return data;
	}

	TableBreakdown importFromWordX(final XWPFDocument doc) {
		TableBreakdown data = null;

		try {
			final List<XWPFTable> tables = doc.getTables();

			if (tables.size() > 1) {
				throw new IllegalArgumentException("Wrongly formatted document");
			}

			final XWPFTable myTable = tables.get(0);

			final DocXHelper helper = new DocXHelper(myTable);

			final Header header = headerFor(helper, DATE_FORMAT);
			final List<RiderEntry> entries = entriesFor(helper, header, TIME_FORMAT);
			data = new TableBreakdown(header, entries);
		} catch (final ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				if (doc != null) {
					doc.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		return data;
	}

	/**
	 * parse a list of strings
	 *
	 * @param data
	 */
	void processThese(final TableBreakdown data) {
		if (data == null) {
			return;
		}

		// keep track of if we've added anything
		boolean dataAdded = false;

		try {
			NarrativeWrapper narrative = null;

			// ok, now we can loop through the strings
			for (final RiderEntry thisN : data.entries) {
				// does it have a bearing?
				if (thisN.bearing != null) {
					addCut(thisN, data.header.platform);
				}

				if (narrative == null) {
					// note, we defer getting/creating the narrative layer
					// until we get here, since if we can't find a parent
					// track to use, we won't load any data
					narrative = getNarrativeLayer();
				}

				// add a narrative entry
				addEntry(thisN, data.header.platform, narrative);

				// ok, take note that we've added something
				dataAdded = true;
			}

			if (dataAdded) {
				_layers.fireModified(getNarrativeLayer());
			}
		} catch (final NoHostPlatformException ne) {
			// ok, user should have been warned. We're done.
		}
	}

	/**
	 * is there a single visible track present?
	 *
	 * @param layers
	 * @param narrativeName
	 * @return
	 */
	private TrackWrapper singleTrackPresent(final Layers layers, final String narrativeName) {
		final TrackWrapper res;
		TrackWrapper candidate = null;
		boolean singleCandidate = false;

		// loop through the layers, see if there is a single track present
		final int ctr = layers.size();
		for (int i = 0; i < ctr; i++) {
			final Layer thisL = layers.elementAt(i);
			if (thisL.getVisible() && thisL instanceof TrackWrapper) {
				// have we already asked about this platform
				final String thisPerm = thisL.getName() + narrativeName;
				if (!askedAbout.contains(thisPerm)) {
					// nope, go for it

					// ok, have we found one already?
					if (candidate != null) {
						// bugger, more than one track. don't bother
						singleCandidate = false;
						break;
					} else {
						// hey, it's a maybe
						candidate = (TrackWrapper) thisL;

						// remember we've found one
						singleCandidate = true;
					}
				}
			}
		}

		if (singleCandidate) {
			res = candidate;
		} else {
			res = null;
		}

		return res;
	}

	private String trackFor(final String originalName, String name) throws NoHostPlatformException {
		if (name == null) {
			name = originalName;
		}

		final String platform = name.trim();
		String match = nameMatches.get(platform);
		if (match == null) {
			// search the layers
			final Layer theL = _layers.findLayer(platform);
			if (theL != null) {
				match = theL.getName();
				nameMatches.put(originalName, match);
			} else {
				// try skipping then names
				final Iterator<String> nameIter = SkipNames.iterator();
				while (nameIter.hasNext() && match == null) {
					final String thisSkip = nameIter.next();
					if (platform.startsWith(thisSkip)) {
						final String subStr = platform.substring(thisSkip.length()).trim();
						match = trackFor(originalName, subStr);
					}
				}

				if (match == null) {
					// ok, if there is just one track present, invite the user to use that
					final TrackWrapper singleTrack = singleTrackPresent(_layers, name);

					// did we find one?
					if (singleTrack != null) {
						// ok, just use the name of this single track
						match = singleTrack.getName();
						nameMatches.put(originalName, match);
					} else {
						// we can't find a host track.

						// is there a primary allocated?
						if (_trackProvider != null) {
							final WatchableList parent = _trackProvider.getPrimaryTrack();
							if (parent != null) {
								// cool, use it
								match = parent.getName();
								nameMatches.put(originalName, match);
							}
						}

						// have we already told the user?
						if (match == null && !_declaredNoHostFound) {
							// ok, stop it appearing again
							_declaredNoHostFound = true;

							// tell the user
							MessageProvider.Base.show("Import Rider's Narrative",
									"Platform in Rider's narrative doesn't match any loaded tracks.\nPlease assign a primary, and the cuts will be added to that platform.",
									MessageProvider.ERROR);

							throw new NoHostPlatformException();
						}
					}
				}
			}
		}

		return match;
	}
}
