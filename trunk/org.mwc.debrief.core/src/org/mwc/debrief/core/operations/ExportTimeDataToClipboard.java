package org.mwc.debrief.core.operations;

import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.TimeControllerOperation;

import Debrief.Tools.FilterOperations.CopyTimeDataToClipboard;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

public class ExportTimeDataToClipboard extends TimeControllerOperation
{

	public ExportTimeDataToClipboard()
	{
		super("Export time data to clipboard", false, false, false);
	}

	@Override
	public ImageDescriptor getDescriptor()
	{
		return CorePlugin.getImageDescriptor("icons/Calculator.gif");
	}

	@Override
	protected void executeExport(final WatchableList primaryTrack,
			WatchableList[] secondaryTracks, TimePeriod period)
	{

		Vector<WatchableList> theSecs = new Vector<WatchableList>(0, 1);
		for (int i = 0; i < secondaryTracks.length; i++)
		{
			WatchableList list = secondaryTracks[i];
			theSecs.add(list);
		}

		// ok, now get the primary & secondary tracks
		CopyTimeDataToClipboard exporter = new CopyTimeDataToClipboard()
		{
			public WatchableList getPrimary()
			{
				return primaryTrack;
			}
		};

		// ok - set the secondary tracks
		exporter.setTracks(theSecs);
		exporter.setPeriod(period.getStartDTG(), period.getEndDTG());

		// ok - go for it.
		exporter.getData();
	}

}
