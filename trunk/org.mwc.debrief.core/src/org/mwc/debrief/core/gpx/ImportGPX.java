package org.mwc.debrief.core.gpx;

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

	public static void doImport(Layers theLayers, InputStream inputStream,
			String fileName)
	{
		GpxHelper helper = new JaxbGpxHelper();
		helper.unmarshall(inputStream, theLayers);
	}

}
