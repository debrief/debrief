/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.preferences.ChartPrefsPage;
import org.mwc.cmap.gt2plot.data.ShapeFileLayer;
import org.mwc.cmap.gt2plot.data.WorldImageLayer;

import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;

/**
 * @author ian.mayo
 * 
 */
public class InsertChartLibrary extends CoreInsertChartFeature
{

	public InsertChartLibrary()
	{
		// tell our parent that we want to be inserted as a top-level layer
		super(true);
	}

	/**
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		String chartLib = CorePlugin.getToolParent().getProperty(
				ChartPrefsPage.PreferenceConstants.CHART_FOLDER);

		// has it been set?
		if (chartLib == null)
		{
			CorePlugin
					.showMessage(
							"Load chart library",
							"To load a chart library you must first specify a root folder via\nWindows/Preferences/Maritime Analysis");
			return null;
		}

		File parent = new File(chartLib);

		// is it a real directory
		if (!parent.exists())
		{
			CorePlugin
					.showMessage(
							"Load chart library",
							"Unable to find the chart library defined in:\nWindows/Preferences/Maritime Analysis");
			return null;
		}

		IOFileFilter fileFilter = new WildcardFileFilter(
				WorldImageLayer.RASTER_FILE + ".shp");
		Collection<File> list = FileUtils.listFiles(parent, fileFilter, TrueFileFilter.INSTANCE);

		Plottable res = null;
		if (list.size() > 0)
		{
			// ok, let the user choose which one
			ListDialog dl = new ListDialog(Display.getCurrent().getActiveShell());
			dl.setLabelProvider(new FileLabelProvider());
			dl.setContentProvider(new ArrayContentProvider());
			dl.setInput(list.toArray());
			dl.setTitle("Load chart library");
			dl.setMessage("The following chart libraries have been found in the \nfolder specifed in the 'Maritime Analysis' Preferences.\n\nIndicate which library you wish to load.");
			dl.open();
			if (dl.getReturnCode() == Window.OK)
			{
				// ok, go for it.
				Object[] selection = dl.getResult();
				if (selection != null)
				{
					if (selection.length > 0)
					{
						File sel = (File) dl.getResult()[0];
						res = ShapeFileLayer.read(sel.getPath());
					}
				}
			}
		}

		// ok - get the chart library
		return res;
	}

	public class FileLabelProvider extends LabelProvider
	{

		@Override
		public String getText(Object element)
		{
			File thisF = (File) element;
			return thisF.getParentFile().getName();
		}

	}
}
