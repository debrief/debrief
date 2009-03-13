package org.mwc.cmap.TimeController.Operations;

import java.util.Vector;

import Debrief.Tools.FilterOperations.CopyTimeDataToClipboard;
import Debrief.Tools.Tote.WatchableList;
import MWC.GenericData.TimePeriod;

public class ExportTimeDataToClipboard
{
	/** ok, do the export
	 * 
	 * @param period the time period we're exporting for
	 * @param thePrimary the primary track
	 * @param theSecs the secondary tracks
	 */
	public static void export(final TimePeriod period, 
			final WatchableList thePrimary, final Vector<WatchableList> theSecs)
	{
	
		// ok, now get the primary & secondary tracks
		CopyTimeDataToClipboard exporter = new CopyTimeDataToClipboard()
		{
			public WatchableList getPrimary()
			{
				return thePrimary;
			}			
		};
		
		// ok - set the secondary tracks
		exporter.setTracks(theSecs);
		exporter.setPeriod(period.getStartDTG(), period.getEndDTG());
		
		// ok - go for it.
		exporter.getData();
	}
}
