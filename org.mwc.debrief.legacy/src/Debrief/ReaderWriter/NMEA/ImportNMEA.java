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
package Debrief.ReaderWriter.NMEA;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportNMEA {

	enum MsgType {
		VESSEL_NAME, OS_POS, CONTACT, TIMESTAMP, UNKNOWN, AIS, OS_DEPTH, OS_COURSE_SPEED, OS_COURSE, OS_SPEED;
	}

	static class State {
		static SimpleDateFormat sdf;

		static Date dateFor(final String dateStr, final String timeStr) {
			if (sdf == null) {
				sdf = new GMTDateFormat("yyyyMMdd,HHmmss.SSS");
			}
			Date res = null;
			try {
				res = sdf.parse(dateStr + "," + timeStr);
			} catch (final ParseException e) {
				Application.logError2(ToolParent.ERROR, "AIS importer failed to parse:" + dateStr + ", " + timeStr, e);
			}
			return res;
		}

		private static WorldLocation locationFor(final String tLat, final String tLong) {
			final double dLat = degsFor(tLat);
			final double dLong = degsFor(tLong);
			final WorldLocation loc = new WorldLocation(dLat, dLong, 0);
			return loc;
		}

		public final Date date;

		public final String name;

		public final WorldLocation location;

		public State(final String name, final String dateStr, final String timeStr, final String tLat,
				final String tLong) {
			if (dateStr != null && timeStr != null) {
				date = dateFor(dateStr, timeStr);
			} else {
				date = null;
			}
			this.name = name;
			location = locationFor(tLat, tLong);
		}
	}

	private static final String DR_NAME = "-DR";

	/**
	 * prefix we use for ownship track that's extractd from NMEA data
	 */
	public static final String WECDIS_OWNSHIP_PREFIX = "WECDIS_OWNSHIP";

	/**
	 * $POSL,AIS,564166000,1212.1234,N,12312.1234,W,0,7.8,327.9,0,330.0,AIS1,0,0*06
	 */
	final private static Pattern aisPattern = Pattern.compile(
			"\\$POSL,AIS,(?<MMSI>\\d+?)," + "(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*,AIS1,.*");

	/**
	 * $POSL,CONTACT,OC,DELETE,AIS 5,AIS
	 * 5,1.0,125.3,T,20160720,010059.897,FS,SFSP------^2a^2a^2a^2a^2a
	 * ,0.0,M,1212.1313,N,12312.1234,W,0,,,*6E"
	 */
	final private static Pattern contactPattern = Pattern
			.compile("\\$POSL,CONTACT,OC,\\w*,(?<NAME>.*?),.*" + ",(?<DATE>\\d{8}),(?<TIME>\\d{6}.\\d{3}),.*,"
					+ "(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*");
	/**
	 * $POSL,VEL,GPS,276.3,4.6,,,*35
	 */
	final static Pattern coursePatternGPS = Pattern.compile("\\$POSL,VEL,GPS,(?<COURSE>\\d+.\\d+),.*");

	/**
	 * $POSL,HDG,111.0,-04.1*7F
	 */
	final static Pattern coursePatternHDG = Pattern.compile("\\$POSL,HDG,(?<COURSE>\\d+.\\d+),.*");

	/**
	 * $POSL,DZA,20160720,000000.859,0007328229*42
	 */
	final private static Pattern datePattern = Pattern.compile("\\$POSL,DZA,(?<DATE>\\d{8}),(?<TIME>\\d{6}.\\d{3}),.*");

	/**
	 * $POSL,PDS,9.2,M*0
	 */
	final private static Pattern depthPattern = Pattern.compile("\\$POSL,PDS,(?<DEPTH>\\d+.\\d+),.*");
	/**
	 * "$POSL,VNM,HMS NONSUCH*03";
	 */
	final private static Pattern namePattern = Pattern.compile("\\$POSL,VNM,(?<NAME>.*)\\*\\d\\d");
	/**
	 * $POSL,VEL,GPS,276.3,4.6,,,*35
	 */
	final static Pattern speedPatternGPS = Pattern.compile("\\$POSL,VEL,GPS,.*,(?<SPEED>\\d+.\\d+),.*");
	/**
	 * $POSL,VEL,SPL,,,4.0,0.0,4.0*12
	 */
	final static Pattern speedPatternLOG = Pattern.compile("\\$POSL,VEL,SPL,,,(?<SPEED>\\d+.\\d+),.*");
	/**
	 * "$POSL,POS,GPS,1122.2222,N,12312.1234,W,0.00,,Center of Rotation,N,,,,,*41";
	 */
	final private static Pattern osPattern = Pattern
			.compile("\\$POSL,(?<SOURCE>\\w?POS\\d?,.*),(?<LAT>\\d{4}.\\d{4},(N|S)),(?<LONG>\\d{5}.\\d{4},(E|W)),.*");

	final private static Pattern typePattern = Pattern.compile("\\$POSL,(?<TYPE1>\\w*),(?<TYPE2>\\w*),*.*");

	static double degsFor(final String text) {
		final int dec = text.indexOf(".");
		final String degs = text.substring(0, dec - 2);
		final String mins = text.substring(dec - 2, text.length() - 2);
		final String hemi = text.substring(text.length() - 1);
		double res = Double.valueOf(degs) + Double.valueOf(mins) / 60d;
		if (hemi.equals("S") | hemi.equals("W")) {
			// switch the direction
			res = -res;
		}

		return res;
	}

	private static FixWrapper fixFor(final Date date, final State state, final FixWrapper lastFix,
			final Double myDepth) {
		// set the depth, if we have it
		if (myDepth != null) {
			state.location.setDepth(myDepth);
		}

		final Fix theF = new Fix(new HiResDate(date), state.location, 0d, 0d);
		final FixWrapper fw = new FixWrapper(theF);

		if (lastFix != null) {
			// have a go at the course & speed
			final WorldVector diff = state.location.subtract(lastFix.getLocation());

			// first the course
			fw.getFix().setCourse(diff.getBearing());

			final double m_travelled = new WorldDistance(diff.getRange(), WorldDistance.DEGS)
					.getValueIn(WorldDistance.METRES);
			final double timeDiffMillis = (date.getTime() - lastFix.getDTG().getDate().getTime());
			final double speed_m_s = m_travelled / (timeDiffMillis / 1000d);
			final WorldSpeed theSpeed = new WorldSpeed(speed_m_s, WorldSpeed.M_sec);

			// and the speed
			final double speed_yps = theSpeed.getValueIn(WorldSpeed.ft_sec) / 3d;
			fw.getFix().setSpeed(speed_yps);

		}

		return fw;
	}

	static State parseAIS(final String nmea_sentence) {

		final Matcher m = aisPattern.matcher(nmea_sentence);
		final State res;
		if (m.matches()) {
			final String name = m.group("MMSI");
			final String tLat = m.group("LAT");
			final String tLong = m.group("LONG");
			res = new State(name, null, null, tLat, tLong);
		} else {
			res = null;
		}

		return res;
	}

	static State parseContact(final String nmea_sentence) {

		final Matcher m = contactPattern.matcher(nmea_sentence);
		final State res;
		if (m.matches()) {
			final String name = m.group("NAME");
			final String dateStr = m.group("DATE");
			final String timeStr = m.group("TIME");
			final String tLat = m.group("LAT");
			final String tLong = m.group("LONG");
			res = new State(name, dateStr, timeStr, tLat, tLong);
		} else {
			res = null;
		}

		return res;
	}

	static double parseMyCourse(final Pattern pattern, final String nmea_sentence) {
		final Matcher m = pattern.matcher(nmea_sentence);
		final double res;
		if (m.matches()) {
			res = Double.parseDouble(m.group("COURSE"));
		} else {
			res = 0d;
		}
		return res;
	}

	static Date parseMyDate(final String nmea_sentence) {
		final Matcher m = datePattern.matcher(nmea_sentence);
		final Date res;
		if (m.matches()) {
			final String dateStr = m.group("DATE");
			final String timeStr = m.group("TIME");
			res = State.dateFor(dateStr, timeStr);
		} else {
			res = null;
		}

		return res;
	}

	static double parseMyDepth(final String nmea_sentence) {

		final Matcher m = depthPattern.matcher(nmea_sentence);
		final double res;
		if (m.matches()) {
			res = Double.parseDouble(m.group("DEPTH"));
		} else {
			res = 0d;
		}
		return res;
	}

	static String parseMyName(final String nmea_sentence) {

		final Matcher m = namePattern.matcher(nmea_sentence);
		final String res;
		if (m.matches()) {
			res = m.group("NAME");
		} else {
			res = null;
		}
		return res;
	}

	static double parseMySpeed(final Pattern pattern, final String nmea_sentence) {
		final Matcher m = pattern.matcher(nmea_sentence);
		final double res;
		if (m.matches()) {
			res = Double.parseDouble(m.group("SPEED"));
		} else {
			res = 0d;
		}
		return res;
	}

	static State parseOwnship(final String nmea_sentence, final String myName) {
		final Matcher m = osPattern.matcher(nmea_sentence);
		final State res;
		if (m.matches()) {
			final String tLat = m.group("LAT");
			final String tLong = m.group("LONG");
			final String source = m.group("SOURCE").replace(",", "_");
			res = new State(myName + "_" + source, null, null, tLat, tLong);
		} else {
			res = null;
		}

		return res;

	}

	static MsgType parseType(final String nmea_sentence) {
		final Matcher m = typePattern.matcher(nmea_sentence);
		final MsgType res;
		if (m.matches()) {
			final String str = m.group("TYPE1");
			final String str2 = m.group("TYPE2");
			if ("VNM".equals(str))
				res = MsgType.VESSEL_NAME;
			else if ("POS".equals(str) && "GPS".equals(str2))
				res = MsgType.OS_POS;
			else if ("POS".equals(str) && "CMP".equals(str2))
				res = MsgType.OS_POS;
			else if ("POS2".equals(str) && "GPS".equals(str2))
				res = MsgType.OS_POS;
			else if (str.contains("VEL") && "GPS".equals(str2))
				res = MsgType.OS_COURSE_SPEED;
			else if (str.contains("VEL") && "SPL".equals(str2))
				res = MsgType.OS_SPEED;
			else if (str.contains("HDG"))
				res = MsgType.OS_COURSE;
			else if ("CONTACT".equals(str))
				res = MsgType.CONTACT;
			else if ("AIS".equals(str))
				res = MsgType.AIS;
			else if ("DZA".equals(str))
				res = MsgType.TIMESTAMP;
			else if ("PDS".equals(str))
				res = MsgType.OS_DEPTH;
			else
				res = MsgType.UNKNOWN;
		} else {
			res = MsgType.UNKNOWN;
		}
		return res;
	}

	private static void storeLocation(final Date date, final State state, final long freq, final Color color,
			final Double myDepth, final HashMap<String, ArrayList<FixWrapper>> destination,
			final HashMap<String, Color> colors) {
		final String myName = state.name;
		ArrayList<FixWrapper> track = destination.get(myName);

		final boolean addIt;

		final FixWrapper lastFix;

		if (track == null) {
			track = new ArrayList<FixWrapper>();
			destination.put(myName, track);
			colors.put(myName, color);

			// ok. we're certainly adding this one
			addIt = true;

			lastFix = null;
		} else {
			// find the time of the last fix stored
			lastFix = track.get(track.size() - 1);
			final long lastTime = lastFix.getDateTimeGroup().getDate().getTime();

			// have we passed the indicated frequency?
			if (date.getTime() >= lastTime + freq) {
				addIt = true;
			} else {
				addIt = false;
			}
		}

		if (addIt) {
			// create a fix from the state
			final FixWrapper theF = fixFor(date, state, lastFix, myDepth);

			// and store it
			track.add(theF);
		}
	}

	/**
	 * where we write our data
	 *
	 */
	private final Layers _layers;

	/**
	 * the set of tracks we build up, to reduce screen updates
	 *
	 */
	private final HashMap<String, ArrayList<FixWrapper>> tracks = new HashMap<String, ArrayList<FixWrapper>>();

	/**
	 * the set of tracks we build up, to reduce screen updates
	 *
	 */
	private final HashMap<String, ArrayList<FixWrapper>> contacts = new HashMap<String, ArrayList<FixWrapper>>();

	/**
	 * the set of tracks we build up, to reduce screen updates
	 *
	 */
	HashMap<String, Color> colors = new HashMap<String, Color>();

	public ImportNMEA(final Layers target) {
		super();
		_layers = target;
	}

	public void importThis(final String fName, final InputStream is, final long osFreq, final long aisFreq,
			final boolean splitOwnshipJumps) {
		String myName = null;
		double myDepth = 0d;
		Date date = null;

		final boolean importOS = !(osFreq == Long.MAX_VALUE);
		final boolean importAIS = !(aisFreq == Long.MAX_VALUE);
		final boolean importContacts = false;

		// reset our list of tracks
		tracks.clear();
		colors.clear();
		contacts.clear();

		// remember the first ownship location, for the DR track
		WorldLocation origin = null;

		// ok, loop through the lines
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String nmea_sentence;

		// flag for if we wish to obtain DR data from GPS message, or from organic
		// sensors
		final boolean DRfromGPS = false;

		// remember the last DR course read in, since we capture course and speed
		// from different messages
		Double drCourse = null;

		// int ctr = 0;

		// loop through the lines
		try {
			while ((nmea_sentence = br.readLine()) != null) {

				final MsgType msg = parseType(nmea_sentence);

				// ctr++;
				//
				// if (ctr % 10000 == 0)
				// {
				// System.out.print(".");
				// }
				// if (ctr % 50000 == 0)
				// {
				// System.out.println("");
				// System.out.print(ctr);
				// }

				switch (msg) {
				case TIMESTAMP:
					// ok, extract the rest of the body
					date = parseMyDate(nmea_sentence);

					// and remember the name
					break;
				case VESSEL_NAME:
					// ok, extract the rest of the body
					myName = parseMyName(nmea_sentence);
					break;
				case OS_DEPTH:
					if (importOS) {
						// ok, extract the rest of the body
						myDepth = parseMyDepth(nmea_sentence);
					}
					break;
				case OS_COURSE:
					if (importOS) {
						// ok, extract the rest of the body
						drCourse = parseMyCourse(coursePatternHDG, nmea_sentence);
					}
					break;
				case OS_SPEED:
					if (importOS) {
						// ok, extract the rest of the body
						final double drSpeedDegs = parseMySpeed(speedPatternLOG, nmea_sentence);

						// are we taking DR from GPS?
						if (DRfromGPS) {
							// ok, skip creating the DR - do it in the other message
						} else {
							// do we know our origin?
							if (origin != null && drCourse != null) {
								// ok, grow the DR track
								storeDRFix(origin, drCourse, drSpeedDegs, date, myName, myDepth, DebriefColors.BLUE);
							}
						}
					}
					break;
				case OS_COURSE_SPEED:
					if (importOS) {
						// ok, extract the rest of the body
						final double myCourseDegs = parseMyCourse(coursePatternGPS, nmea_sentence);
						final double mySpeedKts = parseMySpeed(speedPatternGPS, nmea_sentence);

						// are we taking DR from GPS?
						if (DRfromGPS) {
							// do we know our origin?
							if (origin != null) {
								// ok, grow the DR track
								storeDRFix(origin, myCourseDegs, mySpeedKts, date, myName, myDepth, DebriefColors.BLUE);
							}
						} else {
							// ok, skip creating the DR using GPS deltas - do it from the organic sensors
						}
					}
					break;
				case OS_POS:
					if (importOS) {
						// note: if we don't know ownship name yet,
						// let's make one up
						if (myName == null) {
							myName = WECDIS_OWNSHIP_PREFIX;
						}

						// extract the location
						final State state = parseOwnship(nmea_sentence, myName);

						// do we need an origin?
						if (origin == null) {
							origin = new WorldLocation(state.location);
						}

						// do we know our name yet?
						if (state != null && date != null) {
							// now store the ownship location
							storeLocation(date, state, osFreq, DebriefColors.PURPLE, myDepth, tracks, colors);
						}
					}

					break;
				case CONTACT:
					if (importContacts) {
						// extract the location
						final State hisState = parseContact(nmea_sentence);

						if (hisState == null) {
							System.out.println("INVALID CONTACT");
						} else {
							// now store the ownship location
							storeLocation(hisState.date, hisState, aisFreq, DebriefColors.GREEN, null, contacts,
									colors);
						}
					}
					break;
				case AIS:
					if (importAIS) {
						// extract the location
						final State hisState = parseAIS(nmea_sentence);

						if (hisState == null) {
							// ok, it was prob the "other" AIS receiver
						} else {
							if (date != null) {
								// now store the ownship location
								storeLocation(date, hisState, aisFreq, DebriefColors.YELLOW, null, contacts, colors);
							}
						}
					}
					break;
				case UNKNOWN:
					break;
				}
			}
		} catch (final IOException e) {
			// produce the error message
			MWC.Utilities.Errors.Trace.trace(e, "Problem reading NMEA File");
		}

		final long JUMP_DELTA_MILLIS = 8 * 60 * 1000;

		for (final String trackName : tracks.keySet()) {
			final ArrayList<FixWrapper> track = tracks.get(trackName);
			// ok, build the track
			final TrackWrapper tr = new TrackWrapper();
			tr.setName(trackName);
			tr.setColor(colors.get(trackName));

			// SPECIAL HANDLING - we filter DR tracks at this stage
			Long lastTime = null;
			final boolean resample = trackName.endsWith(DR_NAME);

			for (final FixWrapper fix : track) {
				// ok, also do the label
				fix.resetName();

				final long thisTime = fix.getDateTimeGroup().getDate().getTime();

				long delta = Long.MAX_VALUE;

				if (resample && lastTime != null) {
					delta = thisTime - lastTime;
				}

				if ((!resample) || lastTime == null || delta >= osFreq) {
					// ok, see if there's a jump
					// do we know the last time?
					final boolean added;

					if (lastTime != null) {
						delta = thisTime - lastTime;
						if (delta > JUMP_DELTA_MILLIS) {
							if (splitOwnshipJumps) {
								// ok, we're adding an new track segment
								final TrackSegment newSeg = new TrackSegment(false);
								newSeg.add(fix);
								tr.add(newSeg);
								added = true;
							} else {
								added = false;
							}
						} else {
							added = false;
						}
					} else {
						added = false;
					}

					if (!added) {
						tr.add(fix);
					}
					lastTime = thisTime;
				}
			}

			_layers.addThisLayer(tr);
		}

		BaseLayer contactHolder = null;
		for (final String trackName : contacts.keySet()) {
			final ArrayList<FixWrapper> track = contacts.get(trackName);

			// System.out.println("storing " + track.size() + " for " + trackName);

			if (contactHolder == null) {
				contactHolder = new BaseLayer();
				contactHolder.setName("WECDIS Contacts");
			}

			// ok, build the track
			final LightweightTrackWrapper tr = new LightweightTrackWrapper();
			tr.setName(trackName);
			tr.setColor(colors.get(trackName));

			for (final FixWrapper fix : track) {
				// ok, also do the label
				fix.resetName();
				tr.add(fix);
			}

			contactHolder.add(tr);
		}

		// Ayesha: added contactholder finally after contacts are added.
		if (contactHolder != null) {
			_layers.addThisLayer(contactHolder);
		}

		_layers.fireExtended();
	}

	private void storeDRFix(final WorldLocation origin, final double myCourseDegs, final double mySpeedKts,
			final Date date, final String myName, final double myDepth, final Color color) {
		final String trackName = myName + DR_NAME;

		// find the track
		ArrayList<FixWrapper> track = tracks.get(trackName);

		final FixWrapper newFix;

		// do we have any?
		if (track == null) {
			track = new ArrayList<FixWrapper>();
			tracks.put(trackName, track);
			colors.put(trackName, color);

			// nope, create the origin
			final Fix fix = new Fix(new HiResDate(date.getTime()), origin, Math.toRadians(myCourseDegs),
					MWC.Algorithms.Conversions.Kts2Yps(mySpeedKts));
			newFix = new FixWrapper(fix);
		} else {
			// ok, get the last point
			final FixWrapper lastFix = track.get(track.size() - 1);

			// now calculate the new point
			final long timeDelta = date.getTime() - lastFix.getDateTimeGroup().getDate().getTime();

			// calculate the distance travelled
			final double m_s = new WorldSpeed(mySpeedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec);
			final double distanceM = m_s * timeDelta / 1000d;

			final double distanceDegs = new WorldDistance(distanceM, WorldDistance.METRES)
					.getValueIn(WorldDistance.DEGS);
			final WorldVector offset = new WorldVector(Math.toRadians(myCourseDegs), distanceDegs, 0);

			final WorldLocation newLoc = lastFix.getLocation().add(offset);

			// store the depth
			newLoc.setDepth(myDepth);

			final Fix fix = new Fix(new HiResDate(date.getTime()), newLoc, Math.toRadians(myCourseDegs),
					MWC.Algorithms.Conversions.Kts2Yps(mySpeedKts));
			newFix = new FixWrapper(fix);

			// no, don't set the color, we want the fix to take
			// the color of the parent track
			// newFix.setColor(color);
		}

		track.add(newFix);

	}
}
