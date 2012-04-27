package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;

import Debrief.ReaderWriter.XML.Tactical.*;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.ChartFolio;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Features.*;

public final class DebriefLayersHandler extends MWCXMLReader
{
	public DebriefLayersHandler(MWC.GUI.Layers theLayers)
	{
		// inform our parent what type of class we are
		super("layers");
		
		addHandler(new ChartFolioHandler(theLayers));
		addHandler(new DebriefLayerHandler(theLayers));
		addHandler(new TrackHandler(theLayers));
		addHandler(new PatternHandler(theLayers));
		addHandler(new NarrativeHandler(theLayers));
		addHandler(new ETOPOHandler(theLayers));
		addHandler(new TOPOHandler(theLayers));
		addHandler(new ExternallyManagedLayerHandler(theLayers));

	}

	public static void exportThis(Debrief.GUI.Frames.Session session,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		// fill it out
		MWC.GUI.Layers data = session.getData();

		exportThis(data, parent, doc);

	}

	public static void exportThis(
			Layers data,
			org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{

		if (data == null)
			return;

		// create ourselves
		Element layers = doc.createElement("layers");

		//
		int len = data.size();

		for (int i = 0; i < len; i++)
		{
			MWC.GUI.Layer ly = data.elementAt(i);

			// find out which sort of layer this is
			if (ly instanceof Debrief.Wrappers.TrackWrapper)
			{
				Debrief.ReaderWriter.XML.Tactical.TrackHandler.exportTrack(
						(Debrief.Wrappers.TrackWrapper) ly, layers, doc);
			}
			else if(ly instanceof ChartFolio)
			{
				ChartFolioHandler.exportThisFolio((ChartFolio)ly, layers, doc);
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

		parent.appendChild(layers);

	}

}