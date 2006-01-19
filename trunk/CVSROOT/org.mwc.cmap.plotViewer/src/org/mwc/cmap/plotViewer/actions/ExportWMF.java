/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import java.awt.Dimension;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;

import com.pietjonas.wmfwriter2d.ClipboardCopy;

import MWC.GUI.*;
import MWC.GUI.Canvas.*;
import MWC.GUI.Tools.Chart.WriteMetafile;

/**
 * @author ian.mayo
 */
public class ExportWMF extends CoreEditorAction
{
	public static ToolParent _theParent = null;

	/**
	 * ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(ToolParent theParent)
	{
		_theParent = theParent;
	}

	/**
	 * and execute..
	 */
	protected void run()
	{
		final PlainChart theChart = getChart();

		if (_theParent == null)
		{
			CorePlugin.logError(Status.ERROR, "Tool parent missing for Write Metafile", null);
			return;
		}

		WriteMetafile write = new WriteMetafile(_theParent, theChart, theChart.getLayers())
		{

			/**
			 * @param mf
			 */
			protected void paintToMetafile(MetafileCanvas mf)
			{
				SWTCanvas sc = (SWTCanvas) theChart.getCanvas();
				sc.paintPlot(mf);
			}

		};
		write.execute();

		// try to get the filename
		String fName = MetafileCanvas.getLastFileName();

		if (fName != null)
		{
			// create the clipboard

			// try to copy the wmf to the clipboard
			try
			{
				// get the dimensions
				Dimension dim = MetafileCanvas.getLastScreenSize();
				
				ClipboardCopy cc = new ClipboardCopy();
				 cc.copyWithPixelSize(fName, dim.width, dim.height, false);
				// cc.copyWithPixelSize(fName, 6000, 4000, false);

			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			System.err.println("Target filename missing");
	}

}