/**
 * 
 */
package org.mwc.debrief.core.operations;

import java.io.File;

import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.debrief.core.gpx.ImportGPX;

import MWC.GUI.Layers;
import MWC.GUI.PlainChart;

/**
 * @author ian.mayo
 */
public class ExportGPX extends CoreEditorAction
{

	/**
	 * and execute..
	 */
	protected void execute()
	{
		final PlainChart theChart = getChart();
		final Layers theLayers = theChart.getLayers();
		
		// retrieve the filename via a file-browser dialog
		File someFile = new File("debrief_export.gpx");
		
		ImportGPX.doExport(theLayers, someFile);
	}

}