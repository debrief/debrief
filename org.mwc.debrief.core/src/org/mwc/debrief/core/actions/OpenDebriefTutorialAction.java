/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2015, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.mwc.debrief.core.DebriefPlugin;

public class OpenDebriefTutorialAction extends Action implements IIntroAction
{

	@Override
	public void run(IIntroSite site, Properties params)
	{
		URL url = Platform.getInstallLocation().getURL();
		File dir = new File(url.getFile());
		String fileName = params.getProperty("file");
		File file = new File(dir, fileName);
		if (!file.isFile())
		{
				DebriefPlugin.logError(Status.WARNING, "Cannot find " + fileName, null);
				return;	
		}
		if (Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN))
		{
			try
			{
				Desktop.getDesktop().open(file);
				return;
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		else
		{
			try
				{
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(file.toURI().toURL());
				}
				catch (PartInitException | MalformedURLException e)
				{
					DebriefPlugin.logError(Status.ERROR, "Cannot open " + fileName, e);
				}
			}
		}

}
