package org.mwc.debrief.core.gpx;

import java.io.File;
import java.io.InputStream;

import org.mwc.debrief.core.loaders.GpxHelper;
import org.mwc.debrief.core.loaders.JaxbGpxHelper;

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
	
	public static void doExport(final Layers theLayers, final File outputFile)
	{
		final GpxHelper helper = new JaxbGpxHelper();
		helper.marshall(theLayers, outputFile);
	}

}
