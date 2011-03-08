package org.mwc.debrief.multipath.model;

import java.util.Iterator;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.mwc.debrief.multipath.model.TimeDeltas.Observation;

import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class MultiPathModel
{
	/** get the measured profile
	 * 
	 * @param deltas
	 * @return a time series of measured time delays
	 */
	public TimeSeries getMeasuredProfileFor(TimeDeltas deltas)
	{
		TimeSeries  res = new TimeSeries("Measured delay");
		
		// ok, loop through the times
		Iterator<Observation> iter = deltas.iterator();
		while (iter.hasNext())
		{
			Observation thisO = iter.next();

			// what's this time?
			HiResDate tNow = thisO.getDate();
			double delay = thisO.getInterval();

			// and add it
			res.add(new FixedMillisecond(tNow.getDate().getTime()), delay);
		}
		
		return res;
	}

	/** get the calculated profile
	 * 
	 * @param primary the primary track
	 * @param secondary the secondary track
	 * @param svp the sound speed profile
	 * @param deltas the series of time-delta observations
	 * @param targetDepth target depth to experiment with
	 * @return a time series of calculated time delays
	 */
	public TimeSeries getCalculatedProfileFor(WatchableList primary,
			WatchableList secondary, SVP svp, TimeDeltas deltas, double targetDepth)
	{
		Boolean oldPrimaryInterp = null;
		Boolean oldSecondaryInterp = null;

		TimeSeries res = new TimeSeries("Calculated delay");

		if (primary instanceof TrackWrapper)
		{
			TrackWrapper trk = (TrackWrapper) primary;
			oldPrimaryInterp = trk.getInterpolatePoints();
		}
		if (secondary instanceof TrackWrapper)
		{
			TrackWrapper trk = (TrackWrapper) secondary;
			oldSecondaryInterp = trk.getInterpolatePoints();
		}

		// ok, loop through the times
		Iterator<Observation> iter = deltas.iterator();
		while (iter.hasNext())
		{
			Observation thisO = iter.next();

			// what's this time?
			HiResDate tNow = thisO.getDate();

			// find the locations
			Watchable[] priLocs = primary.getNearestTo(tNow);
			Watchable[] secLocs = secondary.getNearestTo(tNow);

			// do we have data
			if (priLocs.length == 0)
				throw new RuntimeException("Insufficient primary data");
			if (secLocs.length == 0)
				throw new RuntimeException("Insufficient secondary data");

			WorldLocation priLoc = priLocs[0].getLocation();
			WorldLocation secLoc = secLocs[0].getLocation();
			WorldVector sep = priLoc.subtract(secLoc);

			double zR = priLoc.getDepth();
			double zS = targetDepth;

			double hD = MWC.Algorithms.Conversions.Degs2m(sep.getRange());
			double lDirect = Math.sqrt(hD * hD + Math.pow(zS - zR, 2));

			double lsHoriz = (hD * zS) / (zS + zR);
			double lS = Math.sqrt(lsHoriz * lsHoriz + zS * zS);
			double lR = Math.sqrt(Math.pow(hD - lsHoriz, 2) + zR * zR);

			// now sort out the sound speeds
			double cD = svp.getMeanSpeedBetween(zS, zR);
			double cS = svp.getMeanSpeedBetween(0, zS);
			double cR = svp.getMeanSpeedBetween(0, zR);

			double time_delay = (lS / cS + lR / cR) - (lDirect / cD);

			res.add(new FixedMillisecond(tNow.getDate().getTime()), time_delay);
		}

		// restore the interpolation settings
		if (oldPrimaryInterp != null)
		{
			TrackWrapper trk = (TrackWrapper) primary;
			trk.setInterpolatePoints(oldPrimaryInterp.booleanValue());
		}
		if (oldSecondaryInterp != null)
		{
			TrackWrapper trk = (TrackWrapper) secondary;
			trk.setInterpolatePoints(oldSecondaryInterp.booleanValue());
		}

		return res;
	}

}
