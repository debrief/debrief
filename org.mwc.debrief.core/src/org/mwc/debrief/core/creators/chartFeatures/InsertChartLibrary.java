/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.creators.chartFeatures;

import java.io.File;
import java.util.Vector;

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
	protected Plottable getPlottable(final PlainChart theChart)
	{
		final String chartLib = CorePlugin.getToolParent().getProperty(
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

		final Vector<File> matches = new Vector<File>();

		final File parent = new File(chartLib);

		// is it a real directory
		if (!parent.exists())
		{
			CorePlugin
					.showMessage(
							"Load chart library",
							"Unable to find the chart library defined in:\nWindows/Preferences/Maritime Analysis");
			return null;
		}

		final File[] list = parent.listFiles();
		findMatches(list, matches);

		Plottable res = null;
		if (matches.size() > 0)
		{
			// ok, let the user choose which one
			final ListDialog dl = new ListDialog(Display.getCurrent().getActiveShell());
			dl.setLabelProvider(new FileLabelProvider());
			dl.setContentProvider(new ArrayContentProvider());
			dl.setInput(matches.toArray());
			dl.setTitle("Load chart library");
			dl.setMessage("The following chart libraries have been found in the \nfolder specifed in the 'Maritime Analysis' Preferences.\n\nIndicate which library you wish to load.");
			dl.open();
			if (dl.getReturnCode() == Window.OK)
			{
				// ok, go for it.
				final Object[] selection = dl.getResult();
				if (selection != null)
				{
					if (selection.length > 0)
					{
						final File sel = (File) dl.getResult()[0];
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
		public String getText(final Object element)
		{
			final File thisF = (File) element;
			return thisF.getParentFile().getName();
		}

	}

	private void findMatches(final File[] list, final Vector<File> matches)
	{
		// ok, go through them
		for (int i = 0; i < list.length; i++)
		{
			final File file = list[i];
			if (file.isDirectory())
			{
				final File[] thisList = file.listFiles();
				findMatches(thisList, matches);
			}
			else
			{
				if (file.getName().equals(WorldImageLayer.RASTER_FILE + ".shp"))
				{
					matches.add(file);
				}
			}
		}
	}

}
