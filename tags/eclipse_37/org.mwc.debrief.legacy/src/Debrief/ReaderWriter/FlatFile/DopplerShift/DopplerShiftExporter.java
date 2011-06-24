package Debrief.ReaderWriter.FlatFile.DopplerShift;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

/**
 * exporter class to replicate old Strand export format
 * 
 * @author ianmayo
 * 
 */
public class DopplerShiftExporter
{

	/**
	 * type of exception for when we fail to export
	 * 
	 * @author ian
	 * 
	 */
	public static class ExportException extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ExportException(String message)
		{
			super(message);
		}
	}

	/**
	 * export the dataset to a string
	 * 
	 * @param primaryTrack
	 *          the ownship track
	 * @param secondaryTracks
	 *          sec tracks = presumed to be just one
	 * @param period
	 *          the time period to export
	 * @param sensorType
	 *          what sensor type was specified
	 * @return
	 */
	public String export(final WatchableList primaryTrack,
			final WatchableList[] secondaryTracks, final TimePeriod period)
			throws ExportException
	{

		String res2 = null;

		boolean onlyVis = true;
		TrackWrapper sensorHost = (TrackWrapper) primaryTrack;
		TrackWrapper targetTrack = (TrackWrapper) secondaryTracks[0];

		// store the frequency doublets
		final TreeSet<Doublet> res = new TreeSet<Doublet>();

		// and the base freuqency
		double baseFreq = -1;

		// friendly fix-wrapper to save us repeatedly creating it
		FixWrapper index = new FixWrapper(new Fix(null, new WorldLocation(0, 0, 0),
				0.0, 0.0));

		// loop through our sensor data
		Enumeration<Editable> sensors = sensorHost.getSensors().elements();
		if (sensors != null)
		{
			while (sensors.hasMoreElements())
			{
				SensorWrapper wrapper = (SensorWrapper) sensors.nextElement();
				if (!onlyVis || (onlyVis && wrapper.getVisible()))
				{
					Enumeration<Editable> cuts = wrapper.elements();
					while (cuts.hasMoreElements())
					{
						SensorContactWrapper scw = (SensorContactWrapper) cuts
								.nextElement();
						if (!onlyVis || (onlyVis && scw.getVisible()))
						{
							FixWrapper targetFix = null;
							TrackSegment targetParent = null;

							if (targetTrack != null)
							{
								// right, get the track segment and fix nearest to
								// this
								// DTG
								Enumeration<Editable> trkData = targetTrack.elements();
								Vector<TrackSegment> _theSegments = new Vector<TrackSegment>();

								while (trkData.hasMoreElements())
								{

									Editable thisI = trkData.nextElement();
									if (thisI instanceof SegmentList)
									{
										SegmentList thisList = (SegmentList) thisI;
										Enumeration<Editable> theElements = thisList.elements();
										while (theElements.hasMoreElements())
										{

											TrackSegment ts = (TrackSegment) theElements
													.nextElement();
											
											// check it's in our period
											BaseTimePeriod bp = new BaseTimePeriod(ts.startDTG(), ts.endDTG());
											if(bp.overlaps(period))
												_theSegments.add(ts);
										}

									}
									if (thisI instanceof TrackSegment)
									{
										TrackSegment ts = (TrackSegment) thisI;
										
										// check it's in our period
										BaseTimePeriod bp = new BaseTimePeriod(ts.startDTG(), ts.endDTG());
										if(bp.overlaps(period))
											_theSegments.add(ts);
									}
								}

								if (_theSegments.size() == 0)
								{
									throw new ExportException("Target track is not present in specified time period");
								}
								else
								{
									Iterator<TrackSegment> iter = _theSegments.iterator();
									while (iter.hasNext())
									{
										TrackSegment ts = iter.next();

										TimePeriod validPeriod = new TimePeriod.BaseTimePeriod(
												ts.startDTG(), ts.endDTG());
										if (validPeriod.contains(scw.getDTG()))
										{
											// sorted. here we go
											targetParent = ts;

											// create an object with the right time
											index.getFix().setTime(scw.getDTG());

											// and find any matching items
											SortedSet<Editable> items = ts.tailSet(index);
											targetFix = (FixWrapper) items.first();

											// do we have base freq?
											if (targetParent instanceof CoreTMASegment)
											{
												CoreTMASegment tmaSeg = (CoreTMASegment) targetParent;

												double thisFreq = tmaSeg.getBaseFrequency();

												if (baseFreq == -1)
												{
													baseFreq = thisFreq;
												}
												else
												{
													if (baseFreq != thisFreq)
														throw new ExportException(
																"Target track has multiple recorded base frequencies");
												}

											}
										}
									}
								}
							}

							Watchable[] matches = sensorHost.getNearestTo(scw.getDTG());
							if ((matches != null) && (matches.length > 0))
							{
								FixWrapper hostFix = (FixWrapper) matches[0];

								final Doublet thisDub = new Doublet(scw, targetFix,
										targetParent, hostFix);

								// if we've no target track add all the points
								if (targetTrack == null)
								{
									// store our data
									res.add(thisDub);
								}
								else
								{
									// if we've got a target track we only add points
									// for which we
									// have
									// a target location
									if (targetFix != null)
									{
										// store our data
										res.add(thisDub);
									}
								} // if we know the track
							} // if there are any matching items
							// if we find a match
						} // if cut is visible
					} // loop through cuts
				} // if sensor is visible
			} // loop through sensors
		}// if there are sensors

		// ok, ready to dump data - do some checks
		if (baseFreq == -1)
			throw new ExportException(
					"Target track does not have base frequency assigned");

		// have we found some cust
		if (res.size() == 0)
			throw new ExportException("No sensor cuts found");

		// start off with the recorded base frequency
		res2 = "base freq," + baseFreq;
		res2 += "\n";

		// and the header
		res2 += "time, measured frequency, predicted frequency";
		res2 += "\n";

		// now the cuts
		Iterator<Doublet> iter = res.iterator();
		while (iter.hasNext())
		{
			res2 += exportThis(iter.next());
			res2 += "\n";
		}

		return res2;
	}

	private String exportThis(Doublet doublet)
	{
		String res = formatThis(doublet.getDTG().getDate());
		res += ",";
		res += doublet.getMeasuredFrequency();
		res += ",";
		res += doublet.getPredictedFrequency();
		return res;
	}

	/**
	 * format this date in the prescribed format
	 * 
	 * @param val
	 *          the date to format
	 * @return the formatted date
	 */
	static protected String formatThis(Date val)
	{
		DateFormat df = new SimpleDateFormat("HH:mm:ss	dd/MM/yyyy");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(val);
	}

}
