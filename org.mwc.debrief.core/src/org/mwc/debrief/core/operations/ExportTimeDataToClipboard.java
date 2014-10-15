/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
		super("Export calculated data to clipboard", false, false,false, false);
	}

	@Override
	public ImageDescriptor getDescriptor()
	{
		return CorePlugin.getImageDescriptor("icons/Calculator.gif");
	}

	@Override
	protected void executeExport(final WatchableList primaryTrack,
			final WatchableList[] secondaryTracks, final TimePeriod period)
	{

		final Vector<WatchableList> theSecs = new Vector<WatchableList>(0, 1);
		for (int i = 0; i < secondaryTracks.length; i++)
		{
			final WatchableList list = secondaryTracks[i];
			theSecs.add(list);
		}

		// ok, now get the primary & secondary tracks
		final CopyTimeDataToClipboard exporter = new CopyTimeDataToClipboard()
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
