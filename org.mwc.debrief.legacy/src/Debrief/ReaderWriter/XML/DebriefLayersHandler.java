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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import Debrief.ReaderWriter.XML.Tactical.CompositeTrackHandler;
import Debrief.ReaderWriter.XML.Tactical.NarrativeHandler;
import Debrief.ReaderWriter.XML.Tactical.PatternHandler;
import Debrief.ReaderWriter.XML.Tactical.TrackHandler;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.ChartFolio;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Features.ChartFolioHandler;
import MWC.Utilities.ReaderWriter.XML.Features.ETOPOHandler;
import MWC.Utilities.ReaderWriter.XML.Features.ExternallyManagedLayerHandler;
import MWC.Utilities.ReaderWriter.XML.Features.TOPOHandler;

public final class DebriefLayersHandler extends MWCXMLReader
{
	public DebriefLayersHandler(final MWC.GUI.Layers theLayers)
	{
		// inform our parent what type of class we are
		super("layers");

		addHandler(new ChartFolioHandler(theLayers));
		addHandler(new DebriefLayerHandler(theLayers));
		addHandler(new CompositeTrackHandler(theLayers));
		addHandler(new TrackHandler(theLayers));
		addHandler(new PatternHandler(theLayers));
		addHandler(new NarrativeHandler(theLayers));
		addHandler(new ETOPOHandler(theLayers));
		addHandler(new TOPOHandler(theLayers));
		addHandler(new ExternallyManagedLayerHandler(theLayers));
	}

	public static void exportThis(final Debrief.GUI.Frames.Session session,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		// fill it out
		final MWC.GUI.Layers data = session.getData();

		final ArrayList<LayerHandlerExtension> emptyList = new ArrayList<LayerHandlerExtension>();

		exportThis(data, parent, doc, emptyList);

	}

	public static void exportThis(final Layers data, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc, final List<LayerHandlerExtension> extraLoaders)
	{

		if (data == null)
			return;

		// create ourselves
		final Element layers = doc.createElement("layers");

		//
		final int len = data.size();

		for (int i = 0; i < len; i++)
		{
			final MWC.GUI.Layer ly = data.elementAt(i);

			// ok, see if we have a special processor
			boolean handled = false;
			if (extraLoaders != null && extraLoaders.size() > 0)
			{
				// ok, do a run through
				for (final Iterator<LayerHandlerExtension> iterator = extraLoaders.iterator(); iterator.hasNext();)
				{
					final LayerHandlerExtension thisE = (LayerHandlerExtension) iterator.next();
					
					// is it suitable for this layer?
					if (thisE.canExportThis(ly))
					{
						// yes = go for it.
						thisE.exportThis(ly, layers, doc);
						handled = true;
						break;
					}
				}
			}

			if (!handled)
			{

				// find out which sort of layer this is
				if (ly instanceof Debrief.Wrappers.CompositeTrackWrapper)
				{
					Debrief.ReaderWriter.XML.Tactical.CompositeTrackHandler.exportTrack(
							(Debrief.Wrappers.TrackWrapper) ly, layers, doc);
				}
				else if (ly instanceof Debrief.Wrappers.TrackWrapper)
				{
					Debrief.ReaderWriter.XML.Tactical.TrackHandler.exportTrack(
							(Debrief.Wrappers.TrackWrapper) ly, layers, doc);
				}
				else if (ly instanceof ChartFolio)
				{
					ChartFolioHandler.exportThisFolio((ChartFolio) ly, layers, doc);
				}
				else if (ly instanceof Debrief.Wrappers.BuoyPatternWrapper)
				{
					Debrief.ReaderWriter.XML.Tactical.PatternHandler.exportTrack(
							(Debrief.Wrappers.BuoyPatternWrapper) ly, layers, doc);
				}
				else if (ly instanceof Debrief.Wrappers.NarrativeWrapper)
				{
					Debrief.ReaderWriter.XML.Tactical.NarrativeHandler.exportNarrative(
							(Debrief.Wrappers.NarrativeWrapper) ly, layers, doc);
				}
				else if (ly instanceof MWC.GUI.Chart.Painters.ETOPOPainter)
				{
					ETOPOHandler.exportThisPlottable(ly, layers, doc);
				}
				else if (ly instanceof MWC.GUI.ETOPO.ETOPO_2_Minute)
				{
					TOPOHandler.exportThisPlottable(ly, layers, doc);
				}
				else if (ly instanceof ExternallyManagedDataLayer)
				{
					ExternallyManagedLayerHandler.exportThisPlottable(ly, layers, doc);
				}
				else if (ly instanceof MWC.GUI.BaseLayer)
				{
					DebriefLayerHandler.exportLayer((MWC.GUI.BaseLayer) ly, layers, doc);
				}
			}

		}

		parent.appendChild(layers);

	}

}