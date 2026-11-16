package Debrief.ReaderWriter.ais;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;

public class ImportAIS {

	final static String LAYER_NAME = "AIS Tracks";

	/**
	 * keep a tally of vessel names against MMSI numbers. We keep it as static so
	 * that it stays alive between file loads.
	 *
	 */
	private static HashMap<Integer, String> _nameLookups;

	/**
	 * decide if the new seconds is actually from the next minute
	 *
	 * @param lastSecs
	 * @param newSecs
	 * @return yes, no
	 */
	static boolean isNextMinute(final int lastSecs, final int newSecs) {
		final boolean res;

		if (newSecs > lastSecs) {
			res = false;
		} else {
			// so, newSecs is less than lastSecs
			if (lastSecs - newSecs > 30) {
				res = true;
			} else {
				res = false;
			}
		}
		return res;
	}

	/**
	 * decide if the new seconds is actually from the next minute
	 *
	 * @param lastSecs
	 * @param newSecs
	 * @return yes, no
	 */
	static boolean isPreviousMinute(final int lastSecs, final int newSecs) {
		final boolean res;

		if (newSecs > lastSecs) {
			if ((newSecs - lastSecs) > 30) {
				res = true;
			} else {
				res = false;
			}
		} else {
			res = false;
			// // so, newSecs is less than lastSecs
			// if(lastSecs - newSecs < 30)
			// {
			// res = false;
			// }
			// else
			// {
			// res = true;
			// }
		}
		return res;
	}

	/**
	 * where we write our data
	 *
	 */
	private final Layers _layers;

	/**
	 * fixes that are received before we have a TimeStamp from a base
	 *
	 */
	final ArrayList<FixWrapper> _queuedFixes;

	public ImportAIS(final Layers target) {
		super();
		_layers = target;
		_queuedFixes = new ArrayList<FixWrapper>();
	}

	@SuppressWarnings("deprecation")
	public void importThis(final String fName, final InputStream is) throws Exception {

		// we can't assume times continue from the last file - so
		// always start with an empty time stamp
		Timestamp lastTime = null;

		// get ready to parse
		final AISParser parser = new AISParser();

		// ok, loop through the lines
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String nmea_sentence;
		
		int lineCtr = 1;

		// loop through the lines
		while ((nmea_sentence = br.readLine()) != null) {
			// parse this message. Note that fortunately this library consumes any
			// leading text.
			final Optional<IAISMessage> oRes = parser.parse(nmea_sentence, lineCtr++);
			if (oRes.isPresent()){
				IAISMessage res = oRes.get();
				if (res instanceof IPositionMessage) {
					// ok, cast it
					final IPositionMessage ar = (IPositionMessage) res;

					final Timestamp timestamp = ar.getMsgTimestamp();
					
					// check the timestamp is less than 1000
					if (timestamp.getSeconds() > 1000) {
						System.err.println("Seconds value is too high:" + timestamp.getYear());
					} else {
						// and now store it.
						storeThis(ar.getLatitude(), ar.getLongitude(), ar.getCog(), ar.getSog(), ar.getMmsi(),
								ar.getMsgTimestamp().getSeconds(), lastTime);
					}
				} else if (res instanceof AISBaseStation) {
					final AISBaseStation base = (AISBaseStation) res;

					final Timestamp timestamp = base.getTimestamp();
					final Date date = new Date(timestamp.getTime());

					// calculate the date one year in the future from today
					final Calendar yearInTheFuture = Calendar.getInstance();
					yearInTheFuture.add(Calendar.YEAR, 1);
					final Date futureDate = yearInTheFuture.getTime();

					if (date.before(futureDate)) {
						// ok, extract the time stamp - so we can use it to offset positions
						lastTime = base.getTimestamp();
						
						// hey, we may have stacked up some positions while
						// they are waiting for the first data item
						if (_queuedFixes.size() > 0)
							processQueuedPositions(lastTime);
					} else {
						System.err.println("Timestamp is in the future:" + date);
					}
				} else if (res instanceof AISVessel) {
					final AISVessel vess = (AISVessel) res;

					if (_nameLookups == null)
						_nameLookups = new HashMap<Integer, String>();

					// ok, store the id against the name
					_nameLookups.put(vess.getMmsi(), vess.getName());

					// ok, see if we can name this vessel
					final Layer thisLayer = _layers.findLayer("" + vess.getMmsi());
					if (thisLayer != null) {
						thisLayer.setName(vess.getName());
					}
				}
			}
		}
	}

