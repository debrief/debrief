package org.mwc.debrief.core.loaders.xml_handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.PlotEditor;

import Debrief.ReaderWriter.XML.DebriefLayersHandler;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Title: Debrief 2000 Description: Debrief 2000 Track Analysis Software
 * Copyright: Copyright (c) 2000 Company: MWC
 * 
 * @author Ian Mayo
 * @version 1.0
 */

public class SessionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	private static final String EXTENSION_POINT_ID = "XMLLayerHandler";
	private static final String PLUGIN_ID = "org.mwc.debrief.core";

	public SessionHandler(Layers _theLayers, final IControllableViewport view,
			PlotEditor plot)
	{
		// inform our parent what type of class we are
		super("session");

		// define our handlers
		addHandler(new ProjectionHandler()
		{
			public void setProjection(PlainProjection proj)
			{
				view.setProjection(proj);
			}
		});
		addHandler(new SWTGUIHandler(plot)
		{
			public void assignTracks(String primaryTrack,
					Vector<String> secondaryTracks)
			{
				// see if we have our track data listener
				if (view instanceof IAdaptable)
				{
					IAdaptable ad = (IAdaptable) view;
					Object adaptee = ad
							.getAdapter(org.mwc.cmap.core.DataTypes.TrackData.TrackManager.class);
					if (adaptee != null)
					{
						TrackManager tl = (TrackManager) adaptee;
						tl.assignTracks(primaryTrack, secondaryTracks);
					}
				}
			}
		});

		DebriefLayersHandler layersHandler = new DebriefLayersHandler(_theLayers);

		// ok, see if we have any extra layer handlers
		ArrayList<LayerHandlerExtension> extraHandlers = loadLoaderExtensions(_theLayers);
		for (Iterator<LayerHandlerExtension> iterator = extraHandlers.iterator(); iterator
				.hasNext();)
		{
			LayerHandlerExtension thisE = (LayerHandlerExtension) iterator.next();

			// just double check taht it's an MWCXMLReader object
			if (thisE instanceof MWCXMLReader)
			{
				// tell it about the top level layers object
				thisE.setLayers(_theLayers);

				// and remmber it
				layersHandler.addHandler((MWCXMLReader) thisE);
			}
			else
			{
				DebriefPlugin.logError(Status.ERROR,
						"The layer handler we're read in is not of the corect type: "
								+ thisE, null);
			}
		}

		// ok, we're now ready to register the layers handler
		addHandler(layersHandler);

	}

	/**
	 * see if any extra right click handlers are defined
	 * 
	 */
	private ArrayList<LayerHandlerExtension> loadLoaderExtensions(Layers theLayers)
	{
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(
				PLUGIN_ID, EXTENSION_POINT_ID);
		ArrayList<LayerHandlerExtension> res = new ArrayList<LayerHandlerExtension>();

		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++)
		{
			IExtension iExtension = extensions[i];
			IConfigurationElement[] confE = iExtension.getConfigurationElements();
			for (int j = 0; j < confE.length; j++)
			{
				IConfigurationElement iConfigurationElement = confE[j];
				LayerHandlerExtension newInstance;
				try
				{
					newInstance = (LayerHandlerExtension) iConfigurationElement
							.createExecutableExtension("class");
					res.add(newInstance);
				}
				catch (CoreException e)
				{
					CorePlugin.logError(Status.ERROR,
							"Trouble whilst loading right-click handler extensions", e);
				}
			}
		}
		return res;
	}

	public final void elementClosed()
	{
		// and the GUI details
		// setGUIDetails(null);
	}

	public static void exportThis(PlotEditor thePlot, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{
		// ok, get the layers
		Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);

		exportTheseLayers(theLayers, thePlot, parent, doc);
	}

	public static void exportTheseLayers(Layers theLayers, PlotEditor thePlot,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		org.w3c.dom.Element eSession = doc.createElement("session");

		// now the Layers
		DebriefLayersHandler.exportThis(theLayers, eSession, doc);

		// now the projection
		final PlainProjection proj;
		if (thePlot != null)
		{
			proj = (PlainProjection) thePlot.getAdapter(PlainProjection.class);
		}
		else
		{
			proj = new FlatProjection()
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public WorldArea getDataArea()
				{
					// TODO Auto-generated method stub
					return Layers.getDebriefOrigin();
				}

				/**
				 * @return
				 */
				public double getDataBorder()
				{
					return 1.1;
				}

				/**
				 * @return
				 */
				public boolean getPrimaryOriented()
				{
					return false;
				}

			};
		}

		ProjectionHandler.exportProjection(proj, eSession, doc);

		// now the GUI
		// do we have a gui?
		if (thePlot != null)
			SWTGUIHandler.exportThis(thePlot, eSession, doc);

		// send out the data
		parent.appendChild(eSession);
	}

}