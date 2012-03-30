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
	
	public PlotHandler(String fileName, Layers destination,
			IControllableViewport view, final PlotEditor plot)
	{
		// inform our parent what type of class we are
		super("plot");
		
		// sort out the handlers
		addHandler(new SessionHandler(destination, view, plot));
		addHandler(new DetailsHandler(null));

		super.addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(String name, String val)
			{
				System.out.println("Name of Plot is " + val);
			}
		});
		super.addAttributeHandler(new HandleAttribute("Created")
		{
			public void setValue(String name, String val)
			{
				System.out.println("Plot was created on " + val);
			}
		});
		super.addAttributeHandler(new HandleAttribute("PlotId")
		{
			public void setValue(String name, String val)
			{
				TimeProvider prov = (TimeProvider) plot.getAdapter(TimeProvider.class);
				if(prov instanceof TimeManager)
				{
					TimeManager tMgr = (TimeManager) prov;
					tMgr.setId(val);
				}
			}
		});
	}

	public static org.w3c.dom.Element exportPlot(PlotEditor thePlot,
			org.w3c.dom.Document doc, String version)
	{
		org.w3c.dom.Element plt = doc.createElement("plot");
		plt.setAttribute("Created", new java.util.Date().toString());
		plt.setAttribute("Name", "Debrief Plot");
		TimeProvider mgr = (TimeProvider) thePlot.getAdapter(TimeProvider.class);
		if (mgr != null)
		{
			plt.setAttribute("PlotId", mgr.getId());
		}
		if (version == null)
			version = Debrief.GUI.VersionInfo.getVersion();
		String details = "Saved with Debrief version dated " + version;
		DetailsHandler.exportPlot(details, plt, doc);
		SessionHandler.exportThis(thePlot, plt, doc);
		
		return plt;
	}

	public static org.w3c.dom.Element exportPlot(Layers theLayers,
			org.w3c.dom.Document doc)
	{
		org.w3c.dom.Element plt = doc.createElement("plot");
		plt.setAttribute("Created", new java.util.Date().toString());
		plt.setAttribute("Name", "Debrief Plot");
		String details = "Saved with Debrief version dated "
				+ Debrief.GUI.VersionInfo.getVersion();
		DetailsHandler.exportPlot(details, plt, doc);
		SessionHandler.exportTheseLayers(theLayers, null, plt, doc);
		return plt;
	}

}