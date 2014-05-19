/**
 * 
 */
package org.mwc.debrief.core.operations;

import java.io.File;
import java.util.List;

import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.debrief.core.gpx.ImportGPX;

import Debrief.Wrappers.TrackWrapper;
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
		final List<TrackWrapper> tracks = ImportGPX.getTracksToMarshall(theLayers);
		// retrieve the filename via a file-browser dialog
		final File someFile = new File("debrief_export.gpx");
		
		ImportGPX.doExport(tracks, someFile);
	}

}