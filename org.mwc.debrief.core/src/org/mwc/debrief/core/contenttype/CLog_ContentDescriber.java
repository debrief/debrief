/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.debrief.core.contenttype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.FlatFile.CLogFileImporter;
import MWC.GUI.ErrorLogger;

/** import format test for SATC export files
 * 
 * @author ian
 *
 */
@SuppressWarnings("restriction")
public class CLog_ContentDescriber extends TextContentDescriber
{

	@Override
	public int describe(InputStream contents, IContentDescription description)
			throws IOException
	{
		BufferedReader r = null;
		final AtomicBoolean res = new AtomicBoolean();
		try
		{
			r = new BufferedReader(new InputStreamReader(contents));
      ErrorLogger logger = CorePlugin.getToolParent();
      boolean canRead =  CLogFileImporter.canLoad(logger, r);
      res.set(canRead);
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "C-Log content type error", e);
		}
		finally
		{
			try
			{
				if (r != null)
					r.close();
			}
			catch (IOException e)
			{
				CorePlugin.logError(Status.ERROR, "Couldn't close file", e);
			}
		}
		if(res.get())
		{
		  return VALID;
		}
		else
		{
		  return super.describe(contents, description);
		}
	}

}
