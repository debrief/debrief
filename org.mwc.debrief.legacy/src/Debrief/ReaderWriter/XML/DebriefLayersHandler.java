
package Debrief.ReaderWriter.XML;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.ReaderWriter.XML.Tactical.CompositeTrackHandler;
import Debrief.ReaderWriter.XML.Tactical.PatternHandler;
import Debrief.ReaderWriter.XML.Tactical.TrackHandler;
import Debrief.Wrappers.BuoyPatternWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.ETOPO.ETOPO_2_Minute;
import MWC.GUI.Shapes.ChartFolio;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Features.ChartFolioHandler;
import MWC.Utilities.ReaderWriter.XML.Features.ETOPOHandler;
import MWC.Utilities.ReaderWriter.XML.Features.ExternallyManagedLayerHandler;
import MWC.Utilities.ReaderWriter.XML.Features.TOPOHandler;
import MWC.Utilities.ReaderWriter.XML.Util.NarrativeHandler;

public final class DebriefLayersHandler extends MWCXMLReader {
	public static void exportThis(final Debrief.GUI.Frames.Session session, final Element parent, final Document doc) {
		// fill it out
		final MWC.GUI.Layers data = session.getData();

		final ArrayList<LayerHandlerExtension> emptyList = new ArrayList<LayerHandlerExtension>();

		exportThis(data, parent, doc, emptyList);

	}

	public static void exportThis(final Layers data, final org.w3c.dom.Element parent, final Document doc,
			final List<LayerHandlerExtension> extraLoaders) {

		if (data == null)
			return;

		// create ourselves
		final Element layers = doc.createElement("layers");

		//
		final int len = data.size();

		for (int i = 0; i < len; i++) {
			final MWC.GUI.Layer ly = data.elementAt(i);

			// ok, see if we have a special processor
			boolean handled = false;
			if (extraLoaders != null && extraLoaders.size() > 0) {
				// ok, do a run through
				for (final Iterator<LayerHandlerExtension> iterator = extraLoaders.iterator(); iterator.hasNext();) {
					final LayerHandlerExtension thisE = iterator.next();

					// is it suitable for this layer?
					if (thisE.canExportThis(ly)) {
						// yes = go for it.
						thisE.exportThis(ly, layers, doc);
						handled = true;
						break;
					}
				}
			}
			if (!handled) {
				// find out which sort of layer this is
				if (ly instanceof Debrief.Wrappers.CompositeTrackWrapper) {
					CompositeTrackHandler.exportTrack((TrackWrapper) ly, layers, doc);
				} else if (ly instanceof TrackWrapper) {
					TrackHandler.exportTrack((TrackWrapper) ly, layers, doc);
				} else if (ly instanceof ChartFolio) {
					ChartFolioHandler.exportThisFolio((ChartFolio) ly, layers, doc);
				} else if (ly instanceof BuoyPatternWrapper) {
					PatternHandler.exportTrack((BuoyPatternWrapper) ly, layers, doc);
				} else if (ly instanceof NarrativeWrapper) {
					NarrativeHandler.exportNarrative((NarrativeWrapper) ly, layers, doc);
				} else if (ly instanceof ETOPOPainter) {
					ETOPOHandler.exportThisPlottable(ly, layers, doc);
				} else if (ly instanceof ETOPO_2_Minute) {
					TOPOHandler.exportThisPlottable(ly, layers, doc);
				} else if (ly instanceof ExternallyManagedDataLayer) {
					ExternallyManagedLayerHandler.exportThisPlottable(ly, layers, doc);
				} else if (ly instanceof BaseLayer) {
					DebriefLayerHandler.exportLayer((BaseLayer) ly, layers, doc);
				}
			}
		}
		parent.appendChild(layers);
	}

	public DebriefLayersHandler(final MWC.GUI.Layers theLayers) {
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
}