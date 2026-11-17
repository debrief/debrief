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

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.NMEA.ImportNMEA;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportNarrativeDocument {
	/**
	 * collection of fields for an FCS entry
	 *
	 * @author ian
	 *
	 */
	static class FCSEntry {
		static String getClassified(final String input) {
			String res = null;

			final String regexp = "Classified (.*$)";
			final Pattern pattern = Pattern.compile(regexp);

			final Matcher matcher = pattern.matcher(input);
			if (matcher.find()) {
				res = matcher.group(1);
			}

			return res;
		}

		/**
		 * get the element that starts with the provided identifier
		 *
		 * @param identifier
		 * @param input
		 * @return
		 */
		static Double getElement(final String identifier, final String input) {
			Double res = null;

			final String regexp = identifier + "-*(\\d+\\.?\\d*)";
			final Pattern pattern = Pattern.compile(regexp);

			final Matcher matcher = pattern.matcher(input);
			if (matcher.find()) {
				final String found = matcher.group(1);
				try {
					res = Double.parseDouble(found);
				} catch (final NumberFormatException fe) {
					// ok, we failed :-(
				}
			}

			return res;
		}

		/**
		 * special element handler that can accommodate a range of types of units
		 *
		 * @param tidied
		 * @return
		 */
		static WorldDistance getRange(final String input) {
			final WorldDistance res;

			// replace newline control characters
			String tidied = input.replace("\n", "");
			tidied = tidied.replace("\r", "");

			final String regexp = ".*R-(?<RANGE>\\d+\\.?\\d?)(?:\\s*)(?<UNITS>\\w*?)(?:\\.|\\s).*";
			final Matcher m = Pattern.compile(regexp).matcher(tidied);
			if (m.matches()) {
				final double range = Double.valueOf(m.group("RANGE"));
				final String units = m.group("UNITS");

				// ok, create the relevant object
				if (units.toUpperCase().equals("KYDS")) {
					res = new WorldDistance(range, WorldDistance.KYDS);
				} else if (units.toUpperCase().equals("YDS")) {
					res = new WorldDistance(range, WorldDistance.YARDS);
				} else if (units.toUpperCase().equals("M")) {
					res = new WorldDistance(range, WorldDistance.METRES);
				} else {
					res = null;
				}
			} else {
				res = null;
			}

			return res;
		}

		static String parseSource(final String str) {
			// replace newline control characters
			String tidied = str.replace("\n", "");
			tidied = tidied.replace("\r", "");
			tidied = tidied.trim();

			final String regexp = ".*([A-Z]{1,4}\\d{3}|M\\d{2})(?<SOURCE>.*)B-.*";
			final Matcher m = Pattern.compile(regexp).matcher(tidied);
			final String res;
			if (m.matches()) {
				final String source = m.group("SOURCE").trim();

				// ok, special processing. We're getting unpredicable extra text
				// in the source field (between FCS and "B-". So
				// do some inspection to decide what to show
				if (source.contains("LOP")) {
					res = "LOP";
				} else if (source.contains("SMCS")) {
					res = "SMCS";
				} else if (source.contains("WECDIS")) {
					res = "WECDIS";
				} else if (source.contains("1936")) {
					res = "1936";
				} else if (source.contains("1959")) {
					res = "1959";
				} else if (source.contains("CMD")) {
					res = "CMD";
				} else if (source.contains("WECDIS")) {
					res = "WECDIS";
				} else if (source.toUpperCase().contains("TRIANGULATION")) {
					res = "Triangulation";
				} else if (source.contains("HDPR")) {
					res = "HDPR";
				} else {
					res = source;
				}
			} else {
				res = null;
			}

			return res;
		}

		/**
		 * extract the track number from the provided string
		 *
		 * @param str
		 * @return
		 */
		static String parseTrack(final String str) {
			// note: we try to match the master track first, since sometimes
			// both are referred to in the FCS entry

			// NOTE: if we continue to get "thrown" by multiple references in the line,
			// then we should use a more prescriptive regexp, that starts with the
			// FCS marker

			final String shortTrackId = "(M\\d{2})";
			final Pattern shortPattern = Pattern.compile(shortTrackId);

			final Matcher matcher = shortPattern.matcher(str);
			final String res;
			if (matcher.find()) {
				res = matcher.group(1);
			} else {
				final String longTrackId = "[A-Z]{1,4}(\\d{3})";
				final Pattern longPattern = Pattern.compile(longTrackId);

				final Matcher matcher1 = longPattern.matcher(str);
				if (matcher1.find()) {
					res = matcher1.group(1);
				} else {
					res = null;
				}
			}

			return res;
		}

		/**
		 * have brg/rng as objects, so they can be null
		 *
		 */
		final Double brgDegs;
		final Double rangYds;

		final String tgtType;

		final String contact;

		final double crseDegs;

		final double spdKts;

		final String source;

		public FCSEntry(final String msg) {
			// pull out the matching strings
			final Double bVal = getElement("B-", msg);
			final WorldDistance rVal = getRange(msg);
			final Double cVal = getElement("C-", msg);
			final Double sVal = getElement("S-", msg);

			// extract the classification
			final String classStr = getClassified(msg);

			// try to extract the track id
			final String trackId = parseTrack(msg);
			final String source = parseSource(msg);

			this.crseDegs = cVal != null ? cVal : 0d;
			this.brgDegs = bVal != null ? bVal : null;
			this.rangYds = rVal != null ? rVal.getValueIn(WorldDistance.YARDS) : null;
			this.spdKts = sVal != null ? sVal : 0d;
			this.tgtType = classStr != null ? classStr : "N/A";
			this.contact = trackId != null ? trackId : "N/A";
			this.source = source != null ? source : "";
		}

	}

	public static enum ImportNarrativeEnum {
		TRIMMED_DATA(TRIMMED_DATA_STR), ALL_DATA(ALL_DATA_STR), CANCEL(CANCEL_STR);

		public static ImportNarrativeEnum getByName(final String name) {
			switch (name) {
			case TRIMMED_DATA_STR:
				return TRIMMED_DATA;
			case ALL_DATA_STR:
				return ALL_DATA;
			default:
				return CANCEL;
			}
		}

		private String name;

		ImportNarrativeEnum(final String string) {
			this.name = string;
		}

		public String getName() {
			return this.name;
		}
	}

	public static class NarrativeHelperRetVal {
		public ImportNarrativeEnum narrativeEnum;
		public List<String> selectedNarrativeTypes;
	}

	public static interface NarrativeTypeHelper {
		List<String> getSelectedNarrativeTypes(final Map<String, Integer> narrativeTypes);
	}

	static class NarrEntry {
		/**
		 * what is the last valid time we have. if time fields are missing we will
		 * extend from the last DTG
		 */
		private static Date lastDtg;
		/**
		 * what was the last platform we read in, in case the platform is missing
		 *
		 */
		private static String lastPlatform;
		/**
		 * what was the last entry? We remember it, so we can append ourselves to it
		 *
		 */
		private static NarrEntry lastEntry;
		/**
		 * we've encountered circumstances where copy/paste has ended up with the day
		 * being earlier than the current one When we can detect this, we'll use the
		 * previous day.
		 */
		private static String lastDay;

		/**
		 * don#t assume a decreasing day is wrong if the month has incremented
		 */
		private static String lastMonth;

		/**
		 * don#t assume a decreasing day is wrong if the year has incremented
		 */
		private static String lastYear;

		static public NarrEntry create(final String msg, final int lineNum) {
			NarrEntry res = null;
			try {
				res = new NarrEntry(msg);

				if (res.appendedToPrevious && res.text != null) {
					// that's ok - we'll let the parent handle it
				} else {
					// just check it's valid
					final boolean valid = (res.dtg != null) && (res.type != null) && (res.platform != null)
							&& (res.text != null);
					if (!valid) {
						res = null;
					}
				}
			} catch (final ParseException e) {
				logThisError(ToolParent.WARNING, "Failed whilst parsing Word Document, at line:" + lineNum, e);
			}

			return res;
		}

		/**
		 * reset the static variables we use to handle missing, or mangled data
		 *
		 */
		public static void reset() {
			lastDtg = null;
			lastPlatform = null;
			lastEntry = null;
			lastDay = null;
			lastMonth = null;
			lastYear = null;
		}

		HiResDate dtg;

		String type;

		String platform;

		String text;

		boolean appendedToPrevious = false;

		public NarrEntry(final String entry) throws ParseException {
			final String trimmed = entry.trim();
			final String[] parts = trimmed.split(",");

			final boolean correctLength = parts.length > 5;
			final boolean sixFigDTG = correctLength && parts[0].length() == 6 && parts[0].matches(DATE_MATCH_SIX);
			final boolean fourFigDTG = correctLength && parts[0].length() == 4 && parts[0].matches(DATE_MATCH_FOUR);

			if (sixFigDTG || fourFigDTG) {
				parseFullEntry(entry, parts, sixFigDTG);
			} else {
				parseShortEntry(entry, trimmed);
			}
		}

		/**
		 * create a GMT date from its components
		 *
		 * @param month zero-based month
		 */
		private static HiResDate gmtDate(final int year, final int month, final int day, final int hours,
				final int mins) {
			final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.set(year, month, day, hours, mins, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return new HiResDate(cal.getTime());
		}

		/**
		 * special processing, to overcome problem with entries being pulled back from
		 * the next day. The problem has occurred when something that happened at, say
		 * 2345 only gets entered at 0005, so the user moves the entry back to the real
		 * time
		 *
		 * @param sixFigDTG the DDHHMM field
		 * @param dayStr    the day from the hidden metadata
		 * @return the day to use
		 */
		private static String correctLateEntryDay(final String sixFigDTG, final String dayStr) {
			final int dtgDate = Integer.valueOf(sixFigDTG.substring(0, 2));
			final int hours = Integer.valueOf(sixFigDTG.substring(2, 4));

			// is this entry after 2300? (that's the usual destination)
			if (hours == 23 && Integer.parseInt(dayStr) == dtgDate + 1) {
				// ok, the date in the hidden text is one day after
				// that in 6-fix DTG. correct the date
				return "" + dtgDate;
			}
			return dayStr;
		}

		/**
		 * special processing, to overcome the previous day being used
		 *
		 * @return the day to use
		 */
		private static String checkDaySequence(final String dtgStr, final String dayStr, final String monStr,
				final String yrStr) {
			final boolean dayDecreased = lastDay != null && Integer.parseInt(dayStr) < Integer.parseInt(lastDay);
			final boolean monthIncreased = lastMonth != null && Integer.parseInt(monStr) > Integer.parseInt(lastMonth);
			final boolean yearIncreased = lastYear != null && Integer.parseInt(yrStr) > Integer.parseInt(lastYear);

			if (dayDecreased && !monthIncreased && !yearIncreased) {
				// insert warning, since this may be a mangled DTG
				final String msg = "Day decreased, but month didn't increase: " + dtgStr
						+ ". The previous entry may be a mangled cut/paste";
				logThisError(ToolParent.ERROR, msg, null);

				// ok, the day has dropped, but the month hasn't increased
				return lastDay;
			}

			// it's valid, update the last day
			lastDay = dayStr;
			lastMonth = monStr;
			lastYear = yrStr;
			return dayStr;
		}

		private static int yearFrom(final String yrStr) {
			if (yrStr.length() == 2) {
				final int theYear = Integer.parseInt(yrStr);

				// is this from the late 80's onwards?
				if (theYear > 80) {
					return 1900 + theYear;
				} else {
					return 2000 + theYear;
				}
			}
			return Integer.parseInt(yrStr);
		}

		/**
		 * is this a four or six digit number?
		 */
		private static boolean isNumericTime(final String dateStr) {
			if (dateStr.length() != 6 && dateStr.length() != 4) {
				return false;
			}
			try {
				Integer.parseInt(dateStr);
				return true;
			} catch (final NumberFormatException e) {
				return false;
			}
		}

		/**
		 * parse an entry that has a DTG, day, month, year, platform and type
		 */
		private void parseFullEntry(final String entry, final String[] parts, final boolean sixFigDTG) {
			int ctr = 0;
			final String dtgStr;
			if (sixFigDTG) {
				dtgStr = parts[ctr++].substring(2, 6);
			} else {
				dtgStr = parts[ctr++];
			}

			// ok, sort out the time first
			String dayStr = parts[ctr++];
			final String monStr = parts[ctr++];
			final String yrStr = parts[ctr++];
			platform = parts[ctr++].trim();
			type = parts[ctr++].trim();

			if (sixFigDTG) {
				dayStr = correctLateEntryDay(parts[0], dayStr);
			}

			dayStr = checkDaySequence(dtgStr, dayStr, monStr, yrStr);

			// hmm, on occasion we don't get the closing comma on the entry type
			if (type.length() > 20) {
				final int firstSpace = type.indexOf(" ");
				// note: should actually be looking for non-alphanumeric, since it may be a tab
				type = type.substring(0, firstSpace - 1);
			}

			final int hours = Integer.parseInt(dtgStr.substring(0, 2));
			final int mins = Integer.parseInt(dtgStr.substring(2, 4));
			dtg = gmtDate(yearFrom(yrStr), Integer.parseInt(monStr) - 1, Integer.parseInt(dayStr), hours, mins);

			// ok, and the message part
			final int ind = entry.indexOf(type);

			text = entry.substring(ind + type.length() + 1).trim();

			rememberThisEntry();
		}

		/**
		 * parse a line that follows on from a previous entry: a time and some text,
		 * some text, or a new-day marker
		 */
		private void parseShortEntry(final String entry, final String trimmed) {
			final int firstTab = firstWhiteSpace(trimmed);

			// see if the first few characters are date
			final String dateStr = firstTab > 0 ? trimmed.substring(0, Math.min(trimmed.length(), firstTab))
					: trimmed;

			final boolean probHasContent = entry.length() > 8;

			if (isNumericTime(dateStr) && probHasContent) {
				// yes, go for it. But only if we have some stored data
				if (lastDtg != null && lastPlatform != null) {
					parseTimedEntry(trimmed, dateStr);
				}
			} else {
				parseTextOrDayMarker(trimmed);
			}
		}

		/**
		 * parse a line that starts with a time, taking the date and platform from the
		 * previous entry
		 */
		@SuppressWarnings("deprecation")
		private void parseTimedEntry(final String trimmed, final String dateStr) {
			final String parseStr;
			Integer theseDays = null;
			if (dateStr.length() == 6) {
				// reduce to four charts
				theseDays = Integer.parseInt(dateStr.substring(0, 2));
				parseStr = dateStr.substring(2, 6);
			} else {
				parseStr = dateStr;
			}

			// first try to parse it
			final int hours = Integer.parseInt(parseStr.substring(0, 2));
			final int mins = Integer.parseInt(parseStr.substring(2, 4));

			// do some date fiddling
			int daysToUse = lastDtg.getDate();
			int monthToUse = lastDtg.getMonth();
			int yearToUse = lastDtg.getYear();
			if (theseDays != null) {
				// ok, see if the day has changed
				if (theseDays < daysToUse) {
					// day moved backwards, we must be in a different month
					if (monthToUse == 11) {
						// hey, happy new year!
						yearToUse++;

						// set to January
						monthToUse = 0;
					} else {
						// just increment to the next month
						monthToUse++;
					}
				}

				// ok use the new value of days
				daysToUse = theseDays;
			}

			dtg = gmtDate(1900 + yearToUse, monthToUse, daysToUse, hours, mins);

			// stash the platform
			platform = lastPlatform;

			// and catch the rest of the text
			text = trimmed.substring(dateStr.length()).trim();

			final String startOfLine = text.substring(0, Math.min(20, text.length() - 1));
			final String trackNum = FCSEntry.parseTrack(startOfLine);
			if (trackNum != null) {
				type = "FCS";
			} else {
				// explain we don't know what type of comment this is
				type = "N/A";
			}

			// try to replace soft returns with hard returns
			text = text.replace("\r", "\n");

			rememberThisEntry();
		}

		/**
		 * hmm, see if it's just text. If it is, stick it on the end of the previous one
		 */
		private void parseTextOrDayMarker(final String trimmed) {
			// ooh, it may be a next day marker. have a check
			final DateFormat dtgBlock = new GMTDateFormat("dd MMM yy");

			boolean hasDate = false;
			try {
				final Date scrapDate = dtgBlock.parse(trimmed);
				hasDate = true;

				// store the date, ready for successive lines
				lastDtg = scrapDate;

				// hey, maybe this is a data-file without any metadata
				// give it a platform
				if (lastPlatform == null) {
					lastPlatform = NAME_NOT_PRESENT;
				}
			} catch (final ParseException e) {
				// it's ok, we can silently fail
			}

			// ooh, do we have a previous one?
			if (!hasDate && lastEntry != null) {
				text = trimmed;

				// now flag that we've just added ourselves to the previous one
				appendedToPrevious = true;
			}
		}

		/**
		 * remember what's happening, so we can refer back to previous entries
		 */
		private void rememberThisEntry() {
			lastDtg = new Date(dtg.getDate().getTime());
			lastPlatform = platform;
			lastEntry = this;
		}
	}

	/**
	 * helper that can ask the user a question
	 *
	 */
	public static interface QuestionHelper {
		String askQuestion(final String title, final String message, final String defaultStr);

		boolean askYes(final String title, final String message);

		void showMessage(final String title, final String message);

		void showMessageWithLogButton(String title, String message);
	}

	public static interface TrimNarrativeHelper {
		NarrativeHelperRetVal findWhatToImport(Map<String, Integer> narrativeTypes);
	}

	/**
	 * string constants to use for the enum names
	 *
	 */
	private final static String TRIMMED_DATA_STR = "trimmed-data";

	/**
	 * marker for start of narrative entries, in larger document
	 *
	 */
	static final String START_OF_RECORDS_1 = "START OF RECORDS";

	/**
	 * marker for start of narrative entries, in larger document
	 *
	 */
	static final String START_OF_RECORDS_2 = "START RECORDS";

	/**
	 * marker for end of narrative entries, in larger document
	 *
	 */
	private static final String END_OF_RECORDS_1 = "END RECORDS FOR";

	/**
	 * marker for end of narrative entries, in larger document
	 *
	 */
	static final String END_OF_RECORDS_2 = "END OF RECORDS FOR";

	private final static String ALL_DATA_STR = "all-data";

	private final static String CANCEL_STR = "cancel";

	/**
	 * track name to use if we're missing the hidden metadata
	 *
	 */
	private static final String NAME_NOT_PRESENT = "NAME_NOT_PRESENT";

	/**
	 * helper class that can ask the user a question populated via Dependency
	 * Injection
	 */
	private static QuestionHelper questionHelper = null;

	private static List<String> SkipNames = null;

	private static TrimNarrativeHelper trimNarrativeHelper = null;

	/**
	 * match a 6 figure DTG
	 *
	 */
	static final String DATE_MATCH_SIX = "(\\d{6})";

	static final String DATE_MATCH_FOUR = "(\\d{4})";

	static final String NARR_LAYER = "Narrative FCSs";

	/**
	 * repeatably find a color for the specified track id The color will be RED if
	 * it's a master track ("M01"). Shades of gray nor ownship blue are returned.
	 *
	 * @param trackId
	 * @return
	 */
	private static Color colorFor(final String trackId) {
		final Color res;

		// ok, is it a master track?
		if (trackId.startsWith("M")) {
			res = DebriefColors.RED;
		} else {
			// ok, get the hash code
			final int hash = trackId.hashCode();
			res = DebriefColors.RandomColorProvider.getRandomColor(hash);
		}
		return res;
	}

	private static String existingWECDISTrack(final Layers layers, final String dataName) {
		String res = null;

		// loop through the layers, see if one matches the WECDIS header text
		final int ctr = layers.size();
		for (int i = 0; i < ctr; i++) {
			final Layer thisL = layers.elementAt(i);
			if (thisL.getVisible() && thisL.getName().startsWith(ImportNMEA.WECDIS_OWNSHIP_PREFIX)) {
				final String existingName = thisL.getName();

				// ok, change the track name to the provided name
				thisL.setName(dataName);

				// and we'll now return it, as confirmation that it worked
				res = thisL.getName();

				// and politely tell the user
				MessageProvider.Base.show("Import Narrative", "Since it looks like a WECDIS track, we've renamed "
						+ existingName + " to " + res + ", so we can add create FCSs.", MessageProvider.INFO);

				// done
				break;
			}
		}

		return res;
	}

	static int firstWhiteSpace(final String input) {
		for (int index = 0; index < input.length(); index++) {
			{
				if (Character.isWhitespace(input.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	public static ArrayList<String> importFromPdf(final String fileName, final InputStream inputStream) {
		final ArrayList<String> strings = new ArrayList<String>();

		try {
			final PDDocument document = PDDocument.load(inputStream);

			// clear the stored data in the importer
			NarrEntry.reset();
			final PDFTextStripper textStripper = new PDFTextStripper();
			final PDPageTree pages = document.getPages();
			for (int i = 1; i <= pages.getCount(); i++) {
				textStripper.setStartPage(i);
				textStripper.setEndPage(i);
				final String pageText = textStripper.getText(document);
				final String[] split = pageText.split(textStripper.getLineSeparator());
				strings.addAll(Arrays.asList(split));

			}
			document.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return strings;
	}

	public static ArrayList<String> importFromWord(final HWPFDocument doc) {
		final ArrayList<String> strings = new ArrayList<String>();

		final Range r = doc.getRange();

		// clear the stored data in the MS Word importer
		NarrEntry.reset();

		final int lenParagraph = r.numParagraphs();
		for (int x = 0; x < lenParagraph; x++) {
			final Paragraph p = r.getParagraph(x);
			strings.add(p.text());
		}

		return strings;
	}

	public static ArrayList<String> importFromWordX(final XWPFDocument doc) {
		final ArrayList<String> strings = new ArrayList<String>();

		try {

			final List<XWPFParagraph> paragraphs = doc.getParagraphs();

			// clear the stored data in the MS Word importer
			NarrEntry.reset();

			for (final XWPFParagraph xwpfParagraph : paragraphs) {
				strings.add(xwpfParagraph.getText());
			}
			doc.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return strings;
	}

	/**
	 * see if this list contains a "Start of records" entry
	 *
	 * @param strings
	 * @return starting index, or zero
	 */
	static int indexOfStart(final ArrayList<String> strings) {
		int ctr = 0;
		for (final String s : strings) {
			final String trimmed = s.trim();
			if (trimmed.startsWith(START_OF_RECORDS_1) || trimmed.startsWith(START_OF_RECORDS_2)) {
				return ctr;
			}
			ctr++;
		}
		return 0;
	}

	public static void logError(final int status, final String msg, final Exception e) {
		logThisError(status, msg, e);
	}

	public static void logThisError(final int status, final String msg, final Exception e) {
		Application.logError3(status, msg, e, true);
	}

	private static TimePeriod outerPeriodFor(final Layers theLayers) {
		TimePeriod outerPeriod = null;
		final Enumeration<Editable> layers = theLayers.elements();
		while (layers.hasMoreElements()) {
			final Layer thisL = (Layer) layers.nextElement();
			if (thisL instanceof WatchableList) {
				final WatchableList wl = (WatchableList) thisL;
				if (wl.getStartDTG() != null && wl.getEndDTG() != null) {
					final TimePeriod thisP = new TimePeriod.BaseTimePeriod(wl.getStartDTG(), wl.getEndDTG());
					if (outerPeriod == null) {
						outerPeriod = thisP;
					} else {
						outerPeriod.extend(wl.getStartDTG());
						outerPeriod.extend(wl.getEndDTG());
					}
				}
			}
		}
		return outerPeriod;
	}

	/**
	 * do some pre-processing of text, to protect robustness of data written to file
	 *
	 * @param raw_text
	 * @return text with some control chars removed
	 */
	public static String removeBadChars(final String raw_text) {
		// swap soft returns for hard ones
		String res = raw_text.replace('\u000B', '\n');

		// we learned that whilst MS Word includes the following
		// control chars, and we can persist them via XML, we
		// can't restore them via SAX. So, swap them for
		// spaces
		res = res.replace((char) 1, (char) 32);
		res = res.replace((char) 19, (char) 32);
		res = res.replace((char) 8, (char) 32); // backspace char, occurred in Oct 17, near an inserted
												// picture
		res = res.replace((char) 20, (char) 32);
		res = res.replace((char) 21, (char) 32);
		res = res.replace((char) 5, (char) 32); // MS Word comment marker
		res = res.replace((char) 31, (char) 32); // described as units marker, but we had it prior to
													// subscript "2"

		// done.
		return res;
	}

	public static void setNarrativeHelper(final TrimNarrativeHelper helper) {
		trimNarrativeHelper = helper;
	}

	public static void setQuestionHelper(final QuestionHelper helper) {
		questionHelper = helper;
	}

	/**
	 * check if the layers contains a single track object
	 *
	 * @param layers
	 * @return
	 */
	static boolean singleTrackIn(final Layers layers) {
		int ctr = 0;
		final int len = layers.size();
		for (int i = 0; i < len; i++) {
			final Layer next = layers.elementAt(i);
			if (next instanceof TrackWrapper) {
				ctr++;

				if (ctr > 1) {
					break;
				}
			}
		}
		return ctr == 1;
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
	 * keep track of the last successfully imported narrative entry if we've just
	 * received a plain text block, we'll add it to the previous one *
	 */
	private NarrEntry _lastNarrEntry;

	/**
	 * keep track of track names that we have matched
	 *
	 */
	Map<String, String> nameMatches = new HashMap<String, String>();

	private List<String> selectedNarrativeTypes;

	public ImportNarrativeDocument(final Layers target) {
		_layers = target;

		if (SkipNames == null) {
			SkipNames = new ArrayList<String>();
			SkipNames.add("HMS");
			SkipNames.add("Hms");
			SkipNames.add("USS");
			SkipNames.add("RNAS");
			SkipNames.add("HNLMS");
		}
	}

	private boolean addEntries(final List<NarrEntry> narrativeEntries, final List<String> selectedNarrativeTypes,
			final TimePeriod outerPeriod) {
		boolean dataAdded = false;
		// do this on the selected narratives
		for (final NarrEntry thisN : narrativeEntries) {

			// do we know the outer time period?
			if (outerPeriod != null && thisN.dtg != null) {
				// check it's in the currently loaded time period
				if (!outerPeriod.contains(thisN.dtg)) {
					// ok, it's not in our period
					continue;
				}
			}

			// did we process anything?
			if (thisN.type != null && (selectedNarrativeTypes == null || selectedNarrativeTypes.contains(thisN.type))) {
				// ok, process the entry
				switch (thisN.type) {
				case "FCS": {
					// add a narrative entry
					addEntry(thisN);
					// create track for this
					try {
						addFCS(thisN);
					} catch (final StringIndexOutOfBoundsException e) {
						// don't worry about panicking, it may not be an FCS after all
					} catch (final NumberFormatException e) {
						// don't worry about panicking, it may not be an FCS after all
					}
				}

					// ok, take note that we've added something
					dataAdded = true;
					break;
				default: {
					// ok, just add a narrative entry for anything not recognised
					// add a narrative entry
					addEntry(thisN);
					// ok, take note that we've added something
					dataAdded = true;

					break;

				}
				}
			} else if (thisN.platform != null && (thisN.type == null || thisN.type.isBlank())) {
				addEntry(thisN);
				// ok, take note that we've added something
				dataAdded = true;
			}
		}
		return dataAdded;
	}

	private void addEntry(final NarrEntry thisN) {
		final NarrativeWrapper nw = getNarrativeLayer();
		String hisTrack = trackFor(thisN.platform, thisN.platform);

		// did we find a track? Don't worry if we didn't just use the raw text
		if (hisTrack == null) {
			hisTrack = thisN.platform;
		}

		final NarrativeEntry ne = new NarrativeEntry(hisTrack, thisN.type, new HiResDate(thisN.dtg), thisN.text);

		// try to color the entry
		final Layer host = _layers.findLayer(trackFor(thisN.platform));
		if (host instanceof TrackWrapper) {
			final TrackWrapper tw = (TrackWrapper) host;
			ne.setColor(tw.getColor());
		}

		// and store it
		nw.add(ne);
	}

	private void addFCS(final NarrEntry thisN) {
		// ok, parse the message
		final FCSEntry fe = new FCSEntry(thisN.text);

		// do we have enough data to create a solution?
		if (fe.brgDegs == null || fe.rangYds == null) {
			return;
		}

		// find the host
		final TrackWrapper host = (TrackWrapper) _layers.findLayer(trackFor(thisN.platform));
		if (host != null) {
			// find the fix nearest this time
			final Watchable[] nearest = host.getNearestTo(thisN.dtg, false);
			if (nearest != null && nearest.length > 0) {
				final Watchable fix = nearest[0];
				// apply the offset
				final WorldVector vec = new WorldVector(Math.toRadians(fe.brgDegs),
						new WorldDistance(fe.rangYds, WorldDistance.YARDS), new WorldDistance(0, WorldDistance.METRES));
				final WorldLocation loc = fix.getLocation().add(vec);

				// overwrite the depth, to put contact at surface (since we really don't know
				// depth)
				loc.setDepth(0);

				// build the track name
				final String trackName;
				if (fe.source != null) {
					trackName = fe.contact + "_" + fe.source;
				} else {
					trackName = fe.contact;
				}

				// find the track for this solution
				LightweightTrackWrapper hisTrack = (LightweightTrackWrapper) _layers.findLayer(trackName, true);
				if (hisTrack == null) {
					hisTrack = new LightweightTrackWrapper();
					hisTrack.setName(trackName);

					// get a custom color for this contact number (tracks from different
					// will share the same color if they're from the same contact number)
					final Color customColor = colorFor(fe.contact);
					hisTrack.setColor(customColor);

					// other formatting
					hisTrack.setLineThickness(3);

					// do we have narratives folder?
					Layer narrLayer = _layers.findLayer(NARR_LAYER);
					if (narrLayer == null) {
						narrLayer = new BaseLayer();
						narrLayer.setName(NARR_LAYER);
						_layers.addThisLayer(narrLayer);
					}

					// store this new track
					narrLayer.add(hisTrack);
				}

				// ok, now create the fix
				final WorldSpeed ws = new WorldSpeed(fe.spdKts, WorldSpeed.Kts);
				final double yds_per_sec = ws.getValueIn(WorldSpeed.ft_sec / 3);
				final Fix newF = new Fix(thisN.dtg, loc, Math.toRadians(fe.crseDegs), yds_per_sec);
				final FixWrapper newFw = new FixWrapper(newF);

				// lastly, reset the label, so it's legible
				newFw.resetName();

				// oh, and do some more formatting
				newFw.setSymbolShowing(false);
				newFw.setArrowShowing(true);
				newFw.setLabelShowing(true);

				// ok, we may have multiple fixes at the same time
				final Watchable[] hisNearest = hisTrack.getNearestTo(thisN.dtg);
				if (hisNearest != null && hisNearest.length > 0 && hisNearest[0] != null) {
					// ok, have a look at it.
					final Watchable nearestW = hisNearest[0];
//          System.out.println("nearest:" + nearestW);
					// System.out.println("newF:" + newF);
					while (nearestW.getTime().equals(newF.getTime())) {
						newF.setTime(new HiResDate(newF.getTime().getDate().getTime() + 1000));
					}
				}

				// and store it
				hisTrack.add(newFw);
			} else {
				logError(ToolParent.WARNING, "Host fix not present for FCS at:" + thisN.dtg.getDate(), null);
			}
		}
	}

	/**
	 * check with the helper function what the user wants to import
	 *
	 * @param typeVsCount      types of entry, with count of instances
	 * @param narrativeEntries list of narratives
	 * @return
	 */
	private boolean checkWhatToImport(final Map<String, Integer> typeVsCount, final List<NarrEntry> narrativeEntries) {
		final boolean dataAdded;
		final NarrativeHelperRetVal whatToImport = trimNarrativeHelper.findWhatToImport(typeVsCount);
		if (whatToImport.narrativeEnum != ImportNarrativeEnum.CANCEL) {
			if (typeVsCount != null && !typeVsCount.isEmpty()) {
				final TimePeriod outerPeriod;

				// find the outer time period - we only load data into the current time period
				if (whatToImport.narrativeEnum == ImportNarrativeEnum.CANCEL) {
					// if cancelled then do nothing.
					outerPeriod = null;
				} else if (whatToImport.narrativeEnum == ImportNarrativeEnum.ALL_DATA) {
					outerPeriod = null;
				} else {
					outerPeriod = outerPeriodFor(_layers);
				}

				selectedNarrativeTypes = whatToImport.selectedNarrativeTypes;
				dataAdded = addEntries(narrativeEntries, selectedNarrativeTypes, outerPeriod);
			} else {
				questionHelper.showMessage("Narrative Types", "No narrative types found, there is nothing to import");
				dataAdded = false;
			}
		} else {
			dataAdded = false;
		}
		return dataAdded;
	}

	private NarrativeWrapper getNarrativeLayer() {
		NarrativeWrapper nw = (NarrativeWrapper) _layers.findLayer(LayerHandler.NARRATIVE_LAYER);

		if (nw == null) {
			nw = new NarrativeWrapper(LayerHandler.NARRATIVE_LAYER);
			_layers.addThisLayer(nw);
		}

		return nw;
	}

	/**
	 * parse a list of strings
	 *
	 * @param strings
	 */
	public void processThese(final ArrayList<String> strings) {

		if (strings.isEmpty()) {
			return;
		}
		final Map<String, Integer> typeVsCount = new HashMap<>();
		final List<NarrEntry> narrativeEntries = new ArrayList<NarrEntry>();

		// ok, now we can loop through the strings
		collectEntries(strings, typeVsCount, narrativeEntries);

		// keep track of if we've added anything
		final boolean dataAdded;

		if (trimNarrativeHelper != null) {
			// ask the user what to do
			dataAdded = checkWhatToImport(typeVsCount, narrativeEntries);
		} else {
			// if dialog is not displayed then trim to time period. add all entries
			final TimePeriod outerPeriod = outerPeriodFor(_layers);
			dataAdded = addEntries(narrativeEntries, null, outerPeriod);
		}
		if (dataAdded) {
			_layers.fireModified(getNarrativeLayer());
		}
	}

	/**
	 * parse the strings into narrative entries, appending follow-on text to the
	 * previous entry
	 *
	 * @param strings          the lines to parse
	 * @param typeVsCount      (output) how many entries of each type we found
	 * @param narrativeEntries (output) the entries found
	 */
	private void collectEntries(final ArrayList<String> strings, final Map<String, Integer> typeVsCount,
			final List<NarrEntry> narrativeEntries) {
		// maximum number of follow-on-sentences to use
		final int MAX_APPENDED = 6;

		int appendedToPreviousCtr = 0;

		// see if we have an index for start of records
		final int START_INDEX = indexOfStart(strings);

		int ctr = 0;
		for (final String raw_text : strings) {
			// increment counter, for num lines processed
			ctr++;

			// skip lines before the start of records, and empty lines
			if (ctr < START_INDEX + 1 || raw_text.trim().length() == 0) {
				continue;
			}

			// also remove any other control chars that may throw MS Word
			final String text = removeBadChars(raw_text);

			// wrap import process in try/catch, so we can report errors
			try {

				// ok, get the narrative type
				final NarrEntry thisN = NarrEntry.create(text, ctr);

				if (thisN == null) {
					continue;
				}

				// see if it's the special end of records marker
				if (isEndOfRecords(thisN)) {
					// ok. we're done. We don't need to store it.
					break;
				}

				// is it just text, that we will append
				if (thisN.appendedToPrevious && appendedToPreviousCtr < MAX_APPENDED) {
					// ok, keep track of how many times we've appended
					if (appendToPrevious(thisN)) {
						appendedToPreviousCtr++;
					}

					// ok, we can't do any more. carry on
					continue;
				}

				// clear the appended flag
				appendedToPreviousCtr = 0;

				storeEntry(thisN, typeVsCount, narrativeEntries);

			} catch (final Exception e) {
				logThisError(ToolParent.WARNING, "Failed whilst parsing line:" + text, e);
			}
		}
	}

	/**
	 * is this the special end of records marker? If it is, log the fact
	 */
	private static boolean isEndOfRecords(final NarrEntry thisN) {
		if (thisN.text.startsWith(END_OF_RECORDS_1) || thisN.text.startsWith(END_OF_RECORDS_2)) {
			Application.logError3(ToolParent.WARNING, "Import terminated at phrase:" + thisN.text, null, false);
			return true;
		}
		return false;
	}

	/**
	 * add the text of this entry to the previous one
	 *
	 * @return whether there was a previous one
	 */
	private boolean appendToPrevious(final NarrEntry thisN) {
		if (_lastNarrEntry == null) {
			return false;
		}
		_lastNarrEntry.text = _lastNarrEntry.text + "\n" + thisN.text;
		return true;
	}

	/**
	 * store this entry, if we can determine its type
	 */
	private void storeEntry(final NarrEntry thisN, final Map<String, Integer> typeVsCount,
			final List<NarrEntry> narrativeEntries) {
		final String type;
		if (thisN.type != null) {
			type = thisN.type;
		} else if (thisN.platform != null) {
			type = "None";
		} else {
			type = null;
		}
		if (type != null) {
			typeVsCount.merge(type, 1, Integer::sum);

			// remember that entry, in case we get incomplete text inthe future
			_lastNarrEntry = thisN;
			narrativeEntries.add(thisN);
		}
	}

	public void setSelectedNarrativeTypes(final List<String> selectedNarrativeTypes) {
		this.selectedNarrativeTypes = selectedNarrativeTypes;
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

		// is there just one layer present?
		final boolean singleTrackPresent = singleTrackIn(layers);

		// loop through layers
		for (int i = 0; i < ctr; i++) {
			final Layer thisL = layers.elementAt(i);
			if (thisL.getVisible() && thisL instanceof TrackWrapper) {
				final TrackWrapper track = (TrackWrapper) thisL;

				// ok, additionally filter on Blue ownship tracks, if there
				// are more than one layer
				if (singleTrackPresent || track.getColor().equals(DebriefColors.BLUE)) {
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
							candidate = track;

							// remember we've found one
							singleCandidate = true;
						}
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

	private String trackFor(final String originalName) {
		return trackFor(originalName, null);
	}

	String trackFor(final String originalName, String name) {
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
				match = trackForUnknownPlatform(originalName, name, platform);
			}
		}

		return match;
	}

	/**
	 * the platform isn't a layer name. Try the fallback strategies
	 */
	private String trackForUnknownPlatform(final String originalName, final String name, final String platform) {
		// try skipping then names
		String match = trackWithoutSkipName(originalName, platform);

		// ok, fallback processing.
		if (match == null) {
			// do we have a track that has come straight from WECDIS?
			match = existingWECDISTrack(_layers, name);
		}

		if (match == null) {
			// ok, if there is just one track present, invite the user to use that
			match = trackFromSingleTrack(originalName, name);
		}
		return match;
	}

	/**
	 * try the platform name without any of the prefixes we can skip
	 */
	private String trackWithoutSkipName(final String originalName, final String platform) {
		String match = null;
		final Iterator<String> nameIter = SkipNames.iterator();
		while (nameIter.hasNext() && match == null) {
			final String thisSkip = nameIter.next();
			if (platform.startsWith(thisSkip)) {
				final String subStr = platform.substring(thisSkip.length()).trim();
				match = trackFor(originalName, subStr);
			}
		}
		return match;
	}

	/**
	 * if there is just one track present, use it (asking the user first, if we
	 * can)
	 */
	private String trackFromSingleTrack(final String originalName, final String name) {
		final TrackWrapper singleTrack = singleTrackPresent(_layers, name);

		// did we find one?
		if (singleTrack == null) {
			// we can't find a host track.
			declareNoHostFound(originalName);
			return null;
		}

		if (name.equals(NAME_NOT_PRESENT) || questionHelper == null) {
			return singleTrack.getName();
		}

		// ok, ask the user if he wants to change the subject track to this track's name
		final boolean wantsTo = questionHelper.askYes("Change track name",
				"Host platform not found for narrative entries.\nDo you want to rename track ["
						+ singleTrack.getName() + "] to [" + name + "]");

		// remember that we've asked about it
		askedAbout.add(singleTrack.getName() + name);

		if (wantsTo) {
			singleTrack.setName(name);
			return name;
		}
		return null;
	}

	/**
	 * tell the user we can't find a host track, but only once
	 */
	private void declareNoHostFound(final String originalName) {
		// have we already told the user?
		if (!_declaredNoHostFound) {
			// ok, stop it appearing again
			_declaredNoHostFound = true;

			// tell the user
			MessageProvider.Base.show("Import Narrative",
					"Narrative entries will be imported, but we won't be creating FCSs "
							+ "since we couldn't determine the host track for: " + originalName + ".",
					MessageProvider.WARNING);
		}
	}

}
