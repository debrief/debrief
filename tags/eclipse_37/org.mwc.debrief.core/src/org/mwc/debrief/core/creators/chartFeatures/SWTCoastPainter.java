package org.mwc.debrief.core.creators.chartFeatures;

import java.io.*;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.preferences.CoastlineSourcePrefsPage;
import org.mwc.debrief.core.DebriefPlugin;

import MWC.GUI.ToolParent;
import MWC.GUI.Chart.Painters.CoastPainter;


public class SWTCoastPainter extends CoastPainter
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return
	 */
	protected InputStream getCoastLineInput()
	{
		ToolParent parent = CorePlugin.getToolParent();
		String location = parent.getProperty(CoastlineSourcePrefsPage.PreferenceConstants.COASTLINE_FILE);

		System.out.println("loading coastline from:" + location);
		
		InputStream res = null;
		
		try
		{
			res = new FileInputStream(location);
		}
		catch (FileNotFoundException e)
		{
			DebriefPlugin.logError(Status.ERROR, "Whilst loading coastline", e);
		}
		
		return res;
	}
}
