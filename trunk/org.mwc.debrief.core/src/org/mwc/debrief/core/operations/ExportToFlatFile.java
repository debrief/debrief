package org.mwc.debrief.core.operations;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.interfaces.TimeControllerOperation;
import org.mwc.debrief.core.DebriefPlugin;

import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

public class ExportToFlatFile extends TimeControllerOperation
{


	public ExportToFlatFile()
	{
		super("Export to flat file", true);
	}


	@Override
	public ImageDescriptor getDescriptor()
	{
		return DebriefPlugin.getImageDescriptor("icons/new.gif");
	}


	private void writeHeader()
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void executeExport(WatchableList primaryTrack,
			WatchableList[] secondaryTracks, TimePeriod period)
	{
		writeHeader();

		// now loop through the OS track
		System.err.println("DOING EXPORT TO FLAT FILE!!");
	}

}
