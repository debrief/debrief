
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