	/**
	 * get either the vessel name, or the MMS in string form
	 *
	 * @param mmsi
	 * @return
	 */
	private String nameFor(final int mmsi) {
		final String res;
		if ((_nameLookups != null) && _nameLookups.containsKey(mmsi)) {
			// ok, we already know about this one
			res = _nameLookups.get(mmsi);
		} else {
			res = "" + mmsi;
		}

		return res;
	}

	@SuppressWarnings("deprecation")
	void processQueuedPositions(final Timestamp lastTime) {

		// anything to process?
		if (_queuedFixes.isEmpty())
			return;

		// we need the seconds from the timestamp
		final int lastSecs = lastTime.getSeconds();

		// loop through the pending fixes
		final Iterator<FixWrapper> iter = _queuedFixes.iterator();
		while (iter.hasNext()) {
			final FixWrapper fix = iter.next();

			// what is the seconds for the recorded position?
			final int newSecs = fix.getTime().getDate().getSeconds();

			// build the new date
			final Date newDate = new Date(lastTime.getTime());

			// is this less than the queued secs
			if (isPreviousMinute(lastSecs, newSecs)) {
				// ok, we have to decrement the minutes
				newDate.setMinutes(newDate.getMinutes() - 1);
			} else if (isNextMinute(lastSecs, newSecs)) {
				// ok, we have to increment the minutes
				newDate.setMinutes(newDate.getMinutes() + 1);
			}

			// and the seconds
			newDate.setSeconds(newSecs);

			// and store it
			fix.getFix().setTime(new HiResDate(newDate));

			// ok, find the track
			final String parentName = nameFor(Integer.valueOf(fix.getLabel()));

			storeFix(fix, parentName);

			// ok, we've used the name value that was sneaked into
			// the label, now we can override it
			fix.resetName();
		}

		// done, clear the list
		_queuedFixes.clear();
	}

	private void storeFix(final FixWrapper fix, final String parentName) {
		BaseLayer parent = (BaseLayer) _layers.findLayer(LAYER_NAME);
		if (parent == null) {
			parent = new BaseLayer();
			parent.setName(LAYER_NAME);
			_layers.addThisLayer(parent);
		}

		LightweightTrackWrapper track = (LightweightTrackWrapper) parent.find(parentName);
		if (track == null) {
			track = new LightweightTrackWrapper();
			track.setName(parentName);
			track.setColor(new Color(188, 93, 6));
			parent.add(track);
		}

		// cool, now store it
		track.add(fix);

		// that's all easy then. Remember to reset the time label
		fix.resetName();
	}

	@SuppressWarnings("deprecation")
	void storeThis(final double latitude, final double longitude, final double cog, final double sog,
			final int mmsi, final int secs, final Timestamp lastTime) {
		// try to do a name lookup
		final String layerName = nameFor(mmsi);

		// determine what date value to use for this new position
		final Date newDate;

		// do we have a base timestamp?
		if (lastTime != null) {
			// ok, extract the time
			final long theTime = lastTime.getTime();
			newDate = new Date(theTime);

			// store the new value of seconds
			newDate.setSeconds(secs);

			// should this new point be from the previous, or next minute?
			if (isPreviousMinute(lastTime.getSeconds(), secs)) {
				newDate.setMinutes(newDate.getMinutes() - 1);
			} else if (isNextMinute(lastTime.getSeconds(), secs)) {
				newDate.setMinutes(newDate.getMinutes() + 1);
			}
		} else {
			// no existing timestamp - just safely store the seconds
			newDate = new Date(secs * 1000);
			newDate.setSeconds(secs);
		}

		// ok - now we can create the time value
		final HiResDate hDate = new HiResDate(newDate);

		// now collate the other fix-related data
		final WorldLocation theLocation = new WorldLocation(latitude, longitude, 0);
		final double theCourseRads = Math.toRadians(cog);
		final double theSpeedYps = new WorldSpeed(sog, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3d;
		// ok, now add the position
		final Fix newFix = new Fix(hDate, theLocation, theCourseRads, theSpeedYps);
		final FixWrapper fixWrapper = new FixWrapper(newFix);

		// ok, do we have a time offset yet? if we don't we should queue up this fix
		if (lastTime == null) {
			// no previous time, let's sneak the track name into the label
			fixWrapper.setLabel("" + mmsi);

			// and remember the fix, for later procssing
			_queuedFixes.add(fixWrapper);
		} else {
			storeFix(fixWrapper, layerName);
		}

	}
}
