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
package Debrief.ReaderWriter.Word;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportWordDummy {

	static class FCSEntry {
		final double brgDegs;
		final double rangYds;
		final String tgtName;

		public FCSEntry(final String msg) {
			final String[] items = msg.trim().split(" ");
			final String bT = items[0].split(":")[1];
			final String rT = items[1].split(":")[1];
			tgtName = msg.substring(msg.lastIndexOf(":") + 1).trim();

			brgDegs = Double.parseDouble(bT);
			rangYds = Double.parseDouble(rT);
		}
	}

	private static class NarrEntry {
		static public NarrEntry create(final String msg, final int lineNum) {
			NarrEntry res = null;
			try {
				res = new NarrEntry(msg);

				// just check it's valid
				final boolean valid = (res.dtg != null) && (res.type != null) && (res.platform != null)
						&& (res.text != null);
				if (!valid)
					res = null;
			} catch (final ParseException e) {
				logThisError("Failed whilst parsing Word Document, at line:" + lineNum, e);
			}

			return res;
		}

		HiResDate dtg;
		String type;
		String platform;

		String text;

		public NarrEntry(final String entry) throws ParseException {
			final DateFormat dateF = new GMTDateFormat("HH:mm:ss");
			final DateFormat dateF2 = new GMTDateFormat("HH:mm");

			final String[] parts = entry.split(",");
			int ctr = 0;
			if (parts.length > 5) {
				final String yrStr = parts[ctr++];
				final String monStr = parts[ctr++];
				final String dayStr = parts[ctr++];
				final String timeStr = parts[ctr++];
				type = parts[ctr++].trim();
				platform = parts[ctr++].trim();

				@SuppressWarnings("deprecation")
				final Date datePart = new Date(Integer.parseInt(yrStr) - 1900, Integer.parseInt(monStr) - 1,
						Integer.parseInt(dayStr));

				Date timePart = null;
				try {
					timePart = dateF.parse(timeStr);
				} catch (final ParseException pe) {
					Trace.trace("Failed to correctly parse:" + timeStr + " Trying for short time", false);
					timePart = dateF2.parse(timeStr);
				}

				dtg = new HiResDate(new Date(datePart.getTime() + timePart.getTime()));

				// ok, and the message part
				final int ind = entry.indexOf(platform);
				text = entry.substring(ind + platform.length() + 2).trim();
			}

		}
	}

	private static List<String> SkipNames = null;

	public static void logThisError(final String msg, final Exception e) {
		Application.logError2(ToolParent.WARNING, msg, e);
	}

	/**
	 * where we write our data
	 *
	 */
	private final Layers _layers;

	Map<String, String> nameMatches = new HashMap<String, String>();

	public ImportWordDummy(final Layers target) {
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

	private void addEntry(final NarrEntry thisN) {
		final NarrativeWrapper nw = getNarrativeLayer();
		final String hisTrack = trackFor(thisN.platform, thisN.platform);
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

		// find the host
		final TrackWrapper host = (TrackWrapper) _layers.findLayer(trackFor(thisN.platform));
		if (host != null) {
			// find the fix nearest this time
			final Watchable[] nearest = host.getNearestTo(thisN.dtg);
			if (nearest != null && nearest.length > 0) {
				final Watchable fix = nearest[0];
				// apply the offset
				final WorldVector vec = new WorldVector(Math.toRadians(fe.brgDegs),
						new WorldDistance(fe.rangYds, WorldDistance.YARDS), new WorldDistance(0, WorldDistance.METRES));
				final WorldLocation loc = fix.getLocation().add(vec);
				final Fix tgtF = new Fix(thisN.dtg, loc, 0, 0);
				final FixWrapper newF = new FixWrapper(tgtF);
				newF.resetName();

				// and the target track
				TrackWrapper tgt_track = (TrackWrapper) _layers.findLayer(fe.tgtName);

				if (tgt_track == null) {
					tgt_track = new TrackWrapper();
					tgt_track.setName(fe.tgtName);
					_layers.addThisLayer(tgt_track);
				}

				tgt_track.add(newF);
			} else {
				logError("Host fix not present for FCS at:" + thisN.dtg.getDate(), null);
			}
		}
	}

	private NarrativeWrapper getNarrativeLayer() {
		NarrativeWrapper nw = (NarrativeWrapper) _layers.findLayer(LayerHandler.NARRATIVE_LAYER);

		if (nw == null) {
			nw = new NarrativeWrapper(LayerHandler.NARRATIVE_LAYER);
			_layers.addThisLayer(nw);
		}

		return nw;
	}

	public void importThis(final String fName, final InputStream is) {
		HWPFDocument doc = null;
		try {
			doc = new HWPFDocument(is);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		if (doc == null)
			return;

		// keep track of if we've added anything
		boolean dataAdded = false;

		final Range r = doc.getRange();

		final int lenParagraph = r.numParagraphs();
		for (int x = 0; x < lenParagraph; x++) {
			final Paragraph p = r.getParagraph(x);
			final String text = p.text();
			if (text.trim().length() == 0) {
				continue;
			}

			// ok, get the narrative type
			final NarrEntry thisN = NarrEntry.create(text, x);

			if (thisN == null) {
				logError("Unable to parse line:" + text, null);
				continue;
			}

			switch (thisN.type) {
			case "FCS": {
				// add a narrative entry
				addEntry(thisN);

				// create track for this
				addFCS(thisN);

				// ok, take note that we've added something
				dataAdded = true;

				break;
			}
			default: {
				// ok, just add a narrative entry for anything not recognised

				// add a narrative entry
				addEntry(thisN);

				// ok, take note that we've added something
				dataAdded = true;

				break;

			}
			}
		}

		if (dataAdded) {
			_layers.fireModified(getNarrativeLayer());
		}
	}

	public void logError(final String msg, final Exception e) {
		logThisError(msg, e);
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
				// try skipping then names
				final Iterator<String> nameIter = SkipNames.iterator();
				while (nameIter.hasNext() && match == null) {
					final String thisSkip = nameIter.next();
					if (platform.startsWith(thisSkip)) {
						final String subStr = platform.substring(thisSkip.length()).trim();
						match = trackFor(originalName, subStr);
					}
				}
			}
		}

		return match;
	}
}
