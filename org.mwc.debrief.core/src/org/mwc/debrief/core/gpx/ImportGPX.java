package org.mwc.debrief.core.gpx;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.mwc.debrief.core.loaders.GpxHelper;
import org.mwc.debrief.core.loaders.JaxbGpxHelper;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;

/**
 * importer for GPS files
 * 
 * @author ian
 * 
 */
public class ImportGPX
{

	public static void doImport(final Layers theLayers, final InputStream inputStream,
			final String fileName)
	{
		final GpxHelper helper = new JaxbGpxHelper();
		helper.unmarshall(inputStream, theLayers);
	}
	
	public static void doExport(final List<TrackWrapper> tracks, final File outputFile)
	{
		final GpxHelper helper = new JaxbGpxHelper();
		helper.marshall(tracks, outputFile);
	}
	
	public static List<TrackWrapper> getTracksToMarshall(final Layers from)
	{
		final Enumeration<Editable> allLayers = from.elements();
		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		while (allLayers.hasMoreElements())
		{
			final Editable element = allLayers.nextElement();
			if (element instanceof TrackWrapper)
			{
				tracks.add((TrackWrapper) element);
			}
		}
		return tracks;
	}

}
