package org.mwc.debrief.multipath.model;

import java.util.Iterator;

import org.mwc.debrief.multipath.model.TimeDeltas.Observation;

import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;

public class MultiPathModel
{
	
	public void getCalculatedProfileFor(WatchableList primary, WatchableList secondary, SVP svp, TimeDeltas deltas, double depth)
	{
		Boolean oldPrimaryInterp = null;
		Boolean oldSecondaryInterp = null;
		

		if(primary instanceof TrackWrapper)
		{
			TrackWrapper trk = (TrackWrapper) primary;
			oldPrimaryInterp = trk.getInterpolatePoints();
		}
		if(secondary instanceof TrackWrapper)
		{
			TrackWrapper trk = (TrackWrapper) secondary;
			oldSecondaryInterp = trk.getInterpolatePoints();
		}
		
		// ok, loop through the times
		Iterator<Observation> iter = deltas.iterator();
		while(iter.hasNext())
		{
			Observation thisO = iter.next();
			
			// what's this time?
			HiResDate tNow = thisO.getDate();
			
		}
		
		

		// restore the interpolation settings
		if(oldPrimaryInterp != null)
		{
			TrackWrapper trk = (TrackWrapper) primary;
			trk.setInterpolatePoints(oldPrimaryInterp.booleanValue());
		}
		if(oldSecondaryInterp != null)
		{
			TrackWrapper trk = (TrackWrapper) secondary;
			trk.setInterpolatePoints(oldSecondaryInterp.booleanValue());
		}
	}
	
	
}
