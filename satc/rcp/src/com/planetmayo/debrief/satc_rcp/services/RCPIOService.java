package com.planetmayo.debrief.satc_rcp.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.planetmayo.debrief.satc.support.IOService;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class RCPIOService implements IOService
{

	@Override
	public List<String> readLinesFrom(String url) throws IOException
	{

		// now load some data
		Bundle bundle = Platform.getBundle(SATC_Activator.PLUGIN_ID);
		URL fileURL = bundle.getEntry(url);
		FileInputStream input = null;
		// populate the bearing data
		try
		{
			input = new FileInputStream(
					new File(FileLocator.resolve(fileURL).toURI()));
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return IOUtils.readLines(input);
	}

}
