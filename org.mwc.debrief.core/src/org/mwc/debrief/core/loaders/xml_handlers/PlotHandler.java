/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.loaders.xml_handlers;

import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.editors.PlotEditor;

import Debrief.ReaderWriter.XML.DetailsHandler;
import MWC.GUI.Layers;

/**
 * Title: Debrief 2000 Description: Debrief 2000 Track Analysis Software
 * Copyright: Copyright (c) 2000 Company: MWC
 * 
 * @author Ian Mayo
 * @version 1.0
 */

final public class PlotHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	
	public PlotHandler(final String fileName, final Layers destination,
			final IControllableViewport view, final PlotEditor plot)
	{
		// inform our parent what type of class we are
		super("plot");
		
		// sort out the handlers
		addHandler(new SessionHandler(destination, view, plot));
		addHandler(new DetailsHandler(null));

		super.addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(final String name, final String val)
			{
				System.out.println("Name of Plot is " + val);
			}
		});
		super.addAttributeHandler(new HandleAttribute("Created")
		{
			public void setValue(final String name, final String val)
			{
				System.out.println("Plot was created on " + val);
			}
		});
		super.addAttributeHandler(new HandleAttribute("PlotId")
		{
			public void setValue(final String name, final String val)
			{
				final TimeProvider prov = (TimeProvider) plot.getAdapter(TimeProvider.class);
				if(prov instanceof TimeManager)
				{
					final TimeManager tMgr = (TimeManager) prov;
					tMgr.setId(val);
				}
			}
		});
	}

	public static org.w3c.dom.Element exportPlot(final PlotEditor thePlot,
			final org.w3c.dom.Document doc, final String version)
	{
		final org.w3c.dom.Element plt = doc.createElement("plot");
		plt.setAttribute("Created", new java.util.Date().toString());
		plt.setAttribute("Name", "Debrief Plot");
		final TimeProvider mgr = (TimeProvider) thePlot.getAdapter(TimeProvider.class);
		if (mgr != null)
		{
			plt.setAttribute("PlotId", mgr.getId());
		}
		String theVersion = version;
		if (theVersion == null)
			theVersion = Debrief.GUI.VersionInfo.getVersion();
		final String details = "Saved with Debrief version dated " + theVersion;
		DetailsHandler.exportPlot(details, plt, doc);
		SessionHandler.exportThis(thePlot, plt, doc);
		
		return plt;
	}

	public static org.w3c.dom.Element exportPlot(final Layers theLayers,
			final org.w3c.dom.Document doc)
	{
		final org.w3c.dom.Element plt = doc.createElement("plot");
		plt.setAttribute("Created", new java.util.Date().toString());
		plt.setAttribute("Name", "Debrief Plot");
		final String details = "Saved with Debrief version dated "
				+ Debrief.GUI.VersionInfo.getVersion();
		DetailsHandler.exportPlot(details, plt, doc);
		SessionHandler.exportTheseLayers(theLayers, null, plt, doc);
		return plt;
	}

}