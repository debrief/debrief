package org.mwc.debrief.core.loaders.xml_handlers;

import java.util.Vector;

import org.mwc.cmap.core.interfaces.IControllableViewport;

import Debrief.ReaderWriter.XML.DebriefLayersHandler;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */


public class SessionHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  public SessionHandler(Layers _theLayers, final IControllableViewport  view)
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
    addHandler(new GUIHandler()
    		{
					public void assignTracks(String primaryTrack, Vector secondaryTracks)
					{
						System.err.println("SHOULD BE STORING PRIMARY & SECONDARY TRACKS");
					}
    		});
    addHandler(new DebriefLayersHandler(_theLayers));

  }

  public final void elementClosed()
  { 	
  	// and the GUI details
  //	setGUIDetails(null);
  }

  public static void exportThis(Debrief.GUI.Frames.Session session, org.w3c.dom.Element parent,
                                org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eSession = doc.createElement("session");

    // now the Layers
    DebriefLayersHandler.exportThis(session, eSession, doc);

    // now the projection
    Debrief.GUI.Views.PlainView pl = session.getCurrentView();
    if (pl instanceof Debrief.GUI.Views.AnalysisView)
    {
      Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView) pl;
      ProjectionHandler.exportProjection(av.getChart().getCanvas().getProjection(), eSession, doc);
    }

    // now the GUI
    GUIHandler.exportThis(session, eSession, doc);

    // send out the data
    parent.appendChild(eSession);
  }

}