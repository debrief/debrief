package org.mwc.debrief.core.loaders.xml_handlers;

import org.mwc.cmap.core.interfaces.IControllableViewport;

import Debrief.ReaderWriter.XML.DetailsHandler;
import MWC.GUI.Layers;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 *
 * @author Ian Mayo
 * @version 1.0
 */

final public class PlotHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  public PlotHandler(String fileName, Layers destination, IControllableViewport view)
  {
    // inform our parent what type of class we are
    super("plot");

    // sort out the handlers
    addHandler(new SessionHandler(destination, view));
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
  }

  public static org.w3c.dom.Element exportPlot(Debrief.GUI.Frames.Session session, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element plt = doc.createElement("plot");
    plt.setAttribute("Created", new java.util.Date().toString());
    plt.setAttribute("Name", "Debrief Plot");
    String details = "Saved with Debrief version dated " + Debrief.GUI.VersionInfo.getVersion();
    SessionHandler.exportThis(session, plt, doc);
 //   DetailsHandler.exportPlot(details, plt, doc);
    return plt;
  }


}